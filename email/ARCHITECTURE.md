# Arquitetura Clean Architecture

Este microserviço envia e registra e-mails. A organização segue a Clean Architecture: as regras de negócio ficam no centro e não conhecem HTTP, Spring, SMTP ou JPA.

## Regra de dependência

As dependências de código sempre apontam para dentro:

```text
Adapters e Infrastructure  ->  Application  ->  Domain
```

- `domain` não depende de nenhuma outra camada.
- `application` depende apenas de `domain`.
- `adapter` depende de `application` e `domain` para traduzir tecnologias externas.
- `infrastructure` conecta as implementações concretas às portas da aplicação.

## Estrutura de pastas

```text
email/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── ARCHITECTURE.md
└── src/
    ├── main/
    │   ├── java/com/microservico/email/
    │   │   ├── EmailApplication.java
    │   │   ├── domain/
    │   │   │   └── model/
    │   │   │       ├── Email.java
    │   │   │       └── EmailStatus.java
    │   │   ├── application/
    │   │   │   ├── exception/
    │   │   │   │   └── EmailDeliveryException.java
    │   │   │   ├── port/
    │   │   │   │   ├── in/
    │   │   │   │   │   ├── SendEmailCommand.java
    │   │   │   │   │   └── SendEmailUseCase.java
    │   │   │   │   └── out/
    │   │   │   │       ├── EmailDeliveryGateway.java
    │   │   │   │       └── EmailRepository.java
    │   │   │   └── service/
    │   │   │       └── SendEmailService.java
    │   │   ├── adapter/
    │   │   │   ├── in/web/
    │   │   │   │   ├── EmailController.java
    │   │   │   │   ├── EmailRequest.java
    │   │   │   │   └── EmailResponse.java
    │   │   │   └── out/
    │   │   │       ├── mail/
    │   │   │       │   └── SmtpEmailDeliveryGateway.java
    │   │   │       └── persistence/
    │   │   │           ├── EmailEntity.java
    │   │   │           ├── JpaEmailRepository.java
    │   │   │           └── SpringDataEmailRepository.java
    │   │   └── infrastructure/
    │   │       └── config/
    │   │           └── ApplicationConfiguration.java
    │   └── resources/
    │       └── application.properties
    └── test/java/com/microservico/email/
        ├── EmailApplicationTests.java
        └── application/service/
            └── SendEmailServiceTest.java
```

## Camadas e arquivos

### Domain

Contém o vocabulário e o estado do negócio. Não possui anotações de Spring, JPA ou validações HTTP.

| Arquivo | Responsabilidade |
| --- | --- |
| `domain/model/Email.java` | Entidade de domínio. Cria um e-mail, registra êxito (`markAsSent`) ou falha (`markAsError`) e expõe seus dados. |
| `domain/model/EmailStatus.java` | Estados possíveis do processamento: `SENT` e `ERROR`. |

### Application

Contém o caso de uso e os contratos que ele precisa para conversar com o exterior.

| Arquivo | Responsabilidade |
| --- | --- |
| `application/port/in/SendEmailCommand.java` | Dados de entrada independentes de HTTP. |
| `application/port/in/SendEmailUseCase.java` | Porta de entrada do caso de uso: `execute(command)`. |
| `application/service/SendEmailService.java` | Implementa o fluxo de enviar, definir status e salvar o e-mail. Não conhece SMTP nem JPA. |
| `application/port/out/EmailDeliveryGateway.java` | Contrato para qualquer tecnologia capaz de entregar um e-mail. |
| `application/port/out/EmailRepository.java` | Contrato para salvar o histórico de envio. |
| `application/exception/EmailDeliveryException.java` | Falha padronizada que um adaptador de envio informa ao caso de uso. |

### Adapters de entrada

Traduzem requisições HTTP para os contratos da aplicação e convertem a resposta para HTTP.

| Arquivo | Responsabilidade |
| --- | --- |
| `adapter/in/web/EmailController.java` | Expõe `POST /sending-email`, cria o comando e chama `SendEmailUseCase`. |
| `adapter/in/web/EmailRequest.java` | Corpo aceito pela API; contém as validações de entrada (`@NotBlank` e `@Email`). |
| `adapter/in/web/QueuedEmailRequest.java` | Corpo de entrada específico do envio assíncrono, com limites de tamanho para restringir mensagens publicadas na fila. |
| `adapter/in/web/EmailResponse.java` | Corpo devolvido pela API; converte a entidade de domínio para o formato HTTP. |
| `adapter/in/messaging/rabbitmq/SendEmailMessageConsumer.java` | Consome eventos da fila, valida o conteúdo e chama `SendEmailUseCase`. |

### Adapters de saída

Implementam as portas de saída usando ferramentas externas.

| Arquivo | Responsabilidade |
| --- | --- |
| `adapter/out/mail/SmtpEmailDeliveryGateway.java` | Implementa `EmailDeliveryGateway` com `JavaMailSender` e converte `MailException` para `EmailDeliveryException`. |
| `adapter/out/messaging/rabbitmq/RabbitEmailQueuePublisher.java` | Publica mensagens JSON no RabbitMQ e aguarda confirmação do broker antes de aceitar a solicitação assíncrona. |
| `adapter/out/persistence/EmailEntity.java` | Entidade JPA da tabela `TB_EMAIL`. É separada de `domain/model/Email.java` para que o domínio não dependa de JPA. |
| `adapter/out/persistence/SpringDataEmailRepository.java` | Repositório técnico do Spring Data JPA. |
| `adapter/out/persistence/JpaEmailRepository.java` | Implementa a porta `EmailRepository`; transforma `Email` em `EmailEntity` e vice-versa. |

### Infrastructure

É a borda de composição da aplicação.

| Arquivo | Responsabilidade |
| --- | --- |
| `infrastructure/config/ApplicationConfiguration.java` | Cria e conecta os beans: relógio, gateway SMTP, repositório JPA e caso de uso. Esta é a única parte que decide quais implementações concretas serão usadas. |
| `infrastructure/config/RabbitMqConfiguration.java` | Declara exchange, fila durável, bindings e fila de mensagens rejeitadas. A configuração só é ativada quando RabbitMQ está habilitado. |
| `EmailApplication.java` | Inicializa o Spring Boot. |
| `resources/application.properties` | Configura porta, conexão com banco e servidor SMTP. Não deve conter segredos reais versionados. |

## Fluxo de envio

```text
HTTP POST /sending-email
        │
        ▼
EmailController
        │ EmailRequest → SendEmailCommand
        ▼
SendEmailUseCase / SendEmailService
        │
        ├── EmailDeliveryGateway → SmtpEmailDeliveryGateway → SMTP
        │
        ├── Email marca SENT ou ERROR
        │
        └── EmailRepository → JpaEmailRepository → Spring Data JPA → TB_EMAIL
        │
        ▼
EmailResponse → resposta HTTP 201
```

Se o SMTP falhar, o adaptador lança `EmailDeliveryException`. O caso de uso registra o estado `ERROR` e ainda persiste o histórico do envio. Falhas de banco não são ocultadas.

## Fluxo assíncrono com RabbitMQ

`POST /sending-email` continua síncrono e mantém o contrato existente. `POST /sending-email/async` valida a mesma requisição, publica uma mensagem JSON com um identificador e responde `202 Accepted` após confirmação do RabbitMQ. O consumidor valida novamente a mensagem e chama o mesmo `SendEmailUseCase`; mensagens malformadas ou rejeitadas vão para `email.send.dlq` sem requeue infinito.

A integração é desligada por padrão (`EMAIL_RABBITMQ_ENABLED=false`) para que a aplicação existente continue inicializando sem o broker. Para ativar, use `EMAIL_RABBITMQ_ENABLED=true` e configure `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME` e `RABBITMQ_PASSWORD`. Falhas que escapam do caso de uso recebem até três tentativas com espera crescente; mensagens ainda não processadas vão para a DLQ. Uma falha SMTP segue o comportamento existente: fica registrada como `ERROR`, sem tentativa automática adicional. As credenciais `guest` do padrão local não devem ser usadas em produção; restrinja o acesso ao broker e configure TLS na infraestrutura de produção.

O processamento de fila é ao menos uma vez: uma interrupção entre o envio SMTP e o ACK pode ocasionar duplicidade. O `messageId` permite rastrear a mensagem, mas idempotência durável exige persistir e deduplicar esse identificador; isso não foi incluído neste fluxo básico.

## Contrato HTTP

**Rota:** `POST /sending-email`

Exemplo de entrada:

```json
{
  "ownerRef": "cliente-123",
  "emailFrom": "remetente@exemplo.com",
  "emailTo": "destinatario@exemplo.com",
  "subject": "Assunto",
  "text": "Conteúdo da mensagem"
}
```

A resposta possui os mesmos dados acrescidos de `emailId`, `sendDateEmail` e `statusEmail`.

## Testes

| Arquivo | Escopo |
| --- | --- |
| `application/service/SendEmailServiceTest.java` | Testa o caso de uso sem subir Spring, banco ou SMTP: cenário de envio e cenário de falha. |
| `EmailApplicationTests.java` | Verifica que o contexto Spring consegue ser carregado. |

## Como estender

- Para usar outro provedor de e-mail, implemente `EmailDeliveryGateway` e troque o bean em `ApplicationConfiguration`.
- Para usar fila, crie outro adaptador de saída que implemente `EmailDeliveryGateway`.
- Para trocar MySQL por outro banco, implemente `EmailRepository` com o mecanismo desejado.
- Para criar outra entrada, como consumo de fila ou CLI, implemente um novo adaptador de entrada que chame `SendEmailUseCase`.
