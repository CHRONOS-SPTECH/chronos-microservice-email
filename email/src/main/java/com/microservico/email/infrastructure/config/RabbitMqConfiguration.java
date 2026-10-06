package com.microservico.email.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true")
public class RabbitMqConfiguration {

    @Bean
    DirectExchange emailExchange(@Value("${app.rabbitmq.exchange}") String name) {
        return new DirectExchange(name, true, false);
    }

    @Bean
    DirectExchange emailDeadLetterExchange(@Value("${app.rabbitmq.dead-letter-exchange}") String name) {
        return new DirectExchange(name, true, false);
    }

    @Bean
    Queue emailQueue(
            @Value("${app.rabbitmq.queue}") String queue,
            @Value("${app.rabbitmq.dead-letter-exchange}") String deadLetterExchange,
            @Value("${app.rabbitmq.dead-letter-routing-key}") String deadLetterRoutingKey
    ) {
        return QueueBuilder.durable(queue)
                .deadLetterExchange(deadLetterExchange)
                .deadLetterRoutingKey(deadLetterRoutingKey)
                .build();
    }

    @Bean
    Queue emailDeadLetterQueue(@Value("${app.rabbitmq.dead-letter-queue}") String queue) {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    Binding emailBinding(
            @Qualifier("emailQueue") Queue emailQueue,
            @Qualifier("emailExchange") DirectExchange emailExchange,
            @Value("${app.rabbitmq.routing-key}") String routingKey
    ) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(routingKey);
    }

    @Bean
    Binding emailDeadLetterBinding(
            @Qualifier("emailDeadLetterQueue") Queue emailDeadLetterQueue,
            @Qualifier("emailDeadLetterExchange") DirectExchange emailDeadLetterExchange,
            @Value("${app.rabbitmq.dead-letter-routing-key}") String routingKey
    ) {
        return BindingBuilder.bind(emailDeadLetterQueue).to(emailDeadLetterExchange).with(routingKey);
    }
}
