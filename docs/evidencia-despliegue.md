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
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws configure set aws_access_key_id ASIASLSBZOHQUNET5J3Y
hector@Veriton-Z4694G:~/Escritorio/SIGA/SIGA$ aws configure set aws_secret_access_key 2Wo6OhGwTSGK2rx6gP3jIzmYjphAIekZ2bFcoXro
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
kubectl apply -k     ⏳  PENDIENTE
ALB DNS              🟢  siga-production-alb-1444586698.us-east-1.elb.amazonaws.com
```
