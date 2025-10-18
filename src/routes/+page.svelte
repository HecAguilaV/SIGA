<script>
  import { datosNegocio } from '$lib/datosSimulados.js';

  let localSeleccionado = 0;
  let ordenarPor = 'nombre';
  let ordenAscendente = true;

  // Esta reacción asegura que siempre tengamos un local activo cuando el store cargue datos
  // Propósito: evitar que la tabla se renderice vacía y guiar al usuario con una selección por defecto
  $: {
    const localesDisponibles = $datosNegocio.locales ?? [];
    if (!localSeleccionado && localesDisponibles.length) {
      localSeleccionado = localesDisponibles[0].id;
    }
  }

  // Computamos los productos con el stock filtrado según el local seleccionado
  // Propósito: mostrar información relevante de inventario de manera rápida
  $: productosFiltrados = ($datosNegocio.productos ?? []).map((producto) => {
    const stockPorLocal = /** @type {Record<string, number>} */ (producto.stock ?? {});
    const stockActual = stockPorLocal[String(localSeleccionado)] ?? 0;
    return { ...producto, stockActual };
  });

  // Aplicar ordenamiento a los productos
  $: productosOrdenados = [...productosFiltrados].sort((a, b) => {
    let valorA = 0;
    let valorB = 0;

    if (ordenarPor === 'nombre') {
      valorA = a.nombre.toLowerCase() > b.nombre.toLowerCase() ? 1 : -1;
      valorB = 0;
    } else if (ordenarPor === 'sku') {
      valorA = a.sku.toLowerCase() > b.sku.toLowerCase() ? 1 : -1;
      valorB = 0;
    } else if (ordenarPor === 'categoria') {
      valorA = a.categoria.toLowerCase() > b.categoria.toLowerCase() ? 1 : -1;
      valorB = 0;
    } else if (ordenarPor === 'stock') {
      valorA = a.stockActual;
      valorB = b.stockActual;
    }

    const resultado = valorA < valorB ? -1 : valorA > valorB ? 1 : 0;
    return ordenAscendente ? resultado : -resultado;
  });

  /**
   * Cambia el criterio de ordenamiento
   * @param {string} columna
   */
  const cambiarOrdenamiento = (columna) => {
    if (ordenarPor === columna) {
      // Si ya estamos ordenando por esta columna, invertir el orden
      ordenAscendente = !ordenAscendente;
    } else {
      // Si cambiamos de columna, empezar con orden ascendente
      ordenarPor = columna;
      ordenAscendente = true;
    }
  };

  /**
   * Retorna el indicador visual del ordenamiento
   * @param {string} columna
   */
  const obtenerIndicador = (columna) => {
    if (ordenarPor !== columna) return '';
    return ordenAscendente ? ' ↑' : ' ↓';
  };

  /**
   * Actualiza el local seleccionado cuando el usuario cambia la opción del dropdown.
   * Propósito: mantener sincronizado el filtro del inventario con la preferencia del usuario.
   * @param {Event} evento
   */
  const cambiarLocal = (evento) => {
    // Propósito: actualizar el filtro de local de forma controlada
    const elemento = /** @type {HTMLSelectElement} */ (evento.currentTarget);
    localSeleccionado = Number(elemento.value);
  };
</script>

<section class="section">
  <div class="hero-gradient mb-6">
    <div class="columns is-vcentered">
      <div class="column">
        <span class="etiqueta">Sistema Inteligente de Gestión de Activos</span>
        <h1 class="title heading-gradient">Gestiona tu tiempo, no tu inventario</h1>
        <p class="subtitle mt-3">SIGA es tu ERP simplificado. Automatiza el control de stock en tiempo real para que te enfoques en lo que realmente importa: crecer sin interrupciones.</p>
        <div class="buttons mt-5">
          <a class="button is-link is-medium" href="/analisis">Descubre las insights</a>
        </div>
      </div>
    </div>
  </div>

  <!-- KPI Cards para inversores -->
  <div class="columns is-multiline mb-6">
    <div class="column is-half-tablet is-one-third-desktop">
      <div class="kpi-card">
        <p class="heading is-6" style="color: var(--color-primario);">Productos en Stock</p>
        <p class="title is-4" style="color: var(--color-secundario);">{$datosNegocio.productos?.length || 0}</p>
        <p class="is-size-7">SKU activos en el sistema</p>
      </div>
    </div>
    <div class="column is-half-tablet is-one-third-desktop">
      <div class="kpi-card">
        <p class="heading is-6" style="color: var(--color-primario);">Locales Conectados</p>
        <p class="title is-4" style="color: var(--color-secundario);">{$datosNegocio.locales?.length || 0}</p>
        <p class="is-size-7">Kioscos monitoreados</p>
      </div>
    </div>
    <div class="column is-half-tablet is-one-third-desktop">
      <div class="kpi-card">
        <p class="heading is-6" style="color: var(--color-primario);">Precisión de Datos</p>
        <p class="title is-4" style="color: #388e3c;">99.2%</p>
        <p class="is-size-7">Confiabilidad en tiempo real</p>
      </div>
    </div>
  </div>

  <div class="box">
    <h2 class="title is-3 has-text-weight-semibold">Stock por sucursal</h2>
    <p class="subtitle is-5">Visualiza el inventario en tiempo real. Ordena por lo que necesites y actúa al instante.</p>

    <!-- Botones de selección de locales (horizontales con espaciado simétrico) -->
    <div class="local-selector mt-5 mb-5">
      {#each $datosNegocio.locales as local}
        <button
          class={`local-btn ${localSeleccionado === local.id ? 'is-active' : ''}`}
          on:click={() => (localSeleccionado = local.id)}
          aria-pressed={localSeleccionado === local.id}
        >
          {local.nombre}
        </button>
      {/each}
    </div>

    <div class="table-wrapper">
      <div class="table-container">
        <table class="table is-fullwidth is-striped is-hoverable">
          <thead>
            <tr>
              <th class="sortable-header" on:click={() => cambiarOrdenamiento('nombre')}>
                Nombre{obtenerIndicador('nombre')}
              </th>
              <th class="sortable-header" on:click={() => cambiarOrdenamiento('sku')}>
                SKU{obtenerIndicador('sku')}
              </th>
              <th class="sortable-header" on:click={() => cambiarOrdenamiento('categoria')}>
                Categoría{obtenerIndicador('categoria')}
              </th>
              <th class="sortable-header has-text-right" on:click={() => cambiarOrdenamiento('stock')}>
                Stock actual{obtenerIndicador('stock')}
              </th>
            </tr>
          </thead>
          <tbody>
            {#each productosOrdenados as producto}
              {#if producto.stockActual === 0}
                <tr style="background-color: rgba(211, 47, 47, 0.08);">
                  <td><strong>{producto.nombre}</strong></td>
                  <td>{producto.sku}</td>
                  <td>{producto.categoria}</td>
                  <td class="has-text-right"><span class="status-critico">SIN STOCK</span></td>
                </tr>
              {:else if producto.stockActual < 5}
                <tr style="background-color: rgba(245, 124, 0, 0.08);">
                  <td><strong>{producto.nombre}</strong></td>
                  <td>{producto.sku}</td>
                  <td>{producto.categoria}</td>
                  <td class="has-text-right"><span class="status-advertencia">{producto.stockActual}</span></td>
                </tr>
              {:else}
                <tr>
                  <td>{producto.nombre}</td>
                  <td>{producto.sku}</td>
                  <td>{producto.categoria}</td>
                  <td class="has-text-right"><span class="status-normal">{producto.stockActual}</span></td>
                </tr>
              {/if}
            {/each}
          </tbody>
        </table>
      </div>
    </div>
  </div>
</section>

<style>
  .etiqueta {
    display: inline-block;
    padding: 0.4rem 0.9rem;
    border-radius: 999px;
    background-color: rgba(3, 4, 94, 0.1);
    color: var(--color-primario);
    font-size: 0.85rem;
    font-weight: 600;
    letter-spacing: 0.08em;
    text-transform: uppercase;
  }

  .hero-logo {
    max-width: 150px;
    height: auto;
    filter: drop-shadow(0 8px 16px rgba(3, 4, 94, 0.15));
  }

  /* Botones horizontales para seleccionar locales */
  .local-selector {
    display: flex;
    gap: 1rem;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    background-color: rgba(0, 180, 216, 0.08);
    padding: 1.5rem;
    border-radius: 12px;
  }

  .local-btn {
    padding: 0.75rem 1.75rem;
    border: 2px solid transparent;
    border-radius: 10px;
    background-color: transparent;
    color: var(--color-primario);
    font-weight: 600;
    font-size: 15px;
    letter-spacing: 0.5px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  .local-btn:hover {
    background-color: rgba(0, 180, 216, 0.15);
    border-color: var(--color-primario);
    transform: translateY(-2px);
  }

  .local-btn.is-active {
    background-color: var(--color-primario);
    border-color: var(--color-primario);
    color: #ffffff;
    box-shadow: 0 4px 12px rgba(3, 4, 94, 0.2);
  }

  .local-btn.is-active:hover {
    background-color: #051a8f;
    border-color: #051a8f;
    box-shadow: 0 6px 16px rgba(3, 4, 94, 0.3);
  }

  /* Estilos para la tabla */
  .table-wrapper {
    display: flex;
    justify-content: center;
    width: 100%;
    margin-top: 1.5rem;
  }

  .table-container {
    width: 100%;
    max-width: 100%;
    overflow-x: auto;
    padding: 0 1rem;
  }

  /* Encabezados sortables */
  :global(.sortable-header) {
    cursor: pointer;
    user-select: none;
    transition: background-color 0.2s ease, color 0.2s ease;
    padding: 1.25rem !important;
    font-weight: 700;
    white-space: nowrap;
  }

  :global(.sortable-header:hover) {
    background-color: rgba(0, 180, 216, 0.1) !important;
    color: var(--color-secundario);
  }

  /* Mejorar espaciado de celdas */
  :global(.table td),
  :global(.table th) {
    padding: 1.25rem !important;
    vertical-align: middle;
  }

  :global(.table tbody td) {
    font-size: 0.95rem;
  }

  @media screen and (max-width: 768px) {
    .hero-logo {
      max-width: 120px;
    }

    :global(.table td),
    :global(.table th) {
      padding: 0.9rem !important;
    }

    :global(.sortable-header) {
      padding: 0.9rem !important;
      font-size: 0.9rem;
    }

    .local-selector {
      padding: 1rem;
      gap: 0.75rem;
    }

    .local-btn {
      padding: 0.6rem 1rem;
      font-size: 13px;
    }
  }
</style>