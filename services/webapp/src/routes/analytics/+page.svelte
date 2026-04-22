<script>
  import BarChart from "$lib/components/BarChart.svelte";
  import LineChart from "$lib/components/LineChart.svelte";
  import LineChartMultiple from "$lib/components/LineChartMultiple.svelte";
  import { businessStore } from "$lib/stores/businessStore.js";
  import { Star, TrendUp, Target, Warning } from "phosphor-svelte";

  let selectedStore = 0;

  /** @typedef {{ storeId: number; productId: number; quantity: number; product: { id: number; name: string; barcode: string; categoryId: number; unitPrice: number } }} EnrichedSale */

  $: availableStores = $businessStore.stores ?? [];
  $: {
    if (!selectedStore && availableStores.length) {
      selectedStore = availableStores[0].id;
    }
  }

  $: productsById = new Map(
    ($businessStore.products ?? []).map((product) => [product.id, product]),
  );

  $: filteredSales = /** @type {EnrichedSale[]} */ (
    ($businessStore.weeklySales ?? [])
      .filter((sale) => sale.storeId === selectedStore)
      .map((sale) => {
        const product = productsById.get(sale.productId);
        if (!product) return null;
        return { ...sale, product };
      })
      .filter((sale) => sale !== null)
  );

  $: sortedSales = [...filteredSales].sort(
    (a, b) => b.quantity - a.quantity,
  );

  $: labels = sortedSales.map((sale) => sale.product.name);
  $: values = sortedSales.map((sale) => sale.quantity);

  // Data for daily sales line chart
  $: dailyLabels = ($businessStore.dailySales ?? []).map((d) => d.day);
  $: dailyValues = ($businessStore.dailySales ?? []).map((d) => d.totalSales);

  // Data for multi-store chart
  $: uniqueDays = [
    ...new Set(($businessStore.dailySalesByStore ?? []).map((d) => d.day)),
  ];

  $: multiStoreData = availableStores.map((store, index) => {
    const colors = [
      getCssVariable("--color-secondary", "#00b4d8"),
      getCssVariable("--color-primary", "#03045e"),
      getCssVariable("--color-accent", "#80ffdb"),
    ];
    const storeSales = uniqueDays.map((day) => {
      const record = ($businessStore.dailySalesByStore ?? []).find(
        (v) => v.day === day && v.store === store.id,
      );
      return record?.sales ?? 0;
    });
    return {
      storeId: store.id,
      name: store.name,
      values: storeSales,
      color: colors[index % colors.length],
    };
  });

  const getCssVariable = (name, fallback) => {
    if (typeof document === "undefined") return fallback;
    const value = getComputedStyle(document.documentElement)
      .getPropertyValue(name)
      .trim();
    return value || fallback;
  };

  $: storeName =
    (availableStores.find((s) => s.id === selectedStore) ?? {})
      .name ?? `Store ${selectedStore}`;

  $: weeklyTotal = sortedSales.reduce(
    (acc, sale) => acc + sale.quantity,
    0,
  );
  $: averageSales = sortedSales.length
    ? Math.round(weeklyTotal / sortedSales.length)
    : 0;

  $: topRevenue = [...filteredSales].sort(
    (a, b) =>
      b.quantity * (b.product.unitPrice || 0) -
      a.quantity * (a.product.unitPrice || 0),
  )[0];

  // Logic for Slow Moving Inventory
  $: slowMovingItem = (() => {
    const stockStore = $businessStore.stock || [];
    const allProds = $businessStore.products || [];

    const currentStoreStock = stockStore.filter(
      (s) => s.storeId === selectedStore,
    );
    const stockMap = new Map(
      currentStoreStock.map((s) => [s.productId, s.quantity]),
    );

    const salesMap = new Map(
      filteredSales.map((v) => [v.productId, v.quantity]),
    );

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
    <h1 class="title heading-gradient">Intelligent Insights</h1>
    <p class="subtitle">
      Real-time analytics for frictionless decisions. Optimize rotation and prevent stockouts.
    </p>
  </div>

  <!-- KPI Cards with Key Revelations -->
  <div class="columns is-multiline mb-6">
    <!-- Star Product (Units) -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon">
            <Star size={28} color="var(--color-secondary)" weight="light" />
          </span>
          <h3 class="insight-title">Best Seller (Units)</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">
            {sortedSales[0]?.product?.name ?? "N/A"}
          </p>
          <p class="insight-metric">
            {sortedSales[0]?.quantity ?? 0} units
          </p>
          <p class="insight-description">High physical rotation.</p>
        </div>
      </div>
    </div>

    <!-- Profitable Product (Revenue) -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon">
            <TrendUp size={28} color="var(--color-primary)" weight="light" />
          </span>
          <h3 class="insight-title">Highest Revenue</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">
            {topRevenue?.product?.name ?? "N/A"}
          </p>
          <p class="insight-metric">
            Generated $ {(
              (topRevenue?.quantity || 0) *
              (topRevenue?.product?.unitPrice || 0)
            ).toLocaleString("es-CL")}
          </p>
          <p class="insight-description">Gross billing leader.</p>
        </div>
      </div>
    </div>

    <!-- Weekly Total -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon">
            <Target size={28} color="#555" weight="light" />
          </span>
          <h3 class="insight-title">Weekly Total</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">{weeklyTotal}</p>
          <p class="insight-metric">Average: {averageSales} / prod</p>
          <p class="insight-description">Volume in {storeName}</p>
        </div>
      </div>
    </div>

    <!-- Alerts/Recommendation -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon">
            <Warning size={28} color="#ff4757" weight="light" />
          </span>
          <h3 class="insight-title">Stock Alerts</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">{sortedSales.length} Items</p>
          <p class="insight-metric">With recent movement</p>
          <p class="insight-description">Review availability.</p>
        </div>
      </div>
    </div>

    <!-- Slow Moving (Capital Inmovilizado) -->
    <div class="column is-one-quarter-desktop is-half-tablet">
      <div class="insight-card">
        <div class="insight-header">
          <span class="insight-icon">
            <Target size={28} color="#718096" weight="light" />
          </span>
          <h3 class="insight-title">Slow Moving</h3>
        </div>
        <div class="insight-content">
          <p class="insight-value">{slowMovingItem?.name ?? "Optimal Status"}</p>
          <p class="insight-metric">
            {slowMovingItem
              ? `${slowMovingItem.stock} units without sales`
              : "No stagnant products found"}
          </p>
          <p class="insight-description">Potential idle capital.</p>
        </div>
      </div>
    </div>
  </div>

  <!-- Detailed Analysis Section -->
  <div class="box">
    <h2 class="title is-4">Detailed Analysis</h2>

    <!-- Comparative chart of sales by day and store -->
    <div class="box mt-5 mb-5">
      <h2 class="subtitle is-5">Store Sales Comparison (Last 7 Days)</h2>
      <p class="help mb-3">Select/deselect stores to compare performance</p>
      <LineChartMultiple
        titulo="Daily Sales"
        dias={uniqueDays}
        locales={availableStores}
        datosGrafico={multiStoreData}
        nombreGrafico="store-trend"
      />
    </div>

    <hr />

    <h2 class="subtitle is-4 mt-5">Analysis by Store</h2>

    <div class="local-selector mt-5 mb-5">
      {#each availableStores as store}
        <button
          class={`local-btn ${selectedStore === store.id ? "is-active" : ""}`}
          on:click={() => (selectedStore = store.id)}
          aria-pressed={selectedStore === store.id}
        >
          {store.name}
        </button>
      {/each}
    </div>

    <div class="box mt-5">
      <BarChart
        titulo={`Weekly Sales by Product (${storeName})`}
        {labels}
        {values}
        nombreGrafico="product-sales-{selectedStore}"
      />
      <p class="help mt-3">
        💡 Ask the assistant: "explain the chart product-sales-{selectedStore}"
      </p>
    </div>
  </div>
</section>
