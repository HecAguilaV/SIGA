<script>
  import { page } from '$app/stores';
  import { derived } from 'svelte/store';

  // Derivamos la ruta actual para poder resaltar la opción activa en la barra de navegación
  const rutaActual = derived(page, ($page) => $page.url.pathname);

  /** @type {(ruta: string, actual: string) => boolean} */
  const esActivo = (ruta, actual) => ruta === actual;
</script>

<nav class="navbar is-spaced" aria-label="Navegación principal">
  <div class="navbar-brand">
    <a class="navbar-item marca" href="/">
      <img src="/brand/Logo_SIGA.png" alt="Logo de SIGA" class="marca-logo" />
      <span class="marca-texto">SIGA</span>
    </a>

    <button
      type="button"
      class="navbar-burger"
      aria-label="Abrir menú"
      aria-expanded="false"
      data-target="navbar-menu"
      on:click={(evento) => {
        // Comentario: se alterna la clase "is-active" para mostrar u ocultar el menú en móviles
        const objetivo = document.getElementById('navbar-menu');
        const boton = /** @type {HTMLButtonElement} */ (evento.currentTarget);
        const expandido = boton.getAttribute('aria-expanded') === 'true';
        boton.setAttribute('aria-expanded', expandido ? 'false' : 'true');
        boton.classList.toggle('is-active');
        objetivo?.classList.toggle('is-active');
      }}
    >
      <span aria-hidden="true"></span>
      <span aria-hidden="true"></span>
      <span aria-hidden="true"></span>
    </button>
  </div>

  <div id="navbar-menu" class="navbar-menu">
    <div class="navbar-start">
      <a class={`navbar-item enlace ${esActivo('/', $rutaActual) ? 'is-active' : ''}`} href="/">
        Inventario
      </a>
      <a class={`navbar-item enlace ${esActivo('/analisis', $rutaActual) ? 'is-active' : ''}`} href="/analisis">
        Análisis
      </a>
      <a class={`navbar-item enlace ${esActivo('/asistente', $rutaActual) ? 'is-active' : ''}`} href="/asistente">
        Asistente
      </a>
    </div>
  </div>
</nav>

<style>
  nav {
    background-color: #ffffff;
  }

  .marca {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    font-weight: 700;
    color: var(--color-primario);
    text-transform: uppercase;
    letter-spacing: 0.12em;
  }

  .marca-logo {
    height: 2.5rem;
    max-height: 2.5rem;
    width: auto;
  }

  .marca-texto {
    font-size: 1.2rem;
    color: var(--color-primario);
  }

  .enlace {
    position: relative;
    font-weight: 600;
    color: var(--color-primario);
    transition: color 0.2s ease;
  }

  .enlace::after {
    content: '';
    position: absolute;
    left: 0;
    bottom: 0.35rem;
    width: 0;
    height: 3px;
    border-radius: 999px;
    background: linear-gradient(135deg, var(--color-secundario), var(--color-acento));
    transition: width 0.2s ease;
  }

  .enlace:hover {
    color: var(--color-secundario);
  }

  .enlace:hover::after {
    width: 100%;
  }

  .enlace.is-active {
    color: var(--color-secundario);
  }

  .enlace.is-active::after {
    width: 100%;
  }
</style>