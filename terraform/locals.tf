# =============================================================================
# LOCALES - Variables calculadas y reutilizables
# =============================================================================
# Decisión: Centralizar prefijos, tags y listas comunes para mantener
# consistencia en todos los recursos y evitar repetir valores mágicos.
#
# name_prefix: Se usa como identificador único en nombres de recursos AWS.
# common_tags: Merge de tags definidas por el usuario con tags de gestión.
# ecr_repositories: Lista de microservicios que tendrán su propio repo ECR.
# =============================================================================

locals {
  # Prefijo para nombres de recursos: "siga-production"
  name_prefix = "${var.project_name}-${var.environment}"

  # Tags comunes aplicados a TODOS los recursos factibles de taguear
  common_tags = merge(var.tags, {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "Terraform"
  })

  # Lista de servicios que tendrán repositorio ECR propio
  # Decisión: 9 microservicios identificados en la arquitectura SIGA.
  # Cada servicio despliega su propia imagen en un repo aislado.
  ecr_repositories = [
    "gateway",
    "auth",
    "inventory",
    "sales",
    "billing",
    "notification",
    "agent",
    "registry",
    "dashboard",
  ]

  # Zonas de disponibilidad (alias corto para acceso en recursos)
  azs = var.availability_zones
}
