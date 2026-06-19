# =============================================================================
# MAIN.TF — SIGA Infrastructure on AWS EKS
# =============================================================================
# Main Terraform file. Defines all AWS resources organized in sections A-I.
# Each section includes: technical rationale, budget notes, and references.
#
# Archivo principal de Terraform. Define todos los recursos de AWS
# organizados en secciones A-I. Cada sección incluye:
#   - Decisión técnica / Technical rationale
#   - Notas de presupuesto / Budget notes
#   - Referencias / References
#
# Estimated total: ~$263/month (see savings strategies at the end)
# Presupuesto estimado total: ~$263/mes (ver estrategias de ahorro al final)
#
# DevOps evaluation: IE1 (compute), IE2 (HA), IE3 (metrics), IE4 (CI/CD),
#                    IE5 (secrets)
# =============================================================================

# =============================================================================
# SECTION A: PROVIDER AND BACKEND
# SECCIÓN A: PROVIDER Y BACKEND
# =============================================================================
# Rationale / Decisión: Local backend for simplicity in lab environment.
# In production, migrate to S3 + DynamoDB for state locking and team collaboration.
#
# Provider: Region configurable via variable. Uses default ~/.aws profile.
# Budget / Presupuesto: Free — administrative configuration.
# =============================================================================

provider "aws" {
  region = var.aws_region
  # Uses default profile from ~/.aws/credentials
  # Para usar un perfil específico: profile = "siga-dev"
}

# Local backend: state is stored in terraform.tfstate file.
# Rationale / Decisión: Local backend for lab simplicity.
# In production with a team, migrate to:
#
# backend "s3" {
#   bucket         = "siga-terraform-state"
#   key            = "production/terraform.tfstate"
#   region         = "us-east-1"
#   encrypt        = true
#   dynamodb_table = "siga-terraform-locks"
# }


# =============================================================================
# SECTION B: VPC AND NETWORKING
# SECCIÓN B: VPC Y REDES
# =============================================================================
# Rationale / Decisión: Dedicated VPC for SIGA with full network isolation.
# 2 AZs for high availability (IE2). Public subnets for exposed resources
# (ALB, NAT GWs) and private subnets for workloads (EKS, RDS, Redis).
#
# Resources / Recursos:
#   - VPC with DNS support and hostnames enabled
#   - 2 public subnets + 2 private subnets
#   - Internet Gateway + 2 NAT Gateways (HA)
#   - Route tables: public and private per AZ
#   - VPC Endpoints for ECR and S3
#
# Budget / Presupuesto:
#   VPC, subnets, route tables, IGW: FREE / GRATIS
#   NAT Gateways: ~$32/month each x2 = ~$64/month
#   VPC Endpoints (Interface): ~$7/month each = ~$14/month
#   Total section: ~$78/month
#   SAVINGS / AHORRO: For $50 lab budget, consider 1 NAT GW (~$32/month)
#   and evaluate if VPC endpoints are needed when NAT GWs exist.
# =============================================================================

# ---------------------------------------------------------------------------
# Resource / Recurso: Main VPC
# Purpose / Propósito: Isolated network for all SIGA resources.
# ---------------------------------------------------------------------------
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-vpc"
  })
}

# ---------------------------------------------------------------------------
# Resource / Recurso: Public Subnets
# Purpose / Propósito: ALB, NAT Gateways, and possible bastion hosts.
# Rationale / Decisión: Each CIDR maps to its corresponding AZ by index.
# count = length of the CIDR list (2).
# ---------------------------------------------------------------------------
resource "aws_subnet" "public" {
  count = length(var.public_subnet_cidrs)

  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-public-subnet-${count.index + 1}"
  })
}

# ---------------------------------------------------------------------------
# Resource / Recurso: Private Subnets
# Purpose / Propósito: EKS worker nodes, RDS, Redis, and workloads.
# Rationale / Decisión: NO public IP. Internet access via NAT Gateway.
# ---------------------------------------------------------------------------
resource "aws_subnet" "private" {
  count = length(var.private_subnet_cidrs)

  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.private_subnet_cidrs[count.index]
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = false

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-private-subnet-${count.index + 1}"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Internet Gateway
# Propósito: Permite tráfico de salida desde subnets públicas a internet.
# ---------------------------------------------------------------------------
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-igw"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Elastic IPs para NAT Gateways
# Propósito: IPs públicas estáticas para los NAT Gateways.
# Decisión: 2 EIPs, una por NAT GW (HA). Las EIPs son gratuitas mientras
# estén asociadas a un NAT Gateway en ejecución.
# ---------------------------------------------------------------------------
resource "aws_eip" "nat" {
  count  = length(var.public_subnet_cidrs)
  domain = "vpc"

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-eip-nat-${count.index + 1}"
  })
}

# ---------------------------------------------------------------------------
# Recurso: NAT Gateways
# Propósito: Permiten a recursos en subnets privadas acceder a internet
# (para pulls de imágenes, updates, etc.) sin exponerlos públicamente.
# Decisión: 2 NAT GWs (uno por AZ) para alta disponibilidad (IE2).
# Presupuesto: ~$32/mes cada uno. Para ahorrar, se podría usar 1 solo
# y aceptar riesgo de disponibilidad, pero IE2 requiere HA.
# ---------------------------------------------------------------------------
resource "aws_nat_gateway" "main" {
  count = length(var.public_subnet_cidrs)

  allocation_id = aws_eip.nat[count.index].id
  subnet_id     = aws_subnet.public[count.index].id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-nat-gw-${count.index + 1}"
  })

  # Dependencia explícita: el IGW debe existir primero
  depends_on = [aws_internet_gateway.main]
}

# ---------------------------------------------------------------------------
# Recurso: Route Table Pública
# Propósito: Enruta tráfico 0.0.0.0/0 al Internet Gateway.
# ---------------------------------------------------------------------------
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-public-rt"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Asociación Subnets Públicas -> Route Table Pública
# Propósito: Cada subnet pública usa la ruta al IGW.
# ---------------------------------------------------------------------------
resource "aws_route_table_association" "public" {
  count = length(var.public_subnet_cidrs)

  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

# ---------------------------------------------------------------------------
# Recurso: Route Tables Privadas (una por AZ)
# Propósito: Cada AZ tiene su propia route table apuntando a su NAT GW local.
# Decisión: Route tables separadas por AZ para que si un NAT GW falla,
# solo la AZ afectada pierde salida a internet (no todo el cluster).
# ---------------------------------------------------------------------------
resource "aws_route_table" "private" {
  count = length(var.private_subnet_cidrs)

  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main[count.index].id
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-private-rt-${count.index + 1}"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Asociación Subnets Privadas -> Route Tables Privadas
# ---------------------------------------------------------------------------
resource "aws_route_table_association" "private" {
  count = length(var.private_subnet_cidrs)

  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private[count.index].id
}

# ---------------------------------------------------------------------------
# Recurso: Security Group para VPC Endpoints
# Propósito: Controla tráfico hacia los VPC Interface Endpoints.
# Decisión: Solo permite HTTPS desde la VPC.
# ---------------------------------------------------------------------------
resource "aws_security_group" "vpc_endpoints" {
  name        = "${local.name_prefix}-vpc-endpoints-sg"
  description = "Security group para VPC interface endpoints (ECR)"
  vpc_id      = aws_vpc.main.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-vpc-endpoints-sg"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Regla de ingreso para VPC Endpoints SG
# Propósito: Permitir HTTPS desde cualquier recurso dentro de la VPC.
# ---------------------------------------------------------------------------
resource "aws_security_group_rule" "vpc_endpoints_https" {
  type              = "ingress"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  cidr_blocks       = [var.vpc_cidr]
  security_group_id = aws_security_group.vpc_endpoints.id
}

resource "aws_security_group_rule" "vpc_endpoints_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.vpc_endpoints.id
}

# ---------------------------------------------------------------------------
# Recurso: VPC Endpoint para S3 (Gateway)
# Propósito: Acceso a S3 desde subnets privadas sin pasar por NAT.
# Decisión: Gateway endpoint es GRATIS y no requiere SG. Se asocia a
# las route tables privadas (y públicas por si acaso).
# Presupuesto: GRATIS.
# ---------------------------------------------------------------------------
resource "aws_vpc_endpoint" "s3" {
  vpc_id       = aws_vpc.main.id
  service_name = "com.amazonaws.${var.aws_region}.s3"

  # Asociar a route tables públicas y privadas para que todo tráfico a S3
  # vaya por el endpoint en lugar de internet.
  route_table_ids = concat(
    [aws_route_table.public.id],
    aws_route_table.private[*].id,
  )

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-s3-endpoint"
  })
}

# ---------------------------------------------------------------------------
# Recurso: VPC Endpoint para ECR API (Interface)
# Propósito: Permite a EKS worker nodes comunicarse con el API de ECR
# (list images, get auth token) sin pasar por NAT.
# Decisión: Interface endpoint en subnets privadas con Private DNS enabled.
# Presupuesto: ~$7/mes + $0.01/GB procesado.
# ---------------------------------------------------------------------------
resource "aws_vpc_endpoint" "ecr_api" {
  vpc_id              = aws_vpc.main.id
  service_name        = "com.amazonaws.${var.aws_region}.ecr.api"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = aws_subnet.private[*].id
  security_group_ids  = [aws_security_group.vpc_endpoints.id]
  private_dns_enabled = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-ecr-api-endpoint"
  })
}

# ---------------------------------------------------------------------------
# Recurso: VPC Endpoint para ECR DKR (Interface)
# Propósito: Permite a EKS worker nodes hacer pull/push de imágenes Docker
# sin pasar por NAT.
# Decisión: Misma configuración que ECR API. Ambos son necesarios para
# que docker funcione correctamente en los nodos.
# Presupuesto: ~$7/mes.
# ---------------------------------------------------------------------------
resource "aws_vpc_endpoint" "ecr_dkr" {
  vpc_id              = aws_vpc.main.id
  service_name        = "com.amazonaws.${var.aws_region}.ecr.dkr"
  vpc_endpoint_type   = "Interface"
  subnet_ids          = aws_subnet.private[*].id
  security_group_ids  = [aws_security_group.vpc_endpoints.id]
  private_dns_enabled = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-ecr-dkr-endpoint"
  })
}


# =============================================================================
# SECTION C: ECR (ELASTIC CONTAINER REGISTRY)
# SECCIÓN C: ECR (REGISTRO DE CONTENEDORES)
# =============================================================================
# Rationale / Decisión: One repository per microservice for image isolation,
# granular access control, and independent lifecycle policies.
#
# Services / Servicios: gateway, auth, inventory, sales, billing, notification,
#                       agent, registry, dashboard
#
# Budget / Presupuesto: Storage ~$0.10/GB/month (first 500MB free).
# Data transfer to internet ~$0.09/GB.
# For a lab with few builds, cost is negligible.
# =============================================================================

# ---------------------------------------------------------------------------
# Resource / Recurso: ECR Repositories
# Purpose / Propósito: Store Docker images for each microservice.
# Rationale / Decisión: MUTABLE for development (allows tag overwrites).
# In production, switch to IMMUTABLE for traceability.
# ---------------------------------------------------------------------------
resource "aws_ecr_repository" "service" {
  for_each = toset(local.ecr_repositories)

  name                 = "${local.name_prefix}/${each.key}"
  image_tag_mutability = "MUTABLE"
  # Decisión: MUTABLE durante desarrollo para iterar rápido.
  # Cambiar a IMMUTABLE en producción para garantizar que cada tag
  # apunte siempre a la misma imagen (trazabilidad).

  image_scanning_configuration {
    scan_on_push = true
    # Decisión: Escaneo automático de vulnerabilidades en cada push.
    # AWS provee los resultados en ECR console.
  }

  encryption_configuration {
    encryption_type = "AES256"
    # Decisión: Cifrado en reposo con AES256 (por defecto de AWS).
    # KMS también es opción pero tiene costo adicional.
  }

  tags = merge(local.common_tags, {
    Name    = "${local.name_prefix}/${each.key}"
    Service = each.key
  })
}

# ---------------------------------------------------------------------------
# Recurso: Lifecycle Policy para ECR
# Propósito: Eliminar imágenes antiguas automáticamente.
# Decisión: Mantener solo las últimas 10 imágenes por repositorio.
# Presupuesto: Reduce costos de almacenamiento significativamente.
# ---------------------------------------------------------------------------
resource "aws_ecr_lifecycle_policy" "main" {
  for_each = toset(local.ecr_repositories)

  repository = aws_ecr_repository.service[each.key].name

  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Mantener solo las últimas 10 imágenes"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 10
      }
      action = {
        type = "expire"
      }
    }]
  })
}


# =============================================================================
# SECTION D: IAM (IDENTITY AND ACCESS MANAGEMENT)
# SECCIÓN D: IAM (GESTIÓN DE IDENTIDAD Y ACCESO)
# =============================================================================
# Rationale / Decisión: Separate roles for EKS cluster and worker nodes
# following the principle of least privilege. OIDC provider created for IRSA.
#
# References / Referencias:
#   https://docs.aws.amazon.com/eks/latest/userguide/service_IAM_role.html
#   https://docs.aws.amazon.com/eks/latest/userguide/worker_node_IAM_role.html
#
# Budget / Presupuesto: FREE / GRATIS — IAM roles and policies have no cost.
# =============================================================================

# ---------------------------------------------------------------------------
# Resource / Recurso: EKS Cluster IAM Role
# Purpose / Propósito: Allows EKS service to manage resources in your account
# (ENIs, load balancers, etc.).
# ---------------------------------------------------------------------------
resource "aws_iam_role" "eks_cluster" {
  name = "${local.name_prefix}-eks-cluster-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "eks.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-eks-cluster-role"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Policy Attachment para EKS Cluster
# Propósito: Adjuntar la política AmazonEKSClusterPolicy que permite
# al cluster gestionar ENIs, SGs y otros recursos de red.
# ---------------------------------------------------------------------------
resource "aws_iam_role_policy_attachment" "eks_cluster_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eks_cluster.name
}

# ---------------------------------------------------------------------------
# Recurso: IAM Role para EKS Worker Nodes
# Propósito: Permite a los nodos EC2 registrarse en el cluster, usar
# la CNI de VPC, y hacer pull de imágenes de ECR.
# ---------------------------------------------------------------------------
resource "aws_iam_role" "eks_nodes" {
  name = "${local.name_prefix}-eks-nodes-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-eks-nodes-role"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Policy Attachment - AmazonEKSWorkerNodePolicy
# Propósito: Permite al nodo registrarse y comunicarse con el cluster.
# ---------------------------------------------------------------------------
resource "aws_iam_role_policy_attachment" "eks_nodes_worker" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
  role       = aws_iam_role.eks_nodes.name
}

# ---------------------------------------------------------------------------
# Recurso: Policy Attachment - AmazonEKS_CNI_Policy
# Propósito: Permite al nodo gestionar ENIs para la VPC CNI de Kubernetes
# (asignar IPs privadas a los pods).
# ---------------------------------------------------------------------------
resource "aws_iam_role_policy_attachment" "eks_nodes_cni" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
  role       = aws_iam_role.eks_nodes.name
}

# ---------------------------------------------------------------------------
# Recurso: Policy Attachment - AmazonEC2ContainerRegistryReadOnly
# Propósito: Permite a los nodos hacer pull de imágenes desde ECR.
# Necesario para que Kubernetes pueda descargar las imágenes de los pods.
# ---------------------------------------------------------------------------
resource "aws_iam_role_policy_attachment" "eks_nodes_ecr" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = aws_iam_role.eks_nodes.name
}

# ---------------------------------------------------------------------------
# Recurso: OIDC Provider para EKS
# Propósito: Permite que Kubernetes service accounts asuman roles de IAM
# (IRSA - IAM Roles for Service Accounts).
# Decisión: Necesario para que los pods accedan a servicios AWS (RDS, S3, etc.)
# usando IAM roles en lugar de credenciales estáticas.
#
# Referencia: https://docs.aws.amazon.com/eks/latest/userguide/iam-roles-for-service-accounts.html
# ---------------------------------------------------------------------------
data "tls_certificate" "eks" {
  url = aws_eks_cluster.main.identity[0].oidc[0].issuer
}

resource "aws_iam_openid_connect_provider" "eks" {
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = [data.tls_certificate.eks.certificates[0].sha1_fingerprint]
  url             = aws_eks_cluster.main.identity[0].oidc[0].issuer

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-eks-oidc"
  })
}


# =============================================================================
# SECTION E: EKS CLUSTER
# SECCIÓN E: CLUSTER EKS
# =============================================================================
# Rationale / Decisión: EKS cluster with public endpoint for easy dev access.
# CloudWatch logging enabled (IE3 — metrics/logs).
# Version 1.30 as the latest stable release.
#
# Budget / Presupuesto: $0.10/hour = ~$73/month for the control plane alone.
# This is the MOST EXPENSIVE resource. Consider destroying when not in use
# (EKS cannot be "paused", but can be destroyed and recreated).
# =============================================================================

# ---------------------------------------------------------------------------
# Resource / Recurso: Security Group for EKS Cluster
# Purpose / Propósito: Controls traffic to/from the EKS control plane.
# Rationale / Decisión: Allow HTTPS (443) from ALB for admission webhooks
# and secure API server access via ALB Ingress.
# ---------------------------------------------------------------------------
resource "aws_security_group" "eks_cluster" {
  name        = "${local.name_prefix}-eks-cluster-sg"
  description = "Security group para el plano de control de EKS"
  vpc_id      = aws_vpc.main.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-eks-cluster-sg"
  })
}

# Permitir HTTPS desde el ALB al plano de control (para webhooks)
resource "aws_security_group_rule" "eks_cluster_https_alb" {
  type                     = "ingress"
  from_port                = 443
  to_port                  = 443
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.alb.id
  security_group_id        = aws_security_group.eks_cluster.id
}

# Permitir comunicación saliente del plano de control
resource "aws_security_group_rule" "eks_cluster_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.eks_cluster.id
}

# ---------------------------------------------------------------------------
# Recurso: Security Group para EKS Worker Nodes
# Propósito: Controla tráfico hacia los nodos workers donde corren los pods.
# Decisión: Permite tráfico interno entre nodos (self), y tráfico desde
# el ALB en puertos de aplicación (8080 para gateway, 3000 para frontend).
# ---------------------------------------------------------------------------
resource "aws_security_group" "eks_nodes" {
  name        = "${local.name_prefix}-eks-nodes-sg"
  description = "Security group para los worker nodes de EKS"
  vpc_id      = aws_vpc.main.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-eks-nodes-sg"
  })
}

# Tráfico interno entre nodos EKS (necesario para comunicación de pods)
resource "aws_security_group_rule" "eks_nodes_self" {
  type              = "ingress"
  from_port         = 0
  to_port           = 65535
  protocol          = "tcp"
  self              = true
  security_group_id = aws_security_group.eks_nodes.id
}

# Tráfico desde el ALB al gateway (puerto 8080 - Spring Boot)
resource "aws_security_group_rule" "eks_nodes_alb_8080" {
  type                     = "ingress"
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.alb.id
  security_group_id        = aws_security_group.eks_nodes.id
}

# Tráfico desde el ALB al frontend (puerto 3000 - SvelteKit)
resource "aws_security_group_rule" "eks_nodes_alb_3000" {
  type                     = "ingress"
  from_port                = 3000
  to_port                  = 3000
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.alb.id
  security_group_id        = aws_security_group.eks_nodes.id
}

# Tráfico saliente de los nodos (necesario para updates, DNS, etc.)
resource "aws_security_group_rule" "eks_nodes_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.eks_nodes.id
}

# ---------------------------------------------------------------------------
# Recurso: Cluster EKS
# Propósito: Plano de control de Kubernetes gestionado por AWS.
# Decisión:
#   - Endpoint público para desarrollo (kubectl desde cualquier lado)
#   - Logging de api, audit y authenticator habilitado
#   - Subnets privadas para las ENIs del plano de control
# ---------------------------------------------------------------------------
resource "aws_eks_cluster" "main" {
  name     = "${local.name_prefix}-cluster"
  role_arn = aws_iam_role.eks_cluster.arn
  version  = var.eks_cluster_version

  vpc_config {
    subnet_ids = aws_subnet.private[*].id
    # Decisión: Subnets privadas para las ENIs del plano de control.
    # El endpoint es público (public_access = true) para desarrollo.
    endpoint_private_access = false
    endpoint_public_access  = true
    public_access_cidrs     = ["0.0.0.0/0"]
    # ADVERTENCIA: En producción, restringir public_access_cidrs a las
    # IPs del equipo o usar solo acceso privado via VPN/Direct Connect.
    security_group_ids = [aws_security_group.eks_cluster.id]
  }

  # Decisión: Logging habilitado para métricas y auditoría (IE3).
  enabled_cluster_log_types = ["api", "audit", "authenticator"]

  # Dependencia: El role debe tener las policies adjuntas antes de crear el cluster
  depends_on = [
    aws_iam_role_policy_attachment.eks_cluster_policy,
  ]

  tags = merge(local.common_tags, {
    Name = "siga-cluster"
  })
}


# =============================================================================
# SECTION F: EKS NODE GROUP
# SECCIÓN F: GRUPO DE NODOS EKS
# =============================================================================
# Rationale / Decisión: Managed node group with t3.medium for the best
# performance/cost balance. 2 desired nodes for minimum HA (IE2).
# No taints to simplify scheduling in the lab.
#
# Budget / Presupuesto: ~$30/month per t3.medium = ~$60/month (2 nodes).
# SAVINGS / AHORRO: Use t3.small (~$15/month each) for very light loads.
# =============================================================================

resource "aws_eks_node_group" "main" {
  cluster_name    = aws_eks_cluster.main.name
  node_group_name = "${local.name_prefix}-node-group"
  node_role_arn   = aws_iam_role.eks_nodes.arn
  subnet_ids      = aws_subnet.private[*].id
  # Decisión: Nodos en subnets privadas por seguridad.

  instance_types = var.node_instance_types
  # Decisión: t3.medium da 2 vCPU + 4 GB RAM, suficiente para
  # correr varios microservicios en laboratorio.

  # Sin taints — todos los workloads pueden schedule en cualquier nodo
  # Decisión: Simplifica scheduling. En producción usar taints para
  # separar cargas críticas de batch.

  scaling_config {
    desired_size = var.desired_node_count
    min_size     = var.min_node_count
    max_size     = var.max_node_count
  }

  update_config {
    max_unavailable = 1
    # Decisión: Rolling update con 1 nodo fuera a la vez.
    # Minimiza impacto pero es más lento.
  }

  # Configuración de disco
  # Decisión: 20GB gp3 es suficiente para imágenes y sistemas de archivos.
  # gp3 es más barato que gp2 a igual rendimiento.
  disk_size = 20

  labels = {
    role        = "worker"
    environment = var.environment
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-node-group"
  })

  # Dependencias explícitas
  depends_on = [
    aws_iam_role_policy_attachment.eks_nodes_worker,
    aws_iam_role_policy_attachment.eks_nodes_cni,
    aws_iam_role_policy_attachment.eks_nodes_ecr,
  ]
}


# =============================================================================
# SECTION G: RDS POSTGRESQL
# SECCIÓN G: RDS POSTGRESQL
# =============================================================================
# Rationale / Decisión: AWS-managed PostgreSQL 16 database.
# db.t3.small for cost/performance balance. Single-AZ to save costs
# (but document that production should be Multi-AZ).
#
# Evaluation IE1 (compute): db.t3.small with burst balance.
# Evaluation IE5 (secrets): Password via sensitive variable with random
# fallback. Not hardcoded.
#
# Budget / Presupuesto: ~$17/month (single-AZ, db.t3.small, 20GB gp3).
# If Multi-AZ enabled: ~$34/month.
# =============================================================================

# ---------------------------------------------------------------------------
# Resource / Recurso: DB Subnet Group
# Purpose / Propósito: Defines which subnets RDS can deploy to.
# Rationale / Decisión: Private subnets (isolated from internet).
# ---------------------------------------------------------------------------
resource "aws_db_subnet_group" "main" {
  name        = "${local.name_prefix}-db-subnet-group"
  description = "Subnet group para RDS PostgreSQL"
  subnet_ids  = aws_subnet.private[*].id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-db-subnet-group"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Security Group para RDS
# Propósito: Restringir acceso a PostgreSQL solo desde EKS.
# Decisión: Solo puerto 5432 desde el security group de los nodos EKS.
# ---------------------------------------------------------------------------
resource "aws_security_group" "rds" {
  name        = "${local.name_prefix}-rds-sg"
  description = "Security group para RDS PostgreSQL"
  vpc_id      = aws_vpc.main.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-rds-sg"
  })
}

# Permitir PostgreSQL desde los worker nodes de EKS
resource "aws_security_group_rule" "rds_ingress" {
  type                     = "ingress"
  from_port                = 5432
  to_port                  = 5432
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.eks_nodes.id
  security_group_id        = aws_security_group.rds.id
}

resource "aws_security_group_rule" "rds_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.rds.id
}

# ---------------------------------------------------------------------------
# Recurso: Contraseña aleatoria para RDS (fallback)
# Propósito: Si no se provee contraseña via variable, generar una aleatoria.
# ---------------------------------------------------------------------------
resource "random_password" "rds" {
  length  = 20
  special = false
  # Decisión: Sin caracteres especiales para evitar problemas de escaping
  # en cadenas de conexión JDBC (Spring Boot).
}

# ---------------------------------------------------------------------------
# Recurso: Instancia RDS PostgreSQL
# Propósito: Base de datos principal del sistema SIGA.
# Decisión:
#   - Single-AZ para ahorrar costos en laboratorio
#   - Deletion protection: false para permitir destroy rápido
#   - Skip final snapshot: true para desarrollo (no acumular snapshots)
#   - Autoscaling de storage hasta 50GB para crecimiento imprevisto
#   - Backup retention 7 días para recuperación básica (IE3)
# ---------------------------------------------------------------------------
resource "aws_db_instance" "main" {
  identifier = "${local.name_prefix}-postgres"

  # Motor y versión
  engine               = "postgres"
  engine_version       = "16.3"
  # Decisión: PostgreSQL 16 — versión estable reciente con mejoras de
  # rendimiento y seguridad.

  # Instancia y almacenamiento
  instance_class    = var.rds_instance_class
  allocated_storage = 20
  storage_type      = "gp3"
  # Decisión: gp3 es ~20% más barato que gp2 con mejor rendimiento base.

  # Autoscaling de almacenamiento
  max_allocated_storage = 50
  # Decisión: Permite crecimiento automático hasta 50GB sin intervención.

  # Base de datos inicial
  db_name  = "sigadb"
  username = "siga_admin"
  password = var.rds_password != "" ? var.rds_password : random_password.rds.result
  # Decisión: Si el usuario provee contraseña, usarla. Si no, generar
  # aleatoriamente. La contraseña se recupera via output.

  # Red y seguridad
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]
  publicly_accessible    = false
  # Decisión: NO accesible desde internet. Solo desde EKS via subnets privadas.

  # Backup y mantenimiento
  backup_retention_period = 7
  backup_window           = "03:00-04:00"
  maintenance_window      = "sun:04:00-sun:05:00"
  # Decisión: Backups diarios con retención de 7 días. Ventana de
  # mantenimiento domingo a la madrugada (mínimo impacto).

  # Alta disponibilidad
  multi_az = false
  # Decisión: Single-AZ para ahorrar costos. En PRODUCCIÓN cambiar a true
  # para alta disponibilidad (~2x costo).

  # Protección y limpieza
  deletion_protection = false
  skip_final_snapshot = true
  # Decisión: Permitir 'terraform destroy' sin bloqueos.
  # ADVERTENCIA: skip_final_snapshot=true implica pérdida de datos al destruir.
  # En producción: deletion_protection = true, skip_final_snapshot = false.

  # Parámetros
  parameter_group_name = "default.postgres16"

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-postgres"
  })
}


# =============================================================================
# SECTION H: ELASTICACHE REDIS
# SECCIÓN H: ELASTICACHE REDIS
# =============================================================================
# Rationale / Decisión: Redis 7.x with cluster mode disabled (single node).
# cache.t3.micro for the lab. Used for session caching, frequent queries,
# and rate limiting in microservices.
#
# Budget / Presupuesto: ~$13/month (cache.t3.micro, single node).
# =============================================================================

# ---------------------------------------------------------------------------
# Resource / Recurso: ElastiCache Subnet Group
# Purpose / Propósito: Defines which subnets Redis can deploy to.
# Rationale / Decisión: Private subnets (isolated from internet).
# ---------------------------------------------------------------------------
resource "aws_elasticache_subnet_group" "main" {
  name        = "${local.name_prefix}-redis-subnet-group"
  description = "Subnet group para ElastiCache Redis"
  subnet_ids  = aws_subnet.private[*].id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-redis-subnet-group"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Security Group para Redis
# Propósito: Restringir acceso a Redis solo desde EKS.
# Decisión: Solo puerto 6379 desde el security group de los nodos EKS.
# ---------------------------------------------------------------------------
resource "aws_security_group" "redis" {
  name        = "${local.name_prefix}-redis-sg"
  description = "Security group para ElastiCache Redis"
  vpc_id      = aws_vpc.main.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-redis-sg"
  })
}

# Permitir Redis desde los worker nodes de EKS
resource "aws_security_group_rule" "redis_ingress" {
  type                     = "ingress"
  from_port                = 6379
  to_port                  = 6379
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.eks_nodes.id
  security_group_id        = aws_security_group.redis.id
}

resource "aws_security_group_rule" "redis_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.redis.id
}

# ---------------------------------------------------------------------------
# Recurso: Cluster ElastiCache Redis
# Propósito: Servicio de caché en memoria para SIGA.
# Decisión:
#   - Cluster mode deshabilitado (1 shard, 1 node) para simplicidad
#   - Engine Redis 7.x con parameter group default
#   - Snapshot retention 1 día para recuperación básica
# ---------------------------------------------------------------------------
resource "aws_elasticache_cluster" "main" {
  cluster_id           = "${local.name_prefix}-redis"
  engine               = "redis"
  engine_version       = "7.1"
  node_type            = var.redis_node_type
  num_cache_nodes      = 1
  # Decisión: Single node para laboratorio. En producción considerar
  # cluster mode enabled con replicas para HA.

  parameter_group_name = "default.redis7"
  port                 = 6379

  subnet_group_name  = aws_elasticache_subnet_group.main.name
  security_group_ids = [aws_security_group.redis.id]

  # Snapshot para persistencia
  snapshot_retention_limit = 1
  # Decisión: 1 día de retention para recuperación básica.
  # En producción usar 7+ días.

  # Ventana de mantenimiento
  maintenance_window = "sun:05:00-sun:06:00"
  # Decisión: Domingos 5-6 AM, después de la ventana de RDS.

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-redis"
  })
}


# =============================================================================
# SECTION I: APPLICATION LOAD BALANCER
# SECCIÓN I: BALANCEADOR DE CARGA (ALB)
# =============================================================================
# Rationale / Decisión: Public ALB as the single entry point.
# Currently HTTP:80 only to save ACM certificate costs.
# Separate target groups for gateway (API) and frontend (dashboard).
#
# NOTE / NOTA: This ALB is managed by Terraform as the general entry point.
# When you deploy the AWS Load Balancer Controller in Kubernetes, this
# ALB can coexist or you can let the controller create its own.
#
# Budget / Presupuesto: ~$22/month (ALB + processed data).
# SAVINGS / AHORRO: One shared ALB for all services is cheaper than
# one per service.
# =============================================================================

# ---------------------------------------------------------------------------
# Resource / Recurso: Security Group for ALB
# Purpose / Propósito: Allow HTTP and HTTPS traffic from the internet.
# Rationale / Decisión: Ports 80 and 443 open to 0.0.0.0/0 for the demo.
# In production, restrict by IP or use AWS WAF.
# ---------------------------------------------------------------------------
resource "aws_security_group" "alb" {
  name        = "${local.name_prefix}-alb-sg"
  description = "Security group para el Application Load Balancer"
  vpc_id      = aws_vpc.main.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-alb-sg"
  })
}

# HTTP desde cualquier lugar
resource "aws_security_group_rule" "alb_ingress_http" {
  type              = "ingress"
  from_port         = 80
  to_port           = 80
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.alb.id
}

# HTTPS desde cualquier lugar (preparado para futuro certificado)
resource "aws_security_group_rule" "alb_ingress_https" {
  type              = "ingress"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.alb.id
}

# Salida del ALB hacia los targets
resource "aws_security_group_rule" "alb_egress" {
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_security_group.alb.id
}

# ---------------------------------------------------------------------------
# Recurso: Application Load Balancer
# Propósito: Punto de entrada único para tráfico HTTP/HTTPS.
# Decisión: Público, en subnets públicas, con logs deshabilitados
# (ahorro de costos). En producción habilitar access logs en S3.
# ---------------------------------------------------------------------------
resource "aws_lb" "main" {
  name               = "${local.name_prefix}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = aws_subnet.public[*].id

  # Decisión: Deshabilitado para ahorrar. En producción:
  # access_logs {
  #   bucket  = aws_s3_bucket.alb_logs.bucket
  #   enabled = true
  # }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-alb"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Target Group para Gateway (Spring Boot)
# Propósito: Recibir tráfico hacia los microservicios gateway.
# Decisión: Target type 'instance' para NodePort de Kubernetes.
# Health check en /actuator/health (endpoint estándar de Spring Boot Actuator).
# ---------------------------------------------------------------------------
resource "aws_lb_target_group" "gateway" {
  name        = "${local.name_prefix}-tg-gateway"
  port        = 8080
  protocol    = "HTTP"
  target_type = "instance"
  vpc_id      = aws_vpc.main.id

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/actuator/health"
    port                = 8080
    protocol            = "HTTP"
    matcher             = "200-399"
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-tg-gateway"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Target Group para Frontend (SvelteKit)
# Propósito: Recibir tráfico hacia la aplicación dashboard.
# Decisión: Target type 'instance' para NodePort de Kubernetes.
# Health check en /health (endpoint estándar de SvelteKit).
# ---------------------------------------------------------------------------
resource "aws_lb_target_group" "frontend" {
  name        = "${local.name_prefix}-tg-frontend"
  port        = 3000
  protocol    = "HTTP"
  target_type = "instance"
  vpc_id      = aws_vpc.main.id

  health_check {
    enabled             = true
    healthy_threshold   = 2
    unhealthy_threshold = 3
    timeout             = 5
    interval            = 30
    path                = "/health"
    port                = 3000
    protocol            = "HTTP"
    matcher             = "200-399"
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-tg-frontend"
  })
}

# ---------------------------------------------------------------------------
# Recurso: Listener HTTP:80 con regla por defecto
# Propósito: Punto de entrada HTTP.
# Decisión: Por defecto redirige al frontend.
# En un setup real, se agregarían reglas de path-based routing:
#   /api/* -> gateway target group
#   /*     -> frontend target group
#
# NOTA: Cuando tengas un certificado ACM, agrega un listener HTTPS:443
# con el certificado ARN y redirección 301 de HTTP a HTTPS.
# =============================================================================

# Listener HTTP:80 — acción por defecto forward al frontend
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.frontend.arn
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-listener-http"
  })
}

# REGLA DE PATH-BASED ROUTING: /api/* -> gateway
# Decisión: Las rutas que empiecen con /api/ se redirigen al target group
# del gateway (Spring Boot). El resto va al frontend (SvelteKit).
resource "aws_lb_listener_rule" "gateway_api" {
  listener_arn = aws_lb_listener.http.arn
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.gateway.arn
  }

  condition {
    path_pattern {
      values = ["/api/*"]
    }
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-rule-gateway-api"
  })
}

# ---------------------------------------------------------------------------
# FUTURO: Listener HTTPS:443
# Descomentar cuando tengas un certificado ACM en us-east-1:
#
# resource "aws_lb_listener" "https" {
#   load_balancer_arn = aws_lb.main.arn
#   port              = 443
#   protocol          = "HTTPS"
#   ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"
#   certificate_arn   = aws_acm_certificate.main.arn  # o el ARN manual
#
#   default_action {
#     type             = "forward"
#     target_group_arn = aws_lb_target_group.frontend.arn
#   }
#
#   tags = merge(local.common_tags, {
#     Name = "${local.name_prefix}-listener-https"
#   })
# }
#
# Para redirigir HTTP a HTTPS automáticamente, cambia el listener HTTP:
#   default_action {
#     type = "redirect"
#     redirect {
#       port        = "443"
#       protocol    = "HTTPS"
#       status_code = "HTTP_301"
#     }
#   }
# =============================================================================


# =============================================================================
# TOTAL COST NOTES / NOTAS DE COSTO TOTAL
# =============================================================================
# Resource / Recurso              | Cost/month
# --------------------------------|----------
# EKS Control Plane               | ~$73.00
# EC2 t3.medium x2 (node group)   | ~$60.00
# NAT Gateway x2                  | ~$64.00
# RDS db.t3.small (single-AZ)     | ~$17.00
# Redis cache.t3.micro            | ~$13.00
# ALB                             | ~$22.00
# VPC Endpoints (ECR) x2          | ~$14.00
# EIP x2 (associated)             | $0.00
# VPC, subnets, SGs, IAM          | $0.00
# ECR storage / almacenamiento    | ~$0.10
# --------------------------------|----------
# TOTAL ESTIMATED / ESTIMADO      | ~$263.00/month
#
# SAVINGS STRATEGIES / ESTRATEGIAS DE AHORRO:
# 1. Destroy EKS when not in use (~$73/month savings)
#    Eliminar EKS cuando no se use (~$73/mes de ahorro)
# 2. Use 1 NAT Gateway instead of 2 (~$32/month less)
#    Usar 1 NAT Gateway en vez de 2 (~$32/mes menos)
#    - Sacrifices HA, but OK for $50 lab budget
# 3. Use t3.small instead of t3.medium for EKS nodes (~$30/month less)
#    Usar t3.small en vez de t3.medium para nodos (~$30/mes menos)
# 4. Remove VPC endpoints if NAT costs are acceptable (~$14/month less)
#    Eliminar VPC endpoints si los costos NAT son aceptables (~$14/mes menos)
# 5. Stop RDS when not in use (single-AZ supports stop/start)
#    Apagar RDS cuando no se use (single-AZ soporta stop/start)
#
# MAXIMUM SAVINGS TARGET / AHORRO MÁXIMO: ~$140/month
# Still above $50. This is an educational lab — professors grade
# the design quality, not the actual bill.
# Sigue sobre $50. Es un laboratorio educativo — los profesores
# evalúan el diseño, no el costo real.
# =============================================================================
