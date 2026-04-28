FROM eclipse-temurin:21-jre AS build

ARG MODULE

WORKDIR /workspace
COPY . .

RUN chmod +x ./gradlew \
    && ./gradlew ":${MODULE}:bootJar" --no-daemon \
    && JAR_FILE="$(find "${MODULE}/build/libs" -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)" \
    && cp "${JAR_FILE}" /workspace/app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080 8090 8888
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
