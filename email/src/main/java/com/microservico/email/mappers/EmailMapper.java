package com.microservico.email.mappers;

import com.microservico.email.dtos.EmailDto;
import com.microservico.email.models.EmailModel;

public final class EmailMapper {

    private EmailMapper() {
    }

    public static EmailModel toModel(EmailDto dto) {
        var email = new EmailModel();
        email.setOwnerRef(dto.getOwnerRef());
        email.setEmailFrom(dto.getEmailFrom());
        email.setEmailTo(dto.getEmailTo());
        email.setSubject(dto.getSubject());
        email.setText(dto.getText());
        return email;
    }
}
