# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copy Gradle files
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the application (skip tests)
RUN ./gradlew build -x test --no-daemon

# Find and copy JAR
RUN find /app/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xmx256m -Xms128m"

# Execute
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
