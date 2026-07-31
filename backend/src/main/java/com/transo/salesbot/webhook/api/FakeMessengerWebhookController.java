package com.transo.salesbot.webhook.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transo.salesbot.webhook.application.InboundWebhookReceipt;
import com.transo.salesbot.webhook.application.ReceiveInboundWebhookEventService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile({"local", "test"})
@RestController
@RequestMapping("/api/local/webhooks/messenger")
public class FakeMessengerWebhookController {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final ReceiveInboundWebhookEventService receiveInboundWebhookEventService;

    public FakeMessengerWebhookController(
            ObjectMapper objectMapper,
            Validator validator,
            ReceiveInboundWebhookEventService receiveInboundWebhookEventService
    ) {
        this.objectMapper = objectMapper;
        this.validator = validator;
        this.receiveInboundWebhookEventService = receiveInboundWebhookEventService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<FakeMessengerWebhookResponse> receive(@RequestBody String rawPayload) {
        FakeMessengerWebhookEvent event = parseAndValidate(rawPayload);
        InboundWebhookReceipt receipt = receiveInboundWebhookEventService.receive(event.externalEventId(), rawPayload);
        FakeMessengerWebhookResponse response = new FakeMessengerWebhookResponse(
                receipt.externalEventId(),
                receipt.status().name(),
                receipt.duplicate()
        );

        return ResponseEntity.status(receipt.duplicate() ? HttpStatus.OK : HttpStatus.CREATED).body(response);
    }

    private FakeMessengerWebhookEvent parseAndValidate(String rawPayload) {
        FakeMessengerWebhookEvent event;
        try {
            event = objectMapper.readValue(rawPayload, FakeMessengerWebhookEvent.class);
        } catch (JsonProcessingException exception) {
            throw new InvalidWebhookRequestException(exception);
        }

        Set<ConstraintViolation<FakeMessengerWebhookEvent>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            throw new InvalidWebhookRequestException();
        }
        return event;
    }
}
