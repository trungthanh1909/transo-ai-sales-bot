package com.transo.salesbot.webhook.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.transo.salesbot.webhook.application.InboundWebhookReceipt;
import com.transo.salesbot.webhook.application.ProcessingStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.EmptyResultDataAccessException;

@ExtendWith(MockitoExtension.class)
class PostgresInboundWebhookEventStoreTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void returnsCreatedWhenPostgresInsertReturnsStatus() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("mid.local-0001"), eq("{\"message\":{}}")))
                .thenReturn(List.of(ProcessingStatus.RECEIVED));
        PostgresInboundWebhookEventStore store = new PostgresInboundWebhookEventStore(jdbcTemplate);

        InboundWebhookReceipt receipt = store.store("mid.local-0001", "{\"message\":{}}");

        assertFalse(receipt.duplicate());
    }

    @Test
    void returnsDuplicateWhenPostgresConflictReturnsNoInsertedRow() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("mid.local-0001"), eq("{\"message\":{\"text\":\"changed\"}}")))
                .thenReturn(List.of());
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("mid.local-0001")))
                .thenReturn(ProcessingStatus.RECEIVED);
        PostgresInboundWebhookEventStore store = new PostgresInboundWebhookEventStore(jdbcTemplate);

        InboundWebhookReceipt receipt = store.store("mid.local-0001", "{\"message\":{\"text\":\"changed\"}}");

        assertTrue(receipt.duplicate());
    }

    @Test
    void failsWhenPostgresConflictCannotReadExistingEvent() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("mid.local-0001"), eq("{\"message\":{}}")))
                .thenReturn(List.of());
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq("mid.local-0001")))
                .thenThrow(new EmptyResultDataAccessException(1));
        PostgresInboundWebhookEventStore store = new PostgresInboundWebhookEventStore(jdbcTemplate);

        assertThrows(IllegalStateException.class, () -> store.store("mid.local-0001", "{\"message\":{}}"));
    }
}
