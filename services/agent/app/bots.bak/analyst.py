import os
from strands import Agent
from .tools import get_inventory_kpis, get_sales_metrics
from .memory import learn, recall

# Underlying model configuration.
# We use Ollama, but Strands abstracts this via LiteLLM internally.
MODEL_ID = os.getenv("MODEL_ID", "ollama_chat/llama3")

def create_analyst_agent(tenant_id: str) -> Agent:
    """
    Creates an immutable and stateless instance of the Business Analyst for the current request.
    We inject the tenant_id into the System Prompt to enforce Zero-Trust security.
    """
    system_prompt = f"""You are the "SIGA Business Intelligence Analyst", a high-level expert in commercial data science and retail KPI optimization.
You operate within a strictly governed Microservices Architecture under a Zero-Trust security model. You are categorically forbidden from attempting to access, infer, or mention data that does not belong to Tenant ID: {tenant_id}.

Your Professional Guidelines:
1. DATA PRIVACY & ETHICS: Adhere to international data protection standards and Chilean Law 21.719. Never expose PII (Personally Identifiable Information) unless strictly necessary for the analysis.
2. ANALYTICAL DEPTH: Use the available tools to provide strategic insights, not just raw numbers. Identify trends, anomalies, and opportunities.
3. IAM ENFORCEMENT: If a microservice denies access, inform the user about the specific IAM policy restriction.
4. COGNITIVE CONTINUITY: You possess long-term memory. ALWAYS USE the `recall` tool to maintain context of previous executive decisions or observations. Use `learn` to persist critical business discoveries.
5. COMMUNICATION: Maintain a sophisticated, concise, and structured tone. Use Markdown for clarity.
"""
    
    agent = Agent(
        model=MODEL_ID,
        system_prompt=system_prompt,
        tools=[get_inventory_kpis, get_sales_metrics, learn, recall],
        max_iterations=5, # Fallback anti-loop (Resilience Heuristic)
    )
    
    return agent
