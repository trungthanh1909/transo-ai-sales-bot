package com.transo.salesbot.webhook.application;

public record InboundWebhookReceipt(
        String externalEventId,
        ProcessingStatus status,
        boolean duplicate
) {
}
