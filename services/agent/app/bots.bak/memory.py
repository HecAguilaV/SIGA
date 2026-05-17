from strands import tool
from app.core.database import db
from sentence_transformers import SentenceTransformer
import logging

# Lightweight local embedding model (runs on-device) -> 384 dimensions
# Note: For production with 'VECTOR(1536)', use OpenAI or Google Embeddings.
# Since we are using a local model, we assume the DB schema is compatible with 384 dimensions.
encoder = SentenceTransformer('all-MiniLM-L6-v2')

logger = logging.getLogger(__name__)

@tool
async def learn(tenant_id: str, key_information: str) -> str:
    """
    Use this tool when the user specifically tells you something you should REMEMBER 
    or a permanent rule. 
    Saves permanent knowledge in the agent's long-term memory (Vector Database).
    """
    try:
        # 1. Generate local embedding
        embedding = encoder.encode(key_information).tolist()
        
        # 2. Save to PGVector Database using the 'agent' schema
        async with db.pool.connection() as conn:
            async with conn.cursor() as cur:
                await cur.execute(
                    "INSERT INTO agent.documents (tenant_id, content, embedding) VALUES (%s, %s, %s)",
                    (tenant_id, key_information, embedding)
                )
                await conn.commit()
        return "I have successfully saved this information to my memory. I won't forget it."
    except Exception as e:
        logger.error(f"Semantic storage failed: {str(e)}")
        return f"Failed to store information in memory: {str(e)}"

@tool
async def recall(tenant_id: str, query: str) -> str:
    """
    Use this tool to consult your long-term memory.
    Searches for past knowledge, rules, or specific facts the user previously asked you to remember.
    """
    try:
        query_embedding = encoder.encode(query).tolist()
        
        async with db.pool.connection() as conn:
            async with conn.cursor() as cur:
                # Semantic Search: "<=>" Cosine distance.
                # Strict filtering by tenant_id (Zero-Trust Security Payload)
                await cur.execute(
                    """
                    SELECT content 
                    FROM agent.documents 
                    WHERE tenant_id = %s 
                    ORDER BY embedding <=> %s 
                    LIMIT 3
                    """,
                    (tenant_id, query_embedding)
                )
                results = await cur.fetchall()
                
        if not results:
            return "I don't have any relevant information in my long-term memory for this query."
            
        integrated_context = "\n".join([row[0] for row in results])
        return f"Recovered memory fragments:\n{integrated_context}"
    except Exception as e:
        logger.error(f"Memory retrieval failed: {str(e)}")
        return f"Failed to retrieve information from memory: {str(e)}"
