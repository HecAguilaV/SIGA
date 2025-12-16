<script>
  import { onMount, onDestroy } from 'svelte';


  export let titulo = '';
  /** @type {string[]} */
  export let etiquetas = [];
  /** @type {number[]} */
  export let valores = [];
  /** @type {string | null} */
  export let colorLinea = null;
  /** @type {string | null} */
  export let colorRelleno = null;

  /** @type {HTMLCanvasElement | null} */
  let lienzo;
  /** @type {import('chart.js').Chart | null} */
  let instanciaGrafico;

  /**
   * Convierte un color hexadecimal a formato rgba.
   * Propósito: reutilizar los colores de la paleta corporativa con diferentes opacidades.
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
   * Obtiene el valor de una variable CSS o regresa un respaldo.
   * Propósito: respetar la paleta corporativa aún cuando no exista el documento.
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

  // onMount crea la instancia del gráfico una vez que el componente está en el DOM
  // Propósito: inicializar Chart.js únicamente en el cliente y garantizar que el gráfico se dibuje correctamente
  onMount(async () => {
    if (!lienzo) {
      return;
    }

    const { default: Chart } = await import('chart.js/auto');

    const colorTexto = typeof document !== 'undefined'
      ? getComputedStyle(document.documentElement).getPropertyValue('--color-texto') || '#2c3e50'
      : '#2c3e50';

    const linea = colorLinea || obtenerVariable('--color-secundario', '#00b4d8');
    const relleno = colorRelleno || obtenerVariable('--color-secundario', '#00b4d8');

    const canvas = /** @type {HTMLCanvasElement} */ (lienzo);

    instanciaGrafico = new Chart(canvas, {
      type: 'line',
      data: {
        labels: etiquetas,
        datasets: [
          {
            label: titulo,
            data: valores,
            borderColor: linea,
            backgroundColor: hexToRgba(relleno, 0.1),
            fill: true,
            tension: 0.4,
            pointRadius: 5,
            pointBackgroundColor: linea,
            pointBorderColor: '#ffffff',
            pointBorderWidth: 2,
            pointHoverRadius: 7,
            borderWidth: 2
          }
        ]
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
              usePointStyle: true
            }
          },
          title: {
            display: Boolean(titulo),
            text: titulo,
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
