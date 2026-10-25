package com.microservico.email.adapter.out.persistence;

import com.microservico.email.application.port.out.EmailRepository;
import com.microservico.email.domain.model.Email;

public class JpaEmailRepository implements EmailRepository {

    private final SpringDataEmailRepository repository;

    public JpaEmailRepository(SpringDataEmailRepository repository) {
        this.repository = repository;
    }

    @Override
    public Email save(Email email) {
        return toDomain(repository.save(toEntity(email)));
    }

    private EmailEntity toEntity(Email email) {
        var entity = new EmailEntity();
        entity.setId(email.getId());
        entity.setOwnerRef(email.getOwnerRef());
        entity.setEmailFrom(email.getFrom());
        entity.setEmailTo(email.getTo());
        entity.setSubject(email.getSubject());
        entity.setText(email.getBody());
        entity.setSendDateEmail(email.getSentAt());
        entity.setStatusEmail(email.getStatus());
        return entity;
    }

    private Email toDomain(EmailEntity entity) {
        return Email.reconstitute(
                entity.getId(), entity.getOwnerRef(), entity.getEmailFrom(), entity.getEmailTo(),
                entity.getSubject(), entity.getText(), entity.getSendDateEmail(), entity.getStatusEmail()
        );
    }
}
