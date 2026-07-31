package com.transo.salesbot.webhook.api;

public record FakeMessengerWebhookResponse(
        String externalEventId,
        String status,
        boolean duplicate
) {
}
