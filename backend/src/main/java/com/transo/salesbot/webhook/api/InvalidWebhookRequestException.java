package com.transo.salesbot.webhook.api;

class InvalidWebhookRequestException extends RuntimeException {

    InvalidWebhookRequestException(Throwable cause) {
        super(cause);
    }

    InvalidWebhookRequestException() {
    }
}
