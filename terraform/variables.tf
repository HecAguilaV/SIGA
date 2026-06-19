# =============================================================================
# INPUT VARIABLES / VARIABLES DE ENTRADA
# =============================================================================
# Rationale / Decisión: All relevant configurations are parameterizable to
# easily switch between environments (dev, staging, production) without
# modifying code. Defaults target a lab setup with ~$50 AWS credits.
#
# Cost / Costo: Defaults are already lab-optimized.
# =============================================================================

# ---------------------------------------------------------------------------
# Variable: aws_region
# Purpose / Propósito: AWS region where all infrastructure will be deployed.
# Rationale / Decisión: us-east-1 has the most services and lowest prices.
# Budget / Presupuesto: No additional cost — just a region selection.
# ---------------------------------------------------------------------------
variable "aws_region" {
  description = "Región de AWS donde se desplegará la infraestructura"
  type        = string
  default     = "us-east-1"
}

# ---------------------------------------------------------------------------
# Variable: project_name
# Purpose / Propósito: Project identifier for resource namespacing.
# ---------------------------------------------------------------------------
variable "project_name" {
  description = "Project name used as prefix in all resources / Nombre del proyecto usado como prefijo"
  type        = string
  default     = "siga"
}

# ---------------------------------------------------------------------------
# Variable: environment
# Purpose / Propósito: Deployment environment (production, staging, development).
# ---------------------------------------------------------------------------
variable "environment" {
  description = "Deployment environment / Entorno de despliegue"
  type        = string
  default     = "production"
}

# ---------------------------------------------------------------------------
# Variable: vpc_cidr
# Purpose / Propósito: Main VPC CIDR block.
# Rationale / Decisión: /16 allows up to 65536 IPs, more than enough.
# ---------------------------------------------------------------------------
variable "vpc_cidr" {
  description = "VPC CIDR block / Bloque CIDR para la VPC"
  type        = string
  default     = "10.0.0.0/16"
}

# ---------------------------------------------------------------------------
# Variable: public_subnet_cidrs
# Propósito: Subnets públicas para ALB, NAT Gateways y bastiones.
# Decisión: /24 por AZ — 256 IPs cada una, suficiente para ALB y NAT GWs.
# ---------------------------------------------------------------------------
variable "public_subnet_cidrs" {
  description = "Lista de bloques CIDR para las subnets públicas"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

# ---------------------------------------------------------------------------
# Variable: private_subnet_cidrs
# Propósito: Subnets privadas para EKS, RDS, Redis y cargas de trabajo.
# Decisión: /24 por AZ — aisladas de internet directo por seguridad.
# ---------------------------------------------------------------------------
variable "private_subnet_cidrs" {
  description = "Lista de bloques CIDR para las subnets privadas"
  type        = list(string)
  default     = ["10.0.3.0/24", "10.0.4.0/24"]
}

# ---------------------------------------------------------------------------
# Variable: availability_zones
# Propósito: Zonas de disponibilidad para alta disponibilidad (IE2).
# Decisión: 2 AZs para cumplir con requisitos de HA del examen.
# Presupuesto: Us-east-1 tiene 6 AZs, usar 2 minimiza costos de
# datos entre AZs.
# ---------------------------------------------------------------------------
variable "availability_zones" {
  description = "Lista de zonas de disponibilidad para desplegar los recursos"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

# ---------------------------------------------------------------------------
# Variable: eks_cluster_version
# Propósito: Versión de Kubernetes para el cluster EKS.
# Decisión: 1.30 — versión estable reciente con soporte AWS.
# ---------------------------------------------------------------------------
variable "eks_cluster_version" {
  description = "Versión de Kubernetes para el cluster EKS"
  type        = string
  default     = "1.30"
}

# ---------------------------------------------------------------------------
# Variable: node_instance_types
# Propósito: Tipo de instancia EC2 para los nodos workers.
# Decisión: t3.medium (2 vCPU, 4 GB RAM) — el mínimo viable para
# ejecutar los microservicios en laboratorio.
# Presupuesto: ~$30/mes por instancia. Con 2 nodos = ~$60/mes.
# ---------------------------------------------------------------------------
variable "node_instance_types" {
  description = "Tipos de instancia para los nodos del cluster EKS"
  type        = list(string)
  default     = ["t3.medium"]
}

# ---------------------------------------------------------------------------
# Variable: desired_node_count
# Propósito: Número deseado de nodos workers.
# Decisión: 2 nodos para tener redundancia mínima.
# ---------------------------------------------------------------------------
variable "desired_node_count" {
  description = "Número deseado de nodos en el cluster EKS"
  type        = number
  default     = 2
}

# ---------------------------------------------------------------------------
# Variable: min_node_count
# Propósito: Mínimo de nodos para cluster autoscaled.
# ---------------------------------------------------------------------------
variable "min_node_count" {
  description = "Número mínimo de nodos en el cluster EKS"
  type        = number
  default     = 1
}

# ---------------------------------------------------------------------------
# Variable: max_node_count
# Propósito: Máximo de nodos para escalado horizontal.
# ---------------------------------------------------------------------------
variable "max_node_count" {
  description = "Número máximo de nodos en el cluster EKS (para autoscaling)"
  type        = number
  default     = 4
}

# ---------------------------------------------------------------------------
# Variable: rds_instance_class
# Propósito: Clase de instancia para RDS PostgreSQL.
# Decisión: db.t3.small (2 vCPU, 2 GB RAM) — mínimo con burst balance
# para laboratorio. En producción usar db.t3.medium o superior.
# Presupuesto: ~$17/mes en single-AZ.
# ---------------------------------------------------------------------------
variable "rds_instance_class" {
  description = "Clase de instancia para RDS PostgreSQL"
  type        = string
  default     = "db.t3.small"
}

# ---------------------------------------------------------------------------
# Variable: rds_password
# Propósito: Contraseña maestra de RDS PostgreSQL.
# Decisión: Sensible por defecto. Si se deja vacía se genera aleatoriamente
# con random_password. No tener contraseña fija en el código.
# ---------------------------------------------------------------------------
variable "rds_password" {
  description = "Contraseña para RDS PostgreSQL. Si se deja vacía, se genera una aleatoria."
  type        = string
  sensitive   = true
  default     = ""
}

# ---------------------------------------------------------------------------
# Variable: redis_node_type
# Propósito: Tipo de nodo para ElastiCache Redis.
# Decisión: cache.t3.micro (1 vCPU, 0.5 GB RAM) — mínimo para laboratorio.
# Presupuesto: ~$13/mes.
# ---------------------------------------------------------------------------
variable "redis_node_type" {
  description = "Tipo de nodo para ElastiCache Redis"
  type        = string
  default     = "cache.t3.micro"
}

# ---------------------------------------------------------------------------
# Variable: tags
# Propósito: Tags base que se aplican a todos los recursos.
# ---------------------------------------------------------------------------
variable "tags" {
  description = "Tags comunes para todos los recursos de AWS"
  type        = map(string)
  default = {
    Project     = "SIGA"
    Environment = "production"
    ManagedBy   = "Terraform"
  }
}
