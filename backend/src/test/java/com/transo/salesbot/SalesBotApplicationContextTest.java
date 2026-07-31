package com.transo.salesbot;

import com.transo.salesbot.webhook.application.InboundWebhookEventStore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
class SalesBotApplicationContextTest {

    @MockBean
    private InboundWebhookEventStore inboundWebhookEventStore;

    @Test
    void contextLoads() {
    }
}
