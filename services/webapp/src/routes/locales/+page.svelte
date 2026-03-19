<script>
    import { onMount } from "svelte";
    import { api } from "$lib/services/api";
    import CrudTable from "$lib/components/CrudTable.svelte";
    import { goto } from "$app/navigation";

    let locales = [];
    let loading = true;

    const columns = [
        { key: "nombre", label: "Nombre" },
        { key: "direccion", label: "Dirección" },
        { key: "ciudad", label: "Ciudad" },
        {
            key: "activo",
            label: "Estado",
            formatter: (val) => (val ? "Activo" : "Inactivo"),
        },
    ];

    onMount(async () => {
        try {
            const response = await api.get("/api/saas/locales");
            if (response.success) {
                locales = response.locales;
            }
        } catch (error) {
            console.error("Error cargando locales:", error);
        } finally {
            loading = false;
        }
    });

    function handleCreate() {
        goto("/locales/nuevo");
    }

    function handleEdit(event) {
        const local = event.detail;
        goto(`/locales/${local.id}`);
    }

    async function handleDelete(event) {
        const local = event.detail;
        if (confirm(`¿Estás seguro de eliminar el local "${local.nombre}"?`)) {
            // Implementar soft delete si backend lo soporta, o warning
            alert(
                "Funcionalidad de eliminar pendiente de implementación en backend para seguridad.",
            );
        }
    }
</script>

<CrudTable
    title="Gestión de Locales"
    data={locales}
    {columns}
    {loading}
    on:create={handleCreate}
    on:edit={handleEdit}
    on:delete={handleDelete}
/>
