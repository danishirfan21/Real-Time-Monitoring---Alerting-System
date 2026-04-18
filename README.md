# Real-Time Monitoring & Alerting System

A simple Spring Boot service that exposes business metrics using Actuator and Micrometer, configured for Prometheus scraping.

## Prerequisites

- Java 17
- Maven
- Prometheus (installed locally)

## Getting Started

### 1. Run the Spring Boot Application

Start the service using Maven:

```bash
mvn spring-boot:run
```

The app will be available at `http://localhost:8080`.

### 2. Run Prometheus

Use the provided `prometheus.yml` configuration to start Prometheus:

```bash
# Example command assuming prometheus is in your PATH
prometheus --config.file=prometheus.yml
```

Prometheus UI will be available at `http://localhost:9090`.

## Verification

### Endpoints

- **Create Order (Success):** `POST http://localhost:8080/orders`
- **Create Order (Failure):** `POST http://localhost:8080/orders/fail`
- **Health Check:** `GET http://localhost:8080/health`
- **Metrics Endpoint:** `GET http://localhost:8080/actuator/prometheus`

### Verifying Metrics in Prometheus

1. Open `http://localhost:9090`.
2. Query the following custom metrics:
   - `orders_success_total`: Increments when `/orders` is hit.
   - `orders_failed_total`: Increments when `/orders/fail` is hit.
   - `order_request_latency_seconds_count`: Total count of order requests timed.
   - `order_request_latency_seconds_sum`: Total duration of order requests.

### Testing

Run automated tests to verify endpoint and metric behavior:

```bash
mvn test
```
