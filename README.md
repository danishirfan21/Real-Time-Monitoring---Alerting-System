# Real-Time Monitoring & Alerting System

A simple Spring Boot service that exposes business metrics using Actuator and Micrometer, configured for Prometheus scraping, Grafana visualization, and Prometheus alerting.

## Prerequisites

- Java 17
- Maven
- Prometheus (installed locally)
- Grafana (installed locally)

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

### 3. Run Grafana

Start Grafana locally. If using Docker:

```bash
docker run -d -p 3000:3000 --name=grafana grafana/grafana
```

Access Grafana UI at `http://localhost:3000` (Default login: `admin`/`admin`).

## Grafana Setup

### 1. Add Prometheus Data Source

1. Go to **Connections** > **Data sources**.
2. Click **Add data source** and select **Prometheus**.
3. Set the URL to `http://localhost:9090` (or `http://host.docker.internal:9090` if Grafana is in Docker and Prometheus is on the host).
4. Click **Save & test**.

### 2. Import Dashboard

1. Go to **Dashboards** > **New** > **Import**.
2. Upload the `grafana-dashboard.json` file from the project root.
3. Select the Prometheus data source you just created.
4. Click **Import**.

## Alerting

Alert rules are defined in `alerts.yml` and loaded by Prometheus.

### View Active Alerts

Open the Prometheus Alerts page: `http://localhost:9090/alerts`

### Demo: Trigger Failed Order Alert

1. Start the App and Prometheus.
2. Repeatedly call the failure endpoint:
   ```bash
   curl -X POST http://localhost:8080/orders/fail
   ```
3. Watch the `HighFailedOrderRate` alert in Prometheus UI. It will move from **Inactive** to **Pending**, and then to **Firing** after 30 seconds of sustained failure rate.

### Demo: Trigger Application Down Alert

1. Stop the Spring Boot application (Ctrl+C).
2. Observe the `ApplicationDown` alert in Prometheus UI moving to **Firing** state.

## Demo Flow (Visualization)

1. Start all components (App, Prometheus, Grafana).
2. Open the **Real-Time Monitoring Dashboard** in Grafana.
3. Generate traffic:
   - Success: `curl -X POST http://localhost:8080/orders`
   - Failure: `curl -X POST http://localhost:8080/orders/fail`
4. Watch the **Successful Orders**, **Failed Orders**, and **Request Latency** panels update in real-time.

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
