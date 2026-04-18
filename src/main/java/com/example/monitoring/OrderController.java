package com.example.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final Counter successCounter;
    private final Counter failedCounter;
    private final Timer latencyTimer;

    public OrderController(MeterRegistry registry) {
        this.successCounter = Counter.builder("orders_success_total")
                .description("Total successful orders")
                .register(registry);
        this.failedCounter = Counter.builder("orders_failed_total")
                .description("Total failed orders")
                .register(registry);
        this.latencyTimer = Timer.builder("order_request_latency")
                .description("Order request latency")
                .register(registry);
    }

    @PostMapping("/orders")
    public String createOrder() {
        return latencyTimer.record(() -> {
            successCounter.increment();
            return "Order created successfully";
        });
    }

    @PostMapping("/orders/fail")
    public void failOrder() {
        latencyTimer.record(() -> {
            failedCounter.increment();
        });
        throw new RuntimeException("Simulated order failure");
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
