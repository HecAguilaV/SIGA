<script>
  import { datosNegocio } from '$lib/datosSimulados.js';

  let localSeleccionado = 0;

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
        <span class="etiqueta">SIGA inventario inteligente</span>
        <h1 class="title heading-gradient">Controla tu inventario sin perder tiempo</h1>
        <p class="subtitle mt-3">Observa el stock por kiosko en tiempo real, detecta riesgos de quiebre y toma acción con un clic.</p>
        <div class="buttons mt-5">
          <a class="button is-link is-medium" href="/analisis">Conoce las insights</a>
          <a class="button is-link is-light is-medium" href="/asistente">Habla con el asistente</a>
        </div>
      </div>
      <div class="column is-one-third">
        <div class="paleta">
          <p class="has-text-weight-semibold mb-3">Paleta SIGA</p>
          <div class="chips">
            <div class="chip" style="--chip-color: #03045e;">#03045E</div>
            <div class="chip" style="--chip-color: #00b4d8;">#00B4D8</div>
            <div class="chip" style="--chip-color: #80ffdb;">#80FFDB</div>
            <div class="chip" style="--chip-color: #ffffff; color: #1b233f; border: 1px solid var(--color-borde);">#FFFFFF</div>
          </div>
          <p class="nota-paleta">Una identidad fresca y profesional lista para sorprender a tus clientes.</p>
        </div>
      </div>
    </div>
  </div>

  <div class="box">
    <h2 class="title is-3 has-text-weight-semibold">Inventario por local</h2>
    <p class="subtitle is-5">Filtra el kiosko y encuentra el stock disponible para cada producto.</p>

    <div class="field is-grouped is-align-items-center">
      <div class="control">
        <label class="label" for="selector-local">Selecciona un local</label>
        <div class="select">
          <select id="selector-local" on:change={cambiarLocal} bind:value={localSeleccionado}>
            {#each $datosNegocio.locales as local}
              <option value={local.id}>{local.nombre}</option>
            {/each}
          </select>
        </div>
      </div>
    </div>

    <div class="table-container mt-5">
      <table class="table is-fullwidth is-striped is-hoverable">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>SKU</th>
            <th>Categoría</th>
            <th class="has-text-right">Stock actual</th>
          </tr>
        </thead>
        <tbody>
          {#each productosFiltrados as producto}
            <tr>
              <td>{producto.nombre}</td>
              <td>{producto.sku}</td>
              <td>{producto.categoria}</td>
              <td class="has-text-right has-text-weight-semibold">{producto.stockActual}</td>
            </tr>
          {/each}
        </tbody>
      </table>
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

  .paleta {
    background-color: #ffffff;
    border-radius: 20px;
    padding: 1.75rem;
    box-shadow: 0 15px 45px -32px rgba(3, 4, 94, 0.7);
  }

  .chips {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0.75rem;
  }

  .chip {
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 14px;
    background-color: var(--chip-color);
    color: #ffffff;
    font-weight: 600;
    padding: 0.75rem 1rem;
    box-shadow: inset 0 1px 4px rgba(255, 255, 255, 0.12);
  }

  .nota-paleta {
    margin-top: 1.5rem;
    font-size: 0.9rem;
    color: var(--color-primario);
  }

  @media screen and (max-width: 1023px) {
    .hero-gradient {
      padding: 2.25rem;
    }

    .chips {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  @media screen and (max-width: 768px) {
    .hero-gradient {
      padding: 2rem;
    }

    .chip {
      font-size: 0.85rem;
    }
  }
</style>