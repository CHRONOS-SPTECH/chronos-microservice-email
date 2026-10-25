# Chronos microservice de e-mail

Microserviço Spring Boot para envio de e-mail síncrono ou assíncrono via RabbitMQ. O envio assíncrono recebe uma requisição por destinatário. O envio SMTP local pode ser capturado pelo MailDev.

## Executar localmente

Requisitos: Java 21, Docker Compose e MySQL local (ou configure `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`). Na raiz do repositório, copie `.env.example` para `.env` e ajuste os valores se necessário. O Compose usa esse arquivo para configurar o RabbitMQ; exporte/carregue as mesmas variáveis no terminal que inicia o Spring.

```powershell
Copy-Item .env.example .env
docker compose up -d rabbitmq maildev
Set-Location email
./mvnw spring-boot:run
```

O serviço HTTP usa `http://localhost:8081` por padrão (o backend Chronos usa 8080). RabbitMQ recebe conexões AMQP em `localhost:5672` e o painel de administração fica em `http://localhost:15672`. MailDev recebe SMTP em `localhost:1025` e mostra as mensagens em `http://localhost:1080`.

No PowerShell, para habilitar o consumidor/publicador, defina no terminal antes de iniciar o Spring:

```powershell
$env:EMAIL_RABBITMQ_ENABLED = 'true'
$env:RABBITMQ_HOST = 'localhost'
$env:RABBITMQ_PORT = '5672'
$env:RABBITMQ_USERNAME = 'guest'
$env:RABBITMQ_PASSWORD = 'guest'
$env:SPRING_MAIL_HOST = 'localhost'
$env:SPRING_MAIL_PORT = '1025'
$env:SPRING_MAIL_USERNAME = ''
$env:SPRING_MAIL_PASSWORD = ''
```

As variáveis `EMAIL_RABBITMQ_EXCHANGE`, `EMAIL_RABBITMQ_QUEUE`, `EMAIL_RABBITMQ_ROUTING_KEY`, `EMAIL_RABBITMQ_DLX`, `EMAIL_RABBITMQ_DLQ` e `EMAIL_RABBITMQ_DLQ_ROUTING_KEY` permitem personalizar os nomes; os padrões estão em `.env.example`. `SERVER_PORT` altera a porta HTTP. Para SMTP autenticado, configure usuário e senha (`SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`). `EMAIL_FROM` configura o remetente SMTP de envelope; o campo `emailFrom` continua sendo o remetente visível definido individualmente na requisição.

## Integração com o backend Chronos

O Chronos deve chamar uma vez por destinatário:

```http
POST http://localhost:8081/sending-email/async
Content-Type: application/json
```

```json
{
  "ownerRef": "evento-123",
  "emailFrom": "remetente@dominio.com",
  "emailTo": "destinatario@dominio.com",
  "subject": "Novo evento: título",
  "text": "<html><body><h1>Novo evento</h1></body></html>"
}
```

Com RabbitMQ habilitado, a resposta `202 Accepted` é enviada somente depois de o broker confirmar a publicação; a resposta contém `messageId` e `status`. Se a integração estiver desabilitada ou a publicação não for confirmada, o serviço responde `503 Service Unavailable`. O corpo `text` é enviado como HTML e o endereço de destino é validado. O Chronos precisa configurar `EMAIL_RABBITMQ_ENABLED=true`, `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME` e `RABBITMQ_PASSWORD` no ambiente do microserviço. Os nomes de exchange/fila/roteamento devem coincidir entre os serviços caso sejam personalizados.

O endpoint síncrono `POST /sending-email` permanece disponível e mantém seu comportamento existente.
