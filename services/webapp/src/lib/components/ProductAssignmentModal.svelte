<script>
    import { createEventDispatcher, onMount } from "svelte";
    import { api } from "$lib/services/api";

    export let categoria; // La categoría a la que vamos a asignar productos
    export let isOpen = false;

    const dispatch = createEventDispatcher();
    let productos = [];
    let seleccionados = new Set();
    let loading = true;
    let saving = false;

    // Cargar todos los productos al abrir el modal
    onMount(async () => {
        try {
            const res = await api.get("/api/saas/productos");
            if (res.success) {
                productos = res.productos;
                // Pre-seleccionar los que ya pertenecen a esta categoría
                const productosDeLaCategoria = productos
                    .filter((p) => p.categoriaId === categoria.id)
                    .map((p) => p.id);
                seleccionados = new Set(productosDeLaCategoria);
            }
        } catch (error) {
            console.error("Error cargando productos:", error);
            alert("Error al cargar lista de productos");
        } finally {
            loading = false;
        }
    });

    function toggleSeleccion(id) {
        if (seleccionados.has(id)) {
            seleccionados.delete(id);
        } else {
            seleccionados.add(id);
        }
        seleccionados = seleccionados; // Reactividad
    }

    async function guardar() {
        saving = true;
        try {
            // Identificar cambios
            const promesas = productos.map(async (p) => {
                const estabaSeleccionado = p.categoriaId === categoria.id;
                const estaAhora = seleccionados.has(p.id);

                if (estabaSeleccionado && !estaAhora) {
                    // Desasignar (categoriaId = null)
                    await api.put(`/api/saas/productos/${p.id}`, {
                        ...p,
                        categoriaId: null,
                    });
                } else if (!estabaSeleccionado && estaAhora) {
                    // Asignar (categoriaId = categoria.id)
                    await api.put(`/api/saas/productos/${p.id}`, {
                        ...p,
                        categoriaId: categoria.id,
                    });
                }
            });

            await Promise.all(promesas);
            dispatch("save");
            close();
        } catch (error) {
            console.error("Error guardando asignaciones:", error);
            alert("Error al guardar cambios. Revisa la consola.");
        } finally {
            saving = false;
        }
    }

    function close() {
        dispatch("close");
    }
</script>

{#if isOpen}
    <div
        class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
        <div
            class="bg-gray-800 rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] flex flex-col"
        >
            <div class="p-6 border-b border-gray-700">
                <h2 class="text-xl font-bold text-white">
                    Asignar Productos a "{categoria.nombre}"
                </h2>
                <p class="text-gray-400 text-sm mt-1">
                    Selecciona los productos que pertenecen a esta categoría.
                </p>
            </div>

            <div class="flex-1 overflow-y-auto p-6">
                {#if loading}
                    <div class="text-center py-8">
                        <div
                            class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500 mx-auto"
                        ></div>
                        <p class="text-gray-400 mt-2">Cargando productos...</p>
                    </div>
                {:else if productos.length === 0}
                    <p class="text-center text-gray-500 py-8">
                        No hay productos registrados aún.
                    </p>
                {:else}
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                        {#each productos as p}
                            <label
                                class="flex items-center p-3 rounded-lg border border-gray-700 bg-gray-900 hover:bg-gray-700 cursor-pointer transition-colors"
                            >
                                <input
                                    type="checkbox"
                                    class="form-checkbox h-5 w-5 text-indigo-600 rounded border-gray-600 bg-gray-800 focus:ring-indigo-500"
                                    checked={seleccionados.has(p.id)}
                                    on:change={() => toggleSeleccion(p.id)}
                                />
                                <div class="ml-3">
                                    <span
                                        class="block text-sm font-medium text-white"
                                        >{p.nombre}</span
                                    >
                                    <span class="block text-xs text-gray-400">
                                        {#if p.codigoBarras}SKU: {p.codigoBarras}{/if}
                                        {#if p.categoriaId && p.categoriaId !== categoria.id}
                                            (Actual: Otra Categ.)
                                        {/if}
                                    </span>
                                </div>
                            </label>
                        {/each}
                    </div>
                {/if}
            </div>

            <div
                class="p-6 border-t border-gray-700 flex justify-end space-x-3 bg-gray-800 rounded-b-lg"
            >
                <button
                    on:click={close}
                    class="px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded-lg transition-colors"
                >
                    Cancelar
                </button>
                <button
                    on:click={guardar}
                    disabled={saving}
                    class="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg transition-colors flex items-center"
                >
                    {#if saving}
                        <svg
                            class="animate-spin -ml-1 mr-2 h-4 w-4 text-white"
                            fill="none"
                            viewBox="0 0 24 24"
                        >
                            <circle
                                class="opacity-25"
                                cx="12"
                                cy="12"
                                r="10"
                                stroke="currentColor"
                                stroke-width="4"
                            ></circle>
                            <path
                                class="opacity-75"
                                fill="currentColor"
                                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                            ></path>
                        </svg>
                        Guardando...
                    {:else}
                        Guardar Cambios
                    {/if}
                </button>
            </div>
        </div>
    </div>
{/if}
