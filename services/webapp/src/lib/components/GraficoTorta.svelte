<script>
  import { onMount, onDestroy } from 'svelte';


  export let titulo = '';
  /** @type {string[]} */
  export let etiquetas = [];
  /** @type {number[]} */
  export let valores = [];
  /** @type {string[] | null} */
  export let colores = null;

  /** @type {HTMLCanvasElement | null} */
  let lienzo;
  /** @type {import('chart.js').Chart | null} */
  let instanciaGrafico;

  /**
   * Obtiene el valor de una variable CSS o devuelve un respaldo definido.
   * Propósito: mantener la paleta corporativa incluso en SSR.
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

  /** @returns {string[]} */
  const generarPaletaBase = () => {
    if (Array.isArray(colores) && colores.length) {
      return [...colores];
    }
    const primario = obtenerVariable('--color-primario', '#2c3e50');
    const secundario = obtenerVariable('--color-secundario', '#3498db');
    const acento = obtenerVariable('--color-acento', '#e74c3c');
    const apoyo = obtenerVariable('--color-fondo', '#ecf0f1');
    return [primario, secundario, acento, apoyo];
  };

  let paletaBase = generarPaletaBase();

  /**
   * Genera una lista de colores para cubrir todos los segmentos del gráfico.
   * Propósito: reutilizar tonos de la marca sin importar la cantidad de categorías.
   * @param {number} cantidad
   */
  const generarColoresGrafico = (cantidad) => {
    const base = paletaBase.length ? paletaBase : ['#2c3e50', '#3498db', '#e74c3c', '#9b59b6'];
    return Array.from({ length: cantidad }, (_, indice) => base[indice % base.length]);
  };

  let coloresGrafico = generarColoresGrafico(valores.length);

  // Esta función encapsula la creación del gráfico para mantener el código organizado
  // Propósito: centralizar la lógica de inicialización y reutilizarla cuando cambien los datos
  const crearGrafico = async () => {
    if (!lienzo) {
      return;
    }

    const { default: Chart } = await import('chart.js/auto');

    paletaBase = generarPaletaBase();
    coloresGrafico = generarColoresGrafico(valores.length);

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
            backgroundColor: coloresGrafico,
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

  $: paletaBase = generarPaletaBase();
  $: coloresGrafico = generarColoresGrafico(valores.length);

  $: if (instanciaGrafico) {
    instanciaGrafico.data.labels = [...etiquetas];
    instanciaGrafico.data.datasets[0].data = [...valores];
    instanciaGrafico.data.datasets[0].backgroundColor = [...coloresGrafico];
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