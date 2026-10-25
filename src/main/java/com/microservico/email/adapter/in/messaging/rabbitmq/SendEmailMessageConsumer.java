package com.microservico.email.adapter.in.messaging.rabbitmq;

import com.microservico.email.adapter.messaging.rabbitmq.SendEmailMessage;
import com.microservico.email.application.port.in.SendEmailCommand;
import com.microservico.email.application.port.in.SendEmailUseCase;
import jakarta.validation.Validator;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true")
public class SendEmailMessageConsumer {

    private final JsonMapper jsonMapper;
    private final Validator validator;
    private final SendEmailUseCase sendEmailUseCase;

    public SendEmailMessageConsumer(
            JsonMapper jsonMapper,
            Validator validator,
            SendEmailUseCase sendEmailUseCase
    ) {
        this.jsonMapper = jsonMapper;
        this.validator = validator;
        this.sendEmailUseCase = sendEmailUseCase;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void consume(byte[] payload) {
        final SendEmailMessage message;
        try {
            message = jsonMapper.readValue(new String(payload, StandardCharsets.UTF_8), SendEmailMessage.class);
        } catch (JacksonException exception) {
            throw new AmqpRejectAndDontRequeueException("Invalid email message JSON", exception);
        }

        if (message == null || !validator.validate(message).isEmpty()) {
            throw new AmqpRejectAndDontRequeueException("Email message failed validation");
        }

        sendEmailUseCase.execute(new SendEmailCommand(
                message.ownerRef(), message.from(), message.to(), message.subject(), message.body()
        ));
    }
}
