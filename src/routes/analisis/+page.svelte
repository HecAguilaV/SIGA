<script>
  import GraficoBarras from "$lib/components/GraficoBarras.svelte";
  import GraficoLineas from "$lib/components/GraficoLineas.svelte";
  import GraficoLineasMultiple from "$lib/components/GraficoLineasMultiple.svelte";
  import { datosNegocio } from "$lib/stores/datosNegocio.js";
  import { Star, TrendUp, Target } from "phosphor-svelte";

  let localSeleccionado = 0;

  /** @typedef {{ localId: number; productoId: number; cantidad: number; producto: { id: number; nombre: string; sku: string; categoria: string; stock: Record<number, number>; precioUnitario: number } }} VentaEnriquecida */

  $: localesDisponibles = $datosNegocio.locales ?? [];
  $: {
    if (!localSeleccionado && localesDisponibles.length) {
      localSeleccionado = localesDisponibles[0].id;
    }
  }

  $: productosPorId = new Map(
    ($datosNegocio.productos ?? []).map((producto) => [producto.id, producto]),
  );

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

  $: ventasOrdenadas = [...ventasFiltradas].sort(
    (a, b) => b.cantidad - a.cantidad,
  );

  $: etiquetas = ventasOrdenadas.map((venta) => venta.producto.nombre);

  $: valores = ventasOrdenadas.map((venta) => venta.cantidad);

  // Datos para el gráfico de líneas de ventas por día
  $: etiquetasDias = ($datosNegocio.ventasPorDia ?? []).map((d) => d.dia);
  $: valoresDias = ($datosNegocio.ventasPorDia ?? []).map((d) => d.totalVentas);

  // Datos para el gráfico multi-local
  $: diasUnicos = [
    ...new Set(($datosNegocio.ventasPorDiaYLocal ?? []).map((d) => d.dia)),
  ];
  $: datosMultiLocal = localesDisponibles.map((local, indice) => {
    const colores = [
      obtenerVariable("--color-secundario", "#00b4d8"),
      obtenerVariable("--color-primario", "#03045e"),
      obtenerVariable("--color-acento", "#80ffdb"),
    ];
    const ventasDelLocal = diasUnicos.map((dia) => {
      const registro = ($datosNegocio.ventasPorDiaYLocal ?? []).find(
        (v) => v.dia === dia && v.local === local.id,
      );
      return registro?.ventas ?? 0;
    });
    return {
      localId: local.id,
      nombre: local.nombre,
      valores: ventasDelLocal,
      color: colores[indice % colores.length],
    };
  });

  /**
   * Obtiene el valor de una variable CSS
   * @param {string} nombre
   * @param {string} respaldo
   */
  const obtenerVariable = (nombre, respaldo) => {
    if (typeof document === "undefined") {
      return respaldo;
    }
    const valor = getComputedStyle(document.documentElement)
      .getPropertyValue(nombre)
      .trim();
    return valor || respaldo;
  };

  $: nombreLocal =
    (localesDisponibles.find((local) => local.id === localSeleccionado) ?? {})
      .nombre ?? `Local ${localSeleccionado}`;

  $: totalSemanal = ventasOrdenadas.reduce(
    (acumulado, venta) => acumulado + venta.cantidad,
    0,
  );
  $: promedioVenta = ventasOrdenadas.length
    ? Math.round(totalSemanal / ventasOrdenadas.length)
    : 0;

  $: topRevenue = [...ventasFiltradas].sort(
    (a, b) =>
      b.cantidad * (b.producto.precioUnitario || 0) -
      a.cantidad * (a.producto.precioUnitario || 0),
  )[0];

  // Logic for Slow Moving Inventory
  $: bajaRotacion = (() => {
    const stockStore = $datosNegocio.stock || [];
    const allProds = $datosNegocio.productos || [];

    // 1. Get stock for current local
    const currentLocalStock = stockStore.filter(
      (s) => s.localId === localSeleccionado,
    );
    const stockMap = new Map(
      currentLocalStock.map((s) => [s.productoId, s.cantidad]),
    );

    // 2. Get sales map for current period (week)
    const salesMap = new Map(
      ventasFiltradas.map((v) => [v.productoId, v.cantidad]),
    );

    // 3. Find "Bone" (Stock > 5, Sales == 0)
    const candidates = allProds
      .map((p) => ({
        ...p,
        stock: stockMap.get(p.id) || 0,
        sales: salesMap.get(p.id) || 0,
      }))
      .filter((p) => p.stock > 5 && p.sales === 0)
      .sort((a, b) => b.stock - a.stock);

    return candidates[0];
  })();
</script>

<section class="section">
  <div class="hero-gradient mb-6">
    <h1 class="title heading-gradient">Insights inteligentes</h1>
    <p class="subtitle">
      Análisis en tiempo real para decisiones sin fricciones. Optimiza rotación,
      prevén quiebres.
    </p>
  </div>

  <!-- KPI Cards con Revelaciones Clave -->
  <div class="columns is-multiline mb-6">
    <!-- Star Product (Units) -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon"
            ><Star
              size={28}
              color="var(--color-secundario)"
              weight="light"
            /></span
          >
          <h3 class="insight-title">Más Vendido (Unidades)</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">
            {ventasOrdenadas[0]?.producto?.nombre ?? "N/A"}
          </p>
          <p class="insight-metric">
            {ventasOrdenadas[0]?.cantidad ?? 0} unidades
          </p>
          <p class="insight-description">Alta rotación física.</p>
        </div>
      </div>
    </div>

    <!-- Profitable Product (Revenue) -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon"
            ><TrendUp
              size={28}
              color="var(--color-primario)"
              weight="light"
            /></span
          >
          <h3 class="insight-title">Mayor Ingreso</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">
            {topRevenue?.producto?.nombre ?? "N/A"}
          </p>
          <p class="insight-metric">
            Generó $ {(
              (topRevenue?.cantidad || 0) *
              (topRevenue?.producto?.precioUnitario || 0)
            ).toLocaleString("es-CL")}
          </p>
          <p class="insight-description">Líder en facturación bruta.</p>
        </div>
      </div>
    </div>

    <!-- Weekly Total -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon"
            ><Target size={28} color="#555" weight="light" /></span
          >
          <h3 class="insight-title">Total Semanal</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">{totalSemanal}</p>
          <p class="insight-metric">Promedio: {promedioVenta} / prod</p>
          <p class="insight-description">Volumen en {nombreLocal}</p>
        </div>
      </div>
    </div>

    <!-- Alerts/Recommendation -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon"
            ><TrendUp size={28} color="#ff4757" weight="light" /></span
          >
          <h3 class="insight-title">Alertas Stock</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">{ventasOrdenadas.length} Ítems</p>
          <p class="insight-metric">Con movimiento reciente</p>
          <p class="insight-description">Revisar disponibilidad.</p>
        </div>
      </div>
    </div>

    <!-- Slow Moving (Capital Inmovilizado) -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon"
            ><Target size={28} color="#718096" weight="light" /></span
          >
          <h3 class="insight-title">Baja Rotación</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">{bajaRotacion?.nombre ?? "Todo Óptimo"}</p>
          <p class="insight-metric">
            {bajaRotacion
              ? `${bajaRotacion.stock} unds. sin venta`
              : "No hay productos estancados"}
          </p>
          <p class="insight-description">Posible capital inmovilizado.</p>
        </div>
      </div>
    </div>
  </div>

  <!-- Sección de Análisis Detallado -->
  <div class="box">
    <h2 class="title is-4">Análisis Detallado</h2>

    <!-- Gráfico comparativo de ventas por día y por local -->
    <div class="box mt-5 mb-5">
      <h2 class="subtitle is-5">
        Comparativa de ventas por local (últimos 7 días)
      </h2>
      <p class="help mb-3">
        Selecciona/deselecciona locales para comparar desempeño
      </p>
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
          class={`local-btn ${localSeleccionado === local.id ? "is-active" : ""}`}
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
        {etiquetas}
        {valores}
        nombreGrafico="ventas-producto-{localSeleccionado}"
      />
      <p class="help mt-3">
        💡 Escribe al asistente: "explícame el gráfico ventas-producto-{localSeleccionado}"
      </p>
    </div>
  </div>
</section>
