package com.microservico.email.services.ports;

public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(Throwable cause) {
        super(cause);
    }
}
