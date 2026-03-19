<script>
  import { onMount, onDestroy } from 'svelte';


  export let titulo = '';
  /** @type {string[]} */
  export let etiquetas = [];
  /** @type {number[]} */
  export let valores = [];
  /** @type {string | string[] | null} */
  export let colorFondo = null;
  /** @type {string | string[] | null} */
  export let colorBorde = null;
  /** @type {string} */
  export const nombreGrafico = '';

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

  /**
   * Normaliza una entrada de color para devolver un arreglo homogéneo.
   * @param {string | string[] | null} entrada
   * @param {number} cantidad
   */
  const normalizarColores = (entrada, cantidad) => {
    if (Array.isArray(entrada) && entrada.length) {
      return Array.from({ length: cantidad }, (_, indice) => entrada[indice % entrada.length]);
    }
    if (typeof entrada === 'string' && entrada.trim()) {
      return Array(cantidad).fill(entrada);
    }
    return null;
  };

  /**
   * Calcula los colores del dataset con base en la paleta corporativa.
   * Propósito: ofrecer barras contrastantes reutilizando colores de la marca.
   * @param {number} cantidad
   * @returns {{ relleno: string[]; borde: string[] }}
   */
  const calcularColores = (cantidad) => {
    const rellenoPersonalizado = normalizarColores(colorFondo, cantidad);
    const bordePersonalizado = normalizarColores(colorBorde, cantidad);

    const primario = obtenerVariable('--color-primario', '#2c3e50');
    const secundario = obtenerVariable('--color-secundario', '#3498db');
    const acento = obtenerVariable('--color-acento', '#e74c3c');
    const apoyo = obtenerVariable('--color-borde', '#bdc3c7');

    const paletaBase = [secundario, acento, primario, apoyo];

    const rellenoAutomatico = Array.from({ length: cantidad }, (_, indice) =>
      hexToRgba(paletaBase[indice % paletaBase.length], 0.72)
    );

    const bordeAutomatico = Array.from({ length: cantidad }, (_, indice) =>
      hexToRgba(paletaBase[indice % paletaBase.length], 1)
    );

    return {
      relleno: rellenoPersonalizado ?? rellenoAutomatico,
      borde: bordePersonalizado ?? (rellenoPersonalizado ? rellenoPersonalizado : bordeAutomatico)
    };
  };

  let coloresDataset = calcularColores(etiquetas.length);

  // onMount crea la instancia del gráfico una vez que el componente está en el DOM
  // Propósito: inicializar Chart.js únicamente en el cliente y garantizar que el gráfico se dibuje correctamente
  onMount(async () => {
    if (!lienzo) {
      return;
    }

    const { default: Chart } = await import('chart.js/auto');

    coloresDataset = calcularColores(etiquetas.length);
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
            backgroundColor: [...coloresDataset.relleno],
            borderColor: [...coloresDataset.borde],
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

  $: coloresDataset = calcularColores(etiquetas.length);

  // Reaccionamos ante cambios en las props para mantener el gráfico sincronizado
  $: if (instanciaGrafico) {
    instanciaGrafico.data.labels = [...etiquetas];
    instanciaGrafico.data.datasets[0].data = [...valores];
  instanciaGrafico.data.datasets[0].backgroundColor = [...coloresDataset.relleno];
  instanciaGrafico.data.datasets[0].borderColor = [...coloresDataset.borde];
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