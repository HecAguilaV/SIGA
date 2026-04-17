<script>
  import { datosNegocio } from "$lib/stores/datosNegocio.js";
  import { onMount } from "svelte";
  import { fly, fade } from "svelte/transition";
  import {
    Package,
    Storefront,
    ArrowUpRight,
    MagnifyingGlass,
    Funnel,
    CurrencyDollar,
    TrendUp,
    TrendDown
  } from "phosphor-svelte";

  onMount(() => {
    datosNegocio.cargarDatos();
  });

  let localSeleccionado = 0;
  let ordenarPor = "stock"; 
  let ordenAscendente = true;
  let busqueda = "";

  $: {
    const localesDisponibles = $datosNegocio.locales ?? [];
    if (!localSeleccionado && localesDisponibles.length) {
      localSeleccionado = localesDisponibles[0].id;
    }
  }

  $: categoriasMap = new Map(
    ($datosNegocio.categorias ?? []).map((c) => [c.id, c.nombre]),
  );

  $: productosFiltrados = ($datosNegocio.productos ?? [])
    .filter((p) => {
      if (!busqueda) return true;
      const term = busqueda.toLowerCase();
      const nombreCategoria = categoriasMap.get(p.categoriaId)?.toLowerCase() || "";
      return (
        p.nombre.toLowerCase().includes(term) ||
        p.codigoBarras?.toLowerCase().includes(term) ||
        nombreCategoria.includes(term)
      );
    })
    .map((producto) => {
      const stockEntry = ($datosNegocio.stock ?? []).find(
        (s) => s.producto_id === producto.id && s.local_id === localSeleccionado,
      );
      const stockActual = stockEntry ? stockEntry.cantidad : 0;
      const nombreCategoria = categoriasMap.get(producto.categoriaId) || "Sin categoría";
      return { ...producto, stockActual, categoria: nombreCategoria };
    });

  $: productosOrdenados = [...productosFiltrados].sort((a, b) => {
    let valorA = 0; let valorB = 0;
    if (ordenarPor === "nombre") { valorA = a.nombre.toLowerCase() > b.nombre.toLowerCase() ? 1 : -1; valorB = 0; }
    else if (ordenarPor === "stock") { valorA = a.stockActual; valorB = b.stockActual; }
    const resultado = valorA < valorB ? -1 : valorA > valorB ? 1 : 0;
    return ordenAscendente ? resultado : -resultado;
  });

  const cambiarOrdenamiento = (columna) => {
    if (ordenarPor === columna) { ordenAscendente = !ordenAscendente; }
    else { ordenarPor = columna; ordenAscendente = true; }
  };

  $: valorInventario = ($datosNegocio.stock ?? []).reduce((acc, stockItem) => {
    const prod = ($datosNegocio.productos ?? []).find(p => p.id === stockItem.producto_id);
    return acc + stockItem.cantidad * (prod?.precioUnitario || 0);
  }, 0);
</script>

<div class="dashboard-container" in:fade={{ duration: 400 }}>
  <!-- Header Animado -->
  <header class="dashboard-header">
    <div class="title-group" in:fly={{ y: -20, duration: 500 }}>
      <h1 class="main-title">Centro de Control</h1>
      <p class="sub-title">Gestión de activos en tiempo real</p>
    </div>
    
    <div class="header-widgets">
        <div class="glass-pill">
            <span class="pulse-indicator"></span>
            Live Data
        </div>
    </div>
  </header>

  <!-- Key Metrics Grid -->
  <div class="metrics-grid">
    <div class="metric-card glass-card glow-accent" in:fly={{ y: 20, duration: 600, delay: 100 }}>
      <div class="metric-header">
        <div class="metric-icon primary"><Package weight="duotone" size={24} /></div>
      </div>
      <div class="metric-body">
        <span class="metric-label">Productos</span>
        <h3 class="metric-value">{$datosNegocio.productos?.length || 0}</h3>
      </div>
    </div>

    <div class="metric-card glass-card" in:fly={{ y: 20, duration: 600, delay: 200 }}>
      <div class="metric-header">
        <div class="metric-icon secondary"><Storefront weight="duotone" size={24} /></div>
      </div>
      <div class="metric-body">
        <span class="metric-label">Locales</span>
        <h3 class="metric-value">{$datosNegocio.locales?.length || 0}</h3>
      </div>
    </div>

    <div class="metric-card glass-card" in:fly={{ y: 20, duration: 600, delay: 300 }}>
      <div class="metric-header">
        <div class="metric-icon success"><CurrencyDollar weight="duotone" size={24} /></div>
      </div>
      <div class="metric-body">
        <span class="metric-label">Capital Total</span>
        <h3 class="metric-value">${new Intl.NumberFormat("es-CL").format(valorInventario)}</h3>
      </div>
    </div>
  </div>

  <!-- Main Content Area -->
  <div class="main-panel glass-card" in:fly={{ y: 30, duration: 700, delay: 400 }}>
    <div class="panel-header">
      <div class="filter-tabs">
        {#each $datosNegocio.locales as local}
          <button 
            class="filter-tab" 
            class:active={localSeleccionado === local.id}
            on:click={() => (localSeleccionado = local.id)}
          >
            {local.nombre}
          </button>
        {/each}
      </div>

      <div class="panel-actions">
        <div class="premium-search">
            <MagnifyingGlass size={16} />
            <input type="text" placeholder="Buscar activos..." bind:value={busqueda} />
        </div>
      </div>
    </div>

    <div class="table-wrapper">
      <table class="premium-table">
        <thead>
          <tr>
            <th on:click={() => cambiarOrdenamiento("nombre")} class="sortable">Producto</th>
            <th>Categoría</th>
            <th class="right">Precio</th>
            <th on:click={() => cambiarOrdenamiento("stock")} class="sortable right">Stock</th>
            <th class="right">Estado</th>
          </tr>
        </thead>
        <tbody>
          {#each productosOrdenados as producto (producto.id)}
            <tr in:fly={{ x: -10, duration: 300 }}>
              <td>
                <div class="product-cell">
                   <div class="p-avatar">{producto.nombre.charAt(0)}</div>
                   <span>{producto.nombre}</span>
                </div>
              </td>
              <td><span class="badge-subtle">{producto.categoria}</span></td>
              <td class="right mono">${new Intl.NumberFormat("es-CL").format(producto.precioUnitario || 0)}</td>
              <td class="right mono"><strong>{producto.stockActual}</strong></td>
              <td class="right">
                {#if producto.stockActual === 0}
                  <div class="dot-status danger">Agotado</div>
                {:else if producto.stockActual < 5}
                  <div class="dot-status warning">Crítico</div>
                {:else}
                  <div class="dot-status success">Óptimo</div>
                {/if}
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  </div>
</div>

<style>
  .dashboard-container {
    padding: 2rem;
    max-width: 1400px;
    margin: 0 auto;
  }

  .dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2.5rem;
  }

  .main-title {
    font-size: 1.75rem;
    font-weight: 800;
    letter-spacing: -0.03em;
    color: var(--text-primary);
  }

  .sub-title {
    color: var(--text-secondary);
    font-size: 0.95rem;
  }

  /* Pills & Indicators */
  .glass-pill {
    padding: 6px 14px;
    background: var(--surface-secondary);
    border: 1px solid var(--border-subtle);
    border-radius: 99px;
    font-size: 12px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
    color: var(--text-secondary);
  }

  .pulse-indicator {
    width: 6px;
    height: 6px;
    background: var(--status-success);
    border-radius: 50%;
    box-shadow: 0 0 0 rgba(16, 185, 129, 0.4);
    animation: pulse 2s infinite;
  }

  @keyframes pulse {
    0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
    70% { box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); }
    100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
  }

  /* Metrics Grid */
  .metrics-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
  }

  .metric-card {
    padding: 1.5rem;
    border-radius: 16px;
    display: flex;
    flex-direction: column;
    gap: 1.25rem;
  }

  .metric-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .metric-icon {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .metric-icon.primary { background: rgba(var(--accent-primary-rgb), 0.1); color: var(--accent-primary); }
  .metric-icon.secondary { background: rgba(0, 180, 216, 0.1); color: #00B4D8; }
  .metric-icon.success { background: rgba(16, 185, 129, 0.1); color: var(--status-success); }

  .trend {
    font-size: 11px;
    font-weight: 700;
    padding: 4px 8px;
    border-radius: 6px;
    background: var(--surface-secondary);
    display: flex;
    align-items: center;
    gap: 4px;
  }
  .trend.up { color: var(--status-success); }

  .metric-label {
    font-size: 13px;
    font-weight: 600;
    color: var(--text-tertiary);
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .metric-value {
    font-size: 2rem;
    font-weight: 800;
    color: var(--text-primary);
    letter-spacing: -0.02em;
  }

  /* Content Panel */
  .main-panel {
    border-radius: 20px;
    overflow: hidden;
  }

  .panel-header {
    padding: 1.25rem 1.5rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid var(--border-subtle);
    background: rgba(var(--surface-primary-rgb), 0.3);
  }

  .filter-tabs {
    display: flex;
    gap: 4px;
    background: var(--surface-secondary);
    padding: 4px;
    border-radius: 10px;
  }

  .filter-tab {
    padding: 6px 14px;
    border: none;
    background: transparent;
    border-radius: 7px;
    font-size: 13px;
    font-weight: 600;
    color: var(--text-tertiary);
    cursor: pointer;
    transition: all 0.2s;
  }

  .filter-tab.active {
    background: var(--surface-primary);
    color: var(--accent-primary);
    box-shadow: var(--shadow-base);
  }

  .premium-search {
    display: flex;
    align-items: center;
    gap: 10px;
    background: var(--surface-secondary);
    border: 1px solid var(--border-subtle);
    padding: 6px 14px;
    border-radius: 10px;
    color: var(--text-tertiary);
  }

  .premium-search input {
    background: transparent;
    border: none;
    outline: none;
    color: var(--text-primary);
    font-size: 13px;
    width: 200px;
  }

  /* Table styling */
  .table-wrapper {
    overflow-x: auto;
  }

  .premium-table {
    width: 100%;
    border-collapse: collapse;
  }

  .premium-table th {
    text-align: left;
    padding: 1rem 1.5rem;
    font-size: 11px;
    font-weight: 700;
    color: var(--text-tertiary);
    text-transform: uppercase;
    letter-spacing: 0.05em;
    border-bottom: 1px solid var(--border-subtle);
  }

  .premium-table th.sortable { cursor: pointer; }
  .premium-table th.sortable:hover { color: var(--accent-primary); }
  .premium-table th.right { text-align: right; }

  .premium-table td {
    padding: 0.875rem 1.5rem;
    font-size: 13px;
    color: var(--text-secondary);
    border-bottom: 1px solid var(--border-subtle);
    vertical-align: middle;
  }

  .premium-table tr:hover td {
    background: rgba(var(--accent-primary-rgb), 0.02);
    color: var(--text-primary);
  }

  .product-cell {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .p-avatar {
    width: 28px;
    height: 28px;
    background: var(--surface-secondary);
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 800;
    font-size: 11px;
    color: var(--accent-primary);
  }

  .badge-subtle {
    padding: 3px 10px;
    background: var(--surface-secondary);
    border-radius: 6px;
    font-size: 11px;
    font-weight: 600;
  }

  .mono { font-family: 'JetBrains Mono', monospace; font-variant-numeric: tabular-nums; }
  .right { text-align: right; }

  .dot-status {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    font-size: 12px;
  }

  .dot-status::before {
    content: '';
    width: 6px;
    height: 6px;
    border-radius: 50%;
  }

  .dot-status.success { color: var(--status-success); }
  .dot-status.success::before { background: var(--status-success); box-shadow: 0 0 8px var(--status-success); }
  
  .dot-status.warning { color: var(--status-warning); }
  .dot-status.warning::before { background: var(--status-warning); }

  .dot-status.danger { color: var(--status-danger); }
  .dot-status.danger::before { background: var(--status-danger); }

  @media (max-width: 900px) {
    .dashboard-header { flex-direction: column; align-items: flex-start; gap: 1rem; }
    .premium-search input { width: 100%; }
    .panel-header { flex-direction: column; gap: 1rem; align-items: stretch; }
  }
</style>
