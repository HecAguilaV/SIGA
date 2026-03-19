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
        direccion: "",
        ciudad: "",
    };

    onMount(async () => {
        id = $page.params.id;
        if (id && id !== "nuevo") {
            isEditing = true;
            await cargarLocal(id);
        }
    });

    async function cargarLocal(localId) {
        loading = true;
        try {
            const response = await api.get(`/api/saas/locales/${localId}`);
            if (response.success) {
                formData = {
                    nombre: response.local.nombre,
                    direccion: response.local.direccion || "",
                    ciudad: response.local.ciudad || "",
                };
            } else {
                error = "No se pudo cargar el local";
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
                // Backend no documenta PUT locales explícitamente en el resumen, pero lo asumimos por estándar REST
                // Si falla, verificaremos la documentación nuevamente.
                // Documentación dice: PUT /api/saas/locales/{id} - Actualizar local (solo ADMIN)
                response = await api.put(`/api/saas/locales/${id}`, formData);
            } else {
                // Documentación dice: POST /api/saas/locales - Crear local (solo ADMIN)
                response = await api.post("/api/saas/locales", formData);
            }

            if (response.success) {
                goto("/locales");
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
                on:click={() => goto("/locales")}
            >
                <span class="icon">
                    <ArrowLeft />
                </span>
                <span>Volver</span>
            </button>
        </div>
    </div>

    <h1 class="title is-4 mb-5">
        {isEditing ? "Editar Local" : "Nuevo Local"}
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
                    placeholder="Ej: Bodega Central"
                    disabled={loading}
                />
            </div>
        </div>

        <div class="field">
            <label class="label">Dirección</label>
            <div class="control">
                <input
                    class="input"
                    type="text"
                    bind:value={formData.direccion}
                    placeholder="Ej: Av. Principal 123"
                    disabled={loading}
                />
            </div>
        </div>

        <div class="field">
            <label class="label">Ciudad</label>
            <div class="control">
                <input
                    class="input"
                    type="text"
                    bind:value={formData.ciudad}
                    placeholder="Ej: Santiago"
                    disabled={loading}
                />
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
                    on:click={() => goto("/locales")}
                    disabled={loading}
                >
                    Cancelar
                </button>
            </div>
        </div>
    </form>
</div>
