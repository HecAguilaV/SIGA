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
  <div class="box">
    <h1 class="title has-text-weight-semibold">Inventario</h1>
    <p class="subtitle">Visualiza el stock actualizado por local y toma decisiones rápidas.</p>

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