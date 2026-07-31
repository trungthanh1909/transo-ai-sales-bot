package com.transo.salesbot.webhook.application;

public interface InboundWebhookEventStore {

    InboundWebhookReceipt store(String externalEventId, String rawPayload);
}
