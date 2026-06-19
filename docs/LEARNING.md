# Aprendizajes y Decisiones Técnicas — SIGA

> Documento de retrospectiva técnica para la Evaluación Final Transversal (EFT)
> Fullstack + DevOps — Junio 2026

---

## 1. Migración de Docker Hub a Amazon ECR

### Decisión
Se migró el registro de contenedores de Docker Hub a Amazon ECR (Elastic Container Registry).

### ¿Por qué?
1. **Integración nativa con EKS**: ECR está en la misma región que el cluster, lo que elimina costos de salida de red y latencia.
2. **IAM-based authentication**: Los nodos de EKS pueden autenticarse contra ECR usando roles IAM, sin necesidad de manejar tokens Docker Hub.
3. **Seguridad**: ECR tiene escaneo de vulnerabilidades integrado via Amazon Inspector.
4. **Evaluación DevOps**: El profe solicita explícitamente ECR (IE4: Pipeline CI/CD).

### Impacto
- El pipeline de GitHub Actions ahora usa `aws-actions/amazon-ecr-login@v2` en lugar de `docker/login-action@v3`.
- Las imágenes se pushean con tags `latest` y `{commit-sha}` para trazabilidad.
- Se agregó un job opcional de deploy a EKS que aplica los manifests y forza rolling update.

---

## 2. Terraform como IaC

### Decisión
Se implementó Terraform para gestionar toda la infraestructura de AWS.

### ¿Por qué?
1. **Reproducibilidad**: Toda la infraestructura está descrita en código, versionada en el repo.
2. **Destroy rápido**: Con `terraform destroy` se eliminan todos los recursos, crítico para ahorrar costos con $50 de crédito.
3. **Trazabilidad académica**: Cada recurso tiene un comentario explicando la decisión técnica, ideal para la rúbrica (IE1: Configuración de Cómputo, IE2: Alta Disponibilidad).

### Arquitectura Terraform
- `versions.tf` → Providers (AWS ~> 5.0) y versión mínima de Terraform
- `variables.tf` → 18 variables parametrizables (instance types, CIDR, AZs)
- `locals.tf` → Tags comunes, prefijo de nombres, lista de servicios
- `main.tf` → 9 secciones con ~77 recursos AWS
- `outputs.tf` → Endpoints, IDs, URLs útiles para CI/CD

### Aprendizaje clave
Terraform simplifica drásticamente la gestión de infraestructura. Donde antes habría que configurar manualmente VPC, subnets, IAM, EKS, RDS, Redis y ALB (horas en la consola AWS), ahora se ejecuta `terraform apply` y todo se crea en ~15-20 minutos.

---

## 3. Kubernetes: Manifests y Estrategia de Despliegue

### Decisión
Se organizaron los manifests de Kubernetes en 7 categorías usando Kustomize.

### Estructura
```
k8s/
├── 00-namespace.yaml          → Namespace "siga"
├── 01-configmaps/             → 9 configmaps (variables de entorno)
├── 02-secrets/                → Placeholder (secrets via AWS Secrets Manager)
├── 03-deployments/            → 9 deployments con health checks
├── 04-services/               → 9 ClusterIP services
├── 05-hpa/                    → 7 Horizontal Pod Autoscalers
├── 06-ingress/                → ALB Ingress con routing paths
└── kustomization.yaml         → Orquestador
```

### Decisiones técnicas documentadas
- **2 réplicas** para servicios transaccionales (gateway, auth, inventory, sales, billing, notification, dashboard). Garantiza rolling updates sin downtime.
- **1 réplica** para agent (conversaciones en memoria) y registry (Eureka con caché en clientes).
- **Anti-affinity suave** en deployments multi-réplica para distribuir pods en distintos nodos.
- **HPA con CPU al 70% y Memoria al 80%** — escalado automático ante picos de carga.
- **Readiness + Liveness probes** usando Spring Boot Actuator (backend) y endpoint `/health` (frontend).

---

## 4. Frontend Dockerfile: SvelteKit con adapter-node

### Decisión
Se usó `@sveltejs/adapter-node` en lugar de `adapter-static`.

### ¿Por qué?
El dashboard usa server-side data composition (BFF nativo) y rutas protegidas con JWT. `adapter-static` genera archivos HTML estáticos que no pueden manejar SSR ni endpoints de API server-side.

### Buenas prácticas aplicadas
- **Multi-stage build**: Etapa de build con Node.js Alpine y runtime con solo lo necesario (~120 MB final).
- **Usuario no root**: `appuser` en runtime (seguridad).
- **Health check**: Endpoint `/health` monitoreado por Kubernetes.
- **Variables de entorno**: PORT, HOST, NODE_ENV inyectables.

---

## 5. CI/CD Pipeline

### Estructura
El pipeline se activa en pushes a `main` o `migracion-microservicios`, y también manualmente via `workflow_dispatch`.

### Jobs
1. **build-and-push (matrix)**: Corre en paralelo para los 9 servicios. Cada uno se construye y pushea a su repositorio ECR.
2. **deploy-to-eks (solo main)**: Configura kubectl, aplica manifests, forza rolling update y verifica el estado.

### GitHub Secrets requeridos
| Secret | Propósito |
|--------|-----------|
| `AWS_ACCESS_KEY_ID` | Autenticación AWS para ECR + EKS |
| `AWS_SECRET_ACCESS_KEY` | Clave secreta correspondiente |
| `AWS_ACCOUNT_ID` | ID de cuenta para URLs de ECR |

---

## 6. Frontend Dockerfile

### Decisión
Se containerizó el frontend SvelteKit con un Dockerfile multi-etapa.

### Arquitectura del Dockerfile

```
Stage 1 (build): node:20-alpine → pnpm install → pnpm build
Stage 2 (runtime): node:20-alpine → build/index.js (adapter-node)
```

### Decisiones clave
- Se copian `packages/ui-kit` y `packages/shared` en el build porque el dashboard los referencia mediante aliases de Vite (no son dependencias npm publicadas).
- El build se hace desde la raíz del monorepo (context: `.`), consistente con los Dockerfiles de los microservicios backend.

---

## 7. Kafka en EKS: StatefulSet con KRaft mode

### Decisión
Kafka se despliega dentro del cluster EKS como un StatefulSet con almacenamiento persistente, en lugar de usar Amazon MSK.

### ¿Por qué?
1. **Costo**: MSK cuesta ~$100+/mes. Dentro de EKS solo consume recursos de los nodos existentes.
2. **KRaft mode**: Kafka 3.x+ eliminó la dependencia de ZooKeeper (KIP-853). Un solo contenedor hace de broker + controller.
3. **StatefulSet**: Necesario porque Kafka requiere identidad de red estable y almacenamiento persistente para los logs de eventos.

### Alternativa evaluada
- **Amazon MSK**: Descartado por costo ($100+/mes vs presupuesto de $50).
- **Confluent Cloud**: Ofrece free tier pero requiere conectividad a internet.
- **Kafka externo (docker-compose)**: Funciona para desarrollo local pero no para la demo desplegada.

### Impacto en SAGA
Con Kafka en el cluster, los microservicios se comunican vía eventos Kafka para transacciones distribuidas. El flujo típico:
1. Sales emite `SALE_COMPLETED` → Inventory descuenta stock
2. Sales emite `PAYMENT_CONFIRMED` → Billing registra el pago
3. Notification consume ambos eventos para enviar alertas

Sin Kafka, la SAGA no funciona y la demo de transacciones distribuidas falla.

---

## 8. Gestión de Costos AWS

### Presupuesto: $50 créditos de laboratorio

| Medida de ahorro | Impacto |
|------------------|---------|
| t3.medium (burstable) en lugar de instancias dedicadas | ~50% más barato que c5 o m5 |
| RDS db.t3.small (no Multi-AZ) | Ahorra ~$30/mes vs Multi-AZ |
| Redis cache.t3.micro | Mínimo costo ($15/mes) |
| Solo 2 NAT Gateways (1 por AZ) | Necesario para HA, pero se puede reducir a 1 |
| Apagar cluster fuera de horas laborales | Ahorra ~$73/mes del plano de control EKS |

---

## 9. Lecciones Aprendidas

### Técnicas
1. **Spring Boot 4.x + Kafka no es trivial en CI**: Los testcontainers con Kafka requieren manejo cuidadoso de tiempos de espera y limpieza entre tests.
2. **El orden de Terraform importa**: Crear VPC → Subnets → IAM → EKS → NodeGroup → RDS → Redis → ALB. Las dependencias entre recursos son críticas.
3. **SvelteKit adapter-node vs adapter-static**: Si tu app tiene server routes, NO uses adapter-static. Parece obvio, pero fácil de pasar por alto.

### De proceso
1. **SDD (Spec-Driven Development)**: Tener specs antes de codificar evita retrabajo. Especialmente útil cuando el deadline es ajustado.
2. **Monorepo con pnpm**: Los aliases de Vite para paquetes locales funcionan bien en desarrollo, pero en Docker hay que copiar explícitamente las dependencias locales.

---

## 10. Pendientes y Mejora Continua

- [ ] Implementar OIDC para GitHub Actions (más seguro que Access Keys estáticas)
- [ ] Agregar Karpenter para auto-escalado de nodos más eficiente
- [ ] Configurar AWS WAF en el ALB para protección contra OWASP Top 10
- [ ] Migrar Terraform state a S3 backend (hoy está en local)
- [ ] Configurar cert-manager + Let's Encrypt para HTTPS
- [ ] Agregar dashboard de monitoreo con CloudWatch + Prometheus
