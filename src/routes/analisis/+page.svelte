<script>
  import GraficoBarras from '$lib/components/GraficoBarras.svelte';
  import GraficoLineas from '$lib/components/GraficoLineas.svelte';
  import GraficoLineasMultiple from '$lib/components/GraficoLineasMultiple.svelte';
  import { datosNegocio } from '$lib/datosSimulados.js';

  let localSeleccionado = 0;

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

  // Datos para el gráfico de líneas de ventas por día
  $: etiquetasDias = ($datosNegocio.ventasPorDia ?? []).map((d) => d.dia);
  $: valoresDias = ($datosNegocio.ventasPorDia ?? []).map((d) => d.totalVentas);

  // Datos para el gráfico multi-local
  $: diasUnicos = [...new Set(($datosNegocio.ventasPorDiaYLocal ?? []).map((d) => d.dia))];
  $: datosMultiLocal = localesDisponibles.map((local, indice) => {
    const colores = [
      obtenerVariable('--color-secundario', '#00b4d8'),
      obtenerVariable('--color-primario', '#03045e'),
      obtenerVariable('--color-acento', '#80ffdb')
    ];
    const ventasDelLocal = diasUnicos.map((dia) => {
      const registro = ($datosNegocio.ventasPorDiaYLocal ?? []).find(
        (v) => v.dia === dia && v.local === local.id
      );
      return registro?.ventas ?? 0;
    });
    return {
      localId: local.id,
      nombre: local.nombre,
      valores: ventasDelLocal,
      color: colores[indice % colores.length]
    };
  });

  /**
   * Obtiene el valor de una variable CSS
   * @param {string} nombre
   * @param {string} respaldo
   */
  const obtenerVariable = (nombre, respaldo) => {
    if (typeof document === 'undefined') {
      return respaldo;
    }
    const valor = getComputedStyle(document.documentElement).getPropertyValue(nombre).trim();
    return valor || respaldo;
  };

  $: nombreLocal = (localesDisponibles.find((local) => local.id === localSeleccionado) ?? {}).nombre ?? `Local ${localSeleccionado}`;

  $: totalSemanal = ventasOrdenadas.reduce((acumulado, venta) => acumulado + venta.cantidad, 0);
  $: promedioVenta = ventasOrdenadas.length ? Math.round(totalSemanal / ventasOrdenadas.length) : 0;
</script>

<section class="section">
  <div class="hero-gradient mb-6">
    <h1 class="title heading-gradient">Insights inteligentes</h1>
    <p class="subtitle">Análisis en tiempo real para decisiones sin fricciones. Optimiza rotación, prevén quiebres.</p>
  </div>

  <!-- KPI Cards con Revelaciones Clave -->
  <div class="columns is-multiline mb-6">
    <div class="column is-half-tablet is-one-third-desktop">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon">⭐</span>
          <h3 class="insight-title">Producto Estrella</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">{ventasOrdenadas[0]?.producto?.nombre ?? 'N/A'}</p>
          <p class="insight-metric">{ventasOrdenadas[0]?.cantidad ?? 0} unidades/semana</p>
          <p class="insight-description">Tu producto más vendido. Asegura stock constante.</p>
        </div>
      </div>
    </div>

    <div class="column is-half-tablet is-one-third-desktop">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon">📈</span>
          <h3 class="insight-title">Total Semanal</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">{totalSemanal}</p>
          <p class="insight-metric">Promedio: {promedioVenta} por producto</p>
          <p class="insight-description">Volumen de ventas en {nombreLocal}</p>
        </div>
      </div>
    </div>

    <div class="column is-half-tablet is-one-third-desktop">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon">🎯</span>
          <h3 class="insight-title">Recomendación</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">Reponer stock</p>
          <p class="insight-metric">Productos críticos: {ventasOrdenadas.length}</p>
          <p class="insight-description">Revisa productos con stock bajo para evitar quiebres.</p>
        </div>
      </div>
    </div>
  </div>

  <!-- Sección de Análisis Detallado -->
  <div class="box">
    <h2 class="title is-4">Análisis Detallado</h2>

    <!-- Gráfico comparativo de ventas por día y por local -->
    <div class="box mt-5 mb-5">
      <h2 class="subtitle is-5">Comparativa de ventas por local (últimos 7 días)</h2>
      <p class="help mb-3">Selecciona/deselecciona locales para comparar desempeño</p>
      <GraficoLineasMultiple
        titulo="Ventas diarias"
        dias={diasUnicos}
        locales={localesDisponibles}
        datosGrafico={datosMultiLocal}
        nombreGrafico="tendencia-por-local"
      />
    </div>

    <!-- Separador visual -->
    <hr />

    <!-- Título de la sección de análisis por local -->
    <h2 class="subtitle is-4 mt-5">Análisis por local</h2>

    <!-- Botones de selección de locales (horizontales con espaciado simétrico) -->
    <div class="local-selector mt-5 mb-5">
      {#each localesDisponibles as local}
        <button
          class={`local-btn ${localSeleccionado === local.id ? 'is-active' : ''}`}
          on:click={() => (localSeleccionado = local.id)}
          aria-pressed={localSeleccionado === local.id}
        >
          {local.nombre}
        </button>
      {/each}
    </div>

    <div class="box mt-5">
      <GraficoBarras
        titulo={`Ventas semanales por producto (${nombreLocal})`}
        etiquetas={etiquetas}
        valores={valores}
        nombreGrafico="ventas-producto-{localSeleccionado}"
      />
      <p class="help mt-3">💡 Escribe al asistente: "explícame el gráfico ventas-producto-{localSeleccionado}"</p>
    </div>
  </div>
</section>