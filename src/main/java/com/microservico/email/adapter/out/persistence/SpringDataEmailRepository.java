package com.microservico.email.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEmailRepository extends JpaRepository<EmailEntity, Long> {
}
