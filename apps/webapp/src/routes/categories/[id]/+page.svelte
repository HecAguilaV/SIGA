<script>
    import { onMount } from "svelte";
    import { page } from "$app/stores";
    import { goto } from "$app/navigation";
    import { api } from "$lib/services/api";
    import { ArrowLeft, FloppyDisk } from "phosphor-svelte";

    let loading = false;
    let error = "";
    let isEditing = false;
    let id = "";

    let formData = {
        nombre: "",
        descripcion: "",
    };

    onMount(async () => {
        id = $page.params.id;
        if (id && id !== "nuevo") {
            isEditing = true;
            await cargarCategoria(id);
        }
    });

    async function cargarCategoria(catId) {
        loading = true;
        try {
            const response = await api.get(`/api/saas/categorias/${catId}`);
            if (response.success) {
                formData = {
                    nombre: response.categoria.nombre,
                    descripcion: response.categoria.descripcion || "",
                };
            } else {
                error = "No se pudo cargar la categoría";
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
                response = await api.put(
                    `/api/saas/categorias/${id}`,
                    formData,
                );
            } else {
                response = await api.post("/api/saas/categorias", formData);
            }

            if (response.success) {
                goto("/categorias");
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
</script>

<div class="box">
    <div class="level">
        <div class="level-left">
            <button
                class="button is-ghost pl-0"
                on:click={() => goto("/categorias")}
            >
                <span class="icon">
                    <ArrowLeft />
                </span>
                <span>Volver</span>
            </button>
        </div>
    </div>

    <h1 class="title is-4 mb-5">
        {isEditing ? "Editar Categoría" : "Nueva Categoría"}
    </h1>

    {#if error}
        <div class="notification is-danger is-light">
            <button class="delete" on:click={() => (error = "")}></button>
            {error}
        </div>
    {/if}

    <form on:submit|preventDefault={handleSubmit}>
        <div class="field">
            <label class="label"
                >Nombre <span class="has-text-danger">*</span></label
            >
            <div class="control">
                <input
                    class="input"
                    type="text"
                    bind:value={formData.nombre}
                    placeholder="Ej: Electrónica"
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
                    on:click={() => goto("/categorias")}
                    disabled={loading}
                >
                    Cancelar
                </button>
            </div>
        </div>
    </form>
</div>
