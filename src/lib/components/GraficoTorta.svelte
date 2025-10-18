<script>
  import { onMount, onDestroy } from 'svelte';
  import Chart from 'chart.js/auto';

  export let titulo = '';
  /** @type {string[]} */
  export let etiquetas = [];
  /** @type {number[]} */
  export let valores = [];
  /** @type {string[]} */
  export let colores = ['#2c3e50', '#3498db', '#e74c3c', '#9b59b6'];

  /** @type {HTMLCanvasElement | null} */
  let lienzo;
  /** @type {import('chart.js').Chart | null} */
  let instanciaGrafico;

  // Esta función encapsula la creación del gráfico para mantener el código organizado
  // Propósito: centralizar la lógica de inicialización y reutilizarla cuando cambien los datos
  const crearGrafico = () => {
    if (!lienzo) {
      return;
    }

    const colorTexto = typeof document !== 'undefined'
      ? getComputedStyle(document.documentElement).getPropertyValue('--color-texto') || '#2c3e50'
      : '#2c3e50';

    const canvas = /** @type {HTMLCanvasElement} */ (lienzo);

    instanciaGrafico = new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: etiquetas,
        datasets: [
          {
            data: valores,
            backgroundColor: colores,
            borderColor: '#ffffff',
            borderWidth: 2
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              color: colorTexto
            }
          },
          title: {
            display: Boolean(titulo),
            text: titulo,
            color: colorTexto
          }
        }
      }
    });
  };

  // onMount se asegura de que Chart.js solo se ejecute en el navegador
  // Propósito: evitar incompatibilidades con el renderizado del lado del servidor y garantizar que el canvas exista
  onMount(() => {
    crearGrafico();
  });

  onDestroy(() => {
    instanciaGrafico?.destroy();
  });

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
    min-height: 280px;
  }
</style>