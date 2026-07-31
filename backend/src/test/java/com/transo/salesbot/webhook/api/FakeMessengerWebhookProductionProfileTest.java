package com.transo.salesbot.webhook.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.transo.salesbot.webhook.application.InboundWebhookEventStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("production")
@AutoConfigureMockMvc
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
class FakeMessengerWebhookProductionProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InboundWebhookEventStore inboundWebhookEventStore;

    @Test
    void endpointIsAbsentOutsideLocalAndTestProfiles() throws Exception {
        mockMvc.perform(post("/api/local/webhooks/messenger")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isNotFound());
    }
}
