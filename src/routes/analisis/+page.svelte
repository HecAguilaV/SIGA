<script>
  import GraficoBarras from '$lib/components/GraficoBarras.svelte';
  import { datosNegocio } from '$lib/datosSimulados.js';

  let localSeleccionado = 0;
  let mostrarExplicacion = false;

  $: localesDisponibles = $datosNegocio.locales ?? [];
  $: {
    if (!localSeleccionado && localesDisponibles.length) {
      localSeleccionado = localesDisponibles[0].id;
    }
  }

  $: productosLocal = ($datosNegocio.productos ?? []).filter((producto) => {
    const stockPorLocal = /** @type {Record<string, number>} */ (producto.stock ?? {});
    return (stockPorLocal[String(localSeleccionado)] ?? 0) > 0;
  });

  $: ventasFiltradas = ($datosNegocio.ventasSemana ?? []).filter((venta) =>
    productosLocal.some((producto) => producto.id === venta.productoId)
  );

  $: etiquetas = ventasFiltradas.map((venta) => {
    const producto = productosLocal.find((prod) => prod.id === venta.productoId);
    return producto ? producto.nombre : `Producto ${venta.productoId}`;
  });

  $: valores = ventasFiltradas.map((venta) => venta.cantidad);

  $: nombreLocal = (localesDisponibles.find((local) => local.id === localSeleccionado) ?? {}).nombre ?? `Local ${localSeleccionado}`;

  $: textoExplicacion = `Durante la última semana, los productos con mayor rotación fueron Bebida Fantasía y Papas Fritas.
  Esto indica que ${nombreLocal} necesita reposiciones frecuentes en bebidas y snacks para evitar quiebres.
  Recomendamos programar abastecimientos cada dos días y revisar promociones activas.`;

  /**
   * Convierte el valor del select en número para mantener la coherencia del estado.
   * Propósito: asegurar que las operaciones numéricas posteriores funcionen correctamente.
   * @param {Event} evento
   */
  const seleccionarLocal = (evento) => {
    const elemento = /** @type {HTMLSelectElement} */ (evento.currentTarget);
    localSeleccionado = Number(elemento.value);
  };

  /**
   * Alterna la visibilidad del panel de explicación automática.
   * Propósito: simular la interacción con el asistente de inteligencia al explicar los datos del gráfico.
   */
  const alternarExplicacion = () => {
    mostrarExplicacion = !mostrarExplicacion;
  };
</script>

<section class="section">
  <div class="box">
    <h1 class="title has-text-weight-semibold">Análisis</h1>
    <p class="subtitle">Identifica oportunidades y evita quiebres de stock con un vistazo.</p>

    <div class="field is-grouped is-align-items-center">
      <div class="control">
        <label class="label" for="selector-analisis">Selecciona un local</label>
        <div class="select">
          <select id="selector-analisis" bind:value={localSeleccionado} on:change={seleccionarLocal}>
            {#each localesDisponibles as local}
              <option value={local.id}>{local.nombre}</option>
            {/each}
          </select>
        </div>
      </div>
    </div>

    <div class="box mt-5">
      <GraficoBarras
        titulo={`Ventas semanales por producto (${nombreLocal})`}
        {etiquetas}
        {valores}
      />
    </div>

    <button class="button is-link is-light is-medium" on:click={alternarExplicacion}>
      ✨ Explícame este gráfico
    </button>

    {#if mostrarExplicacion}
      <article class="message is-info mt-4">
        <div class="message-header">
          <p>Explicación de SIGA</p>
        </div>
        <div class="message-body">
          {textoExplicacion}
        </div>
      </article>
    {/if}
  </div>
</section>