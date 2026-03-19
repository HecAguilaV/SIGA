<script>
    import { authService } from "$lib/services/auth";
    import { goto } from "$app/navigation";
    import { Eye, EyeSlash } from "phosphor-svelte";

    let email = "";
    let password = "";
    let showPassword = false;
    let loading = false;
    let error = "";

    const handleLogin = async () => {
        loading = true;
        error = "";

        const result = await authService.login(email, password);

        if (result.success) {
            goto("/");
        } else {
            error = result.message || "Credenciales inválidas";
        }
        loading = false;
    };
</script>

<div class="login-container">
    <div class="box login-box">
        <div class="has-text-centered mb-5">
            <h1 class="title is-3 heading-gradient">SIGA</h1>
            <p class="subtitle is-6">
                Sistema Inteligente de Gestión de Activos
            </p>
        </div>

        <form on:submit|preventDefault={handleLogin}>
            <div class="field">
                <label class="label" style="color: #333;">Email</label>
                <div class="control">
                    <input
                        class="input"
                        type="email"
                        bind:value={email}
                        placeholder="usuario@siga.com"
                        required
                        style="background-color: #fff; color: #333; border-color: #dbdbdb;"
                    />
                </div>
            </div>

            <div class="field">
                <label class="label" style="color: #333;">Contraseña</label>
                <div class="control has-icons-right">
                    <input
                        class="input"
                        type={showPassword ? "text" : "password"}
                        bind:value={password}
                        placeholder="••••••••"
                        required
                        style="background-color: #fff; color: #333; border-color: #dbdbdb;"
                    />
                    <!-- Botón "Ojito" posicionado a la derecha -->
                    <button
                        type="button"
                        class="icon is-small is-right"
                        style="pointer-events: auto; border: none; background: transparent; cursor: pointer; height: 100%; right: 5px;"
                        on:click={() => (showPassword = !showPassword)}
                        tabindex="-1"
                    >
                        <svelte:component
                            this={showPassword ? EyeSlash : Eye}
                            size={20}
                            color="#4a4a4a"
                        />
                    </button>
                </div>
            </div>

            {#if error}
                <div class="notification is-danger is-light">
                    {error}
                </div>
            {/if}

            <div class="field mt-5">
                <div class="control">
                    <button
                        class="button is-primary is-fullwidth {loading
                            ? 'is-loading'
                            : ''}"
                        type="submit"
                    >
                        Ingresar
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<style>
    .login-container {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 100vh;
        background: radial-gradient(
            circle at 10% 20%,
            rgb(0, 180, 216) 0%,
            rgb(3, 4, 94) 90%
        );
        padding: 1rem;
    }

    .login-box {
        width: 100%;
        max-width: 400px;
        padding: 2.5rem;
        border-radius: 12px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    }
</style>
