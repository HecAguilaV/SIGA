# Build stage
FROM gradle:8.10-jdk21 AS build
WORKDIR /app

# 1. Copiar Gradle Wrapper y archivos de configuración básicos
COPY gradlew .
COPY gradle gradle

# 2. Generar un settings.gradle.kts temporal para construir solo este módulo
RUN echo 'rootProject.name = "siga"\ninclude("services:registry")' > settings.gradle.kts

# 3. Copiar el microservicio específico
COPY services/registry services/registry

# 4. Construir el microservicio
RUN ./gradlew :services:registry:assemble --no-daemon

# 5. Extraer el JAR
RUN find services/registry/build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} /app/app.jar \;

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/app.jar app.jar
EXPOSE 8761
ENV JAVA_OPTS="-Xmx256m -Xms128m"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
