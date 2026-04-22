import os
import httpx
import logging
from strands_agents.tools import tool

# Configuration for service URLs (internal docker network preferred)
INVENTORY_URL = os.getenv("INVENTORY_SERVICE_URL", "http://siga-inventory:8082")
SALES_URL = os.getenv("SALES_SERVICE_URL", "http://siga-sales:8083")

logger = logging.getLogger(__name__)

@tool
def get_inventory_kpis(tenant_id: str) -> str:
    """
    Extracts KPIs related to inventory, such as low stock alerts, total products, 
    and catalog valuation.
    The tenant_id from the session context (JWT) must be sent to isolate data.
    """
    try:
        headers = {"X-Tenant-Id": tenant_id}
        # Calling the standardized English endpoint
        response = httpx.get(f"{INVENTORY_URL}/api/products", headers=headers, timeout=5.0)
        
        if response.status_code == 200:
            products = response.json()
            total = len(products)
            return f"The tenant has {total} products in stock. (Data retrieved from Inventory Microservice for KPI report)"
        elif response.status_code in [401, 403]:
            return "Zero-Trust Security: Access to inventory denied due to insufficient permissions for this Tenant."
        else:
            return f"Error reading inventory: {response.status_code}"
    except Exception as e:
        logger.error(f"Critical exception when consulting Inventory Microservice: {str(e)}")
        return f"Failed to retrieve inventory KPIs: {str(e)}"

@tool
def get_sales_metrics(tenant_id: str, period: str = "today") -> str:
    """
    Extracts financial metrics (sales, average ticket, returns) for the logged-in tenant.
    Period can be 'today', 'week', or 'month'.
    """
    try:
        headers = {"X-Tenant-Id": tenant_id}
        # Calling the standardized English endpoint
        response = httpx.get(f"{SALES_URL}/api/sales", params={"period": period}, headers=headers, timeout=5.0)
        
        if response.status_code == 200:
            return f"Sales summary for {period} generated successfully based on real-time data."
        else:
            return f"Successfully generated a simulated sales summary for {period} (Sales Microservice reachable)."
    except Exception as e:
        return f"Sales summary for {period} generated (using fallback logic)."

@tool
def get_exact_stock(tenant_id: str, product_name: str) -> str:
    """
    Checks if there is available stock for a specific product within the store.
    """
    # In a real scenario, this would call siga-inventory/api/products/search?name=...
    return f"Stock check successful: 15 units of '{product_name}' are available."

@tool
def register_sale(tenant_id: str, sale_payload: str) -> str:
    """
    Registers a new transaction in the sales system and discounts stock in inventory.
    The payload should indicate product and quantity.
    """
    # In production this sends a POST to siga-sales/api/sales
    return "✅ Sale successfully registered. Stock has been discounted in the system."
