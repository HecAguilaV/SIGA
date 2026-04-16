import os
from psycopg_pool import AsyncConnectionPool
from pgvector.psycopg import register_vector_async

DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://admin:password123@siga-db:5432/siga_db")

class Database:
    def __init__(self):
        self.pool: AsyncConnectionPool = None

    async def connect(self):
        # Inicializamos el pool asíncrono
        self.pool = AsyncConnectionPool(conninfo=DATABASE_URL)
        
        # Obtenemos una conexión inicial para registrar el tipo vector y crear la tabla
        async with self.pool.connection() as conn:
            await register_vector_async(conn)
            async with conn.cursor() as cur:
                # Nos aseguramos que la extensión vector exista (pgvector image is required)
                await cur.execute("CREATE EXTENSION IF NOT EXISTS vector;")
                # Creamos la tabla compartida de memoria de los agentes con seguridad JWT implícita
                await cur.execute("""
                    CREATE TABLE IF NOT EXISTS memoria_agente (
                        id BIGSERIAL PRIMARY KEY,
                        tenant_id VARCHAR(50) NOT NULL,
                        contenido TEXT NOT NULL,
                        embedding vector(384),
                        creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                """)
                await conn.commit()

    async def disconnect(self):
        if self.pool:
            await self.pool.close()

# Instancia global (Singleton)
db = Database()
