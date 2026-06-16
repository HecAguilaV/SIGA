# Build stage
FROM gradle:8.10-jdk21 AS build
WORKDIR /app

# 1. Copiar Gradle Wrapper y archivos de configuración básicos
COPY gradlew .
COPY gradle gradle

# 2. Generar un settings.gradle.kts temporal para construir solo este módulo
# Esto evita errores por falta de otros microservicios en el contexto de Docker
RUN echo 'rootProject.name = "siga"\ninclude("services:common")\ninclude("services:inventory")' > settings.gradle.kts

# 3. Copiar la librería común y el microservicio específico
COPY services/common services/common
COPY services/inventory services/inventory

# 4. Construir el microservicio (omitir tests para velocidad)
RUN ./gradlew :services:inventory:assemble --no-daemon

# 5. Extraer el JAR (evitando el -plain.jar)
RUN find services/inventory/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/app.jar app.jar
EXPOSE 8082
ENV JAVA_OPTS="-Xmx512m -Xms256m"

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Ejecución
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
