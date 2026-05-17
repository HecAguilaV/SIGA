FROM python:3.11-slim

WORKDIR /app

# Instalar dependencias
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el app
COPY app ./app

# Exponer el puerto por defecto de FastAPI
EXPOSE 8000

# Variables de entorno
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://siga-eureka:8761/eureka/
ENV PORT=8000

# Ejecutar con uvicorn
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
