# =============================================================================
# OUTPUTS - Información útil post-despliegue
# =============================================================================
# Decisión: Exponer endpoints y datos necesarios para:
#   1) Configurar kubectl (cluster_endpoint, cluster_certificate)
#   2) Configurar aplicaciones (rds_endpoint, redis_endpoint)
#   3) Configurar CI/CD (ecr_repository_urls)
#   4) Validar infraestructura (vpc_id, subnet_ids, alb_dns_name)
#
# Los outputs marcados como sensitive = true no se muestran en texto plano
# al hacer 'terraform output' sin la flag -json.
# =============================================================================

# ---------------------------------------------------------------------------
# Output: cluster_endpoint
# Propósito: Endpoint del API Server de Kubernetes.
# Uso: Configurar kubectl con 'aws eks update-kubeconfig'.
# ---------------------------------------------------------------------------
output "cluster_endpoint" {
  description = "Endpoint del API Server del cluster EKS"
  value       = aws_eks_cluster.main.endpoint
}

# ---------------------------------------------------------------------------
# Output: cluster_name
# Propósito: Nombre del cluster EKS para usarlo con kubectl y aws CLI.
# ---------------------------------------------------------------------------
output "cluster_name" {
  description = "Nombre del cluster EKS"
  value       = aws_eks_cluster.main.name
}

# ---------------------------------------------------------------------------
# Output: cluster_certificate_authority_data
# Propósito: Certificado CA del cluster para autenticación TLS mutua.
# ---------------------------------------------------------------------------
output "cluster_certificate_authority_data" {
  description = "Certificado CA del cluster EKS (base64)"
  value       = aws_eks_cluster.main.certificate_authority[0].data
  sensitive   = true
}

# ---------------------------------------------------------------------------
# Output: vpc_id
# Propósito: ID de la VPC para referenciar desde otros scripts o módulos.
# ---------------------------------------------------------------------------
output "vpc_id" {
  description = "ID de la VPC creada"
  value       = aws_vpc.main.id
}

# ---------------------------------------------------------------------------
# Output: public_subnet_ids
# Propósito: IDs de las subnets públicas (para ALB y NAT Gateways).
# ---------------------------------------------------------------------------
output "public_subnet_ids" {
  description = "IDs de las subnets públicas"
  value       = aws_subnet.public[*].id
}

# ---------------------------------------------------------------------------
# Output: private_subnet_ids
# Propósito: IDs de las subnets privadas (para EKS, RDS, Redis).
# ---------------------------------------------------------------------------
output "private_subnet_ids" {
  description = "IDs de las subnets privadas"
  value       = aws_subnet.private[*].id
}

# ---------------------------------------------------------------------------
# Output: rds_endpoint
# Propósito: Endpoint de conexión a la base de datos PostgreSQL.
# Uso: Configurar datasources en Spring Boot (application.yml).
# ---------------------------------------------------------------------------
output "rds_endpoint" {
  description = "Endpoint de conexión a RDS PostgreSQL"
  value       = aws_db_instance.main.endpoint
  sensitive   = true
}

# ---------------------------------------------------------------------------
# Output: redis_endpoint
# Propósito: Endpoint de conexión a ElastiCache Redis.
# Uso: Configurar Spring Cache o sesiones distribuidas.
# ---------------------------------------------------------------------------
output "redis_endpoint" {
  description = "Endpoint de conexión a ElastiCache Redis"
  value       = aws_elasticache_cluster.main.cache_nodes[0].address
  sensitive   = true
}

# ---------------------------------------------------------------------------
# Output: alb_dns_name
# Propósito: Nombre DNS del ALB para acceder a la aplicación.
# Uso: Apuntar un CNAME o acceder directamente desde el navegador.
# ---------------------------------------------------------------------------
output "alb_dns_name" {
  description = "Nombre DNS del Application Load Balancer"
  value       = aws_lb.main.dns_name
}

# ---------------------------------------------------------------------------
# Output: ecr_repository_urls
# Propósito: URLs completas de los repositorios ECR, organizadas por servicio.
# Uso: docker build, tag y push para cada microservicio.
# Ejemplo:
#   docker build -t $(terraform output -json ecr_repository_urls | jq -r '.gateway') .
#   docker push $(terraform output -json ecr_repository_urls | jq -r '.gateway')
# ---------------------------------------------------------------------------
output "ecr_repository_urls" {
  description = "Mapa de URLs de los repositorios ECR por servicio"
  value       = { for repo in aws_ecr_repository.service : repo.name => repo.repository_url }
}

# ---------------------------------------------------------------------------
# Output: rds_password (condicional)
# Propósito: Solo se muestra si la contraseña fue generada automáticamente.
# ---------------------------------------------------------------------------
output "rds_password" {
  description = "Contraseña de RDS (solo si fue generada automáticamente)"
  value       = var.rds_password == "" ? random_password.rds.result : "Usa la variable rds_password definida"
  sensitive   = true
}
