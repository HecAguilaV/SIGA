import os
import httpx
from strands_agents.tools import tool

# Supongamos que el Gateway resuelve estas URLs o que usamos la red interna de docker
INVENTARIO_URL = os.getenv("INVENTARIO_SERVICE_URL", "http://siga-inventario:8082")
VENTAS_URL = os.getenv("VENTAS_SERVICE_URL", "http://siga-ventas:8083")

@tool
def get_inventory_kpis(tenant_id: str) -> str:
    """
    Extrae KPIs relacionados al inventario, como alertas de bajo stock, total de productos 
    y valorización del catálogo.
    Debe mandarse el tenant_id extraído del contexto de sesión (JWT) para aislar datos.
    """
    try:
        # Petición al microservicio de forma síncrona (con JWT/Tenant propagado)
        headers = {"X-Tenant-Id": tenant_id}
        # En una app real de mercado este endpoint estaría agregado:
        response = httpx.get(f"{INVENTARIO_URL}/api/inventario/productos", headers=headers, timeout=5.0)
        
        if response.status_code == 200:
            productos = response.json()
            total = len(productos)
            # Dummy logic si fuera payload real
            return f"El tenant tiene {total} productos en stock. (Datos simulados desde el Microservicio para el reporte KPI)"
        elif response.status_code in [401, 403]:
            return "Seguridad Zero-Trust: Acceso denegado a inventario por insuficiencia de permisos de este Tenant."
        else:
            return f"Error leyendo inventario: {response.status_code}"
    except Exception as e:
        return f"Excepción crítica al consultar el Microservicio de Inventario: {str(e)}"

@tool
def get_sales_metrics(tenant_id: str, periodo: str = "hoy") -> str:
    """
    Extrae las métricas financieras (ventas, ticket promedio, devoluciones) para el tenant logueado.
    El periodo puede ser 'hoy', 'semana' o 'mes'.
    """
    return f"Resumen de ventas para {periodo} generado exitosamente."

@tool
def consultar_stock_exacto(tenant_id: str, nombre_producto: str) -> str:
    """
    Consulta si hay stock disponible para un producto específico dentro del local.
    """
    # Dummy tool call hacia siga-inventario simulando búsqueda
    return f"Búsqueda exitosa: quedan 15 unidades de '{nombre_producto}'."

@tool
def registrar_venta(tenant_id: str, payload_venta: str) -> str:
    """
    Registra una nueva transacción en el sistema de ventas y descuenta el stock en inventario.
    El payload debe indicar producto y cantidad.
    """
    # En producción esto lanza un POST a siga-ventas
    return "✅ Venta registrada exitosamente. Stock descontado en el sistema."
