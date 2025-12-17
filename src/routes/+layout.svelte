<script>
  import "../app.css";
  import Sidebar from "$lib/components/Sidebar.svelte";
  import AsistenteContextual from "$lib/components/AsistenteContextual.svelte";
  import { authStore } from "$lib/stores/authStore";
  import { uiStore } from "$lib/stores/uiStore";
  import ToastContainer from "$lib/components/ToastContainer.svelte";
  import { onMount } from "svelte";
  import { goto } from "$app/navigation";
  import { page } from "$app/stores";
  import { browser } from "$app/environment";
  import { List } from "phosphor-svelte";

  // Rutas públicas que no requieren autenticación
  const publicRoutes = ["/login", "/sso"];

  $: isAuthenticated = $authStore.isAuthenticated;
  $: isPublicRoute = publicRoutes.some((route) =>
    $page.url.pathname.startsWith(route),
  );

  // Protección de rutas
  $: if (browser && !isAuthenticated && !isPublicRoute) {
    goto("/login");
  }

  onMount(() => {
    if (browser) {
      const checkMobile = () => {
        uiStore.setMobile(window.innerWidth < 768);
      };
      checkMobile();
      window.addEventListener("resize", checkMobile);
      return () => window.removeEventListener("resize", checkMobile);
    }
  });
</script>

<div class="app-layout">
  <ToastContainer />
  {#if isAuthenticated && !isPublicRoute}
    <!-- Usuario autenticado en ruta privada -->
    <Sidebar />

    <!-- Botón Toggle (Hamburger) - Visible solo si el sidebar está cerrado -->
    {#if !$uiStore.isSidebarOpen}
      <button class="toggle-btn" on:click={uiStore.openSidebar}>
        <List size={28} />
      </button>
    {/if}

    <main
      class="main-content authenticated"
      class:sidebar-closed={!$uiStore.isSidebarOpen}
    >
      <slot />
    </main>
    <AsistenteContextual />
  {:else if isPublicRoute}
    <!-- Ruta pública (Login/SSO) -->
    <main class="main-content public">
      <slot />
    </main>
  {:else}
    <!-- Usuario no autenticado en ruta privada: No mostrar nada mientras redirige -->
    <div class="loading-screen">
      <div class="spinner"></div>
    </div>
  {/if}
</div>

<style>
  .app-layout {
    min-height: 100vh;
    background-color: var(--color-fondo);
    position: relative;
  }

  .main-content.authenticated {
    margin-left: 260px; /* Ancho del Sidebar */
    padding: 2rem;
    min-height: 100vh;
    transition: margin-left 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  .main-content.authenticated.sidebar-closed {
    margin-left: 0;
    padding-top: 5rem; /* Espacio para que el botón no tape el título */
  }

  .toggle-btn {
    position: fixed;
    top: 1rem;
    left: 1rem;
    z-index: 900;
    background: white;
    border: none;
    border-radius: 8px;
    padding: 0.5rem;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    color: var(--color-primario);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .main-content.public {
    margin-left: 0;
    padding: 0;
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }
  .loading-screen {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: var(--color-fondo);
  }

  .spinner {
    width: 40px;
    height: 40px;
    border: 4px solid rgba(0, 0, 0, 0.1);
    border-left-color: var(--color-primario);
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @media (max-width: 768px) {
    .main-content.authenticated {
      margin-left: 0 !important; /* En móvil siempre ocupa todo el ancho */
      padding: 1rem;
      padding-top: 5rem; /* Espacio para el header/toggle */
    }
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
</style>
