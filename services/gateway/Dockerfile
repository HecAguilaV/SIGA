# Build stage
FROM gradle:8.10-jdk21 AS build
WORKDIR /app

# 1. Copiar Gradle Wrapper y archivos de configuración básicos
COPY gradlew .
COPY build.gradle.kts .
COPY gradle.properties .
COPY gradle gradle

# 2. Generar un settings.gradle.kts temporal para construir solo este módulo
RUN echo 'rootProject.name = "siga"\ninclude("services:common")\ninclude("services:gateway")' > settings.gradle.kts

# 3. Copiar la librería común y el microservicio específico
COPY services/common services/common
COPY services/gateway services/gateway

# 4. Construir el microservicio
RUN ./gradlew :services:gateway:assemble --no-daemon

# 5. Extraer el JAR
RUN find services/gateway/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
