<script>
  import { datosNegocio } from "$lib/stores/datosNegocio.js";
  import { onMount } from "svelte";
  import {
    Package,
    Storefront,
    CheckCircle,
    ArrowUp,
    ArrowDown,
    MagnifyingGlass,
    Funnel,
  } from "phosphor-svelte";

  onMount(() => {
    datosNegocio.cargarDatos();
  });

  let localSeleccionado = 0;
  let ordenarPor = "stock"; // Default sort by stock to see critical items first
  let ordenAscendente = true;
  let busqueda = "";

  // Esta reacción asegura que siempre tengamos un local activo cuando el store cargue datos
  $: {
    const localesDisponibles = $datosNegocio.locales ?? [];
    if (!localSeleccionado && localesDisponibles.length) {
      localSeleccionado = localesDisponibles[0].id;
    }
  }

  // Filtrado y búsqueda
  $: productosFiltrados = ($datosNegocio.productos ?? [])
    .filter((p) => {
      if (!busqueda) return true;
      const term = busqueda.toLowerCase();
      return (
        p.nombre.toLowerCase().includes(term) ||
        p.sku.toLowerCase().includes(term) ||
        p.categoriaId.toString().includes(term)
      );
    })
    .map((producto) => {
      // Búsqueda real en el array de stock plano del store
      const stockEntry = ($datosNegocio.stock ?? []).find(
        (s) =>
          s.producto_id === producto.id && s.local_id === localSeleccionado,
      );

      const stockActual = stockEntry ? stockEntry.cantidad : 0;

      return {
        ...producto,
        stockActual,
        // Mapeo de nombre de categoría si es necesario, o usar categoriaId temporalmente
        categoria: producto.categoriaId || "Sin categoría",
      };
    });

  // Ordenamiento
  $: productosOrdenados = [...productosFiltrados].sort((a, b) => {
    let valorA = 0;
    let valorB = 0;

    if (ordenarPor === "nombre") {
      valorA = a.nombre.toLowerCase() > b.nombre.toLowerCase() ? 1 : -1;
      valorB = 0;
    } else if (ordenarPor === "sku") {
      const skuA = (a.sku || "").toString();
      const skuB = (b.sku || "").toString();
      valorA = skuA.toLowerCase() > skuB.toLowerCase() ? 1 : -1;
      valorB = 0;
    } else if (ordenarPor === "categoria") {
      const catA = (a.categoria || "").toString();
      const catB = (b.categoria || "").toString();
      valorA = catA.toLowerCase() > catB.toLowerCase() ? 1 : -1;
      valorB = 0;
    } else if (ordenarPor === "stock") {
      valorA = a.stockActual;
      valorB = b.stockActual;
    }

    const resultado = valorA < valorB ? -1 : valorA > valorB ? 1 : 0;
    return ordenAscendente ? resultado : -resultado;
  });

  const cambiarOrdenamiento = (columna) => {
    if (ordenarPor === columna) {
      ordenAscendente = !ordenAscendente;
    } else {
      ordenarPor = columna;
      ordenAscendente = true;
    }
  };
</script>

<div class="dashboard-view">
  <!-- Header -->
  <header class="page-header mb-5">
    <div>
      <h1 class="title is-4 mb-2">Dashboard</h1>
      <p class="subtitle is-6 has-text-grey">Vista general del inventario</p>
    </div>
    <div class="header-actions">
      <div class="fecha-actual">
        {new Date().toLocaleDateString("es-ES", {
          weekday: "long",
          year: "numeric",
          month: "long",
          day: "numeric",
        })}
      </div>
    </div>
  </header>

  <!-- Widgets -->
  <div class="columns is-multiline mb-5">
    <div class="column is-4-desktop">
      <div class="widget-card">
        <div class="widget-icon primary">
          <Package size={32} />
        </div>
        <div class="widget-info">
          <p class="widget-label">Total SKUs</p>
          <p class="widget-value">{$datosNegocio.productos?.length || 0}</p>
        </div>
      </div>
    </div>

    <div class="column is-4-desktop">
      <div class="widget-card">
        <div class="widget-icon secondary">
          <Storefront size={32} />
        </div>
        <div class="widget-info">
          <p class="widget-label">Locales Activos</p>
          <p class="widget-value">{$datosNegocio.locales?.length || 0}</p>
        </div>
      </div>
    </div>

    <div class="column is-4-desktop">
      <div class="widget-card">
        <div class="widget-icon success">
          <CheckCircle size={32} />
        </div>
        <div class="widget-info">
          <p class="widget-label">Disponibilidad</p>
          <p class="widget-value">98.5%</p>
        </div>
      </div>
    </div>
  </div>

  <!-- DataGrid Area -->
  <div class="box datagrid-panel">
    <div class="datagrid-header mb-4">
      <div class="tabs-locales">
        {#each $datosNegocio.locales as local}
          <button
            class={`tab-local ${localSeleccionado === local.id ? "active" : ""}`}
            on:click={() => (localSeleccionado = local.id)}
          >
            {local.nombre}
          </button>
        {/each}
      </div>

      <div class="search-box">
        <MagnifyingGlass size={18} class="search-icon" />
        <input
          type="text"
          placeholder="Buscar producto..."
          bind:value={busqueda}
          class="search-input"
        />
      </div>
    </div>

    <div class="table-container">
      <table class="table is-fullwidth is-hoverable modern-table">
        <thead>
          <tr>
            <th
              class="clickable"
              on:click={() => cambiarOrdenamiento("nombre")}
            >
              Producto
              {#if ordenarPor === "nombre"}
                <span class="sort-icon">{ordenAscendente ? "↑" : "↓"}</span>
              {/if}
            </th>
            <th class="clickable" on:click={() => cambiarOrdenamiento("sku")}>
              SKU
              {#if ordenarPor === "sku"}
                <span class="sort-icon">{ordenAscendente ? "↑" : "↓"}</span>
              {/if}
            </th>
            <th
              class="clickable"
              on:click={() => cambiarOrdenamiento("categoria")}
            >
              Categoría
              {#if ordenarPor === "categoria"}
                <span class="sort-icon">{ordenAscendente ? "↑" : "↓"}</span>
              {/if}
            </th>
            <th class="has-text-right">Precio</th>
            <th
              class="has-text-right clickable"
              on:click={() => cambiarOrdenamiento("stock")}
            >
              Stock
              {#if ordenarPor === "stock"}
                <span class="sort-icon">{ordenAscendente ? "↑" : "↓"}</span>
              {/if}
            </th>
            <th class="has-text-right">Estado</th>
          </tr>
        </thead>
        <tbody>
          {#each productosOrdenados as producto}
            <tr>
              <td
                class="has-text-weight-medium text-primary"
                data-label="Producto">{producto.nombre}</td
              >
              <td class="is-family-monospace has-text-grey" data-label="SKU"
                >{producto.sku || "S/N"}</td
              >
              <td data-label="Categoría"
                ><span class="tag is-light">{producto.categoria}</span></td
              >
              <td class="has-text-right is-family-monospace" data-label="Precio"
                >${producto.precioUnitario || 0}</td
              >
              <td
                class="has-text-right has-text-weight-semibold"
                data-label="Stock">{producto.stockActual}</td
              >
              <td class="has-text-right" data-label="Estado">
                {#if producto.stockActual === 0}
                  <span class="status-badge critical">Sin Stock</span>
                {:else if producto.stockActual < 5}
                  <span class="status-badge warning">Bajo</span>
                {:else}
                  <span class="status-badge success">Normal</span>
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
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    border-bottom: 1px solid var(--color-borde);
    padding-bottom: 1rem;
  }

  .fecha-actual {
    font-size: 0.9rem;
    color: #666;
    text-transform: capitalize;
  }

  /* Widgets */
  .widget-card {
    background: #fff;
    border-radius: 12px;
    padding: 1.5rem;
    display: flex;
    align-items: center;
    gap: 1.5rem;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    border: 1px solid var(--color-borde);
  }

  .widget-icon {
    width: 64px;
    height: 64px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .widget-icon.primary {
    background: rgba(3, 4, 94, 0.1);
    color: var(--color-primario);
  }
  .widget-icon.secondary {
    background: rgba(0, 180, 216, 0.1);
    color: var(--color-secundario);
  }
  .widget-icon.success {
    background: rgba(56, 142, 60, 0.1);
    color: #388e3c;
  }

  .widget-label {
    font-size: 0.85rem;
    color: #666;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    font-weight: 600;
  }
  .widget-value {
    font-size: 2rem;
    font-weight: 700;
    color: var(--color-texto);
    line-height: 1.2;
  }

  /* DataGrid Panel */
  .datagrid-panel {
    padding: 1.5rem;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  }

  .datagrid-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 1rem;
  }

  .tabs-locales {
    display: flex;
    gap: 0.5rem;
    background: #f1f3f5;
    padding: 0.25rem;
    border-radius: 8px;
  }

  .tab-local {
    padding: 0.5rem 1rem;
    border: none;
    background: transparent;
    border-radius: 6px;
    font-weight: 600;
    font-size: 0.9rem;
    color: #666;
    cursor: pointer;
    transition: all 0.2s;
  }

  .tab-local.active {
    background: #fff;
    color: var(--color-primario);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }

  .search-box {
    position: relative;
    width: 300px;
  }

  .search-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #aaa;
  }

  .search-input {
    width: 100%;
    padding: 0.6rem 1rem 0.6rem 2.5rem;
    border: 1px solid var(--color-borde);
    border-radius: 8px;
    outline: none;
    transition: border-color 0.2s;
  }

  .search-input:focus {
    border-color: var(--color-secundario);
  }

  /* Table modernization */
  .modern-table thead th {
    border-bottom: 2px solid #f1f3f5;
    color: #888;
    text-transform: uppercase;
    font-size: 0.75rem;
    letter-spacing: 0.5px;
    padding-bottom: 1rem;
  }

  .clickable {
    cursor: pointer;
    user-select: none;
  }
  .clickable:hover {
    color: var(--color-primario);
  }

  .modern-table tbody td {
    border-bottom: 1px solid #f8f9fa;
    padding: 1rem 0.75rem;
    vertical-align: middle;
  }

  .text-primary {
    color: var(--color-primario);
  }

  .status-badge {
    font-size: 0.75rem;
    padding: 0.25rem 0.75rem;
    border-radius: 99px;
    font-weight: 700;
  }

  .status-badge.critical {
    background: rgba(211, 47, 47, 0.1);
    color: #d32f2f;
  }
  .status-badge.warning {
    background: rgba(245, 124, 0, 0.1);
    color: #f57c00;
  }
  .status-badge.success {
    background: rgba(56, 142, 60, 0.1);
    color: #388e3c;
  }

  @media (max-width: 768px) {
    .datagrid-header {
      flex-direction: column;
      align-items: stretch;
    }

    .tabs-locales {
      overflow-x: auto;
      padding-bottom: 0.5rem;
    }

    .search-box {
      width: 100%;
    }

    /* Table Mobile Card View */
    .table-container {
      overflow-x: visible; /* Permitir que las cards usen el espacio */
    }

    .modern-table thead {
      display: none; /* Ocultar encabezados en móvil */
    }

    .modern-table tbody tr {
      display: block;
      background: #fff;
      border: 1px solid var(--color-borde);
      border-radius: 12px;
      margin-bottom: 1rem;
      padding: 1rem;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02);
    }

    .modern-table tbody td {
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 1px solid #f8f9fa;
      padding: 0.75rem 0;
      text-align: right;
    }

    .modern-table tbody td:last-child {
      border-bottom: none;
    }

    .modern-table tbody td::before {
      content: attr(data-label);
      font-weight: 600;
      color: #888;
      font-size: 0.85rem;
      text-transform: uppercase;
      text-align: left;
      margin-right: 1rem;
    }
  }
</style>
