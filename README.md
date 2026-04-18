# Real-Time Monitoring & Alerting System

## Overview
The **Real-Time Monitoring & Alerting System** is a portfolio-ready Spring Boot application designed to demonstrate robust observability using industry-standard tools. It provides a simple order processing API that tracks business-critical metrics, visualizes system health via Grafana, and triggers alerts through Prometheus.

## Architecture & Stack
- **Backend:** Java 17, Spring Boot 3.2.0 (Spring Web, Actuator)
- **Observability:** Micrometer (Prometheus Registry)
- **Monitoring:** Prometheus
- **Visualization:** Grafana
- **Testing:** JUnit 5, AssertJ, MockMvc

## Metrics Tracked
The system exposes custom business metrics through the `/actuator/prometheus` endpoint using Micrometer:
- **`orders_success_total`**: A counter tracking successful order creations.
- **`orders_failed_total`**: A counter tracking simulated order failures.
- **`order_request_latency`**: A timer recording the distribution and duration of order requests.

## Prometheus Configuration & Alerting
The project includes a `prometheus.yml` configuration optimized for local development, scraping metrics every 15 seconds.

### Alert Rules (`alerts.yml`)
- **`HighFailedOrderRate`**: Triggers a **Warning** if any failed orders are detected within a 1-minute rate for more than 30 seconds.
- **`ApplicationDown`**: Triggers a **Critical** alert if the service instance becomes unreachable (`up == 0`).

## Grafana Visualization
A pre-configured dashboard (`grafana-dashboard.json`) is included for immediate visualization:
- **Successful Orders**: Real-time rate of successful transactions.
- **Failed Orders**: Visual spikes for error tracking.
- **Request Latency**: Average response time trends derived from Micrometer timers.

## Demo Flow
1. **Startup**: Launch the Application, Prometheus, and Grafana.
2. **Generate Success**: Call `POST /orders` to increment the success counter and update latency graphs.
3. **Trigger Alert**: Repeatedly call `POST /orders/fail` to observe the `HighFailedOrderRate` alert moving from Pending to Firing in Prometheus.
4. **Test Resilience**: Stop the application to trigger the `ApplicationDown` critical alert.
5. **Visualize**: Monitor all state changes in real-time on the Grafana dashboard.

## Local Setup

### Prerequisites
- Java 17+
- Maven 3.9+
- Prometheus & Grafana installed locally or available via Docker.

### 1. Run the Application
```bash
mvn spring-boot:run
```
The service will be available at `http://localhost:8080`.

### 2. Run Prometheus
```bash
prometheus --config.file=prometheus.yml
```
Access the UI at `http://localhost:9090` and the Alerts page at `http://localhost:9090/alerts`.

### 3. Run Grafana
```bash
docker run -d -p 3000:3000 --name=grafana grafana/grafana
```
1. Open `http://localhost:3000` (Default: `admin`/`admin`).
2. Add `http://localhost:9090` as a Prometheus Data Source.
3. Import `grafana-dashboard.json` to view the **Real-Time Monitoring Dashboard**.

## Testing
Automated tests verify both API behavior and metric accuracy:
- **Endpoint Tests**: Ensure correct HTTP status codes and response bodies.
- **Metric Tests**: Directly assert that `MeterRegistry` counters and timers increment correctly upon request.
```bash
mvn test
```

## Future Improvements
- Integrate **Alertmanager** for Slack or Email notifications.
- Implement **Distributed Tracing** using Micrometer Tracing or OpenTelemetry.
- Add **Log Aggregation** with Grafana Loki.
