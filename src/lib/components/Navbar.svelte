<script>
  import { page } from '$app/stores';
  import { derived } from 'svelte/store';

  // Derivamos la ruta actual para poder resaltar la opción activa en la barra de navegación
  const rutaActual = derived(page, ($page) => $page.url.pathname);

  let menuAbierto = false;

  /** @type {(ruta: string, actual: string) => boolean} */
  const esActivo = (ruta, actual) => ruta === actual;

  /**
   * Alterna el estado del menú hamburguesa
   * @param {MouseEvent} evento
   */
  const alternarMenu = (evento) => {
    const objetivo = document.getElementById('navbar-menu');
    const boton = /** @type {HTMLButtonElement} */ (evento.currentTarget);
    menuAbierto = !menuAbierto;
    boton.setAttribute('aria-expanded', menuAbierto ? 'true' : 'false');
    boton.classList.toggle('is-active');
    objetivo?.classList.toggle('is-active');
  };

  /**
   * Cierra el menú al hacer clic en un enlace
   */
  const cerrarMenu = () => {
    if (menuAbierto) {
      menuAbierto = false;
      const objetivo = document.getElementById('navbar-menu');
      const boton = document.querySelector('.navbar-burger');
      boton?.setAttribute('aria-expanded', 'false');
      boton?.classList.remove('is-active');
      objetivo?.classList.remove('is-active');
    }
  };
</script>

<nav class="navbar" aria-label="Navegación principal">
  <div class="navbar-content">
    <div class="navbar-brand-section">
      <a class="navbar-item marca" href="/">
        <img src="/brand/Logo_SIGA.png" alt="Logo de SIGA - Volver al inicio" class="marca-logo" />
      </a>
    </div>

    <div class="navbar-menu-section" id="navbar-menu">
      <a class={`navbar-item enlace ${esActivo('/', $rutaActual) ? 'is-active' : ''}`} href="/" on:click={cerrarMenu}>
        Inventario
      </a>
      <a class={`navbar-item enlace ${esActivo('/analisis', $rutaActual) ? 'is-active' : ''}`} href="/analisis" on:click={cerrarMenu}>
        Análisis
      </a>
      <a class={`navbar-item enlace ${esActivo('/acerca', $rutaActual) ? 'is-active' : ''}`} href="/acerca" on:click={cerrarMenu}>
        Acerca de
      </a>
    </div>

    <button
      type="button"
      class="navbar-burger"
      aria-label="Abrir menú"
      aria-expanded="false"
      on:click={alternarMenu}
    >
      <span aria-hidden="true"></span>
      <span aria-hidden="true"></span>
      <span aria-hidden="true"></span>
    </button>
  </div>
</nav>

<style>
  nav {
    background-color: #ffffff;
  }

  .navbar-content {
    display: flex;
    align-items: center;
    gap: 2rem;
    padding: 1rem 2rem;
  }

  .navbar-brand-section {
    display: flex;
    align-items: center;
    gap: 0;
    flex-shrink: 0;
  }

  .navbar-menu-section {
    display: flex;
    gap: 2rem;
    align-items: center;
    flex: 1;
  }

  .marca {
    display: inline-flex;
    align-items: center;
    gap: 0;
    font-weight: 700;
    color: var(--color-primario);
  }

  .marca-logo {
    height: 2.5rem;
    max-height: 2.5rem;
    width: auto;
  }

  .enlace {
    position: relative;
    font-weight: 600;
    color: var(--color-primario);
    transition: color 0.2s ease;
    display: flex;
    align-items: center;
    text-decoration: none;
    font-size: 14px;
    white-space: nowrap;
  }

  .enlace:hover {
    color: var(--color-secundario);
  }

  .enlace:focus,
  .enlace:focus-visible {
    outline: 3px solid var(--color-secundario);
    outline-offset: 2px;
    color: var(--color-primario);
  }

  .enlace:active {
    color: var(--color-primario);
  }

  .enlace.is-active {
    color: var(--color-secundario);
  }

  :global(.navbar-burger) {
    color: var(--color-primario);
    display: none;
  }

  :global(.navbar-burger span) {
    background-color: var(--color-primario);
  }

  :global(.navbar-burger.is-active span) {
    background-color: var(--color-secundario);
  }

  /* Media query para móvil */
  @media screen and (max-width: 768px) {
    .navbar-content {
      flex-wrap: wrap;
      gap: 1rem;
      padding: 1rem;
    }

    .navbar-menu-section {
      width: 100%;
      display: none;
      flex-direction: column;
      gap: 0;
    }

    .navbar-menu-section.is-active {
      display: flex;
    }

    .enlace {
      padding: 0.75rem 0;
      border-bottom: 1px solid var(--color-borde);
      width: 100%;
    }

    .enlace.is-active {
      color: var(--color-secundario);
      padding-left: 0;
      padding-right: 0;
    }

    :global(.navbar-burger) {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: 50px;
      height: 50px;
      padding: 0;
      margin-left: auto;
      background: none;
      border: 2px solid transparent;
      cursor: pointer;
      border-radius: 8px;
      transition: all 0.2s ease;
    }

    :global(.navbar-burger:hover) {
      background-color: rgba(0, 180, 216, 0.1);
      border-color: var(--color-secundario);
    }

    :global(.navbar-burger.is-active) {
      background-color: rgba(0, 180, 216, 0.15);
      border-color: var(--color-secundario);
    }

    :global(.navbar-burger span) {
      height: 3px;
      width: 1.75rem;
      display: block;
      margin: 4px 0;
      border-radius: 2px;
      transition: all 0.3s ease;
      background-color: var(--color-primario);
    }

    :global(.navbar-burger.is-active span:nth-child(1)) {
      transform: translateY(11px) rotate(45deg);
    }

    :global(.navbar-burger.is-active span:nth-child(2)) {
      opacity: 0;
    }

    :global(.navbar-burger.is-active span:nth-child(3)) {
      transform: translateY(-11px) rotate(-45deg);
    }
  }
</style>