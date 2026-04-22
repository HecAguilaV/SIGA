<script>
  import { onMount, onDestroy } from 'svelte';


  /** @type {string} */
  export let titulo = '';
  /** @type {string[]} */
  export let dias = [];
  /** @type {{ id: number; nombre: string }[]} */
  export let locales = [];
  /** @type {{ localId: number; nombre: string; valores: number[]; color: string }[]} */
  export let datosGrafico = [];
  /** @type {string} */
  export let nombreGrafico = '';

  /** @type {Map<number, boolean>} */
  let localesVisibles = new Map();
  /** @type {HTMLCanvasElement | null} */
  let lienzo;
  /** @type {import('chart.js').Chart | null} */
  let instanciaGrafico;

  $: {
    // Inicializar locales visibles
    locales.forEach((local) => {
      if (!localesVisibles.has(local.id)) {
        localesVisibles.set(local.id, true);
      }
    });
  }

  /**
   * @param {string} hex
   * @param {number} alpha
   */
  const hexToRgba = (hex, alpha = 1) => {
    const limpio = hex.replace('#', '').trim();
    const esCorto = limpio.length === 3;
    const expandido = esCorto ? limpio.split('').map((c) => `${c}${c}`).join('') : limpio;
    const valor = expandido.padEnd(6, '0');
    const r = parseInt(valor.slice(0, 2), 16);
    const g = parseInt(valor.slice(2, 4), 16);
    const b = parseInt(valor.slice(4, 6), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
  };

  /**
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

  const crearGrafico = async () => {
    if (!lienzo) return;

    const { default: Chart } = await import('chart.js/auto');

    const colorTexto = typeof document !== 'undefined'
      ? getComputedStyle(document.documentElement).getPropertyValue('--color-texto') || '#2c3e50'
      : '#2c3e50';

    const datasets = datosGrafico
      .filter((dataset) => localesVisibles.get(dataset.localId))
      .map((dataset) => ({
        label: dataset.nombre,
        data: dataset.valores,
        borderColor: dataset.color,
        backgroundColor: hexToRgba(dataset.color, 0.1),
        fill: true,
        tension: 0.4,
        pointRadius: 5,
        pointBackgroundColor: dataset.color,
        pointBorderColor: '#ffffff',
        pointBorderWidth: 2,
        pointHoverRadius: 7,
        borderWidth: 2
      }));

    const canvas = /** @type {HTMLCanvasElement} */ (lienzo);

    instanciaGrafico = new Chart(canvas, {
      type: 'line',
      data: {
        labels: dias,
        datasets: datasets
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          intersect: false,
          mode: 'index'
        },
        plugins: {
          legend: {
            position: 'top',
            labels: {
              color: colorTexto,
              usePointStyle: true,
              padding: 15
            }
          },
          title: {
            display: Boolean(titulo),
            text: `${titulo}${nombreGrafico ? ` (${nombreGrafico})` : ''}`,
            color: colorTexto,
            font: {
              size: 14
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              color: colorTexto
            },
            grid: {
              color: hexToRgba(colorTexto, 0.1)
            }
          },
          x: {
            ticks: {
              color: colorTexto
            },
            grid: {
              display: false
            }
          }
        }
      }
    });
  };

  onMount(() => {
    crearGrafico();
  });

  onDestroy(() => {
    instanciaGrafico?.destroy();
  });

  /**
   * @param {number} localId
   */
  const alternarLocal = (localId) => {
    localesVisibles.set(localId, !localesVisibles.get(localId));
    localesVisibles = localesVisibles; // Trigger reactivity
    
    if (instanciaGrafico) {
      const nuevosDatasets = datosGrafico
        .filter((dataset) => localesVisibles.get(dataset.localId))
        .map((dataset) => ({
          label: dataset.nombre,
          data: dataset.valores,
          borderColor: dataset.color,
          backgroundColor: hexToRgba(dataset.color, 0.1),
          fill: true,
          tension: 0.4,
          pointRadius: 5,
          pointBackgroundColor: dataset.color,
          pointBorderColor: '#ffffff',
          pointBorderWidth: 2,
          pointHoverRadius: 7,
          borderWidth: 2
        }));
      
      instanciaGrafico.data.datasets = nuevosDatasets;
      instanciaGrafico.update();
    }
  };

  $: if (instanciaGrafico && datosGrafico.length) {
    instanciaGrafico.data.labels = [...dias];
    instanciaGrafico.update();
  }
</script>

<div class="contenedor-grafico-multiple">
  <div class="selector-locales mb-4">
    {#each locales as local}
      <button
        class={`local-filter-btn ${localesVisibles.get(local.id) ? 'is-active' : ''}`}
        on:click={() => alternarLocal(local.id)}
        aria-pressed={localesVisibles.get(local.id)}
      >
        {local.nombre}
      </button>
    {/each}
  </div>

  <div class="contenedor-grafico">
    <canvas bind:this={lienzo} aria-label={titulo}></canvas>
  </div>
</div>

<style>
  .contenedor-grafico-multiple {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .selector-locales {
    display: flex;
    gap: 0.75rem;
    flex-wrap: wrap;
  }

  .local-filter-btn {
    padding: 0.5rem 1rem;
    border: 2px solid var(--color-borde);
    border-radius: 24px;
    background-color: #fff;
    color: var(--color-texto);
    cursor: pointer;
    font-size: 0.875rem;
    font-weight: 500;
    transition: all 0.2s ease;
  }

  .local-filter-btn:hover {
    border-color: var(--color-secundario);
    background-color: rgba(0, 180, 216, 0.05);
  }

  .local-filter-btn.is-active {
    background-color: var(--color-secundario);
    color: #fff;
    border-color: var(--color-secundario);
  }

  .contenedor-grafico {
    position: relative;
    width: 100%;
    min-height: 320px;
  }
</style>
