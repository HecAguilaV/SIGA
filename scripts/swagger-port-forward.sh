#!/bin/bash
# =============================================================================
# SIGA - Swagger Port-Forward
# =============================================================================
# Abre túneles locales para acceder al Swagger UI de cada microservicio.
# Ejecutar ANTES de la defensa y mantener la terminal abierta.
#
# Uso:
#   ./scripts/swagger-port-forward.sh
#
# Luego abrir en el navegador:
#   Auth:       http://localhost:18081/swagger-ui.html
#   Inventory:  http://localhost:18082/swagger-ui.html
#   Sales:      http://localhost:18083/swagger-ui.html
#   Billing:    http://localhost:18084/swagger-ui.html
#   Notification: http://localhost:18085/swagger-ui.html
# =============================================================================

set -e

NAMESPACE="siga"

echo "═══════════════════════════════════════════════════════════════"
echo "  SIGA - Swagger Port-Forward"
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "Abriendo túneles para Swagger UI..."
echo ""

# Matar túneles previos si existen
pkill -f "kubectl port-forward.*siga" 2>/dev/null || true

# Auth
kubectl port-forward -n $NAMESPACE deployment/siga-auth 18081:8081 > /dev/null 2>&1 &
echo "  🔐 Auth        → http://localhost:18081/swagger-ui.html"

# Inventory
kubectl port-forward -n $NAMESPACE deployment/siga-inventory 18082:8082 > /dev/null 2>&1 &
echo "  📦 Inventory   → http://localhost:18082/swagger-ui.html"

# Sales
kubectl port-forward -n $NAMESPACE deployment/siga-sales 18083:8083 > /dev/null 2>&1 &
echo "  💰 Sales       → http://localhost:18083/swagger-ui.html"

# Billing
kubectl port-forward -n $NAMESPACE deployment/siga-billing 18084:8084 > /dev/null 2>&1 &
echo "  🧾 Billing     → http://localhost:18084/swagger-ui.html"

# Notification
kubectl port-forward -n $NAMESPACE deployment/siga-notification 18085:8085 > /dev/null 2>&1 &
echo "  📧 Notification → http://localhost:18085/swagger-ui.html"

echo ""
echo "✅ Túneles abiertos. Mantené esta terminal abierta."
echo "   Abrí las URLs en tu navegador."
echo ""
echo "   Para cerrar: pkill -f 'kubectl port-forward'"
echo ""

# Mantener vivo
wait
