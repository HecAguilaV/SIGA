<script>
    import { onMount } from "svelte";
    import { page } from "$app/stores";
    import { goto } from "$app/navigation";
    import { api } from "$lib/services/api";
    import { ArrowLeft, FloppyDisk } from "phosphor-svelte";
    import { toast } from "$lib/stores/toast";
    import { authStore } from "$lib/stores/authStore";

    let loading = false;
    let error = "";
    let isEditing = false;
    let id = "";

    let formData = {
        nombre: "",
        apellido: "",
        email: "",
        password: "",
        rol: "OPERADOR",
        activo: true,
    };

    const roles = ["ADMINISTRADOR", "OPERADOR", "CAJERO"];

    let permisosDisponiblesPorCategoria = {};
    let permisosUsuario = [];
    let categoriaActiva = "";
    let loadingPermisos = false;

    onMount(async () => {
        id = $page.params.id;
        if (id && id !== "nuevo") {
            isEditing = true;
            await cargarUsuario(id);
            await cargarPermisosDisponibles();
            await cargarPermisosUsuario();
        }
    });

    async function cargarUsuario(userId) {
        loading = true;
        try {
            const response = await api.get(`/api/saas/usuarios/${userId}`);
            if (response.success) {
                formData = {
                    nombre: response.usuario.nombre,
                    apellido: response.usuario.apellido || "",
                    email: response.usuario.email,
                    rol: response.usuario.rol,
                    activo: response.usuario.activo,
                    password: "", // No cargar password
                };
            } else {
                error = "No se pudo cargar el usuario";
            }
        } catch (err) {
            error = err.message;
        } finally {
            loading = false;
        }
    }

    async function cargarPermisosDisponibles() {
        try {
            const response = await api.get(
                "/api/saas/usuarios/permisos/disponibles",
            );
            if (response.success) {
                permisosDisponiblesPorCategoria = response.permisosPorCategoria;
                if (Object.keys(permisosDisponiblesPorCategoria).length > 0) {
                    categoriaActiva = Object.keys(
                        permisosDisponiblesPorCategoria,
                    )[0];
                }
            }
        } catch (e) {
            console.error("Error cargando permisos disponibles", e);
        }
    }

    async function cargarPermisosUsuario() {
        loadingPermisos = true;
        try {
            const response = await api.get(`/api/saas/usuarios/${id}/permisos`);
            if (response.success) {
                permisosUsuario = response.permisos || [];
            }
        } catch (e) {
            console.error("Error cargando permisos usuario", e);
        } finally {
            loadingPermisos = false;
        }
    }

    async function togglePermiso(codigo, checked) {
        // Optimistic update
        const originalState = [...permisosUsuario];
        if (checked) {
            permisosUsuario = [...permisosUsuario, codigo];
        } else {
            permisosUsuario = permisosUsuario.filter((p) => p !== codigo);
        }

        try {
            let response;
            if (checked) {
                response = await api.post(`/api/saas/usuarios/${id}/permisos`, {
                    codigoPermiso: codigo,
                });
            } else {
                response = await api.delete(
                    `/api/saas/usuarios/${id}/permisos/${codigo}`,
                );
            }

            if (!response.success && response.message) {
                toast.add(response.message, "error");
                permisosUsuario = originalState;
            } else if (response.success) {
                // Sync with server state to be sure (some roles imply permissions)
                permisosUsuario = response.permisos || permisosUsuario;
                if (checked) toast.add("Permiso asignado", "success", 2000);
            }
        } catch (e) {
            console.error(e);
            permisosUsuario = originalState; // Revert
            toast.add("No se pudo actualizar el permiso.", "error");
        }
    }

    // ... handleSubmit ...
    async function vincularEmpresa() {
        if (
            !confirm(
                "Esto vinculará este usuario a TU empresa actual (basado en tu sesión de Admin). ¿Continuar?",
            )
        )
            return;

        loading = true;
        try {
            // El usuario actual (Admin) debe tener un email válido
            const adminEmail = $authStore.user?.email;
            if (!adminEmail) {
                toast.add(
                    "No se puede identificar tu usuario administrador.",
                    "error",
                );
                return;
            }

            const response = await api.put(`/api/saas/usuarios/${id}/empresa`, {
                emailComercial: adminEmail,
            });

            if (response.success) {
                toast.add(
                    "Usuario vinculado a la empresa correctamente",
                    "success",
                );
                await cargarUsuario(id);
            } else {
                toast.add(response.message || "Error al vincular", "error");
            }
        } catch (e) {
            console.error(e);
            toast.add("Error de conexión", "error");
        } finally {
            loading = false;
        }
    }

    async function handleSubmit() {
        loading = true;
        error = "";

        if (!formData.nombre.trim() || !formData.email.trim()) {
            error = "Nombre y Email son obligatorios";
            loading = false;
            return;
        }

        if (!isEditing && !formData.password) {
            error = "La contraseña es obligatoria para usuarios nuevos";
            loading = false;
            return;
        }

        try {
            let response;
            if (isEditing) {
                const payload = {
                    nombre: formData.nombre,
                    apellido: formData.apellido,
                    rol: formData.rol,
                    activo: formData.activo,
                };
                response = await api.put(`/api/saas/usuarios/${id}`, payload);
            } else {
                response = await api.post("/api/saas/usuarios", formData);
            }

            if (response.success) {
                goto("/usuarios");
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
                on:click={() => goto("/usuarios")}
            >
                <span class="icon">
                    <ArrowLeft />
                </span>
                <span>Volver</span>
            </button>
        </div>
    </div>

    <h1 class="title is-4 mb-5">
        {isEditing ? "Editar Usuario" : "Nuevo Usuario"}
    </h1>

    {#if error}
        <div class="notification is-danger is-light">
            <button class="delete" on:click={() => (error = "")}></button>
            {error}
        </div>
    {/if}

    <form on:submit|preventDefault={handleSubmit}>
        <div class="columns">
            <div class="column is-6">
                <div class="field">
                    <label class="label"
                        >Nombre <span class="has-text-danger">*</span></label
                    >
                    <div class="control">
                        <input
                            class="input"
                            type="text"
                            bind:value={formData.nombre}
                            placeholder="Juan"
                            disabled={loading}
                        />
                    </div>
                </div>

                <div class="field">
                    <label class="label">Apellido</label>
                    <div class="control">
                        <input
                            class="input"
                            type="text"
                            bind:value={formData.apellido}
                            placeholder="Pérez"
                            disabled={loading}
                        />
                    </div>
                </div>

                <div class="field">
                    <label class="label"
                        >Email <span class="has-text-danger">*</span></label
                    >
                    <div class="control">
                        <input
                            class="input"
                            type="email"
                            bind:value={formData.email}
                            placeholder="juan@ejemplo.com"
                            disabled={loading || isEditing}
                        />
                    </div>
                    {#if isEditing}
                        <p class="help">El email no se puede cambiar aquí.</p>
                    {/if}
                </div>
            </div>

            <div class="column is-6">
                <div class="field">
                    <label class="label"
                        >Rol <span class="has-text-danger">*</span></label
                    >
                    <div class="control">
                        <div class="select is-fullwidth">
                            <select
                                bind:value={formData.rol}
                                disabled={loading}
                            >
                                {#each roles as rol}
                                    <option value={rol}>{rol}</option>
                                {/each}
                            </select>
                        </div>
                    </div>
                </div>

                {#if !isEditing}
                    <div class="field">
                        <label class="label"
                            >Contraseña <span class="has-text-danger">*</span
                            ></label
                        >
                        <div class="control">
                            <input
                                class="input"
                                type="password"
                                bind:value={formData.password}
                                placeholder="******"
                                disabled={loading}
                            />
                        </div>
                    </div>
                {:else}
                    <div class="field">
                        <label class="label">Estado</label>
                        <div class="control">
                            <label class="checkbox">
                                <input
                                    type="checkbox"
                                    bind:checked={formData.activo}
                                    disabled={loading}
                                />
                                Usuario Activo
                            </label>
                        </div>
                    </div>
                {/if}
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
                    on:click={() => goto("/usuarios")}
                    disabled={loading}
                >
                    Cancelar
                </button>
            </div>

            {#if isEditing && formData.rol !== "ADMINISTRADOR"}
                <div class="control ml-auto">
                    <button
                        type="button"
                        class="button is-warning is-light"
                        on:click={vincularEmpresa}
                        title="Usar si el usuario tiene error 402 (Falta de pago)"
                    >
                        🔗 Reparar Acceso Empresa
                    </button>
                </div>
            {/if}
        </div>
    </form>
</div>

{#if isEditing}
    <div class="box mt-5">
        <h2 class="title is-5 mb-4">Permisos Granulares</h2>
        <p class="subtitle is-6 has-text-grey mb-4">
            Gestiona accesos específicos. Los permisos heredados del rol <span
                class="tag is-light is-info">Rol Base</span
            > no se pueden quitar.
        </p>

        {#if loadingPermisos}
            <progress class="progress is-small is-primary" max="100"
                >15%</progress
            >
        {:else}
            <div class="tabs is-boxed is-small">
                <ul>
                    {#each Object.keys(permisosDisponiblesPorCategoria) as categoria}
                        <li class:is-active={categoriaActiva === categoria}>
                            <!-- svelte-ignore a11y-missing-attribute -->
                            <a
                                on:click|preventDefault={() =>
                                    (categoriaActiva = categoria)}
                            >
                                {categoria}
                            </a>
                        </li>
                    {/each}
                </ul>
            </div>

            {#if categoriaActiva}
                <div class="columns is-multiline">
                    {#each permisosDisponiblesPorCategoria[categoriaActiva] as permiso}
                        {@const tienePermiso = permisosUsuario.includes(
                            permiso.codigo,
                        )}
                        <!-- Simular lógica de rol: Si el usuario es ADMIN, todo está activo y bloqueado. 
                         Si no, verificar si viene del rol base (no tenemos esa data directa sin llamar al endpoint, 
                         así que asumiremos que si está en el rol, el delete fallará o el backend nos dirá. 
                         Mejor: El backend nos dice qué permisos tiene. Si intentamos borrar y falla, es de rol. 
                         Visualmente: Si tiene permiso, check. -->

                        <div class="column is-6-tablet is-4-desktop">
                            <div
                                class="card p-3 permission-card"
                                class:is-active={tienePermiso}
                            >
                                <div class="field">
                                    <input
                                        id="perm_{permiso.codigo}"
                                        type="checkbox"
                                        class="switch is-rounded is-small is-info"
                                        checked={tienePermiso}
                                        on:change={(e) =>
                                            togglePermiso(
                                                permiso.codigo,
                                                e.target.checked,
                                            )}
                                        disabled={loading ||
                                            formData.rol === "ADMINISTRADOR"}
                                    />
                                    <label
                                        for="perm_{permiso.codigo}"
                                        class="is-size-7 has-text-weight-bold"
                                    >
                                        {permiso.nombre}
                                    </label>
                                </div>
                                <p class="help is-size-7 mt-1">
                                    {permiso.descripcion || "Sin descripción"}
                                </p>
                            </div>
                        </div>
                    {/each}
                </div>
                {#if formData.rol === "ADMINISTRADOR"}
                    <div class="notification is-info is-light mt-3 is-size-7">
                        ℹ️ Los administradores tienen acceso total por defecto.
                    </div>
                {/if}
            {/if}
        {/if}
    </div>
{/if}

<style>
    .permission-card {
        border: 1px solid #f0f0f0;
        transition: all 0.2s;
    }
    .permission-card.is-active {
        border-color: #3e8ed0;
        background-color: #fca; /* Wait, using bulma variables is safer */
        background-color: var(--bulma-info-light);
    }
    .permission-card:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    }
</style>
