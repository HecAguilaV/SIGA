#!/usr/bin/env bash
# =============================================================================
# SIGA - Reconexión del Laboratorio AWS Academy
# =============================================================================
# Propósito: Reconectar kubectl al cluster EKS después de que la sesión
# del lab caduca (cada 4 horas) y verificar el estado del despliegue.
#
# CUÁNDO USARLO:
#   1. Abrís el lab de nuevo (te dan credenciales AWS nuevas)
#   2. Actualizás los 3 GitHub Secrets manualmente en GitHub:
#      - AWS_ACCESS_KEY_ID
#      - AWS_SECRET_ACCESS_KEY
#      - AWS_SESSION_TOKEN
#      (Repo → Settings → Secrets and variables → Actions)
#   3. Ejecutás este script
#
# DESDE DÓNDE EJECUTARLO:
#   cd ~/Escritorio/SIGA/SIGA
#   ./scripts/reconectar-lab.sh
#
# PREREQUISITOS:
#   - aws CLI configurada con las credenciales nuevas del lab
#     (se configura automáticamente al abrir el lab con "aws configure"
#      o pegando las credenciales en ~/.aws/credentials)
#   - kubectl instalado
# =============================================================================

set -euo pipefail

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

CLUSTER_NAME="siga-production-cluster"
REGION="us-east-1"
NAMESPACE="siga"

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  SIGA - Reconexión del Laboratorio AWS Academy${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# ---------------------------------------------------------------------------
# Paso 0: Recordatorio de GitHub Secrets
# ---------------------------------------------------------------------------
echo -e "${YELLOW}[Paso 0] Recordatorio: GitHub Secrets${NC}"
echo -e "Antes de continuar, asegurate de haber actualizado los 3 secrets"
echo -e "en GitHub (Repo → Settings → Secrets and variables → Actions):"
echo -e "  • AWS_ACCESS_KEY_ID"
echo -e "  • AWS_SECRET_ACCESS_KEY"
echo -e "  • AWS_SESSION_TOKEN"
echo ""
read -p "¿Ya actualizaste los secrets en GitHub? (s/N): " -r
if [[ ! $REPLY =~ ^[Ss]$ ]]; then
    echo -e "${RED}Actualizá los secrets primero y volvé a ejecutar este script.${NC}"
    exit 1
fi
echo ""

# ---------------------------------------------------------------------------
# Paso 1: Verificar credenciales AWS
# ---------------------------------------------------------------------------
echo -e "${BLUE}[Paso 1] Verificando credenciales AWS...${NC}"
if ! aws sts get-caller-identity &>/dev/null; then
    echo -e "${RED}ERROR: Las credenciales AWS no son válidas o no están configuradas.${NC}"
    echo ""
    echo "Para configurarlas:"
    echo "  1. Abrí el lab de AWS Academy"
    echo "  2. Copiá las credenciales nuevas"
    echo "  3. Ejecutá: aws configure"
    echo "     O pegá las credenciales en ~/.aws/credentials"
    echo ""
    echo "El formato de ~/.aws/credentials debe ser:"
    echo "[default]"
    echo "aws_access_key_id = AKIA..."
    echo "aws_secret_access_key = ..."
    echo "aws_session_token = ..."
    exit 1
fi

IDENTITY=$(aws sts get-caller-identity --query 'Arn' --output text 2>/dev/null)
echo -e "${GREEN}✅ Credenciales válidas:${NC} $IDENTITY"
echo ""

# ---------------------------------------------------------------------------
# Paso 2: Verificar región
# ---------------------------------------------------------------------------
echo -e "${BLUE}[Paso 2] Verificando región AWS...${NC}"
CURRENT_REGION=$(aws configure get region 2>/dev/null || echo "no configurada")
if [[ "$CURRENT_REGION" != "$REGION" ]]; then
    echo -e "${YELLOW}Región actual: $CURRENT_REGION — configurando a $REGION${NC}"
    aws configure set region "$REGION"
fi
echo -e "${GREEN}✅ Región: $REGION${NC}"
echo ""

# ---------------------------------------------------------------------------
# Paso 3: Reconectar kubectl al cluster EKS
# ---------------------------------------------------------------------------
echo -e "${BLUE}[Paso 3] Reconectando kubectl al cluster EKS...${NC}"
if ! aws eks describe-cluster --name "$CLUSTER_NAME" --region "$REGION" &>/dev/null; then
    echo -e "${RED}ERROR: No se puede acceder al cluster '$CLUSTER_NAME'.${NC}"
    echo "Posibles causas:"
    echo "  • El cluster fue eliminado (el lab se reinició)"
    echo "  • Las credenciales no tienen permisos sobre EKS"
    echo "  • El nombre del cluster cambió"
    echo ""
    echo "Clusters disponibles:"
    aws eks list-clusters --region "$REGION" --output text 2>/dev/null || echo "  (no se pueden listar)"
    exit 1
fi

aws eks update-kubeconfig --name "$CLUSTER_NAME" --region "$REGION" 2>&1
echo -e "${GREEN}✅ kubectl reconectado al cluster: $CLUSTER_NAME${NC}"
echo ""

# ---------------------------------------------------------------------------
# Paso 4: Verificar nodos
# ---------------------------------------------------------------------------
echo -e "${BLUE}[Paso 4] Verificando nodos del cluster...${NC}"
NODE_COUNT=$(kubectl get nodes --no-headers 2>/dev/null | wc -l)
if [[ "$NODE_COUNT" -eq 0 ]]; then
    echo -e "${RED}ERROR: No hay nodos en el cluster.${NC}"
    echo "El node group puede haberse escalado a 0. Para restaurarlo:"
    echo "  aws eks update-nodegroup-config \\"
    echo "    --cluster-name $CLUSTER_NAME \\"
    echo "    --nodegroup-name siga-production-node-group \\"
    echo "    --scaling-config desiredSize=3,minSize=1,maxSize=4"
    exit 1
fi
kubectl get nodes
echo -e "${GREEN}✅ $NODE_COUNT nodos Ready${NC}"
echo ""

# ---------------------------------------------------------------------------
# Paso 5: Verificar pods
# ---------------------------------------------------------------------------
echo -e "${BLUE}[Paso 5] Verificando pods en namespace '$NAMESPACE'...${NC}"
kubectl get pods -n "$NAMESPACE"
echo ""

RUNNING=$(kubectl get pods -n "$NAMESPACE" --field-selector=status.phase=Running --no-headers 2>/dev/null | wc -l)
TOTAL=$(kubectl get pods -n "$NAMESPACE" --no-headers 2>/dev/null | wc -l)
echo -e "${GREEN}✅ $RUNNING/$TOTAL pods Running${NC}"
echo ""

# ---------------------------------------------------------------------------
# Paso 6: Verificar servicios y LoadBalancers
# ---------------------------------------------------------------------------
echo -e "${BLUE}[Paso 6] Verificando servicios y LoadBalancers...${NC}"
kubectl get svc -n "$NAMESPACE" | grep -E "NAME|siga-gateway|siga-dashboard"
echo ""

GATEWAY_LB=$(kubectl get svc -n "$NAMESPACE" siga-gateway -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || echo "")
DASHBOARD_LB=$(kubectl get svc -n "$NAMESPACE" siga-dashboard -o jsonpath='{.status.loadBalancer.ingress[0].hostname}' 2>/dev/null || echo "")

if [[ -n "$GATEWAY_LB" ]]; then
    echo -e "${GREEN}✅ Gateway LoadBalancer:${NC} http://$GATEWAY_LB"
else
    echo -e "${YELLOW}⚠️  Gateway LoadBalancer no asignado${NC}"
fi

if [[ -n "$DASHBOARD_LB" ]]; then
    echo -e "${GREEN}✅ Dashboard LoadBalancer:${NC} http://$DASHBOARD_LB"
else
    echo -e "${YELLOW}⚠️  Dashboard LoadBalancer no asignado${NC}"
fi
echo ""

# ---------------------------------------------------------------------------
# Paso 7: Verificar Eureka
# ---------------------------------------------------------------------------
echo -e "${BLUE}[Paso 7] Verificando servicios en Eureka...${NC}"
EUREKA_APPS=$(kubectl exec -n "$NAMESPACE" deployment/siga-auth -- sh -c "wget -q -O- http://siga-registry:8761/eureka/apps" 2>/dev/null | grep -oP '<name>[A-Z-]+</name>' | grep -v MyOwn | sort -u || echo "")
if [[ -n "$EUREKA_APPS" ]]; then
    echo "$EUREKA_APPS"
    APP_COUNT=$(echo "$EUREKA_APPS" | wc -l)
    echo -e "${GREEN}✅ $APP_COUNT servicios registrados en Eureka${NC}"
else
    echo -e "${YELLOW}⚠️  No se pudieron obtener los servicios de Eureka${NC}"
    echo "  (puede que registry aún esté arrancando — esperá 1-2 min y re-ejecutá)"
fi
echo ""

# ---------------------------------------------------------------------------
# Resumen final
# ---------------------------------------------------------------------------
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  Resumen de Reconexión${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "Credenciales AWS:     ${GREEN}✅ Válidas${NC}"
echo -e "kubectl conectado:    ${GREEN}✅ $CLUSTER_NAME${NC}"
echo -e "Nodos:                ${GREEN}✅ $NODE_COUNT Ready${NC}"
echo -e "Pods:                 ${GREEN}✅ $RUNNING/$TOTAL Running${NC}"
if [[ -n "$GATEWAY_LB" ]]; then
    echo -e "Gateway ELB:          ${GREEN}✅ http://$GATEWAY_LB${NC}"
fi
if [[ -n "$DASHBOARD_LB" ]]; then
    echo -e "Dashboard ELB:        ${GREEN}✅ http://$DASHBOARD_LB${NC}"
fi
echo ""
echo -e "${YELLOW}Recordatorio: GitHub Secrets ya actualizados (Paso 0)${NC}"
echo -e "${YELLOW}El CI/CD está listo para pushear cambios.${NC}"
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
