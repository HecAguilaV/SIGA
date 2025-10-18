<script>
  import GraficoBarras from '$lib/components/GraficoBarras.svelte';
  import { datosNegocio } from '$lib/datosSimulados.js';

  let localSeleccionado = 0;
  let mostrarExplicacion = false;

  /** @typedef {{ localId: number; productoId: number; cantidad: number; producto: { id: number; nombre: string; sku: string; categoria: string; stock: Record<number, number> } }} VentaEnriquecida */

  $: localesDisponibles = $datosNegocio.locales ?? [];
  $: {
    if (!localSeleccionado && localesDisponibles.length) {
      localSeleccionado = localesDisponibles[0].id;
    }
  }

  $: productosPorId = new Map(($datosNegocio.productos ?? []).map((producto) => [producto.id, producto]));

  $: ventasFiltradas = /** @type {VentaEnriquecida[]} */ (
    ($datosNegocio.ventasSemana ?? [])
      .filter((venta) => venta.localId === localSeleccionado)
      .map((venta) => {
        const producto = productosPorId.get(venta.productoId);
        if (!producto) {
          return null;
        }
        return { ...venta, producto };
      })
      .filter((venta) => venta !== null)
      .map((venta) => /** @type {VentaEnriquecida} */ (venta))
  );

  $: ventasOrdenadas = [...ventasFiltradas].sort((a, b) => b.cantidad - a.cantidad);

  $: etiquetas = ventasOrdenadas.map((venta) => venta.producto.nombre);

  $: valores = ventasOrdenadas.map((venta) => venta.cantidad);

  $: nombreLocal = (localesDisponibles.find((local) => local.id === localSeleccionado) ?? {}).nombre ?? `Local ${localSeleccionado}`;

  $: totalSemanal = ventasOrdenadas.reduce((acumulado, venta) => acumulado + venta.cantidad, 0);
  $: promedioVenta = ventasOrdenadas.length ? Math.round(totalSemanal / ventasOrdenadas.length) : 0;
  $: topUno = ventasOrdenadas[0];
  $: topDos = ventasOrdenadas[1];

  $: textoExplicacion = ventasOrdenadas.length
    ? (() => {
        const fraseTopDos = topDos
          ? `Le siguió ${topDos.producto.nombre} con ${topDos.cantidad} unidades, confirmando la demanda en ${topDos.producto.categoria.toLowerCase()}.`
          : 'Refuerza el surtido de los productos líderes para sostener el ritmo de venta.';
        return `En ${nombreLocal}, el producto con mayor rotación fue ${topUno.producto.nombre} con ${topUno.cantidad} unidades. ${fraseTopDos}
        El total semanal alcanzó ${totalSemanal} unidades y el promedio por referencia fue de ${promedioVenta}. Programa reposiciones priorizando estas categorías para evitar quiebres locales.`;
      })()
    : `Aún no registramos ventas para ${nombreLocal} en la última semana. Revisa la configuración del local o programa acciones comerciales para activar la demanda.`;

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