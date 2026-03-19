# Docker Orchestration Skill

Este protocolo asegura que SIGA funcione idénticamente en cualquier entorno (Local, Dev, Test).

## 🐳 Estándar de Contenedores
- **Imagen Base**: Usar imágenes `alpine` o `slim` para ligereza.
- **Multistage Builds**: Separar el build de Kotlin de la imagen de ejecución.
- **Network**: Usar una red dedicada en Docker Compose (`siga-network`).

## 🛠️ Docker Compose Checklist
1.  **Postgres**: Persistencia local (emulando Supabase).
2.  **Backend**: Servicio Spring Boot vinculado a la DB.
3.  **Frontend**: Servicio Node/Nginx para la WebApp.
4.  **Volumes**: Mapear logs y datos persistentes.

## 🚀 Comandos de Supervivencia
- `docker-compose up -d`: Levantar el ecosistema completo.
- `docker-compose logs -f`: Monitoreo en tiempo real.
- `docker-compose down -v`: Limpiar todo (incluyendo volúmenes).
