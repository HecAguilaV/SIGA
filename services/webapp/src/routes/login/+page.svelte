<script>
    import { authService } from "$lib/services/auth";
    import { goto } from "$app/navigation";
    import { Eye, EyeSlash } from "phosphor-svelte";
    import { fade, fly } from "svelte/transition";

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

<div class="login-wrapper" in:fade={{ duration: 600 }}>
    <div class="login-container glass-card" in:fly={{ y: 20, duration: 800 }}>
        <div class="has-text-centered mb-6">
            <div class="login-logo mb-4">
                <img src="/S.png" alt="SIGA" />
            </div>
            <h1 class="main-title">Iniciar Sesión</h1>
            <p class="sub-title">Bienvenido al sistema SIGA MNGMNT</p>
        </div>

        <form on:submit|preventDefault={handleLogin}>
            <div class="field mb-4">
                <label class="label-premium">Email Corporativo</label>
                <div class="control">
                    <input
                        class="input-premium"
                        type="email"
                        bind:value={email}
                        placeholder="usuario@siga.cl"
                        required
                    />
                </div>
            </div>

            <div class="field mb-5">
                <label class="label-premium">Contraseña</label>
                <div class="control has-icons-right">
                    <input
                        class="input-premium"
                        type={showPassword ? "text" : "password"}
                        bind:value={password}
                        placeholder="••••••••"
                        required
                    />
                    <button
                        type="button"
                        class="ojo-btn"
                        on:click={() => (showPassword = !showPassword)}
                        tabindex="-1"
                    >
                        <svelte:component
                            this={showPassword ? EyeSlash : Eye}
                            size={18}
                        />
                    </button>
                </div>
            </div>

            {#if error}
                <div class="notification-premium mb-4" in:fade>
                    {error}
                </div>
            {/if}

            <div class="control">
                <button
                    class="btn-primary-glow {loading ? 'is-loading' : ''}"
                    type="submit"
                >
                    Entrar al Sistema
                </button>
            </div>
            
            <div class="has-text-centered mt-5">
                <a href="/sso" class="sso-link">Acceder vía Google SSO</a>
            </div>
        </form>
    </div>
</div>

<style>
    .login-wrapper {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 100vh;
        background: var(--canvas);
        position: relative;
        overflow: hidden;
    }

    /* Efecto de luz ambiental de fondo */
    .login-wrapper::before {
        content: '';
        position: absolute;
        width: 500px;
        height: 500px;
        background: radial-gradient(circle, rgba(94, 106, 210, 0.08) 0%, transparent 70%);
        top: -100px;
        right: -100px;
        z-index: 0;
    }

    .login-container {
        width: 100%;
        max-width: 420px;
        padding: 3rem 2.5rem;
        border-radius: 24px;
        z-index: 1;
        position: relative;
    }

    .login-logo {
        width: 48px;
        height: 48px;
        background: var(--accent-primary);
        margin: 0 auto;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 0 20px rgba(var(--accent-primary-rgb), 0.4);
    }

    .login-logo img {
        width: 28px;
        filter: brightness(0) invert(1);
    }

    .main-title {
        font-size: 1.5rem;
        font-weight: 800;
        color: var(--text-primary);
        letter-spacing: -0.02em;
    }

    .sub-title {
        color: var(--text-tertiary);
        font-size: 0.9rem;
    }

    .label-premium {
        font-size: 12px;
        font-weight: 700;
        color: var(--text-secondary);
        text-transform: uppercase;
        letter-spacing: 0.05em;
        margin-bottom: 8px;
        display: block;
    }

    .input-premium {
        width: 100%;
        background: var(--surface-secondary);
        border: 1px solid var(--border-subtle);
        padding: 12px 16px;
        border-radius: 12px;
        color: var(--text-primary);
        font-size: 14px;
        outline: none;
        transition: all 0.2s;
    }

    .input-premium:focus {
        border-color: var(--accent-primary);
        background: var(--surface-primary);
        box-shadow: 0 0 0 4px rgba(var(--accent-primary-rgb), 0.1);
    }

    .ojo-btn {
        position: absolute;
        right: 12px;
        top: 50%;
        transform: translateY(-50%);
        background: transparent;
        border: none;
        color: var(--text-tertiary);
        cursor: pointer;
        display: flex;
    }

    .ojo-btn:hover {
        color: var(--accent-primary);
    }

    .btn-primary-glow {
        width: 100%;
        background: var(--accent-primary);
        color: white;
        border: none;
        padding: 14px;
        border-radius: 12px;
        font-weight: 700;
        font-size: 15px;
        cursor: pointer;
        transition: all 0.3s;
        box-shadow: 0 4px 12px rgba(var(--accent-primary-rgb), 0.3);
    }

    .btn-primary-glow:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(var(--accent-primary-rgb), 0.4);
        background: var(--accent-hover);
    }

    .notification-premium {
        background: rgba(var(--status-danger-rgb, 220, 38, 38), 0.1);
        color: var(--status-danger);
        padding: 10px;
        border-radius: 8px;
        font-size: 13px;
        font-weight: 600;
        text-align: center;
        border: 1px solid rgba(var(--status-danger-rgb, 220, 38, 38), 0.2);
    }

    .sso-link {
        font-size: 13px;
        color: var(--text-tertiary);
        text-decoration: none;
        font-weight: 600;
        transition: color 0.2s;
    }

    .sso-link:hover {
        color: var(--accent-primary);
    }
</style>
