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
    <a class="navbar-item has-text-weight-semibold" href="/">
      SIGA Prototype
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
      <a class={`navbar-item ${esActivo('/', $rutaActual) ? 'is-active has-text-weight-semibold' : ''}`} href="/">
        Inventario
      </a>
      <a class={`navbar-item ${esActivo('/analisis', $rutaActual) ? 'is-active has-text-weight-semibold' : ''}`} href="/analisis">
        Análisis
      </a>
      <a class={`navbar-item ${esActivo('/asistente', $rutaActual) ? 'is-active has-text-weight-semibold' : ''}`} href="/asistente">
        Asistente
      </a>
    </div>
  </div>
</nav>

<style>
  nav {
    background-color: var(--color-fondo);
    border-bottom: 1px solid var(--color-borde);
  }

  .navbar-item.is-active {
    color: var(--color-secundario);
    border-bottom: 2px solid var(--color-secundario);
  }
</style>