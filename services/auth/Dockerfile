# Build stage
FROM gradle:8.10-jdk21 AS build
WORKDIR /app

# 1. Copiar Gradle Wrapper y archivos de configuración básicos
COPY gradlew .
COPY gradle gradle

# 2. Generar un settings.gradle.kts temporal para construir solo este módulo
RUN echo 'rootProject.name = "siga"\ninclude("services:common")\ninclude("services:auth")' > settings.gradle.kts

# 3. Copiar la librería común y el microservicio específico
COPY services/common services/common
COPY services/auth services/auth

# 4. Construir el microservicio
RUN ./gradlew :services:auth:assemble --no-daemon

# 5. Extraer el JAR
RUN find services/auth/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/app.jar app.jar
EXPOSE 8081
ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
