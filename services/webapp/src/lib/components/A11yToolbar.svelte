<script>
    import { uiStore } from "$lib/stores/uiStore";
    import { onMount } from "svelte";
    import { browser } from "$app/environment";
    import { Moon, Sun, Monitor, Wheelchair, ArrowCounterClockwise, TextAUnderline, TextT, Eyedropper, TextAa, MagnifyingGlassPlus, MagnifyingGlassMinus, DropHalfBottom } from "phosphor-svelte";

    let isOpen = false;

    $: a11y = $uiStore.a11y;

    // Lógica para determinar el tema actual e inyectar variables globales
    function applyGlobalA11y(state) {
        if (!browser) return;
        const html = document.documentElement;
        
        let activeTheme = state.theme;
        if (activeTheme === 'auto') {
            const hour = new Date().getHours();
            // Default: Dark desde las 19:00 hasta las 07:00
            activeTheme = (hour >= 19 || hour < 7) ? 'dark' : 'light';
        }

        html.setAttribute("data-theme", activeTheme);
        html.style.fontSize = `${state.fontSize}%`;

        // Modificadores globales
        const clases = [];
        if (state.grayscale) clases.push('a11y-grayscale');
        if (state.highContrast) clases.push('a11y-high-contrast');
        if (state.negativeContrast) clases.push('a11y-negative-contrast');
        if (state.linksUnderline) clases.push('a11y-links-underline');
        if (state.readableFont) clases.push('a11y-readable-font');
        if (state.lightBackground) clases.push('a11y-light-background');

        // Limpiar clases a11y anteriores y aplicar nuevas
        html.className = html.className.replace(/a11y-[a-z-]+/g, '').trim();
        if (clases.length > 0) {
            html.classList.add(...clases);
        }
    }

    // Reaction for a11y object change
    $: applyGlobalA11y(a11y);

</script>

<div class="a11y-toolbar-container" class:is-open={isOpen}>
    <button class="a11y-toggle" on:click={() => { isOpen = !isOpen; }} aria-label="Herramientas de accesibilidad">
        <Wheelchair size={24} weight="bold" />
    </button>

    {#if isOpen}
        <div class="a11y-panel">
            <div class="a11y-panel-header">
                <h3>Herramientas de accesibilidad</h3>
            </div>

            <div class="a11y-panel-body">
                <h4>Tema de Pantalla</h4>
                <div class="theme-buttons">
                    <button class="theme-btn" class:active={a11y.theme === 'light'} on:click={() => uiStore.setTheme('light')} title="Tema Claro">
                        <Sun size={20} />
                    </button>
                    <button class="theme-btn" class:active={a11y.theme === 'dark'} on:click={() => uiStore.setTheme('dark')} title="Tema Oscuro">
                        <Moon size={20} />
                    </button>
                    <button class="theme-btn" class:active={a11y.theme === 'auto'} on:click={() => uiStore.setTheme('auto')} title="Automático (Reloj)">
                        <Monitor size={20} />
                    </button>
                </div>

                <h4>Visualización</h4>
                <ul class="a11y-tools-list">
                    <li>
                        <button on:click={() => uiStore.setFontSize(a11y.fontSize + 10)}>
                            <MagnifyingGlassPlus size={20}/> Aumentar texto
                        </button>
                    </li>
                    <li>
                        <button on:click={() => uiStore.setFontSize(Math.max(50, a11y.fontSize - 10))}>
                            <MagnifyingGlassMinus size={20}/> Disminuir texto
                        </button>
                    </li>
                    <li>
                        <button class:active={a11y.grayscale} on:click={uiStore.toggleGrayscale}>
                            <DropHalfBottom size={20}/> Escala de grises
                        </button>
                    </li>
                    <li>
                        <button class:active={a11y.highContrast} on:click={uiStore.toggleHighContrast}>
                            <DropHalfBottom size={20} weight="fill"/> Alto contraste
                        </button>
                    </li>
                    <li>
                        <button class:active={a11y.negativeContrast} on:click={uiStore.toggleNegativeContrast}>
                            <Eyedropper size={20}/> Contraste negativo
                        </button>
                    </li>
                    <li>
                        <button class:active={a11y.lightBackground} on:click={uiStore.toggleLightBackground}>
                            <Sun size={20}/> Fondo claro forzado
                        </button>
                    </li>
                </ul>

                <h4>Lectura</h4>
                <ul class="a11y-tools-list">
                    <li>
                        <button class:active={a11y.linksUnderline} on:click={uiStore.toggleLinksUnderline}>
                            <TextAUnderline size={20}/> Subrayar enlaces
                        </button>
                    </li>
                    <li>
                        <button class:active={a11y.readableFont} on:click={uiStore.toggleReadableFont}>
                            <TextAa size={20}/> Fuente legible
                        </button>
                    </li>
                </ul>

                <button class="a11y-reset" on:click={uiStore.resetA11y}>
                    <ArrowCounterClockwise size={18} /> Restablecer opciones
                </button>
            </div>
        </div>
    {/if}
</div>

<style>
    .a11y-toolbar-container {
        position: fixed;
        right: 0;
        top: 20%;
        z-index: 9999;
        display: flex;
        flex-direction: row-reverse;
        align-items: flex-start;
    }

    .a11y-toggle {
        background: var(--accent-primary, #5E6AD2);
        color: white;
        border: none;
        border-radius: 8px 0 0 8px;
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        box-shadow: -4px 0 12px rgba(0,0,0,0.15);
        transition: transform 0.2s, background 0.2s;
    }

    .a11y-toggle:hover {
        background: var(--accent-hover, #737EE0);
    }

    .a11y-panel {
        background: var(--surface-primary, #ffffff);
        color: var(--text-primary, #111);
        width: 320px;
        border: 1px solid var(--border-subtle, #ddd);
        border-radius: 12px 0 0 12px;
        box-shadow: -8px 8px 32px rgba(0,0,0,0.2);
        display: flex;
        flex-direction: column;
        overflow: hidden;
    }

    .a11y-panel-header {
        background: var(--surface-secondary, #f4f4f4);
        padding: 16px;
        border-bottom: 1px solid var(--border-subtle, #ddd);
    }

    .a11y-panel-header h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
    }

    .a11y-panel-body {
        padding: 16px;
        max-height: 70vh;
        overflow-y: auto;
    }

    h4 {
        margin: 0 0 8px 0;
        font-size: 13px;
        text-transform: uppercase;
        color: var(--text-tertiary, #666);
        letter-spacing: 0.05em;
    }

    .theme-buttons {
        display: flex;
        gap: 8px;
        margin-bottom: 24px;
    }

    .theme-btn {
        flex: 1;
        background: var(--surface-secondary, #eee);
        border: 1px solid var(--border-subtle, #ddd);
        color: var(--text-primary, #111);
        border-radius: 6px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.2s;
    }

    .theme-btn:hover {
        background: var(--surface-elevated, #ddd);
    }

    .theme-btn.active {
        background: var(--accent-primary, #5E6AD2);
        color: white;
        border-color: var(--accent-primary, #5E6AD2);
    }

    .a11y-tools-list {
        list-style: none;
        padding: 0;
        margin: 0 0 24px 0;
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 8px;
    }

    .a11y-tools-list button {
        width: 100%;
        background: var(--surface-secondary, #eee);
        border: 1px solid var(--border-subtle, #ddd);
        color: var(--text-primary, #111);
        border-radius: 6px;
        padding: 12px 8px;
        font-size: 12px;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        cursor: pointer;
        transition: all 0.2s;
        text-align: center;
    }

    .a11y-tools-list button:hover {
        border-color: var(--accent-primary, #5E6AD2);
    }

    .a11y-tools-list button.active {
        background: rgba(94, 106, 210, 0.1);
        border-color: var(--accent-primary, #5E6AD2);
        color: var(--accent-primary, #5E6AD2);
    }

    .a11y-reset {
        width: 100%;
        background: #ef4444;
        color: white;
        border: none;
        border-radius: 6px;
        padding: 12px;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
    }

    .a11y-reset:hover {
        background: #dc2626;
    }

    @media (max-width: 480px) {
        .a11y-panel {
            width: 280px;
        }
    }
</style>
