<script>
    import { toast } from "$lib/stores/toast";
    import { fly } from "svelte/transition";
    import { CheckCircle, Warning, XCircle, Info, X } from "phosphor-svelte";

    const icons = {
        success: CheckCircle,
        warning: Warning,
        error: XCircle,
        info: Info,
    };

    const colors = {
        success: "is-success",
        warning: "is-warning",
        error: "is-danger",
        info: "is-info",
    };
</script>

<div class="toast-container">
    {#each $toast as t (t.id)}
        <div
            class="notification {colors[t.type] || 'is-info'} toast-item"
            transition:fly={{ y: 20, duration: 300 }}
            role="alert"
        >
            <button
                class="delete"
                on:click={() => toast.remove(t.id)}
                aria-label="Cerrar"
            ></button>
            <div class="toast-content">
                <svelte:component
                    this={icons[t.type] || Info}
                    size={24}
                    weight="fill"
                    class="toast-icon"
                />
                <span class="toast-message">{t.message}</span>
            </div>
        </div>
    {/each}
</div>

<style>
    .toast-container {
        position: fixed;
        bottom: 24px;
        right: 24px; /* Bottom-right corner same as assistant but higher z-index */
        z-index: 20000; /* Above everything */
        display: flex;
        flex-direction: column;
        gap: 12px;
        pointer-events: none; /* Let clicks pass through container */
    }

    .toast-item {
        pointer-events: auto; /* Re-enable clicks on toasts */
        min-width: 300px;
        max-width: 400px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        border-radius: 8px;
        padding: 1.25rem 2.5rem 1.25rem 1.5rem; /* Extra padding right for close button */
        animation: active 0.3s ease-out;
        display: flex;
        align-items: center;
    }

    .toast-content {
        display: flex;
        align-items: center;
        gap: 12px;
    }

    .toast-icon {
        flex-shrink: 0;
        opacity: 0.9;
    }

    .toast-message {
        font-weight: 500;
        line-height: 1.4;
    }

    /* Override Bulma notification padding/margins if needed */
    .notification {
        margin-bottom: 0 !important;
    }
</style>
