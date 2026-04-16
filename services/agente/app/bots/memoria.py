from strands_agents.tools import tool
from app.core.database import db
from sentence_transformers import SentenceTransformer

# Modelo ligero de embeddings local (corre on-device) -> 384 dimensiones
encoder = SentenceTransformer('all-MiniLM-L6-v2')

@tool
async def aprender(tenant_id: str, informacion_clave: str) -> str:
    """
    Usa esta herramienta cuando el usuario te diga específicamente algo que debes RECORDAR 
    o una regla permanente. 
    Guarda conocimiento permanente en la memoria de largo plazo (Base de datos vectorial) del agente.
    """
    try:
        # 1. Generar embedding sincrónico de forma local
        embedding = encoder.encode(informacion_clave).tolist()
        
        # 2. Guardar en Base de Datos PGVector usando Psycopg de forma asíncrona
        async with db.pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "INSERT INTO memoria_agente (tenant_id, contenido, embedding) VALUES (%s, %s, %s)",
                    (tenant_id, informacion_clave, embedding)
                )
                await conn.commit()
        return "He guardado esta información con éxito en el disco. No se me olvidará."
    except Exception as e:
        return f"Falló el almacenamiento semántico: {str(e)}"

@tool
async def recordar(tenant_id: str, consulta: str) -> str:
    """
    Usa esta herramienta SIEMPRE que vayas a responder para consultar tu memoria de largo plazo.
    Busca conocimientos, reglas pasadas o hechos específicos que el usuario te pidió que recordarás.
    """
    try:
        query_embedding = encoder.encode(consulta).tolist()
        
        async with db.pool.connection() as conn:
            async with conn.cursor() as cur:
                # Busqueda Semantica: "<=>" Distancia del coseno.
                # Filtrado estricto por tenant_id (Zero-Trust Security Payload)
                await cur.execute(
                    """
                    SELECT contenido 
                    FROM memoria_agente 
                    WHERE tenant_id = %s 
                    ORDER BY embedding <=> %s 
                    LIMIT 3
                    """,
                    (tenant_id, query_embedding)
                )
                resultados = await cur.fetchall()
                
        if not resultados:
            return "No hay información relevante en mi memoria a largo plazo."
            
        contexto_integrado = "\\n".join([row[0] for row in resultados])
        return f"Fragmentos recuperados de mi memoria:\\n{contexto_integrado}"
    except Exception as e:
        return f"Falló la recuperación de memoria: {str(e)}"
