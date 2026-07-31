package com.transo.salesbot.webhook.application;

import org.springframework.stereotype.Service;

@Service
public class ReceiveInboundWebhookEventService {

    private final InboundWebhookEventStore inboundWebhookEventStore;

    public ReceiveInboundWebhookEventService(InboundWebhookEventStore inboundWebhookEventStore) {
        this.inboundWebhookEventStore = inboundWebhookEventStore;
    }

    public InboundWebhookReceipt receive(String externalEventId, String rawPayload) {
        return inboundWebhookEventStore.store(externalEventId, rawPayload);
    }
}
