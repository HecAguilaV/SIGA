import os
import logging
from psycopg_pool import AsyncConnectionPool
from pgvector.psycopg import register_vector_async

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

DATABASE_URL = os.getenv("DATABASE_URL", "postgresql://siga_admin:siga_dev_2026@siga-db:5432/siga_db")

class Database:
    def __init__(self):
        self.pool: AsyncConnectionPool = None

    async def connect(self):
        """Initializes the connection pool and verifies extensions."""
        logger.info("Connecting to PostgreSQL...")
        self.pool = AsyncConnectionPool(conninfo=DATABASE_URL)
        
        async with self.pool.connection() as conn:
            await register_vector_async(conn)
            async with conn.cursor() as cur:
                # Ensure pgvector extension exists
                await cur.execute("CREATE EXTENSION IF NOT EXISTS vector;")
                
                # Verify that the 'agent' schema exists (created by init script)
                await cur.execute("SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'agent';")
                schema_exists = await cur.fetchone()
                
                if not schema_exists:
                    logger.warning("Schema 'agent' not found, creating it...")
                    await cur.execute("CREATE SCHEMA IF NOT EXISTS agent;")
                
                await conn.commit()
        logger.info("Database connection established and 'agent' schema verified.")

    async def disconnect(self):
        """Closes the connection pool cleanly."""
        if self.pool:
            await self.pool.close()
            logger.info("Database pool closed.")

# Global Singleton instance
db = Database()
