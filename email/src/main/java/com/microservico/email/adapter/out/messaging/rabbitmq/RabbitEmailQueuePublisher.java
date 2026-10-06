package com.microservico.email.adapter.out.messaging.rabbitmq;

import com.microservico.email.adapter.messaging.rabbitmq.SendEmailMessage;
import com.microservico.email.application.port.in.SendEmailCommand;
import com.microservico.email.application.port.out.EmailQueuePublisher;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true")
public class RabbitEmailQueuePublisher implements EmailQueuePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final JsonMapper jsonMapper;
    private final String exchange;
    private final String routingKey;

    public RabbitEmailQueuePublisher(
            RabbitTemplate rabbitTemplate,
            JsonMapper jsonMapper,
            @Value("${app.rabbitmq.exchange}") String exchange,
            @Value("${app.rabbitmq.routing-key}") String routingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.jsonMapper = jsonMapper;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public UUID publish(SendEmailCommand command) {
        var messageId = UUID.randomUUID();
        var event = new SendEmailMessage(
                messageId, command.ownerRef(), command.from(), command.to(), command.subject(), command.body()
        );
        try {
            var payload = jsonMapper.writeValueAsString(event);
            var correlationData = new CorrelationData(messageId.toString());
            rabbitTemplate.convertAndSend(exchange, routingKey, payload, message -> {
                message.getMessageProperties().setMessageId(messageId.toString());
                message.getMessageProperties().setContentType("application/json");
                return message;
            }, correlationData);
            var confirm = correlationData.getFuture().get(Duration.ofSeconds(5).toMillis(), TimeUnit.MILLISECONDS);
            if (!confirm.isAck() || correlationData.getReturned() != null) {
                throw new IllegalStateException("RabbitMQ did not confirm delivery of the email message");
            }
            return messageId;
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not serialize email message", exception);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not publish email message to RabbitMQ", exception);
        }
    }
}
