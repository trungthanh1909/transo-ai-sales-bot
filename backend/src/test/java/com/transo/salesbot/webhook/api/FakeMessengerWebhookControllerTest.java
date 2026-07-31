package com.transo.salesbot.webhook.api;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.transo.salesbot.webhook.application.InboundWebhookReceipt;
import com.transo.salesbot.webhook.application.ProcessingStatus;
import com.transo.salesbot.webhook.application.ReceiveInboundWebhookEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(FakeMessengerWebhookController.class)
class FakeMessengerWebhookControllerTest {

    private static final String VALID_EVENT = """
            {
              "externalEventId": "mid.local-0001",
              "senderId": "customer-123",
              "recipientId": "page-456",
              "occurredAt": "2026-07-30T16:00:00Z",
              "message": {
                "text": "Bao gia loc dau ma 123"
              }
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceiveInboundWebhookEventService receiveInboundWebhookEventService;

    @Test
    void returnsCreatedForFirstValidEvent() throws Exception {
        when(receiveInboundWebhookEventService.receive("mid.local-0001", VALID_EVENT))
                .thenReturn(new InboundWebhookReceipt("mid.local-0001", ProcessingStatus.RECEIVED, false));

        mockMvc.perform(post("/api/local/webhooks/messenger")
                        .contentType("application/json")
                        .content(VALID_EVENT))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.externalEventId").value("mid.local-0001"))
                .andExpect(jsonPath("$.status").value("RECEIVED"))
                .andExpect(jsonPath("$.duplicate").value(false));
    }

    @Test
    void returnsOkForDuplicateEvent() throws Exception {
        when(receiveInboundWebhookEventService.receive("mid.local-0001", VALID_EVENT))
                .thenReturn(new InboundWebhookReceipt("mid.local-0001", ProcessingStatus.RECEIVED, true));

        mockMvc.perform(post("/api/local/webhooks/messenger")
                        .contentType("application/json")
                        .content(VALID_EVENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duplicate").value(true));
    }

    @Test
    void retainsUnknownFieldsInRawPayloadWhenCurrentJacksonBehaviorIgnoresThem() throws Exception {
        String eventWithUnknownField = VALID_EVENT.replace("\"message\": {", "\"additionalField\": {\"source\": \"fixture\"},\n  \"message\": {");
        when(receiveInboundWebhookEventService.receive("mid.local-0001", eventWithUnknownField))
                .thenReturn(new InboundWebhookReceipt("mid.local-0001", ProcessingStatus.RECEIVED, false));

        mockMvc.perform(post("/api/local/webhooks/messenger")
                        .contentType("application/json")
                        .content(eventWithUnknownField))
                .andExpect(status().isCreated());

        verify(receiveInboundWebhookEventService).receive("mid.local-0001", eventWithUnknownField);
    }

    @Test
    void rejectsEachMissingRequiredFieldWithoutPersisting() throws Exception {
        String[] invalidEvents = {
                VALID_EVENT.replace("  \"externalEventId\": \"mid.local-0001\",\n", ""),
                VALID_EVENT.replace("  \"senderId\": \"customer-123\",\n", ""),
                VALID_EVENT.replace("  \"recipientId\": \"page-456\",\n", ""),
                VALID_EVENT.replace("  \"occurredAt\": \"2026-07-30T16:00:00Z\",\n", ""),
                VALID_EVENT.replace("  \"message\": {\n    \"text\": \"Bao gia loc dau ma 123\"\n  }\n", "")
        };

        for (String invalidEvent : invalidEvents) {
            mockMvc.perform(post("/api/local/webhooks/messenger")
                            .contentType("application/json")
                            .content(invalidEvent))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("invalid_request"));
        }

        verify(receiveInboundWebhookEventService, never()).receive(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void rejectsEachBlankBoundedFieldWithoutPersisting() throws Exception {
        String[] invalidEvents = {
                VALID_EVENT.replace("mid.local-0001", " "),
                VALID_EVENT.replace("customer-123", " "),
                VALID_EVENT.replace("page-456", " "),
                VALID_EVENT.replace("Bao gia loc dau ma 123", " ")
        };

        for (String invalidEvent : invalidEvents) {
            mockMvc.perform(post("/api/local/webhooks/messenger")
                            .contentType("application/json")
                            .content(invalidEvent))
                    .andExpect(status().isBadRequest());
        }

        verify(receiveInboundWebhookEventService, never()).receive(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void rejectsMessageWithoutTextWithoutPersisting() throws Exception {
        String messageWithoutText = """
                {
                  "externalEventId": "evt-1",
                  "senderId": "sender-1",
                  "recipientId": "recipient-1",
                  "occurredAt": "2026-07-31T08:00:00+07:00",
                  "message": {}
                }
                """;

        mockMvc.perform(post("/api/local/webhooks/messenger")
                        .contentType("application/json")
                        .content(messageWithoutText))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_request"));

        verify(receiveInboundWebhookEventService, never()).receive(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void rejectsEachOversizedBoundedFieldWithoutPersisting() throws Exception {
        String[] invalidEvents = {
                VALID_EVENT.replace("mid.local-0001", "x".repeat(256)),
                VALID_EVENT.replace("customer-123", "x".repeat(256)),
                VALID_EVENT.replace("page-456", "x".repeat(256)),
                VALID_EVENT.replace("Bao gia loc dau ma 123", "x".repeat(4001))
        };

        for (String invalidEvent : invalidEvents) {
            mockMvc.perform(post("/api/local/webhooks/messenger")
                            .contentType("application/json")
                            .content(invalidEvent))
                    .andExpect(status().isBadRequest());
        }

        verify(receiveInboundWebhookEventService, never()).receive(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void rejectsInvalidTimestampMissingMessageAndMalformedJsonWithoutPersisting() throws Exception {
        String invalidTimestamp = VALID_EVENT.replace("2026-07-30T16:00:00Z", "2026-07-30T16:00:00");

        mockMvc.perform(post("/api/local/webhooks/messenger").contentType("application/json").content(invalidTimestamp))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/local/webhooks/messenger").contentType("application/json").content("{not json"))
                .andExpect(status().isBadRequest());

        verify(receiveInboundWebhookEventService, never()).receive(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }
}
