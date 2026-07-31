package com.transo.salesbot.webhook.persistence;

import com.transo.salesbot.webhook.application.InboundWebhookEventStore;
import com.transo.salesbot.webhook.application.InboundWebhookReceipt;
import com.transo.salesbot.webhook.application.ProcessingStatus;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresInboundWebhookEventStore implements InboundWebhookEventStore {

    private static final String INSERT_EVENT = """
            INSERT INTO inbound_webhook_event (external_event_id, raw_payload)
            VALUES (?, CAST(? AS jsonb))
            ON CONFLICT (external_event_id) DO NOTHING
            RETURNING processing_status
            """;

    private static final String FIND_STATUS = """
            SELECT processing_status
            FROM inbound_webhook_event
            WHERE external_event_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public PostgresInboundWebhookEventStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public InboundWebhookReceipt store(String externalEventId, String rawPayload) {
        List<ProcessingStatus> insertedStatuses = jdbcTemplate.query(
                INSERT_EVENT,
                (resultSet, rowNumber) -> ProcessingStatus.valueOf(resultSet.getString("processing_status")),
                externalEventId,
                rawPayload
        );

        if (!insertedStatuses.isEmpty()) {
            return new InboundWebhookReceipt(externalEventId, insertedStatuses.getFirst(), false);
        }

        try {
            ProcessingStatus existingStatus = jdbcTemplate.queryForObject(
                    FIND_STATUS,
                    (resultSet, rowNumber) -> ProcessingStatus.valueOf(resultSet.getString("processing_status")),
                    externalEventId
            );
            return new InboundWebhookReceipt(externalEventId, existingStatus, true);
        } catch (EmptyResultDataAccessException exception) {
            throw new IllegalStateException("An existing webhook event could not be read after a conflict", exception);
        }
    }
}
