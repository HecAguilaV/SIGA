# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# 1. Copy the common library
COPY services/common /common

# 2. Copy gateway service files
COPY services/gateway/build.gradle.kts services/gateway/settings.gradle.kts services/gateway/gradlew ./
COPY services/gateway/gradle ./gradle
COPY services/gateway/src ./src

# Build the application (skip tests)
RUN ./gradlew build -x test --no-daemon

# Find and copy JAR
RUN find /app/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar ;

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Execute
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
