<script>
    import { onMount } from "svelte";
    import { page } from "$app/stores";
    import { goto } from "$app/navigation";
    import { authService } from "$lib/services/auth";

    let message = "Validando credenciales...";
    let error = false;

    onMount(async () => {
        const token = $page.url.searchParams.get("token");

        if (!token) {
            error = true;
            message = "Token de acceso no proporcionado";
            setTimeout(() => goto("/login"), 2000);
            return;
        }

        const result = await authService.validateSsoToken(token);

        if (result.success) {
            message = "Acceso concedido. Redirigiendo...";
            goto("/");
        } else {
            error = true;
            message =
                "Token inválido o expirado. Por favor inicie sesión nuevamente.";
            setTimeout(() => goto("/login"), 3000);
        }
    });
</script>

<div class="sso-container">
    <div class="box sso-box has-text-centered">
        {#if error}
            <span class="icon is-large has-text-danger mb-3">
                <i class="fas fa-exclamation-triangle fa-2x"></i>
            </span>
        {:else}
            <span class="icon is-large has-text-info mb-3">
                <i class="fas fa-circle-notch fa-spin fa-2x"></i>
            </span>
        {/if}
        <h2 class="title is-5">{message}</h2>
    </div>
</div>

<style>
    .sso-container {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 100vh;
        background-color: var(--color-background);
    }

    .sso-box {
        padding: 3rem;
        min-width: 300px;
    }
</style>
