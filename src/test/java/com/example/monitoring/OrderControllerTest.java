package com.example.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
                .andExpect(status().isOk())
                .andExpect(content().string("Order created successfully"));

        double finalCount = meterRegistry.get("orders_success_total").counter().count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    void testFailedOrderIncrementsCounter() throws Exception {
        double initialCount = meterRegistry.get("orders_failed_total").counter().count();

        assertThatThrownBy(() -> mockMvc.perform(post("/orders/fail")))
                .hasCauseInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated order failure");

        double finalCount = meterRegistry.get("orders_failed_total").counter().count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void testActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("UP")));
    }

    @Test
    void testMetricsExistence() {
        assertThat(meterRegistry.find("orders_success_total").counter()).isNotNull();
        assertThat(meterRegistry.find("orders_failed_total").counter()).isNotNull();
        assertThat(meterRegistry.find("order_request_latency").timer()).isNotNull();
    }

    @Test
    void testLatencyTimerIncrements() throws Exception {
        Timer timer = meterRegistry.find("order_request_latency").timer();
        assertThat(timer).isNotNull();

        long initialCount = timer.count();

        mockMvc.perform(post("/orders"))
                .andExpect(status().isOk());

        assertThat(timer.count()).isEqualTo(initialCount + 1);
    }
}
