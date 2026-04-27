#!/bin/sh

set -e

NETWORK_NAME="rc-kafka-net"
ZOOKEEPER_CONTAINER="rc-zookeeper"
KAFKA_CONTAINER="rc-kafka"
KAFKA_TOPIC="emails"
KAFKA_PARTITIONS="5"
KAFKA_REPLICATION_FACTOR="1"

CONFLUENT_VERSION="7.6.1"

echo "Creating Docker network if not exists..."
docker network inspect "$NETWORK_NAME" >/dev/null 2>&1 || \
  docker network create "$NETWORK_NAME"

echo "Starting ZooKeeper..."
if docker ps -a --format '{{.Names}}' | grep -q "^${ZOOKEEPER_CONTAINER}$"; then
  docker rm -f "$ZOOKEEPER_CONTAINER" >/dev/null
fi

docker run -d \
  --name "$ZOOKEEPER_CONTAINER" \
  --network "$NETWORK_NAME" \
  -p 2181:2181 \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  -e ZOOKEEPER_TICK_TIME=2000 \
  confluentinc/cp-zookeeper:${CONFLUENT_VERSION}

echo "Starting Kafka..."
if docker ps -a --format '{{.Names}}' | grep -q "^${KAFKA_CONTAINER}$"; then
  docker rm -f "$KAFKA_CONTAINER" >/dev/null
fi

docker run -d \
  --name "$KAFKA_CONTAINER" \
  --network "$NETWORK_NAME" \
  -p 9092:9092 \
  -p 29092:29092 \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT="${ZOOKEEPER_CONTAINER}:2181" \
  -e KAFKA_LISTENERS="PLAINTEXT://0.0.0.0:29092,PLAINTEXT_HOST://0.0.0.0:9092" \
  -e KAFKA_ADVERTISED_LISTENERS="PLAINTEXT://${KAFKA_CONTAINER}:29092,PLAINTEXT_HOST://localhost:9092" \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP="PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT" \
  -e KAFKA_INTER_BROKER_LISTENER_NAME="PLAINTEXT" \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  confluentinc/cp-kafka:${CONFLUENT_VERSION}

echo "Waiting for Kafka to become available..."

MAX_ATTEMPTS=30
ATTEMPT=1

until docker exec "$KAFKA_CONTAINER" kafka-topics \
  --bootstrap-server localhost:9092 \
  --list >/dev/null 2>&1
do
  if [ "$ATTEMPT" -ge "$MAX_ATTEMPTS" ]; then
    echo "Kafka did not start in time."
    echo "Kafka logs:"
    docker logs "$KAFKA_CONTAINER"
    exit 1
  fi

  echo "Kafka is not ready yet... attempt ${ATTEMPT}/${MAX_ATTEMPTS}"
  ATTEMPT=$((ATTEMPT + 1))
  sleep 2
done

echo "Kafka is ready."

echo "Creating topic '${KAFKA_TOPIC}' if not exists..."
docker exec "$KAFKA_CONTAINER" kafka-topics \
  --bootstrap-server localhost:9092 \
  --create \
  --if-not-exists \
  --topic "$KAFKA_TOPIC" \
  --partitions "$KAFKA_PARTITIONS" \
  --replication-factor "$KAFKA_REPLICATION_FACTOR"

echo "Existing Kafka topics:"
docker exec "$KAFKA_CONTAINER" kafka-topics \
  --bootstrap-server localhost:9092 \
  --list

echo "Done."
echo ""
echo "Use this in Spring Boot:"
echo "spring.kafka.bootstrap-servers=localhost:9092"

docker run -d \
  --name rc-kafka-ui \
  --network $NETWORK_NAME \
  -p 9093:8080 \
  -e KAFKA_CLUSTERS_0_NAME=local \
  -e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=$KAFKA_CONTAINER:29092 \
  -e KAFKA_CLUSTERS_0_ZOOKEEPER=$ZOOKEEPER_CONTAINER:2181 \
  provectuslabs/kafka-ui:latest
