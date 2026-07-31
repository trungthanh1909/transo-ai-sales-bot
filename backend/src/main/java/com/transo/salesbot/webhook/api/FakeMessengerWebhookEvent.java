package com.transo.salesbot.webhook.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record FakeMessengerWebhookEvent(
        @NotBlank @Size(max = 255) String externalEventId,
        @NotBlank @Size(max = 255) String senderId,
        @NotBlank @Size(max = 255) String recipientId,
        @NotNull OffsetDateTime occurredAt,
        @NotNull @Valid Message message
) {

    public record Message(@NotBlank @Size(max = 4000) String text) {
    }
}
