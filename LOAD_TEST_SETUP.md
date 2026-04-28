# Load Test Setup

This setup runs the application as several logical services behind a gateway and exposes metrics through Prometheus and Grafana.

## Start

```bash
docker compose -f docker-compose.loadtest.yml up --build
```

Useful URLs:

- Gateway: http://localhost:8080
- Grafana: http://localhost:3000, login `admin` / `admin`
- Prometheus: http://localhost:9090
- Config Server: http://localhost:8888
- MailHog: http://localhost:8025
- Kafka UI: http://localhost:9093

## Scale Services

```bash
docker compose -f docker-compose.loadtest.yml up --build --scale user-service=3 --scale real-estate-service=2
```

Keep `operational-service` at one replica unless you intentionally want multiple scheduler instances.

## Run Smoke Load

With the stack already running:

```bash
docker compose -f docker-compose.loadtest.yml --profile load run --rm k6
```

Override the default load:

```bash
K6_VUS=50 K6_DURATION=5m docker compose -f docker-compose.loadtest.yml --profile load run --rm k6
```

## Local Config

The load-test stack starts `config-server` with the `native` profile and reads config files from `docker/config/local`.
This avoids depending on the remote Git config repository during load tests.
