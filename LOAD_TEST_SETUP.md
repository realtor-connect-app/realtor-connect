# Load Test Setup

This setup runs Realtor Connect as several logical services behind an Nginx gateway and exposes metrics through Prometheus and Grafana. The load-test suite uses k6, not JMeter.

## Start Environment

```bash
docker compose -f docker-compose.loadtest.yml up -d --no-build
```

Use `--build` only when Java code, Gradle files, or the Dockerfile changed. For repeated experiment runs with the same images, start or rescale services without rebuilding:

```bash
docker compose -f docker-compose.loadtest.yml up -d --no-build \
  --scale user-service=3 \
  --scale real-estate-service=3 \
  --scale email-service=2
```

The Dockerfile uses a BuildKit cache for `/root/.gradle`, so even when a rebuild is required, Gradle distribution and dependencies should be reused instead of downloaded from scratch.

Useful URLs:

- Gateway: http://localhost:8080
- Grafana: http://localhost:3000, login `admin` / `admin`
- Prometheus: http://localhost:9090
- Config Server: http://localhost:8888
- MailHog: http://localhost:8025
- Kafka UI: http://localhost:9093

The load-test stack starts `config-server` with the `native` profile and reads config files from `docker/config/local`. This avoids depending on the remote Git config repository during load tests.

## Test Suite

k6 scripts live in `load-tests/k6`.

```text
load-tests/k6/
  lib/
    auth.js
    checks.js
    config.js
    data.js
    flows.js
    http.js
    mailhog.js
    metrics.js
    scenarios.js
    summary.js
  scenarios/
    smoke.js
    capacity-public.js
    capacity-mixed.js
    stress.js
    spike.js
    soak.js
```

Docker Compose runs `load-tests/k6/scenarios/smoke.js` by default. All experiments should run files from `load-tests/k6/scenarios`.

## Common Parameters

- `BASE_URL`: gateway URL. In Docker Compose use `http://gateway:8080`; from host use `http://localhost:8080`.
- `MAILHOG_URL`: MailHog URL. In Docker Compose use `http://mailhog:8025`; from host use `http://localhost:8025`.
- `TARGET_RPS`: target requests per second for capacity and soak tests.
- `DURATION`: test duration, for example `10m` or `1h`.
- `PRE_ALLOCATED_VUS`: initial k6 VU pool for arrival-rate executors.
- `MAX_VUS`: maximum VUs k6 may allocate to maintain target RPS.
- `RESULTS_DIR`: output directory for JSON and text summaries.
- `K6_RUN_ID`: optional deterministic run identifier.
- `TEST_PREFIX`: optional prefix for generated test users and listings.

Default SLA thresholds:

- HTTP failure rate `< 1%`
- overall p95 latency `< 1000 ms`
- overall p99 latency `< 2000 ms`
- public browsing p95 `< 800 ms`
- auth p95 `< 1000 ms`
- write operation p95 `< 1500 ms`
- flow success rates `> 95%`

You can override SLA values with environment variables such as `SLA_HTTP_P95_MS`, `SLA_PUBLIC_P95_MS`, `SLA_WRITE_P95_MS`, and `SLA_SUCCESS_RATE`.

## Why `--no-deps` Is Used

All test-run commands below include `--no-deps`. This is intentional.

After you start the environment with a specific scale, for example `--scale user-service=3 --scale real-estate-service=3`, a plain `docker compose run k6` may start or reconcile dependency services using the default Compose scale. That can accidentally bring scaled services back to one instance. `--no-deps` tells Compose to run only the one-off k6 container and leave the already running service topology unchanged.

## Smoke Test

Smoke verifies the full application path before serious experiments:

- public discovery endpoints;
- realtor registration;
- email verification through MailHog polling;
- login;
- current user endpoint;
- private realtor inventory endpoint;
- real estate creation;
- mark-called operation.

```bash
docker compose -f docker-compose.loadtest.yml --profile load run --rm --no-deps \
  k6 run /scripts/scenarios/smoke.js
```

Do not pass `K6_VUS` or `K6_DURATION` to smoke. Those are special k6 environment variables and override the script's `shared-iterations` options, turning smoke into an unintended high-RPS closed-loop test. Use `SMOKE_VUS`, `SMOKE_ITERATIONS`, or `SMOKE_MAX_DURATION` instead.

Optional smoke variables:

- `SMOKE_ITERATIONS`: number of full smoke runs, default `1`;
- `SMOKE_VUS`: VUs used to execute shared smoke iterations, default `1`;
- `SMOKE_MAX_DURATION`: maximum smoke duration, default `2m`.

## Public Capacity Test

This is a read-heavy RPS-controlled test for public endpoints:

- `GET /api/realtors`
- `GET /api/realtors/real-estates`
- `GET /api/realtors/real-estates/{id}` when list results contain data
- `GET /api/settings/{type}`

It varies pages, page sizes, and supported filters such as city, price range, building type, announcement type, rooms count, realtor name, and agency.

```bash
docker compose -f docker-compose.loadtest.yml --profile load run --rm \
  --no-deps \
  -e BASE_URL=http://gateway:8080 \
  -e TARGET_RPS=200 \
  -e DURATION=10m \
  -e PRE_ALLOCATED_VUS=200 \
  -e MAX_VUS=1500 \
  k6 run /scripts/scenarios/capacity-public.js
```

Important variables:

- `TARGET_RPS`: target request rate, for example `25`, `50`, `100`, `200`, `500`;
- `DURATION`: test duration, for example `10m`;
- `PRE_ALLOCATED_VUS` and `MAX_VUS`: increase these if k6 reports dropped iterations or cannot maintain RPS.

## Mixed Capacity Test

This is the main diploma experiment scenario. It uses `constant-arrival-rate` and models a realistic user-facing workload:

- default 70% public browsing;
- default 10% auth/current-user checks;
- default 10% private realtor inventory access;
- default 10% writes: private real-estate creation and mark-called.

Generated data is isolated with names like `loadtest_<timestamp>_...`. Created real estates are private so they do not materially change public browsing behavior. Cleanup is available in the helper layer where the API supports deletion, but the experiment does not rely on deletion during high-throughput runs.

```bash
docker compose -f docker-compose.loadtest.yml --profile load run --rm --no-deps \
  -e BASE_URL=http://gateway:8080 \
  -e MAILHOG_URL=http://mailhog:8025 \
  -e TARGET_RPS=200 \
  -e DURATION=10m \
  -e PRE_ALLOCATED_VUS=300 \
  -e MAX_VUS=2000 \
  k6 run /scripts/scenarios/capacity-mixed.js
```

Important variables:

- `TARGET_RPS`: target request rate;
- `DURATION`: test duration;
- `PUBLIC_BROWSING_PERCENT`: default `70`;
- `AUTH_PERCENT`: default `10`;
- `INVENTORY_PERCENT`: default `10`;
- remaining percentage becomes write traffic;
- `PRE_ALLOCATED_VUS` and `MAX_VUS`: tune according to target RPS and response time.

## Stress Test

Stress gradually increases RPS to find the breaking point where latency, failures, dropped iterations, or backend saturation become unacceptable.

Default stages:

- 1m to 25 RPS
- 2m to 50 RPS
- 2m to 100 RPS
- 2m to 200 RPS
- 2m to 300 RPS
- 2m to 400 RPS
- 2m to 500 RPS
- 1m cooldown

```bash
docker compose -f docker-compose.loadtest.yml --profile load run --rm --no-deps \
  -e BASE_URL=http://gateway:8080 \
  -e MAILHOG_URL=http://mailhog:8025 \
  -e PRE_ALLOCATED_VUS=300 \
  -e MAX_VUS=2500 \
  k6 run /scripts/scenarios/stress.js
```

Important variables:

- `STAGES`: optional comma-separated `duration:targetRps` pairs, for example `1m:50,2m:100,2m:200,1m:0`;
- `PRE_ALLOCATED_VUS` and `MAX_VUS`: should be high enough for the largest stage.

## Spike Test

Spike simulates sudden traffic increase and recovery.

Default pattern:

- 30s at 50 RPS
- 5s ramp to 400 RPS
- 3m hold at 400 RPS
- 5s return to 50 RPS
- 1m recovery
- cooldown

```bash
docker compose -f docker-compose.loadtest.yml --profile load run --rm --no-deps \
  -e BASE_URL=http://gateway:8080 \
  -e MAILHOG_URL=http://mailhog:8025 \
  -e BASE_RPS=50 \
  -e SPIKE_RPS=400 \
  -e SPIKE_HOLD_DURATION=3m \
  -e PRE_ALLOCATED_VUS=300 \
  -e MAX_VUS=2500 \
  k6 run /scripts/scenarios/spike.js
```

Important variables:

- `BASE_RPS`: stable rate before and after spike, default `50`;
- `SPIKE_RPS`: spike target rate, default `400`;
- `BASE_DURATION`, `SPIKE_RAMP_DURATION`, `SPIKE_HOLD_DURATION`, `RECOVERY_RAMP_DURATION`, `RECOVERY_DURATION`: optional timing controls;
- `PRE_ALLOCATED_VUS` and `MAX_VUS`: should be sized for `SPIKE_RPS`.

## Soak Test

Soak runs a stable mixed workload for a longer period. Use about 60-70% of the stable RPS discovered by capacity/stress tests.

```bash
docker compose -f docker-compose.loadtest.yml --profile load run --rm --no-deps \
  -e BASE_URL=http://gateway:8080 \
  -e MAILHOG_URL=http://mailhog:8025 \
  -e TARGET_RPS=150 \
  -e DURATION=1h \
  -e PRE_ALLOCATED_VUS=300 \
  -e MAX_VUS=2000 \
  k6 run /scripts/scenarios/soak.js
```

Important variables:

- `TARGET_RPS`: recommended 60-70% of the stable RPS discovered earlier;
- `DURATION`: default `1h`, adjust for the diploma experiment;
- `PRE_ALLOCATED_VUS` and `MAX_VUS`: tune to avoid dropped iterations during long runs.

## Scaling Services

Do not scale `operational-service` by default. It owns scheduler-enabled behavior in this load-test topology, and duplicate scheduler instances can distort results.

Use these patterns for instance-count experiments after the images are already built:

```bash
docker compose -f docker-compose.loadtest.yml up -d --no-build \
  --scale user-service=1 \
  --scale real-estate-service=1 \
  --scale email-service=1
```

```bash
docker compose -f docker-compose.loadtest.yml up -d --no-build \
  --scale user-service=2 \
  --scale real-estate-service=2 \
  --scale email-service=1
```

```bash
docker compose -f docker-compose.loadtest.yml up -d --no-build \
  --scale user-service=3 \
  --scale real-estate-service=3 \
  --scale email-service=2
```

```bash
docker compose -f docker-compose.loadtest.yml up -d --no-build \
  --scale user-service=4 \
  --scale real-estate-service=4 \
  --scale email-service=2
```

## Recommended Experiment Matrix

Instance configurations:

| Config | user-service | real-estate-service | email-service | operational-service |
|---|---:|---:|---:|---:|
| C1 | 1 | 1 | 1 | 1 |
| C2 | 2 | 2 | 1 or 2 | 1 |
| C3 | 3 | 3 | 1 or 2 | 1 |
| C4 | 4 | 4 | 1 or 2 | 1 |

RPS levels:

| Level | Target RPS |
|---|---:|
| R1 | 25 |
| R2 | 50 |
| R3 | 100 |
| R4 | 150 |
| R5 | 200 |
| R6 | 300 |
| R7 | 400 |
| R8 | 500 |

For each instance configuration, run `capacity-public.js` and `capacity-mixed.js` at every target RPS. Then run `stress.js` once per configuration to find the degradation point. Run `spike.js` and `soak.js` for selected stable configurations.

For every run, record:

- UTC start time;
- instance count;
- target RPS;
- achieved RPS;
- p50, p90, p95, and p99 latency;
- error rate;
- dropped iterations;
- CPU usage per service;
- memory usage per service;
- PostgreSQL connections;
- PostgreSQL CPU if available;
- Kafka lag and message throughput if available;
- whether SLA passed or failed.

Use `load-tests/RESULTS_TEMPLATE.md` for consistent recording.

## Results Output

Each script has `handleSummary()` and writes:

```text
load-tests/results/<scenario>-<run-id>.json
load-tests/results/<scenario>-<run-id>-summary.txt
```

`load-tests/results/` is ignored by Git except for `.gitkeep`.
