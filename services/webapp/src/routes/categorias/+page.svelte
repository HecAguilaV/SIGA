<script>
    import { onMount } from "svelte";
    import { api } from "$lib/services/api";
    import { toast } from "$lib/stores/toast";
    import { authStore } from "$lib/stores/authStore";
    import CrudTable from "$lib/components/CrudTable.svelte";
    import { goto } from "$app/navigation";

    let categorias = [];
    let loading = true;
    let canCreate = false;
    let canEdit = false;
    let canDelete = false;

    // Check permissions on mount (simple check based on role for now, ideally strictly map backend permissions)
    $: {
        const user = $authStore.user;
        if (user) {
            // CORRECCIÓN: Permitir Operadores tmb (el backend valida permisos finos)
            const isAdmin = user.rol === "ADMINISTRADOR";
            const isOperator = user.rol === "OPERADOR";

            // Por ahora abrimos la UI para ambos roles.
            // Idealmente leeríamos user.permisos si existiera en el store.
            const hasAccess = isAdmin || isOperator;

            canCreate = hasAccess;
            canEdit = hasAccess;
            canDelete = hasAccess;
        }
    }

    const columns = [
        { key: "nombre", label: "Nombre" },
        { key: "descripcion", label: "Descripción" },
        {
            key: "activa",
            label: "Estado",
            formatter: (val) => (val !== false ? "Activa" : "Inactiva"),
        },
        {
            key: "acciones_extra",
            label: "Productos",
            // Usamos un botón con clase 'button' de Bulma y un evento onclick global o manejado por CrudTable si soportara.
            // PRO TIP: Como usamos @html, podemos poner un <button> pero el evento onclick svelte no funciona directo en string.
            // TRUCO: Usamos una clase especial y delegación de eventos, O simplemente un botón onclick="window.assignProducts(...)" (sucio pero efectivo para demo)
            // MEJOR: Despachamos un evento custom desde el elemento si pudiéramos.
            //
            // SOLUCIÓN ELEGANTE para Svelte + @html:
            // Modificamos CrudTable para que detecte clicks en elementos con data-action.
            //
            // PERO para no complicar CrudTable ahora, usaremos el evento 'edit' para todo O simplemente hacemos:
            formatter: (val, row) =>
                `<button class="button is-small is-info is-light" onclick="document.dispatchEvent(new CustomEvent('assign-products', { detail: ${row.id} }))">🔗 Asignar</button>`,
        },
    ];

    onMount(async () => {
        try {
            const response = await api.get("/api/saas/categorias");
            if (response.success) {
                categorias = response.categorias;
            }
        } catch (error) {
            console.error("Error cargando categorías:", error);
        } finally {
            loading = false;
        }

        // Listener para el botón "Asignar (Hack)"
        document.addEventListener("assign-products", (e) => {
            const categoryId = e.detail;
            const category = categorias.find((c) => c.id === categoryId);
            if (category) handleAssignProducts(category);
        });
    });

    function handleCreate() {
        goto("/categorias/nuevo");
    }

    function handleEdit(event) {
        const item = event.detail;
        goto(`/categorias/${item.id}`);
    }

    async function handleDelete(event) {
        const item = event.detail;
        if (
            confirm(`¿Estás seguro de eliminar la categoría "${item.nombre}"?`)
        ) {
            try {
                // Eliminar categoría
                const response = await api.delete(
                    `/api/saas/categorias/${item.id}`,
                );
                if (response.success) {
                    categorias = categorias.filter((c) => c.id !== item.id);
                    toast.add("Categoría eliminada", "success");
                } else {
                    toast.add("Error: " + response.message, "error");
                }
            } catch (e) {
                toast.add("Error al eliminar: " + e.message, "error");
            }
        }
    }
    import ProductAssignmentModal from "$lib/components/ProductAssignmentModal.svelte";

    let modalOpen = false;
    let selectedCategory = null;

    /**
     * Abre el modal de asignación masiva de productos
     * @param {Object} category - La categoría seleccionada
     */
    function handleAssignProducts(category) {
        selectedCategory = category;
        modalOpen = true;
    }

    /**
     * Cierra el modal y refresca (opcional, aunque no afecta la lista de categorías)
     */
    function handleModalClose() {
        modalOpen = false;
        selectedCategory = null;
    }
</script>

<CrudTable
    title="Gestión de Categorías"
    data={categorias}
    {columns}
    {loading}
    {canCreate}
    {canEdit}
    {canDelete}
    on:create={handleCreate}
    on:edit={handleEdit}
    on:delete={handleDelete}
>
    <!-- Slot personalizado para validaciones extra o acciones por fila si CrudTable lo soportara -->
    <!-- Como CrudTable es genérico, vamos a inyectar la acción via "customActions" si lo tuviéramos, 
         o agregamos una columna de acciones. 
         
         ESTRATEGIA: Modificar columns dinámicamente o asumiendo que CrudTable permite clicks en filas.
         Pero para hacerlo simple y visual, agregaremos un botón en la columna de acciones si modificamos CrudTable,
         o usaremos el evento 'edit' para todo y dentro del edit poner el botón.
         
         MEJOR OPCIÓN PROVISIONAL: Agregar una columna extra con un botón de "🔗 Productos"
    -->
</CrudTable>

<!-- Modal de Asignación -->
{#if modalOpen && selectedCategory}
    <ProductAssignmentModal
        categoria={selectedCategory}
        isOpen={modalOpen}
        on:close={handleModalClose}
        on:save={() => {
            handleModalClose();
            toast.add("Productos asignados correctamente", "success");
        }}
    />
{/if}
