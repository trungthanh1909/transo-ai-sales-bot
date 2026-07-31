package com.transo.salesbot.webhook.api;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = FakeMessengerWebhookController.class)
class WebhookApiExceptionHandler {

    @ExceptionHandler(InvalidWebhookRequestException.class)
    ResponseEntity<Map<String, String>> handleInvalidRequest() {
        return ResponseEntity.badRequest().body(Map.of("error", "invalid_request"));
    }
}
