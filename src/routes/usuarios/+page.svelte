<script>
    import { onMount } from "svelte";
    import { api } from "$lib/services/api";
    import CrudTable from "$lib/components/CrudTable.svelte";
    import { goto } from "$app/navigation";

    let usuarios = [];
    let loading = true;

    const columns = [
        { key: "nombre", label: "Nombre" },
        { key: "email", label: "Email" },
        { key: "rol", label: "Rol" },
        {
            key: "activo",
            label: "Estado",
            formatter: (val) => (val ? "Activo" : "Inactivo"),
        },
    ];

    onMount(async () => {
        try {
            const response = await api.get("/api/saas/usuarios");
            if (response.success) {
                usuarios = response.usuarios;
            }
        } catch (error) {
            console.error("Error cargando usuarios:", error);
        } finally {
            loading = false;
        }
    });

    function handleCreate() {
        goto("/usuarios/nuevo");
    }

    function handleEdit(event) {
        const item = event.detail;
        goto(`/usuarios/${item.id}`);
    }

    async function handleDelete(event) {
        const item = event.detail;
        if (
            confirm(`¿Estás seguro de desactivar al usuario "${item.nombre}"?`)
        ) {
            try {
                // DELETE en usuarios es soft delete (desactivar)
                // Eliminar usuario
                const response = await api.delete(
                    `/api/saas/usuarios/${item.id}`,
                );
                if (response.success) {
                    // Actualizar estado localmente
                    usuarios = usuarios.map((u) =>
                        u.id === item.id ? { ...u, activo: false } : u,
                    );
                } else {
                    alert("Error: " + response.message);
                }
            } catch (e) {
                alert("Error al desactivar: " + e.message);
            }
        }
    }
</script>

<CrudTable
    title="Gestión de Usuarios Operativos"
    data={usuarios}
    {columns}
    {loading}
    on:create={handleCreate}
    on:edit={handleEdit}
    on:delete={handleDelete}
/>
