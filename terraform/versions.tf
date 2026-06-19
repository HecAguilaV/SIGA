# =============================================================================
# VERSIONES DE TERRAFORM Y PROVIDERS
# =============================================================================
# Decisión: Se define Terraform >= 1.5 para aprovechar mejoras en
# estructura de bloques y validación de tipos. AWS provider ~> 5.0 para
# compatibilidad completa con EKS, ECR, RDS, ElastiCache y ALB.
# TLS provider necesario para obtener el thumbprint del OIDC provider de EKS.
# Random provider para generar contraseñas y sufijos.
#
# Referencia:
#   https://registry.terraform.io/providers/hashicorp/aws/latest
#   https://registry.terraform.io/providers/hashicorp/random/latest
#   https://registry.terraform.io/providers/hashicorp/tls/latest
# =============================================================================

terraform {
  required_version = ">= 1.5"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.5"
    }
    tls = {
      source  = "hashicorp/tls"
      version = "~> 4.0"
    }
  }
}
