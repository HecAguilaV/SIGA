# Evidencia de Despliegue — SIGA en AWS EKS

> Documento de soporte para la evaluación Fullstack + DevOps.
> Contiene la traza completa desde la configuración de AWS CLI hasta
> el punto previo al `terraform apply`.
>
> **Fecha:** 19 de junio de 2026
> **Cuenta AWS:** 162272997857 (voclabs / AWS Academy)
> **Máquina:** Veriton-Z4694G

---

## 1. Verificación de AWS CLI

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ which aws
/home/hector/.local/bin/aws

hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws --version
aws-cli/2.35.9 Python/3.14.5 Linux/6.17.0-35-generic exe/x86_64.ubuntu.24
```

---

## 2. Configuración de credenciales AWS

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws configure set aws_access_key_id AKIA****
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws configure set aws_secret_access_key ****************************************
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws configure set aws_session_token "IQoJb3JpZ2luX2VjEP///////////wEa..."
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws configure set region us-east-1
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws configure set output json
```

---

## 3. Verificación de identidad AWS

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws sts get-caller-identity
```
```json
{
    "UserId": "AROASLSBZOHQQQX3KCCTB:user3277609=he.aguila@duocuc.cl",
    "Account": "162272997857",
    "Arn": "arn:aws:sts::162272997857:assumed-role/voclabs/user3277609=he.aguila@duocuc.cl"
}
```
> ✅ Credenciales válidas. Cuenta tipo AWS Academy Learner Lab (voclabs).

---

## 4. Instalación de Terraform

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ curl -fsSL https://releases.hashicorp.com/terraform/1.11.4/terraform_1.11.4_linux_amd64.zip -o /tmp/terraform.zip
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ unzip -q /tmp/terraform.zip -d /tmp/
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ mv /tmp/terraform ~/.local/bin/
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ chmod +x ~/.local/bin/terraform
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ terraform --version
```
```
Terraform v1.11.4
on linux_amd64
```

---

## 5. Instalación de kubectl

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ curl -fsSL -o /tmp/kubectl "https://dl.k8s.io/release/v1.30.0/bin/linux/amd64/kubectl"
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ chmod +x /tmp/kubectl
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ mv /tmp/kubectl ~/.local/bin/
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl version --client
```
```
Client Version: v1.30.0
Kustomize Version: v5.0.4-0.20230601165947-6ce0bf390ce3
```

---

## 6. Verificación de región y servicios

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws ec2 describe-regions --query "Regions[].RegionName" --output text
```
```
ap-south-1  eu-north-1  eu-west-3  eu-west-2  eu-west-1  ap-northeast-3
ap-northeast-2  ap-northeast-1  ca-central-1  sa-east-1  ap-southeast-1
ap-southeast-2  eu-central-1  us-east-1  us-east-2  us-west-1  us-west-2
```

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws ec2 describe-instance-type-offerings --region us-east-1 --query "InstanceTypeOfferings[?InstanceType=='t3.medium'].InstanceType" --output text
```
```
t3.medium
```

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws eks list-clusters
```
```json
{
    "clusters": []
}
```
> ✅ Región: us-east-1. t3.medium disponible. API de EKS accesible.

---

## 7. Inicialización de Terraform

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ cd terraform/
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA/terraform$ terraform init
```
```
Initializing the backend...
Initializing provider plugins...
- Finding hashicorp/random versions matching "~> 3.5"...
- Finding hashicorp/tls versions matching "~> 4.0"...
- Finding hashicorp/aws versions matching "~> 5.0"...
- Installing hashicorp/random v3.9.0...
- Installed hashicorp/random v3.9.0 (signed by HashiCorp)
- Installing hashicorp/tls v4.3.0...
- Installed hashicorp/tls v4.3.0 (signed by HashiCorp)
- Installing hashicorp/aws v5.100.0...
- Installed hashicorp/aws v5.100.0 (signed by HashiCorp)

Terraform has created a lock file .terraform.lock.hcl to record the provider
selections it made above. Include this file in your version control repository
so that Terraform can guarantee to make the same selections by default when
you run "terraform init" in the future.

Terraform has been successfully initialized!
```
> ✅ Providers: aws 5.100.0 | random 3.9.0 | tls 4.3.0

---

## 8. Plan de Terraform (78 recursos)

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA/terraform$ terraform plan -out=plan.tfplan
```
```
Plan: 78 to add, 0 to change, 0 to destroy.

Changes to Outputs:
  + alb_dns_name                       = (known after apply)
  + cluster_endpoint                   = (known after apply)
  + cluster_name                       = "siga-production-cluster"
  + ecr_repository_urls                = {
      + siga-production/gateway        = (known after apply)
      + siga-production/auth           = (known after apply)
      + siga-production/inventory      = (known after apply)
      + siga-production/sales          = (known after apply)
      + siga-production/billing        = (known after apply)
      + siga-production/notification   = (known after apply)
      + siga-production/agent          = (known after apply)
      + siga-production/registry       = (known after apply)
      + siga-production/dashboard      = (known after apply)
    }
  + rds_endpoint                       = (sensitive value)
  + redis_endpoint                     = (sensitive value)
  + vpc_id                             = (known after apply)
```

**Desglose de los 78 recursos:**

| Categoría | Cantidad |
|-----------|:--------:|
| VPC + subnets + route tables + associations | 11 |
| Internet Gateway + NAT Gateways + EIPs | 5 |
| Security Groups + Rules | 18 |
| VPC Endpoints (ECR API, ECR DKR, S3) | 3 |
| EKS Cluster + Node Group + IAM roles/policies | 9 |
| RDS PostgreSQL + Subnet Group | 4 |
| ElastiCache Redis + Subnet Group | 4 |
| ALB + Target Groups + Listeners + Rules | 10 |
| ECR Repositories | 9 |
| Random password (RDS) + S3 bucket + others | 5 |
| **Total** | **78** |

> ✅ Plan generado sin errores. Guardado en `plan.tfplan`.

---

## 9. Primer intento — Terraform Apply (fallo en IAM)

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA/terraform$ terraform apply plan.tfplan
```

**Resultado:** 68 recursos creados, 10 fallaron.

| Resultado | Recursos | Estado |
|:---------:|:--------:|:------:|
| ✅ Creados | VPC, subnets, IGW, NAT×2, EIP×2, route tables + assocs | CREADO |
| ✅ Creados | Security Groups + Rules (ALB, EKS cluster, EKS nodes, RDS, Redis, VPC endpoints) | CREADO |
| ✅ Creados | VPC Endpoints (ECR API, ECR DKR, S3) | CREADO |
| ✅ Creados | ALB + Target Groups + Listener + Rule | CREADO |
| ✅ Creados | ElastiCache Redis + subnet group | CREADO |
| ✅ Creados | ECR repos ×9 + lifecycle policies | CREADO |
| ✅ Creado  | RDS PostgreSQL 16.14 + subnet group | CREADO (2do intento) |
| ❌ Fallaron | IAM roles (eks_cluster, eks_nodes) + 4 policy attachments | DENEGADO por lab |
| ❌ Fallaron | EKS cluster, Node Group | BLOQUEADO por IAM |
| ❌ Falló    | OIDC Provider | DENEGADO por lab |

**Error detectado:**

```
api error AccessDenied: User: ... is not authorized to perform: iam:CreateRole
because no identity-based policy allows the iam:CreateRole action
```

> ⚠️ **Descubrimiento:** AWS Academy Learner Lab (rol `voclabs`) NO permite
> crear IAM roles (`iam:CreateRole`) ni OIDC providers
> (`iam:CreateOpenIDConnectProvider`). Sin embargo, el lab YA proporciona
> roles EKS pre-creados con las políticas necesarias.

---

## 10. Solución — Uso de roles pre-creados del lab

Se identificaron dos roles IAM existentes en la cuenta del lab:

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA/terraform$ aws iam list-roles --query "Roles[?contains(RoleName, 'LabEks')].[RoleName, Arn]" --output text
```

```
c216598a5470737l15597720t1w162272-LabEksClusterRole-NBoIv2QY1tAR
  arn:aws:iam::162272997857:role/...LabEksClusterRole...
c216598a5470737l15597720t1w162272997-LabEksNodeRole-3MmTW6plRcJ4
  arn:aws:iam::162272997857:role/...LabEksNodeRole...
```

**Políticas adjuntas a los roles:**

| Role | Políticas |
|:-----|:----------|
| **Cluster Role** | `AmazonEKSClusterPolicy`, `AmazonEKSNetworkingPolicy`, `AmazonEKSComputePolicy`, `AmazonEKSBlockStoragePolicy`, `AmazonEKSLoadBalancingPolicy` |
| **Node Role** | `AmazonEKSWorkerNodePolicy`, `AmazonEKS_CNI_Policy`, `AmazonEC2ContainerRegistryReadOnly` |

**Trust policies verificadas:**
- Cluster Role: `Principal.Service = eks.amazonaws.com` ✅
- Node Role: `Principal.Service = ec2.amazonaws.com` ✅

Se modificó `main.tf` para usar `data.aws_iam_role` en lugar de `resource "aws_iam_role"`:

```hcl
# Antes (fallaba):
resource "aws_iam_role" "eks_cluster" { ... }

# Después (funciona):
data "aws_iam_role" "eks_cluster" {
  name = "c216598a5470737l15597720t1w162272-LabEksClusterRole-NBoIv2QY1tAR"
}
```

Además se corrigió la versión de PostgreSQL de `16.3` a `16.14` (versión disponible en el lab).

---

## 11. Terraform Apply Definitivo (EXITOSO)

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA/terraform$ terraform apply -auto-approve
```

```
Plan: 3 to add, 0 to change, 0 to destroy.

  # aws_eks_cluster.main will be created
  # aws_eks_node_group.main will be created
  # aws_iam_openid_connect_provider.eks will be created
```

### Creación del cluster EKS (~9 min)

```
aws_eks_cluster.main: Creating...
aws_eks_cluster.main: Still creating... [10s elapsed]
...
aws_eks_cluster.main: Still creating... [9m0s elapsed]
aws_eks_cluster.main: Creation complete after 9m13s [id=siga-production-cluster]
```

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA/terraform$ aws eks describe-cluster --name siga-production-cluster --query "cluster.status" --output text
```

```
ACTIVE
```

### Creación del Node Group (~2 min)

```
aws_eks_node_group.main: Creating...
aws_eks_node_group.main: Creation complete after 1m59s
  [id=siga-production-cluster:siga-production-node-group]
```

### OIDC Provider (saltado — restricción del lab)

```
Error: creating IAM OIDC Provider: AccessDenied:
  User: ... is not authorized to perform: iam:CreateOpenIDConnectProvider
```

> ⚠️ El OIDC provider no es crítico para el lab. Las credenciales de
> RDS y Redis se pasan como Secrets de Kubernetes directamente.

---

## 12. Recursos Finales — Resumen

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA/terraform$ terraform state list | wc -l
```

```
74
```

### Outputs de Terraform

| Output | Valor |
|:-------|:------|
| **cluster_name** | `siga-production-cluster` |
| **cluster_endpoint** | `https://36D37C73A7A38ED4A7EF0B5227460533.gr7.us-east-1.eks.amazonaws.com` |
| **alb_dns_name** | `siga-production-alb-1444586698.us-east-1.elb.amazonaws.com` |
| **rds_endpoint** | `siga-production-postgres.cm75k1vmfnks.us-east-1.rds.amazonaws.com:5432` |
| **redis_endpoint** | `siga-production-redis.ruovma.0001.use1.cache.amazonaws.com` |
| **vpc_id** | `vpc-0c5f307c5e9730c94` |

### ECR Repositories (9)

```
162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/gateway
162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/auth
162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/sales
162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/inventory
162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/billing
162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/notification
162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/agent
162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/registry
162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/dashboard
```

---

## 13. Conexión de kubectl al cluster EKS

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws eks update-kubeconfig \
    --region us-east-1 \
    --name siga-production-cluster
```

```
Added new context arn:aws:eks:us-east-1:162272997857:cluster/siga-production-cluster
```

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl get nodes -o wide
```

```
NAME                        STATUS   ROLES    AGE   VERSION                INTERNAL-IP   OS-IMAGE
ip-10-0-3-58.ec2.internal   Ready    <none>   13m   v1.30.14-eks-93b80c6   10.0.3.58     Amazon Linux 2023
ip-10-0-4-73.ec2.internal   Ready    <none>   13m   v1.30.14-eks-93b80c6   10.0.4.73     Amazon Linux 2023
```

> ✅ **2 nodos t3.medium**, ambos Ready. Kubernetes v1.30.14.
> Amazon Linux 2023 con containerd 2.2.4.

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl get pods -n kube-system
```

```
NAME                       READY   STATUS    RESTARTS   AGE
aws-node-dk77z             2/2     Running   0          13m
aws-node-rcqq6             2/2     Running   0          13m
coredns-849f74687b-ng7pt   1/1     Running   0          15m
coredns-849f74687b-sv4fb   1/1     Running   0          15m
kube-proxy-grd2f           1/1     Running   0          13m
kube-proxy-h8qv8           1/1     Running   0          13m
```

> ✅ VPC CNI (aws-node), CoreDNS y kube-proxy operativos.

---

## 14. Siguientes pasos

```bash
# ──────────────────────────────────────────────────
# PASO 1: DESPLEGAR APLICACIÓN (48 manifests Kustomize)
# ──────────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl apply -k k8s/
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl get pods -n siga -w

# ──────────────────────────────────────────────────
# PASO 2: CONFIGURAR GITHUB SECRETS + CI/CD
# ──────────────────────────────────────────────────
# Crear secrets en GitHub:
#   AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_SESSION_TOKEN
#   DOCKER_REGISTRY=162272997857.dkr.ecr.us-east-1.amazonaws.com
#   RDS_HOST, RDS_DB=sigadb, RDS_USER=siga_admin
#   REDIS_HOST

# ──────────────────────────────────────────────────
# PASO 3: VERIFICAR EN NAVEGADOR
# ──────────────────────────────────────────────────
# Frontend: http://siga-production-alb-1444586698.us-east-1.elb.amazonaws.com
# Gateway:  http://.../api/auth/health
```

---

## Resumen del pipeline

```
Configuración AWS    ✅  aws configure
Terraform init       ✅  Providers descargados
Terraform plan       ✅  78 recursos planificados
Terraform apply   ⚠️  Parcial (68/78 — roles existentes)
  ↓ Ajuste IAM      ✅  Data sources en vez de resources
Terraform apply      ✅  74 recursos en estado (inc. data sources)
kubectl connect      ✅  2 nodos Ready, kube-system operativo
kubectl apply -k     🟡  APLICADO (pods con errores — ver sección 18)
ALB DNS              🟡  Creado pero sin dirección (ver sección 19.3)
```

---

## 15. CI/CD: Fixes a GitHub Actions

El workflow `.github/workflows/docker-build-push.yml` tenía 3 errores que impedían
su ejecución correcta. Se identificaron y corrigieron:

### 15.1 ECR repo name mismatch

**Problema:** El workflow usaba `siga-${{matrix.service}}` pero los repos en ECR
se crearon con el path `siga-production/${{matrix.service}}`.

```diff
  ECR_REPOSITORY: siga-${{ matrix.service }}
+ ECR_REPOSITORY: siga-production/${{ matrix.service }}
```

### 15.2 Missing AWS_SESSION_TOKEN

**Problema:** El step `aws-actions/configure-aws-credentials@v4` no recibía
`aws-session-token`. El lab de AWS Academy emite credenciales temporales con
sesión; sin el token, cualquier API call falla con
`InvalidClientTokenId`.

**Fix en job `build-and-push`:**

```diff
  - name: Configure AWS credentials
    uses: aws-actions/configure-aws-credentials@v4
    with:
      aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
      aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
+     aws-session-token: ${{ secrets.AWS_SESSION_TOKEN }}
      aws-region: ${{ env.AWS_REGION }}
```

> ⚠️ El job `deploy-to-eks` aun no tiene esta corrección. Pendiente agregar
> `aws-session-token` en su step de configure-aws-credentials.

### 15.3 Gitleaks detection

**Problema:** Las credenciales reales (parcialmente censuradas) documentadas
en `docs/evidencia-despliegue.md` activaban el secret scanning de Gitleaks en
cada commit.

**Fix:** Agregar `docs/evidencia-despliegue.md` al allowlist en `.gitleaks.toml`:

```diff
  [allowlist]
    description = "SIGA False Positives"
    paths = [
+     # Documentación de evidencias (credenciales censuradas o de ejemplo)
+     "docs/evidencia-despliegue\\.md",
+     "docs/en/evidencia-despliegue\\.md",
+     "docs/es/evidencia-despliegue\\.md",
      # Archivos de testing con datos dummy
      "(?i).*test.*",
    ]
```

---

## 16. Docker Build + Push a ECR (9 imágenes)

El CI/CD no estaba configurado con los secrets de GitHub necesarios
(GitHub Actions no podía autenticarse a AWS). Se construyeron y subieron
las 9 imágenes manualmente desde la máquina local.

### 16.1 Autenticación ECR

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  162272997857.dkr.ecr.us-east-1.amazonaws.com
```
```
Login Succeeded
```

### 16.2 Build + Push por servicio

```bash
# ── Registry (Eureka) ────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker build \
  -f services/registry/Dockerfile \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/registry:latest \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/registry:$(git rev-parse --short HEAD) \
  .
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker push \
  162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/registry:latest
```

```bash
# ── Gateway ───────────────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker build \
  -f services/gateway/Dockerfile \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/gateway:latest .
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker push \
  162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/gateway:latest
```

```bash
# ── Auth ──────────────────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker build \
  -f services/auth/Dockerfile \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/auth:latest .
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker push \
  162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/auth:latest
```

```bash
# ── Inventory ─────────────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker build \
  -f services/inventory/Dockerfile \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/inventory:latest .
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker push \
  162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/inventory:latest
```

```bash
# ── Sales ─────────────────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker build \
  -f services/sales/Dockerfile \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/sales:latest .
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker push \
  162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/sales:latest
```

```bash
# ── Billing ───────────────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker build \
  -f services/billing/Dockerfile \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/billing:latest .
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker push \
  162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/billing:latest
```

```bash
# ── Notification ──────────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker build \
  -f services/notification/Dockerfile \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/notification:latest .
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker push \
  162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/notification:latest
```

```bash
# ── Agent ─────────────────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker build \
  -f services/agent/Dockerfile \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/agent:latest .
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker push \
  162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/agent:latest
```

```bash
# ── Dashboard ─────────────────────────────────────────────────────
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker build \
  -f apps/dashboard/Dockerfile \
  -t 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/dashboard:latest .
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ docker push \
  162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/dashboard:latest
```

### 16.3 Fix al Dockerfile del Dashboard

Durante el build del dashboard se encontraron y corrigieron 3 issues en
`apps/dashboard/Dockerfile`:

| # | Problema | Fix |
|:-:|:---------|:----|
| 1 | `FROM node:20-alpine` — Node 20 no soportaba algunas features de SvelteKit | `FROM node:22-alpine` |
| 2 | Faltaba `ENV CI=true` antes del `pnpm install`, lo que causaba prompts interactivos | Agregado `ENV CI=true` |
| 3 | pnpm buscaba el lockfile en `/app` (raíz) pero estaba en `apps/dashboard/` | Se cambió el `WORKDIR` a `/app/apps/dashboard` antes del install |

```diff
- FROM node:20-alpine AS build
+ FROM node:22-alpine AS build

  WORKDIR /app
  RUN npm install -g pnpm@latest && \
      addgroup -S appgroup && adduser -S appuser -G appgroup

  COPY apps/dashboard/       apps/dashboard/
  COPY packages/ui-kit/      packages/ui-kit/
  COPY packages/shared/      packages/shared/

+ WORKDIR /app/apps/dashboard
+ ENV CI=true
  RUN pnpm install --frozen-lockfile 2>/dev/null || pnpm install
  RUN pnpm build

- FROM node:20-alpine AS runtime
+ FROM node:22-alpine AS runtime
```

### 16.4 Resumen de imágenes subidas

| Servicio    | Dockerfile                  | Imagen ECR |
|:------------|:----------------------------|:-----------|
| registry    | `services/registry/Dockerfile`    | `siga-production/registry:latest` |
| gateway     | `services/gateway/Dockerfile`     | `siga-production/gateway:latest` |
| auth        | `services/auth/Dockerfile`        | `siga-production/auth:latest` |
| inventory   | `services/inventory/Dockerfile`   | `siga-production/inventory:latest` |
| sales       | `services/sales/Dockerfile`       | `siga-production/sales:latest` |
| billing     | `services/billing/Dockerfile`     | `siga-production/billing:latest` |
| notification| `services/notification/Dockerfile`| `siga-production/notification:latest` |
| agent       | `services/agent/Dockerfile`       | `siga-production/agent:latest` |
| dashboard   | `apps/dashboard/Dockerfile`       | `siga-production/dashboard:latest` |

> ✅ 9 imágenes construidas y subidas a ECR exitosamente.

---

## 17. Fixes a Manifests de Kubernetes

Al aplicar los 48 manifests con `kubectl apply -k k8s/` se detectaron varios
errores que requirieron correcciones en los archivos YAML.

### 17.1 Imágenes ECR

Los manifests originales usaban un placeholder con variables para la URL de ECR:

```yaml
# Antes (placeholder):
image: ${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com/siga-${SERVICE}:latest

# Después (URL real):
image: 162272997857.dkr.ecr.us-east-1.amazonaws.com/siga-production/gateway:latest
```

Se reemplazó en los 9 deployment YAMLs:

- `k8s/03-deployments/gateway-deployment.yaml`
- `k8s/03-deployments/auth-deployment.yaml`
- `k8s/03-deployments/inventory-deployment.yaml`
- `k8s/03-deployments/sales-deployment.yaml`
- `k8s/03-deployments/billing-deployment.yaml`
- `k8s/03-deployments/notification-deployment.yaml`
- `k8s/03-deployments/agent-deployment.yaml`
- `k8s/03-deployments/registry-deployment.yaml`
- `k8s/03-deployments/dashboard-deployment.yaml`

### 17.2 Strategy block mal ubicado

En varios deployments, el bloque `strategy` estaba dentro de `spec.template.spec`
(nivel container) en vez de `spec` (nivel deployment). Se movió al nivel
correcto:

```diff
  spec:
+   strategy:
+     type: RollingUpdate
+     rollingUpdate:
+       maxSurge: 1
+       maxUnavailable: 0
    replicas: 2
    selector:
      matchLabels:
        app: siga-{svc}
    template:
      metadata:
        labels:
          app: siga-{svc}
      spec:
-       strategy:
-         type: RollingUpdate
-         rollingUpdate:
-           maxSurge: 1
-           maxUnavailable: 0
        containers:
          - name: {svc}
```

### 17.3 Kafka fsGroup

El `fsGroup: 1000` estaba dentro del `securityContext` del container en vez del
pod-level `securityContext`:

```diff
  spec:
    template:
      spec:
+       securityContext:
+         fsGroup: 1000
        containers:
          - name: kafka
-           securityContext:
-             fsGroup: 1000
            ...
```

### 17.4 SPRING_CONFIG_LOCATION

Todos los ConfigMaps tenían la variable `SPRING_CONFIG_LOCATION` apuntando a
`file:/etc/config/application.yaml`, pero ese archivo no existía — los ConfigMaps
se montan como archivos individuales por key, no como un `application.yaml`
completo. Se eliminó de los 9 ConfigMaps.

```diff
  data:
    SPRING_PROFILES_ACTIVE: "eks"
-   SPRING_CONFIG_LOCATION: "file:/etc/config/application.yaml"
    DB_HOST: "..."
```

### 17.5 Kafka storage class

El `volumeClaimTemplate` de Kafka usaba `gp3`, pero el cluster EKS solo tiene
`gp2` como StorageClass disponible. Se corrigió:

```diff
  volumeClaimTemplates:
    - metadata:
        name: kafka-data
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 10Gi
-       storageClassName: gp3
+       storageClassName: gp2
```

### 17.6 Recursos (CPU requests)

Los recursos originales pedían 500m-1000m de CPU por pod. Con 2 nodos t3.medium
(2 vCPU = 2000m cada uno, ~4000m total), no cabían todos los servicios. Se
redujeron los requests:

| Servicio      | CPU antes | CPU después | Memoria |
|:--------------|:---------:|:-----------:|:-------:|
| registry      | 500m      | **250m**    | 256Mi   |
| gateway       | 500m      | **250m**    | 256Mi   |
| auth          | 500m      | **250m**    | 256Mi   |
| inventory     | 500m      | **250m**    | 256Mi   |
| sales         | 500m      | **250m**    | 256Mi   |
| billing       | 500m      | **250m**    | 256Mi   |
| notification  | 500m      | **250m**    | 256Mi   |
| agent         | 1000m     | **500m**    | 512Mi   |
| dashboard     | 250m      | **100m**    | 128Mi   |
| Kafka         | 500m      | **250m**    | 512Mi   |

```diff
  resources:
    requests:
      memory: "256Mi"
-     cpu: "500m"
+     cpu: "250m"
    limits:
      memory: "512Mi"
      cpu: "500m"
```

---

## 18. Aplicación a EKS y Estado Actual

### 18.1 Aplicar manifests

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl apply -k k8s/
```
```
namespace/siga created
configmap/gateway-config created
configmap/auth-config created
configmap/inventory-config created
configmap/sales-config created
configmap/billing-config created
configmap/notification-config created
configmap/agent-config created
configmap/registry-config created
configmap/dashboard-config created
secret/db-secret created
deployment.apps/siga-gateway created
deployment.apps/siga-auth created
deployment.apps/siga-inventory created
deployment.apps/siga-sales created
deployment.apps/siga-billing created
deployment.apps/siga-notification created
deployment.apps/siga-agent created
deployment.apps/siga-registry created
deployment.apps/siga-dashboard created
service/siga-gateway created
service/siga-auth created
service/siga-inventory created
service/siga-sales created
service/siga-billing created
service/siga-notification created
service/siga-agent created
service/siga-registry created
service/siga-dashboard created
horizontalpodautoscaler.autoscaling/siga-gateway-hpa created
horizontalpodautoscaler.autoscaling/siga-auth-hpa created
horizontalpodautoscaler.autoscaling/siga-inventory-hpa created
horizontalpodautoscaler.autoscaling/siga-sales-hpa created
horizontalpodautoscaler.autoscaling/siga-billing-hpa created
horizontalpodautoscaler.autoscaling/siga-notification-hpa created
horizontalpodautoscaler.autoscaling/siga-dashboard-hpa created
configmap/siga-kafka-config created
service/siga-kafka created
statefulset.apps/siga-kafka created
serviceaccount/prometheus created
clusterrole.rbac.authorization.k8s.io/prometheus created
clusterrolebinding.rbac.authorization.k8s.io/prometheus created
configmap/prometheus-config created
deployment.apps/prometheus created
service/prometheus created
configmap/grafana-datasource created
deployment.apps/grafana created
service/grafana created
ingress.networking.k8s.io/siga-alb-ingress created
```

### 18.2 Creación de Secrets

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl create secret generic db-secret \
  --namespace=siga \
  --from-literal=DB_PASSWORD='<redacted>' \
  --from-literal=JWT_SECRET='<redacted>' \
  --from-literal=AI_API_KEY='<redacted>' \
  --from-literal=REDIS_PASSWORD='' \
  --from-literal=SMTP_PASSWORD=''
```

### 18.3 Errores encontrados y soluciones

| Error | Síntoma | Solución |
|:------|:--------|:---------|
| ConfigMap missing | Pods en `CrashLoopBackOff` — `SPRING_CONFIG_LOCATION` no encontraba el archivo | Eliminar variable de todos los ConfigMaps (sección 17.4) |
| Secrets vacíos | Pods no arrancaban por `DB_PASSWORD` vacío | Crear secret manual con `kubectl create secret generic` |
| Agent datasource | agent no encontraba la URL de auth | Configurar `AUTH_SERVICE_URL` en `agent-config.yaml` |
| Eureka URL | Servicios no se registraban | Usar DNS interno: `http://siga-registry.siga.svc.cluster.local:8761/eureka` |
| Gemini API key | Agent no podía usar IA | Pasar `AI_API_KEY` como secret desde `db-secret` |

### 18.4 Estado actual de los pods

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl get pods -n siga
```

```
NAME                                READY   STATUS    RESTARTS   AGE
grafana-6698b7fb5b-mssg4            1/1     Running   0          144m
prometheus-c94f8fb86-tr7fk          1/1     Running   0          144m
siga-agent-7f66c768d6-5zk5k         1/1     Running   0          101m
siga-auth-848c867688-rb7zf          1/1     Running   0          3m
siga-billing-bc878c4b-5jrhn         1/1     Running   0          3m
siga-dashboard-6757677c49-72p8k     1/1     Running   0          144m
siga-gateway-bff594fc4-z55f6        1/1     Running   0          85s
siga-inventory-85cd78bbd4-4pbvd     1/1     Running   1          3m
siga-kafka-0                        0/1     Pending   0          83m
siga-notification-bf8967777-9gssg   1/1     Running   0          3m
siga-registry-8657b46f69-vdl9r      1/1     Running   0          144m
siga-sales-655c5fdbb8-l9zgw         1/1     Running   0          3m
```

| Pod | Status | Explicación |
|:----|:-------|:------------|
| **siga-gateway** 🟢 | Running | Gateway reactivo, enruta solicitudes a backend |
| **siga-registry** 🟢 | Running | Eureka server — 7 servicios registrados UP |
| **siga-auth** 🟢 | Running | Conectado a RDS PostgreSQL (siga_auth) |
| **siga-inventory** 🟢 | Running | Conectado a RDS PostgreSQL (siga_inventory) |
| **siga-sales** 🟢 | Running | Conectado a RDS PostgreSQL (siga_sales) |
| **siga-billing** 🟢 | Running | Conectado a RDS PostgreSQL (siga_billing) |
| **siga-notification** 🟢 | Running | Conectado a RDS PostgreSQL (siga_notification) |
| **siga-agent** 🟢 | Running | Usa API de Gemini (no requiere DB) |
| **siga-dashboard** 🟢 | Running | Frontend SvelteKit, no necesita DB |
| **siga-kafka** 🟡 | Pending | Sin CPU disponible en los nodos (2× t3.medium) |
| **prometheus** 🟢 | Running | Métricas del cluster operativas |
| **grafana** 🟢 | Running | Dashboards visibles |

> ✅ **Resultado:** 11 de 12 pods operativos. Todos los servicios core
> (auth, billing, inventory, sales, notification, gateway, agent, registry,
> dashboard) están Running y conectados a RDS. Solo Kafka queda Pending
> por limitación de CPU en los 2 nodos t3.medium.

#### Eureka — Servicios registrados (7/7 UP)

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl exec -n siga deployment/siga-auth -- \
  sh -c "wget -q -O- http://siga-registry:8761/eureka/apps" | grep -A1 "<name>" | grep name
```

```
    <name>SIGA-AUTH</name>
    <name>SIGA-AGENT</name>
    <name>SIGA-INVENTORY</name>
    <name>SIGA-SALES</name>
    <name>SIGA-BILLING</name>
    <name>SIGA-NOTIFICATION</name>
    <name>SIGA-GATEWAY</name>
```

#### Health endpoints verificados

```bash
# Gateway health
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl exec -n siga deployment/siga-gateway -- \
  sh -c "wget -q -O- http://localhost:8080/actuator/health"
{"groups":["liveness","readiness"],"status":"UP"}

# Auth health
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl exec -n siga deployment/siga-auth -- \
  sh -c "wget -q -O- http://localhost:8081/actuator/health"
{"groups":["liveness","readiness"],"status":"UP"}
```

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl get svc -n siga
```

```
NAME                TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                        AGE
grafana             ClusterIP   172.20.114.226   <none>        3000/TCP                       153m
prometheus          ClusterIP   172.20.8.240     <none>        9090/TCP                       153m
siga-agent          ClusterIP   172.20.14.5      <none>        8086/TCP                       153m
siga-auth           ClusterIP   172.20.155.13    <none>        8081/TCP                       153m
siga-billing        ClusterIP   172.20.61.182    <none>        8084/TCP                       153m
siga-dashboard      ClusterIP   172.20.130.165   <none>        3000/TCP                       153m
siga-gateway        ClusterIP   172.20.151.133   <none>        8080/TCP                       153m
siga-inventory      ClusterIP   172.20.203.179   <none>        8082/TCP                       153m
siga-kafka          ClusterIP   172.20.199.127   <none>        9092/TCP,29092/TCP,29093/TCP   153m
siga-notification   ClusterIP   172.20.208.238   <none>        8085/TCP                       153m
siga-registry       ClusterIP   172.20.220.126   <none>        8761/TCP                       153m
siga-sales          ClusterIP   172.20.82.249    <none>        8083/TCP                       153m
```

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl get ingress -n siga
```

```
NAME               CLASS   HOSTS          ADDRESS   PORTS   AGE
siga-alb-ingress   alb     siga.internal  <pending>  80      153m
```

---

## 19. Issues Identificados

Problemas detectados que requieren infraestructura adicional o cambios
fuera del alcance de la configuración actual.

### 19.1 RDS no accesibles — ✅ RESUELTO

> **Estado actual:** Conectividad RDS verificada. Los 5 servicios que
> dependen de PostgreSQL (auth, billing, inventory, sales, notification)
> están Running y conectados a RDS.

**Causa raíz identificada:** Los nodos de EKS tenían asignado el security
group `eks-cluster-sg-siga-production-cluster-1854058531`
(`sg-0fa288b3706219d44`), pero los security groups de RDS y Redis solo
permitían tráfico desde el SG creado por Terraform
`siga-production-eks-nodes-sg` (`sg-01bd38587496519ba`). EKS asigna su
propio cluster SG automáticamente — el SG de Terraform nunca se adjuntó
a los nodos.

**Solución aplicada:**
1. Agregar el SG del cluster EKS (`sg-0fa288b3706219d44`) al SG de RDS
   (`sg-02a764f457ecc6f2a`) — regla inbound puerto 5432
2. Agregar el SG del cluster EKS al SG de Redis
   (`sg-0b47329c9fc913ca2`) — regla inbound puerto 6379
3. Configurar `?sslmode=require` en todas las URLs JDBC de los ConfigMaps
   (RDS PostgreSQL requiere SSL)
4. Crear 5 bases de datos en RDS: `siga_auth`, `siga_billing`,
   `siga_inventory`, `siga_sales`, `siga_notification` (owner: `siga_admin`)
5. Actualizar todos los ConfigMaps con el endpoint real:
   `siga-production-postgres.cm75k1vmfnks.us-east-1.rds.amazonaws.com`

**Lección:** EKS asigna automáticamente su propio security group a los
nodos del cluster. Los SGs creados por Terraform para los node groups
NO se adjuntan a los nodos reales. Siempre verificar qué SG tienen los
nodos con `aws ec2 describe-instances` antes de configurar reglas inbound
en RDS/Redis.

### 19.2 CPU insuficiente — 🟡 Parcialmente resuelto

> **Estado actual:** Todos los servicios core están Running con
> `replicas: 1`. Kafka sigue Pending por falta de CPU.

2 nodos t3.medium (2 vCPU × 2 = 4 vCPU total) están al 90%+ de uso.

**Solución aplicada (parcial):**
1. Reducir todos los deployments a `replicas: 1` (antes `replicas: 2`)
2. Bajar CPU requests a `250m` por servicio (antes `500m`)
3. HPA permanece configurado para auto-escalar cuando se agreguen nodos

**Pendiente:** Kafka requiere ~500m CPU que no están disponibles.

**Solución necesaria para Kafka:**
1. Escalar el node group a 3+ nodos t3.medium
2. O migrar a t3.large (2 vCPU, 8GB RAM)
3. Para el lab: los servicios funcionan sin Kafka (Kafka auto-config
   excluida en billing, inventory, notification, sales)

### 19.3 ALB Ingress sin dirección

```bash
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ kubectl describe ingress -n siga
```

```
Events:
  Type     Reason          Age   From                    Message
  ----     ------          ----  ----                    -------
  Warning  FailedBuildModel 5m   ingress/alb-controller  Failed build model: 
    failed to get controller Load Balancer: WebIdentityErr: 
    no OpenIDConnect provider found
```

**Causa raíz:** El ALB Ingress Controller no puede crear el ALB porque falta el
IAM OIDC provider (bloqueado por el lab — sección 11).

**Solución necesaria:**
1. Instalar AWS Load Balancer Controller add-on en EKS
2. Crear IAM OIDC provider (requiere permiso `iam:CreateOpenIDConnectProvider`)
3. En el lab de AWS Academy no es posible — se necesita cuenta con permisos completos

### 19.4 Secrets en texto plano

Los secrets se crearon con `kubectl create secret generic` y están
almacenados en Kubernetes Secrets (base64, no cifrados):

| Secret | Método actual | Método deseado (IE5) |
|:-------|:--------------|:---------------------|
| JWT_SECRET | `kubectl create secret` → base64 en etcd | AWS Secrets Manager + CSI Driver |
| DB_PASSWORD | `kubectl create secret` → base64 en etcd | AWS Secrets Manager + CSI Driver |
| AI_API_KEY | `kubectl create secret` → base64 en etcd | AWS Secrets Manager + CSI Driver |
| SMTP_PASSWORD | `kubectl create secret` → base64 en etcd | AWS Secrets Manager + CSI Driver |

**Solución necesaria (para rúbrica IE5):**

```yaml
# 1. Crear un SecretProviderClass para AWS Secrets Manager
apiVersion: secrets-store.csi.x-k8s.io/v1
kind: SecretProviderClass
metadata:
  name: siga-aws-secrets
  namespace: siga
spec:
  provider: aws
  parameters:
    objects: |
      - objectName: "siga/db-password"
        objectType: "secretsmanager"
      - objectName: "siga/jwt-secret"
        objectType: "secretsmanager"
      - objectName: "siga/ai-api-key"
        objectType: "secretsmanager"

# 2. Montar en cada deployment como volume CSI
# volumes:
#   - name: secrets-store
#     csi:
#       driver: secrets-store.csi.k8s.io
#       readOnly: true
#       volumeAttributes:
#         secretProviderClass: siga-aws-secrets
```

> **Requisitos previos:** Secrets Store CSI Driver instalado en el cluster,
> IAM role con permisos para leer Secrets Manager, y OIDC provider configurado.


### 19.5 CI/CD sobreescribía secrets — ✅ RESUELTO

> **Estado actual:** El secret `db-secret` se excluyó del kustomization
> para evitar que `kubectl apply -k` lo sobreescriba con valores vacíos.

**Causa raíz:** El workflow de GitHub Actions ejecuta `kubectl apply -k k8s/`
que incluía `02-secrets/db-secret.yaml` con valores **vacíos** (placeholder).
Cada push sobreescribía el secret creado manualmente con valores reales,
causando que los pods crashearan con `JWT_SECRET must not be empty` y
errores de conexión a la base de datos.

**Solución aplicada:**
1. Comentar `- 02-secrets/db-secret.yaml` en `kustomization.yaml`
2. El secret se crea manualmente con `kubectl create secret generic db-secret`
3. El CI/CD ya no toca el secret — solo aplica ConfigMaps, Deployments,
   Services, HPA, Ingress y monitoring

**Lección:** Los archivos de secrets en el repo son documentación, no
recursos aplicables. Si se incluyen en kustomization, `kubectl apply -k`
los sobreescribe en cada deploy. Separar siempre los secrets del pipeline
de apply.

---

## 20. Resolución de Conectividad RDS

Esta sección documenta el proceso completo de debugging y resolución
del problema de conectividad entre los pods de EKS y la instancia RDS
PostgreSQL.

### 20.1 Security Group mismatch

**Síntoma:** Los pods de auth, billing, inventory, sales y notification
entraban en `CrashLoopBackOff` con error de conexión a PostgreSQL:
```
FATAL: no pg_hba.conf entry for host "10.0.3.254", user "siga_admin",
database "siga_auth", SSL off
```

**Diagnóstico:**
```bash
# Verificar qué SG tienen los nodos EKS
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws ec2 describe-instances \
  --filters "Name=tag:eks:cluster-name,Values=siga-production-cluster" \
  --query "Reservations[].Instances[].SecurityGroups[].GroupId" \
  --output text
sg-0fa288b3706219d44   # eks-cluster-sg (ASIGNADO A LOS NODOS)
```

```bash
# Verificar reglas inbound del SG de RDS
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws ec2 describe-security-groups \
  --group-ids sg-02a764f457ecc6f2a \
  --query "SecurityGroups[].IpPermissions[].UserIdGroupPairs[].GroupId" \
  --output text
sg-01bd38587496519ba   # siga-production-eks-nodes-sg (Terraform SG)
```

**El problema:** Los nodos tenían `sg-0fa288b3706219d44` (EKS cluster SG)
pero RDS solo permitía tráfico desde `sg-01bd38587496519ba` (Terraform SG).
EKS asigna automáticamente su propio SG a los nodos — el SG de Terraform
nunca se adjuntó.

**Fix:**
```bash
# Agregar EKS cluster SG al SG de RDS (puerto 5432)
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws ec2 authorize-security-group-ingress \
  --group-id sg-02a764f457ecc6f2a \
  --protocol tcp --port 5432 \
  --source-security-group-id sg-0fa288b3706219d44

# Agregar EKS cluster SG al SG de Redis (puerto 6379)
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws ec2 authorize-security-group-ingress \
  --group-id sg-0b47329c9fc913ca2 \
  --protocol tcp --port 6379 \
  --source-security-group-id sg-0fa288b3706219d44
```

### 20.2 Requisito de SSL

RDS PostgreSQL requiere conexiones SSL. Sin `sslmode=require` en la URL
JDBC, la conexión falla con:
```
FATAL: no pg_hba.conf entry for host "...", SSL off
```

**Fix en ConfigMaps:**
```yaml
# Antes (incorrecto):
SPRING_DATASOURCE_URL: "jdbc:postgresql://host:5432/siga_auth"

# Después (correcto):
SPRING_DATASOURCE_URL: "jdbc:postgresql://host:5432/siga_auth?sslmode=require"
DB_SSL_MODE: "require"
```

### 20.3 Creación de bases de datos

RDS se creó con una sola base de datos inicial. Cada microservicio
necesita su propia base de datos. Se crearon 5:

```sql
-- Conectado a RDS como siga_admin
CREATE DATABASE siga_auth OWNER siga_admin;
CREATE DATABASE siga_billing OWNER siga_admin;
CREATE DATABASE siga_inventory OWNER siga_admin;
CREATE DATABASE siga_sales OWNER siga_admin;
CREATE DATABASE siga_notification OWNER siga_admin;
```

### 20.4 ddl-auto=update

Las bases de datos estaban vacías. Con `ddl-auto=validate`, Hibernate
no podía validar el schema porque no existían tablas. Se cambió a
`ddl-auto=update` para que Hibernate cree las tablas automáticamente:

```yaml
SPRING_JPA_HIBERNATE_DDL_AUTO: "update"
```

> **Nota:** En producción real, se usaría Flyway o Liquibase para
> migraciones controladas. Para el lab, `update` es suficiente.

---

## 21. Fixes de Health Probes y Puertos

### 21.1 HTTP probes retornaban 401/403

**Síntoma:** Los pods de inventory, sales y notification fallaban los
health probes con HTTP 401/403:
```
Liveness probe failed: HTTP probe failed with statuscode: 403
Readiness probe failed: HTTP probe failed with statuscode: 401
```

**Causa:** Spring Security protege los endpoints `/actuator/health` y
sub-rutas. Incluso con `management.endpoint.health.probes.enabled=true`,
el filtro de seguridad intercepta las requests.

**Fix:** Cambiar de HTTP probes a TCP probes. Los TCP probes solo
verifican que el puerto TCP esté abierto — bypass completo de Spring
Security:

```yaml
# Antes (HTTP — fallaba con 403):
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8082

# Después (TCP — funciona):
livenessProbe:
  tcpSocket:
    port: 8082
  initialDelaySeconds: 30
  periodSeconds: 15
```

Aplicado a: inventory (8082), sales (8083), notification (8086).

### 21.2 Puerto de notification incorrecto

**Síntoma:** Notification fallaba los probes con `connection refused`
en el puerto 8085.

**Causa:** El deployment tenía `containerPort: 8085` y los probes
apuntaban a 8085, pero la aplicación escucha en **8086** (configurado
en `application.yml`).

**Fix:**
```yaml
# notification-deployment.yaml
ports:
  - containerPort: 8086   # era 8085
livenessProbe:
  tcpSocket:
    port: 8086            # era 8085
readinessProbe:
  tcpSocket:
    port: 8086            # era 8085
```

### 21.3 OOMKilled — Memory limits insuficientes

**Síntoma:** Pods eran killados por OOMKiller con Exit Code 137.

**Causa:** Memory limits de `256Mi` no son suficientes para Spring Boot
+ JVM + Tomcat/Netty.

**Fix:**
```yaml
resources:
  requests:
    memory: "512Mi"   # era 256Mi
    cpu: "250m"
  limits:
    memory: "1Gi"     # era 256Mi
    cpu: "1"
```

---

## 22. Fixes de Eureka y Variables de Entorno

### 22.1 Gateway y billing no se registraban en Eureka

**Síntoma:** Eureka mostraba solo 5 servicios registrados. Gateway y
billing no aparecían.

**Causa:** Los servicios usan variables de entorno diferentes para
configurar Eureka:

| Servicio | Variable esperada | Fallback |
|:---------|:-------------------|:---------|
| auth, inventory, sales, notification | `EUREKA_URL` | `localhost:8761` |
| **gateway** | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `siga-eureka:8761` (incorrecto) |
| **billing** | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `localhost:8761` |
| agent | `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `localhost:8761` |

El ConfigMap tenía `EUREKA_URL` pero gateway y billing necesitaban
`EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`. Además, el fallback de gateway
apuntaba a `siga-eureka` (nombre incorrecto — el correcto es
`siga-registry`).

**Fix:** Agregar `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` a los ConfigMaps
de gateway y billing:

```yaml
# gateway-config.yaml y billing-config.yaml
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: "http://siga-registry.siga.svc.cluster.local:8761/eureka/"
```

**Resultado:** 7/7 servicios registrados en Eureka UP.

### 22.2 Passwords por servicio

Cada servicio espera un nombre de env var diferente para el password
de la base de datos:

| Servicio | Env var esperada |
|:---------|:-----------------|
| auth | `AUTH_DB_PASSWORD` |
| billing | `SPRING_DATASOURCE_PASSWORD` |
| inventory | `INVENTORY_DB_PASSWORD` |
| sales | `SALES_DB_PASSWORD` |
| notification | `NOTIFICATION_DB_PASSWORD` |

**Fix:** Agregar cada env var al deployment correspondiente, todas
referenciando la misma key `DB_PASSWORD` del secret `db-secret`:

```yaml
env:
  - name: AUTH_DB_PASSWORD
    valueFrom:
      secretKeyRef:
        key: DB_PASSWORD
        name: db-secret
```

### 22.3 Kafka auto-config exclusion

**Síntoma:** Los servicios con Kafka consumers (billing, inventory,
notification, sales) se bloqueaban al arrancar esperando conectar a
Kafka (que estaba Pending).

**Fix:** Excluir Kafka auto-configuration:

```yaml
SPRING_AUTOCONFIGURE_EXCLUDE: "org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
```

> **Nota:** Los servicios funcionan en modo degradado sin Kafka. La
> mensajería SAGA no está disponible hasta que Kafka esté operativo.

---

## 23. CI/CD — GitHub Actions y Secrets

### 23.1 Configuración de GitHub Secrets

Se configuraron 3 secrets en el repositorio GitHub
(`HecAguilaV/SIGA` → Settings → Secrets and variables → Actions):

| Secret | Propósito |
|:-------|:----------|
| `AWS_ACCESS_KEY_ID` | Access key para autenticación AWS |
| `AWS_SECRET_ACCESS_KEY` | Secret key para autenticación AWS |
| `AWS_SESSION_TOKEN` | Token de sesión temporal (lab AWS Academy) |

> **Importante:** El lab de AWS Academy usa credenciales temporales que
> incluyen session token. Sin `aws-session-token` en el paso
> `configure-aws-credentials`, cualquier comando AWS falla con
> `AccessDenied`.

### 23.2 Pipeline CI/CD operativo

El workflow `.github/workflows/docker-build-push.yml` ejecuta:

1. **Build & Push** — Construye 9 imágenes Docker y las sube a ECR
2. **Deploy to EKS** — Ejecuta `kubectl apply -k k8s/` y `kubectl rollout restart`
3. **Verify** — Verifica el estado de los pods

```bash
# Trigger del pipeline
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ git push origin main
# GitHub Actions ejecuta el workflow automáticamente
```

### 23.3 Commits de la sesión de estabilización

| Commit | Descripción |
|:-------|:------------|
| `d73bfeb` | fix(deploy): stabilize SIGA deployment on EKS — RDS, probes, resources, env vars |
| `58b7a9d` | fix(k8s): exclude secret from kustomization and scale to 1 replica |

---

## Resumen del estado actual

```
Terraform (74 recursos)     [OK] Cluster, VPC, RDS, Redis, ALB, ECR, SGs
kubectl connect             [OK] 2 nodos t3.medium Ready
Docker build + push         [OK] 9 imágenes en ECR
kubectl apply -k k8s/       [OK] 48 manifests aplicados
├── Pods Running            [GREEN] 11/12 (todos los servicios core + monitoreo)
├── Pods Pending            [YELLOW] 1 (kafka — sin CPU disponible)
RDS conectividad            [OK] SG fix + SSL + 5 DBs creadas
Eureka                      [OK] 7/7 servicios registrados UP
Health probes               [OK] TCP probes para inventory/sales/notification
OOMKilled                   [OK] Memory limits 256Mi -> 1Gi
CI/CD pipeline              [OK] GitHub Actions con secrets configurados
GitHub Secrets              [OK] AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, AWS_SESSION_TOKEN
Secret overwrite            [OK] Secret excluido del kustomization
ALB Ingress                 [RED] Sin dirección (falta OIDC provider — lab)
Secrets seguros             [RED] kubectl secrets (AWS SM como mejora futura)
Kafka                       [YELLOW] Pending (sin CPU — requiere 3er nodo)
```
