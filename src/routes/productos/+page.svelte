<script>
    import { onMount, onDestroy } from "svelte";
    import { api } from "$lib/services/api";
    import CrudTable from "$lib/components/CrudTable.svelte";
    import { goto } from "$app/navigation";
    import { datosNegocio } from "$lib/stores/datosNegocio"; // Reuse the store for stock

    let productos = [];
    let loading = true;
    let stockData = [];

    // Subscribe to stock AND products data from store (Single Source of Truth)
    const unsubscribe = datosNegocio.subscribe((store) => {
        stockData = store.stock || [];
        // Si ya hay productos en el store, los usamos (Cache)
        if (store.productos && store.productos.length > 0) {
            productos = store.productos;
            loading = false;
        }
    });

    // Helper to get stock for a product (sum of all locals or total)
    const getStockTotal = (prodId) => {
        return stockData
            .filter((s) => s.producto_id === prodId)
            .reduce((acc, curr) => acc + curr.cantidad, 0);
    };

    const columns = [
        { key: "nombre", label: "Nombre" },
        {
            key: "stock_total",
            label: "Stock Total",
            // Formateador: Semáforo de Stock
            formatter: (_, row) => {
                const total = getStockTotal(row.id);
                let colorClass = "is-success"; // Verde por defecto
                let icon = "";

                if (total <= 5) {
                    colorClass = "is-danger"; // Rojo (Crítico)
                    icon = "🔥";
                } else if (total <= 10) {
                    colorClass = "is-warning"; // Amarillo (Bajo)
                    icon = "⚠️";
                }

                // Retornamos un "Tag" de Bulma (etiqueta)
                return `<span class="tag ${colorClass} is-light">
                            ${icon} <b>${total} u.</b>
                        </span>`;
            },
        },
        { key: "codigoBarras", label: "SKU / Código" },
        {
            key: "precioUnitario",
            label: "Precio",
            // Formateador: Convierte a CLP (ej: $ 1.500)
            formatter: (val) =>
                val ? `$ ${parseInt(val).toLocaleString("es-CL")}` : "-",
        },
        {
            key: "activo",
            label: "Estado",
            formatter: (val) => (val ? "Activo" : "Inactivo"),
        },
    ];

    onMount(async () => {
        try {
            // Cargar datos globales (Productos + Stock + Locales) en paralelo
            // Si ya están cargados, el store notificará inmediatamente
            if ($datosNegocio.productos.length === 0) {
                await datosNegocio.cargarDatos();
            } else {
                loading = false; // Ya tenemos datos
                // Opcional: recargar en segundo plano para frescura
                datosNegocio.cargarDatos();
            }
        } catch (error) {
            console.error("Error cargando datos:", error);
            loading = false;
        }
    });
    onDestroy(() => {
        unsubscribe();
    });

    function handleCreate() {
        goto("/productos/nuevo");
    }

    /**
     * Maneja la navegación a la pantalla de edición
     * @param {CustomEvent} event - Evento disparado por la tabla
     */
    function handleEdit(event) {
        const item = event.detail;
        goto(`/productos/${item.id}`);
    }

    /**
     * Elimina un producto tras confirmación
     * @param {CustomEvent} event - Evento con el item a eliminar
     */
    async function handleDelete(event) {
        const item = event.detail;
        if (
            confirm(`¿Estás seguro de eliminar el producto "${item.nombre}"?`)
        ) {
            try {
                // CORRECCIÓN: Usar api.delete en lugar de api.del
                const response = await api.delete(
                    `/api/saas/productos/${item.id}`,
                );
                if (response.success) {
                    productos = productos.filter((p) => p.id !== item.id);
                } else {
                    alert("Error: " + response.message);
                }
            } catch (e) {
                alert("Error al eliminar: " + e.message);
            }
        }
    }
</script>

<CrudTable
    title="Gestión de Productos"
    data={productos}
    {columns}
    {loading}
    on:create={handleCreate}
    on:edit={handleEdit}
    on:delete={handleDelete}
/>
