<script>
  import { businessStore } from "$lib/stores/businessStore.js";
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
    businessStore.loadData();
  });

  let selectedStore = 0;
  let sortBy = "stock"; 
  let sortAscending = true;
  let searchQuery = "";

  $: {
    const availableStores = $businessStore.stores ?? [];
    if (!selectedStore && availableStores.length) {
      selectedStore = availableStores[0].id;
    }
  }

  $: categoriesMap = new Map(
    ($businessStore.categories ?? []).map((c) => [c.id, c.name]),
  );

  $: filteredProducts = ($businessStore.products ?? [])
    .filter((p) => {
      if (!searchQuery) return true;
      const term = searchQuery.toLowerCase();
      const categoryName = categoriesMap.get(p.categoryId)?.toLowerCase() || "";
      return (
        p.name.toLowerCase().includes(term) ||
        p.barcode?.toLowerCase().includes(term) ||
        categoryName.includes(term)
      );
    })
    .map((product) => {
      const stockEntry = ($businessStore.stock ?? []).find(
        (s) => s.productId === product.id && s.storeId === selectedStore,
      );
      const currentStock = stockEntry ? stockEntry.quantity : 0;
      const categoryName = categoriesMap.get(product.categoryId) || "No Category";
      return { ...product, currentStock, category: categoryName };
    });

  $: sortedProducts = [...filteredProducts].sort((a, b) => {
    let valA = 0; let valB = 0;
    if (sortBy === "name") { valA = a.name.toLowerCase() > b.name.toLowerCase() ? 1 : -1; valB = 0; }
    else if (sortBy === "stock") { valA = a.currentStock; valB = b.currentStock; }
    const result = valA < valB ? -1 : valA > valB ? 1 : 0;
    return sortAscending ? result : -result;
  });

  const toggleSort = (column) => {
    if (sortBy === column) { sortAscending = !sortAscending; }
    else { sortBy = column; sortAscending = true; }
  };

  $: inventoryValue = ($businessStore.stock ?? []).reduce((acc, stockItem) => {
    const prod = ($businessStore.products ?? []).find(p => p.id === stockItem.productId);
    return acc + stockItem.quantity * (prod?.unitPrice || 0);
  }, 0);
</script>

<div class="dashboard-container" in:fade={{ duration: 400 }}>
  <!-- Animated Header -->
  <header class="dashboard-header">
    <div class="title-group" in:fly={{ y: -20, duration: 500 }}>
      <h1 class="main-title">Control Center</h1>
      <p class="sub-title">Real-time asset management</p>
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
        <span class="metric-label">Products</span>
        <h3 class="metric-value">{$businessStore.products?.length || 0}</h3>
      </div>
    </div>

    <div class="metric-card glass-card" in:fly={{ y: 20, duration: 600, delay: 200 }}>
      <div class="metric-header">
        <div class="metric-icon secondary"><Storefront weight="duotone" size={24} /></div>
      </div>
      <div class="metric-body">
        <span class="metric-label">Stores</span>
        <h3 class="metric-value">{$businessStore.stores?.length || 0}</h3>
      </div>
    </div>

    <div class="metric-card glass-card" in:fly={{ y: 20, duration: 600, delay: 300 }}>
      <div class="metric-header">
        <div class="metric-icon success"><CurrencyDollar weight="duotone" size={24} /></div>
      </div>
      <div class="metric-body">
        <span class="metric-label">Total Capital</span>
        <h3 class="metric-value">${new Intl.NumberFormat("es-CL").format(inventoryValue)}</h3>
      </div>
    </div>
  </div>

  <!-- Main Content Area -->
  <div class="main-panel glass-card" in:fly={{ y: 30, duration: 700, delay: 400 }}>
    <div class="panel-header">
      <div class="filter-tabs">
        {#each $businessStore.stores as store}
          <button 
            class="filter-tab" 
            class:active={selectedStore === store.id}
            on:click={() => (selectedStore = store.id)}
          >
            {store.name}
          </button>
        {/each}
      </div>

      <div class="panel-actions">
        <div class="premium-search">
            <MagnifyingGlass size={16} />
            <input type="text" placeholder="Search assets..." bind:value={searchQuery} />
        </div>
      </div>
    </div>

    <div class="table-wrapper">
      <table class="premium-table">
        <thead>
          <tr>
            <th on:click={() => toggleSort("name")} class="sortable">Product</th>
            <th>Category</th>
            <th class="right">Price</th>
            <th on:click={() => toggleSort("stock")} class="sortable right">Stock</th>
            <th class="right">Status</th>
          </tr>
        </thead>
        <tbody>
          {#each sortedProducts as product (product.id)}
            <tr in:fly={{ x: -10, duration: 300 }}>
              <td>
                <div class="product-cell">
                   <div class="p-avatar">{product.name.charAt(0)}</div>
                   <span>{product.name}</span>
                </div>
              </td>
              <td><span class="badge-subtle">{product.category}</span></td>
              <td class="right mono">${new Intl.NumberFormat("es-CL").format(product.unitPrice || 0)}</td>
              <td class="right mono"><strong>{product.currentStock}</strong></td>
              <td class="right">
                {#if product.currentStock === 0}
                  <div class="dot-status danger">Out of Stock</div>
                {:else if product.currentStock < 5}
                  <div class="dot-status warning">Critical</div>
                {:else}
                  <div class="dot-status success">Optimal</div>
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
