package com.microservico.email.application.exception;

public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(Throwable cause) {
        super(cause);
    }
}
