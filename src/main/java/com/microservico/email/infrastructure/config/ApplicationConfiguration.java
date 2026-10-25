package com.microservico.email.infrastructure.config;

import com.microservico.email.adapter.out.mail.SmtpEmailDeliveryGateway;
import com.microservico.email.adapter.out.persistence.JpaEmailRepository;
import com.microservico.email.adapter.out.persistence.SpringDataEmailRepository;
import com.microservico.email.application.port.in.SendEmailUseCase;
import com.microservico.email.application.port.out.EmailDeliveryGateway;
import com.microservico.email.application.port.out.EmailRepository;
import com.microservico.email.application.service.SendEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Clock;

@Configuration
public class ApplicationConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    EmailDeliveryGateway emailDeliveryGateway(JavaMailSender mailSender) {
        return new SmtpEmailDeliveryGateway(mailSender);
    }

    @Bean
    EmailRepository emailRepository(SpringDataEmailRepository repository) {
        return new JpaEmailRepository(repository);
    }

    @Bean
    SendEmailUseCase sendEmailUseCase(EmailDeliveryGateway deliveryGateway, EmailRepository repository, Clock clock) {
        return new SendEmailService(deliveryGateway, repository, clock);
    }
}
