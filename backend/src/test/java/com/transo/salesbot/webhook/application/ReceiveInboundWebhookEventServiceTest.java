package com.transo.salesbot.webhook.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReceiveInboundWebhookEventServiceTest {

    @Test
    void returnsCreatedReceiptFromPersistenceBoundary() {
        InboundWebhookEventStore store = (externalEventId, rawPayload) ->
                new InboundWebhookReceipt(externalEventId, ProcessingStatus.RECEIVED, false);
        ReceiveInboundWebhookEventService service = new ReceiveInboundWebhookEventService(store);

        InboundWebhookReceipt receipt = service.receive("mid.local-0001", "{\"externalEventId\":\"mid.local-0001\"}");

        assertEquals("mid.local-0001", receipt.externalEventId());
        assertEquals(ProcessingStatus.RECEIVED, receipt.status());
        assertFalse(receipt.duplicate());
    }

    @Test
    void returnsDuplicateReceiptFromPersistenceBoundaryWithoutChangingPayload() {
        String originalPayload = "{\"message\":{\"text\":\"original\"}}";
        String changedPayload = "{\"message\":{\"text\":\"changed\"}}";
        InboundWebhookEventStore store = new InboundWebhookEventStore() {
            private String storedPayload = originalPayload;

            @Override
            public InboundWebhookReceipt store(String externalEventId, String rawPayload) {
                assertEquals(changedPayload, rawPayload);
                assertEquals(originalPayload, storedPayload);
                return new InboundWebhookReceipt(externalEventId, ProcessingStatus.RECEIVED, true);
            }
        };
        ReceiveInboundWebhookEventService service = new ReceiveInboundWebhookEventService(store);

        InboundWebhookReceipt receipt = service.receive("mid.local-0001", changedPayload);

        assertTrue(receipt.duplicate());
        assertEquals(ProcessingStatus.RECEIVED, receipt.status());
    }
}
