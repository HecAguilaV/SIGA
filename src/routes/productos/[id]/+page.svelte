<script>
    import { onMount } from "svelte";
    import { page } from "$app/stores";
    import { goto } from "$app/navigation";
    import { api } from "$lib/services/api";
    import { datosNegocio } from "$lib/stores/datosNegocio";
    import { ArrowLeft, FloppyDisk, Storefront } from "phosphor-svelte";

    let loading = false;
    let error = "";
    let isEditing = false;
    let id = "";

    let formData = {
        nombre: "",
        descripcion: "",
        codigoBarras: "",
        precioUnitario: "",
        categoriaId: "",
    };

    let categorias = [];

    // Gestión de Stock
    let stockPorLocal = []; // { localId, nombreLocal, cantidad, nuevoStock }
    let updatingStock = false;

    // Suscribirse a datos de negocio para obtener locales y stock
    $: locales = $datosNegocio.locales || [];
    $: stockGlobal = $datosNegocio.stock || [];

    onMount(async () => {
        id = $page.params.id;

        // Asegurar que datos de negocio estén cargados para el stock
        if ($datosNegocio.locales.length === 0) {
            await datosNegocio.cargarDatos();
        }

        await cargarCategorias();
        if (id && id !== "nuevo") {
            isEditing = true;
            await cargarProducto(id);
            mapearStock();
        }
    });

    $: if (isEditing && locales.length > 0) {
        mapearStock();
    }

    function mapearStock() {
        stockPorLocal = locales.map((local) => {
            const stockEntry = stockGlobal.find(
                (s) => s.producto_id == id && s.local_id == local.id,
            );
            return {
                localId: local.id,
                nombreLocal: local.nombre,
                cantidad: stockEntry ? stockEntry.cantidad : 0,
                nuevoStock: stockEntry ? stockEntry.cantidad : 0,
            };
        });
    }

    async function cargarCategorias() {
        try {
            const response = await api.get("/api/saas/categorias");
            if (response.success) {
                categorias = response.categorias;
                // Si hay categorías y es nuevo producto, preseleccionar la primera
                if (!isEditing && categorias.length > 0) {
                    formData.categoriaId = categorias[0].id;
                }
            }
        } catch (e) {
            console.error("Error cargando categorías para el select:", e);
        }
    }

    async function cargarProducto(proId) {
        loading = true;
        try {
            const response = await api.get(`/api/saas/productos/${proId}`);
            if (response.success) {
                formData = {
                    nombre: response.producto.nombre,
                    descripcion: response.producto.descripcion || "",
                    codigoBarras: response.producto.codigoBarras || "",
                    precioUnitario: response.producto.precioUnitario
                        ? Math.round(response.producto.precioUnitario)
                        : "",
                    categoriaId: response.producto.categoriaId || "",
                };
            } else {
                error = "No se pudo cargar el producto";
            }
        } catch (err) {
            error = err.message;
        } finally {
            loading = false;
        }
    }

    async function handleSubmit() {
        loading = true;
        error = "";

        if (!formData.nombre.trim()) {
            error = "El nombre es obligatorio";
            loading = false;
            return;
        }

        try {
            let response;
            if (isEditing) {
                response = await api.put(`/api/saas/productos/${id}`, formData);
            } else {
                response = await api.post("/api/saas/productos", formData);
            }

            if (response.success) {
                // Si es nuevo, redirigimos. Si es edición, nos quedamos para editar stock.
                if (!isEditing) {
                    goto("/productos");
                } else {
                    // Feedback visual
                    alert(
                        "Producto actualizado. Ahora puedes gestionar el stock.",
                    );
                }
            } else {
                error = response.message || "Error al guardar";
            }
        } catch (err) {
            console.error(err);
            error = err.message || "Error de conexión";
        } finally {
            loading = false;
        }
    }

    async function actualizarStock(item) {
        updatingStock = true;
        try {
            const payload = {
                productoId: parseInt(id),
                localId: item.localId,
                cantidad: parseInt(item.nuevoStock),
                cantidadMinima: 5, // Default
            };

            const response = await api.post("/api/saas/stock", payload);

            if (response.success) {
                // Actualizar localmente el store o recargar
                await datosNegocio.cargarDatos(); // Recargar todo para sincronizar
                mapearStock();
                alert(`Stock actualizado para ${item.nombreLocal}`);
            } else {
                alert(`Error: ${response.message}`);
            }
        } catch (e) {
            console.error(e);
            alert("Error al actualizar stock");
        } finally {
            updatingStock = false;
        }
    }
</script>

<div class="box">
    <div class="level">
        <div class="level-left">
            <button
                class="button is-ghost pl-0"
                on:click={() => goto("/productos")}
            >
                <span class="icon">
                    <ArrowLeft />
                </span>
                <span>Volver</span>
            </button>
        </div>
    </div>

    <h1 class="title is-4 mb-5">
        {isEditing ? "Editar Producto" : "Nuevo Producto"}
    </h1>

    {#if error}
        <div class="notification is-danger is-light">
            <button class="delete" on:click={() => (error = "")}></button>
            {error}
        </div>
    {/if}

    <form on:submit|preventDefault={handleSubmit}>
        <div class="columns">
            <div class="column is-8">
                <div class="field">
                    <label class="label"
                        >Nombre <span class="has-text-danger">*</span></label
                    >
                    <div class="control">
                        <input
                            class="input"
                            type="text"
                            bind:value={formData.nombre}
                            placeholder="Ej: Bebida Cola 350ml"
                            disabled={loading}
                        />
                    </div>
                </div>

                <div class="field">
                    <label class="label">Descripción</label>
                    <div class="control">
                        <textarea
                            class="textarea"
                            bind:value={formData.descripcion}
                            placeholder="Descripción opcional"
                            disabled={loading}
                        ></textarea>
                    </div>
                </div>
            </div>
            <div class="column is-4">
                <div class="field">
                    <label class="label">Categoría</label>
                    <div class="control">
                        <div class="select is-fullwidth">
                            <select
                                bind:value={formData.categoriaId}
                                disabled={loading}
                            >
                                <option value="" disabled>Seleccione...</option>
                                {#each categorias as cat}
                                    <option value={cat.id}>{cat.nombre}</option>
                                {/each}
                            </select>
                        </div>
                    </div>
                    {#if categorias.length === 0}
                        <p class="help is-warning">
                            Debes crear categorías primero
                        </p>
                    {/if}
                </div>

                <div class="field">
                    <label class="label">Código Barras / SKU</label>
                    <div class="control">
                        <input
                            class="input"
                            type="text"
                            bind:value={formData.codigoBarras}
                            placeholder="Ej: 780123456"
                            disabled={loading}
                        />
                    </div>
                </div>

                <div class="field">
                    <label class="label">Precio Unitario</label>
                    <div class="control has-icons-left">
                        <input
                            class="input"
                            type="number"
                            bind:value={formData.precioUnitario}
                            placeholder="Ej: 1500"
                            disabled={loading}
                        />
                        <span class="icon is-small is-left"> $ </span>
                    </div>
                </div>
            </div>
        </div>

        <div class="field is-grouped mt-5">
            <div class="control">
                <button
                    class="button is-primary"
                    class:is-loading={loading}
                    disabled={loading}
                >
                    <span class="icon">
                        <FloppyDisk />
                    </span>
                    <span>Guardar</span>
                </button>
            </div>
            <div class="control">
                <button
                    type="button"
                    class="button is-light"
                    on:click={() => goto("/productos")}
                    disabled={loading}
                >
                    Cancelar
                </button>
            </div>
        </div>
    </form>
</div>

{#if isEditing}
    <div class="box mt-5">
        <h2 class="title is-5 mb-4 is-flex is-align-items-center">
            <span class="icon mr-2 has-text-info">
                <Storefront />
            </span>
            Gestión de Stock por Sucursal
        </h2>

        {#if stockPorLocal.length === 0}
            <div class="notification is-warning is-light">
                No hay locales configurados o no se pudo cargar el stock.
            </div>
        {:else}
            <div class="table-container">
                <table class="table is-fullwidth is-striped">
                    <thead>
                        <tr>
                            <th>Sucursal</th>
                            <th class="has-text-right">Stock Actual</th>
                            <th class="has-text-centered" style="width: 150px"
                                >Nuevo Stock</th
                            >
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        {#each stockPorLocal as item}
                            <tr>
                                <td class="is-vcentered has-text-weight-medium"
                                    >{item.nombreLocal}</td
                                >
                                <td class="is-vcentered has-text-right">
                                    <span class="tag is-light is-medium">
                                        {item.cantidad}
                                    </span>
                                </td>
                                <td>
                                    <input
                                        class="input is-small has-text-right"
                                        type="number"
                                        min="0"
                                        bind:value={item.nuevoStock}
                                        disabled={updatingStock}
                                    />
                                </td>
                                <td>
                                    <button
                                        class="button is-small is-info is-light"
                                        on:click={() => actualizarStock(item)}
                                        class:is-loading={updatingStock}
                                        disabled={updatingStock ||
                                            item.cantidad === item.nuevoStock}
                                    >
                                        Actualizar
                                    </button>
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>
        {/if}
    </div>
{/if}
