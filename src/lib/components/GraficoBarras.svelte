<script>
  import { onMount, onDestroy } from 'svelte';
  import Chart from 'chart.js/auto';

  export let titulo = '';
  /** @type {string[]} */
  export let etiquetas = [];
  /** @type {number[]} */
  export let valores = [];
  export let colorFondo = 'rgba(52, 152, 219, 0.6)';
  export let colorBorde = 'rgba(41, 128, 185, 1)';

  /** @type {HTMLCanvasElement | null} */
  let lienzo;
  /** @type {import('chart.js').Chart | null} */
  let instanciaGrafico;

  // onMount crea la instancia del gráfico una vez que el componente está en el DOM
  // Propósito: inicializar Chart.js únicamente en el cliente y garantizar que el gráfico se dibuje correctamente
  onMount(() => {
    if (!lienzo) {
      return;
    }

    const colorTexto = typeof document !== 'undefined'
      ? getComputedStyle(document.documentElement).getPropertyValue('--color-texto') || '#2c3e50'
      : '#2c3e50';

  const canvas = /** @type {HTMLCanvasElement} */ (lienzo);

  instanciaGrafico = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: etiquetas,
        datasets: [
          {
            label: titulo,
            data: valores,
            backgroundColor: colorFondo,
            borderColor: colorBorde,
            borderWidth: 1,
            borderRadius: 6
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
            labels: {
              color: colorTexto
            }
          },
          title: {
            display: Boolean(titulo),
            text: titulo,
            color: colorTexto
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              color: colorTexto
            }
          },
          x: {
            ticks: {
              color: colorTexto
            }
          }
        }
      }
    });
  });

  // onDestroy destruye la instancia cuando el componente se desmonta
  // Propósito: prevenir fugas de memoria al limpiar los recursos de Chart.js
  onDestroy(() => {
    instanciaGrafico?.destroy();
  });

  // Reaccionamos ante cambios en las props para mantener el gráfico sincronizado
  $: if (instanciaGrafico) {
    instanciaGrafico.data.labels = [...etiquetas];
    instanciaGrafico.data.datasets[0].data = [...valores];
    instanciaGrafico.update();
  }
</script>

<div class="contenedor-grafico">
  <canvas bind:this={lienzo} aria-label={titulo}></canvas>
</div>

<style>
  .contenedor-grafico {
    position: relative;
    width: 100%;
    min-height: 320px;
  }
</style>