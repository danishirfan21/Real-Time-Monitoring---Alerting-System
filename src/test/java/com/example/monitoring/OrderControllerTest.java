package com.example.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void testSuccessOrderIncrementsCounter() throws Exception {
        double initialCount = meterRegistry.get("orders_success_total").counter().count();

        mockMvc.perform(post("/orders"))
                .andExpect(status().isOk());

        double finalCount = meterRegistry.get("orders_success_total").counter().count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    void testFailedOrderIncrementsCounter() throws Exception {
        double initialCount = meterRegistry.get("orders_failed_total").counter().count();

        try {
            mockMvc.perform(post("/orders/fail"));
        } catch (Exception e) {
            // Expected
        }

        double finalCount = meterRegistry.get("orders_failed_total").counter().count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }
}
