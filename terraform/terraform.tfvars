# SIGA-Dev — terraform.tfvars
# Optimizado para despliegue temporal (Lab académico): mínimo costo.
# DESTRUIR con `terraform destroy` cuando termines la presentación.

# Nodos EKS: 1 t3.medium (suficiente para 9 microservicios + Kafka)
node_instance_types = ["t3.medium"]
desired_node_count  = 1
min_node_count      = 1
max_node_count      = 1
