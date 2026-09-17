package com.microservico.email.infrastructure.persistence;

import com.microservico.email.models.EmailModel;
import com.microservico.email.repositories.EmailRepository;
import com.microservico.email.services.ports.EmailPersistence;
import org.springframework.stereotype.Component;

@Component
public class JpaEmailPersistenceAdapter implements EmailPersistence {

    private final EmailRepository emailRepository;

    public JpaEmailPersistenceAdapter(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public EmailModel save(EmailModel email) {
        return emailRepository.save(email);
    }
}
