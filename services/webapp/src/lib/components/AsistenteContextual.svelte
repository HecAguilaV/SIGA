<script>
  import { page } from "$app/stores";
  import { derived } from "svelte/store";
  import { onMount } from "svelte";
  import { datosNegocio } from "$lib/stores/datosNegocio.js";
  import { api } from "$lib/services/api.js";
  import GraficoTorta from "./GraficoTorta.svelte";
  import GraficoBarras from "./GraficoBarras.svelte";
  import GraficoLineas from "./GraficoLineas.svelte";
  import {
    X,
    ArrowsOutSimple,
    FileText,
    ChartBar,
    Microphone,
    Sparkle,
  } from "phosphor-svelte";

  let estaAbierto = false;
  let mensajeUsuario = "";
  let estaPensando = false;
  let mensajeError = "";
  let posX = 0;
  let posY = 0;
  let estaArrastrando = false;
  let offsetX = 0;
  let offsetY = 0;

  // Insight Window State
  let mostrarInsight = false;
  let insightData = null; // { titulo, tipo, datos, explicacion }
  let posInsightX = 40;
  let posInsightY = 40;
  let draggingInsight = false;

  /** @type {HTMLButtonElement | undefined} */
  let botonToggle;
  /** @type {HTMLDivElement | undefined} */
  let panelElement;
  /** @type {HTMLDivElement | undefined} */
  let insightElement;
  /** @type {HTMLDivElement | undefined} */
  let mensajesContainer;
  /** @type {HTMLInputElement | undefined} */
  let inputMensaje;
  let estamosUsandoVoz = false;
  /** @type {any} */
  let reconocimiento;

  let mensajes = [];

  const rutaActual = derived(page, ($page) => $page.url.pathname);

  $: contextoActual = (() => {
    const ruta = $rutaActual;
    if (ruta === "/") return "El usuario está viendo el inventario. ";
    if (ruta === "/analisis")
      return "El usuario está viendo análisis de ventas. ";
    if (ruta === "/acerca") return "El usuario está leyendo sobre SIGA. ";
    return "";
  })();

  const posicionarPanelAlLado = () => {
    // Simplificado: Panel fijo o lógica simple si se requiere
  };

  const abrirAsistente = () => {
    estaAbierto = !estaAbierto;
  };

  const cerrarInsight = () => {
    mostrarInsight = false;
  };

  const generarInsightSimulado = (tipo) => {
    const productos = $datosNegocio.productos || [];
    const stock = $datosNegocio.stock || [];

    // 1. Análisis por Producto / Ventas (Simulado con nombres reales)
    if (
      tipo.includes("producto") ||
      tipo.includes("ventas") ||
      tipo.includes("ganancias")
    ) {
      // Tomamos 5 productos reales o defaults
      const items =
        productos.length > 0
          ? productos.slice(0, 5)
          : [
              { nombre: "Producto A" },
              { nombre: "Producto B" },
              { nombre: "Producto C" },
            ];

      // Generamos valores aleatorios para simular ventas
      const valores = items.map(() => Math.floor(Math.random() * 100) + 20);
      const etiquetas = items.map((p) => p.nombre);

      // Encontrar el mejor
      const maxVal = Math.max(...valores);
      const mejorIndice = valores.indexOf(maxVal);
      const mejorProducto = etiquetas[mejorIndice];

      return {
        titulo: "Rendimiento por Producto (Simulado)",
        tipo: "barras",
        etiquetas: etiquetas,
        valores: valores,
        explicacion: `
                <p><strong>Resumen Ejecutivo:</strong></p>
                <p>Basado en la proyección actual, <strong>${mejorProducto}</strong> está liderando las preferencias con un volumen estimado de <strong>${maxVal} unidades</strong>.</p>
                <hr style="border-color: rgba(0,0,0,0.1); margin: 8px 0;">
                <p>💡 <em>Insight:</em> Este producto representa el ${((maxVal / valores.reduce((a, b) => a + b, 0)) * 100).toFixed(1)}% del volumen total analizado en esta muestra.</p>
                <p>🚀 <em>Acción sugerida:</em> Verificar stock de ${mejorProducto} para evitar quiebres ante esta tendencia.</p>
            `,
      };
    }

    // 2. Análisis de Stock (Datos Reales si existen)
    if (tipo.includes("stock") || tipo.includes("inventario")) {
      // Intentar cruzar stock con productos
      let dataStock = [];

      if (stock.length > 0 && productos.length > 0) {
        // Agrupar stock por producto (sumando locales)
        const stockMap = {};
        stock.forEach((item) => {
          const prod = productos.find((p) => p.id === item.productoId);
          const nombre = prod ? prod.nombre : `ID ${item.productoId}`;
          stockMap[nombre] = (stockMap[nombre] || 0) + item.cantidad;
        });

        const labels = Object.keys(stockMap).slice(0, 6); // Top 6
        dataStock = labels.map((l) => ({ nombre: l, cantidad: stockMap[l] }));
      }

      // Fallback si no hay stock real cargado
      if (dataStock.length === 0) {
        const items =
          productos.length > 0
            ? productos.slice(0, 5)
            : [{ nombre: "Sin Datos" }];
        dataStock = items.map((p) => ({
          nombre: p.nombre,
          cantidad: Math.floor(Math.random() * 50),
        })); // Mock
      }

      const etiquetas = dataStock.map((d) => d.nombre);
      const valores = dataStock.map((d) => d.cantidad);

      return {
        titulo: "Distribución de Inventario Actual",
        tipo: "barras", // Podría ser torta también
        etiquetas: etiquetas,
        valores: valores,
        explicacion: `
                <p><strong>Estado del Inventario:</strong></p>
                <p>Se visualiza la disponibilidad física actual en los locales conectados.</p>
                <hr style="border-color: rgba(0,0,0,0.1); margin: 8px 0;">
                <p>📦 <em>Stock Total en Muestra:</em> ${valores.reduce((a, b) => a + b, 0)} unidades.</p>
                <p>⚠️ <em>Alerta:</em> Revisa los productos con barras bajas. Si es un producto de alta rotación, considera reponer pronto.</p>
            `,
      };
    }

    // Default / Generico
    return {
      titulo: "Análisis General (Demo)",
      tipo: "barras",
      etiquetas: ["Q1", "Q2", "Q3", "Q4"],
      valores: [20, 45, 28, 80],
      explicacion:
        "<p>No pude detectar una categoría específica (Ventas, Stock, Productos). Mostrando datos genéricos de ejemplo.</p>",
    };
  };

  const enviarMensaje = async () => {
    const contenido = mensajeUsuario.trim();
    if (!contenido) return;

    mensajes = [
      ...mensajes,
      { id: crypto.randomUUID(), emisor: "usuario", tipo: "texto", contenido },
    ];
    mensajeUsuario = "";
    estaPensando = true;
    mensajeError = "";

    try {
      /* 
      // 1. Detectar intención de Gráfico/Insight localmente (Mock para demo) - DESACTIVADO POR PETICIÓN DE CLIENTE (USAR AI REAL)
      const insight = generarInsightSimulado(contenido.toLowerCase());

      if (insight) {
        setTimeout(() => {
          insightData = insight;
          mostrarInsight = true;
          mensajes = [
            ...mensajes,
            {
              id: crypto.randomUUID(),
              emisor: "siga",
              tipo: "texto",
              contenido:
                "He generado el informe detallado en la ventana adjunta. 📊",
            },
          ];
          estaPensando = false;
        }, 1500);
        return;
      } 
      */

      // 2. Si no es demo, ir al backend
      const data = await api.post("/api/saas/chat", {
        message: contextoActual + contenido,
      });

      const textoIA = data.response ?? "Estoy procesando tu solicitud...";

      if (data.action && data.action.executed) {
        datosNegocio.cargarDatos();
      }

      mensajes = [
        ...mensajes,
        {
          id: crypto.randomUUID(),
          emisor: "siga",
          tipo: "texto",
          contenido: textoIA,
        },
      ];
    } catch (error) {
      let errorMsg = error.message || "";

      // Detectar error de Rate Limit (Google Gemini u otro)
      if (
        errorMsg.includes("demasiadas solicitudes") ||
        errorMsg.includes("429") ||
        errorMsg.includes("503")
      ) {
        mensajeError =
          "🧠 El asistente está pensando muy rápido. Dame unos segundos para enfriar los circuitos...";
        // No loguear error en consola para no asustar al usuario
        console.warn("Rate limit alcanzado en chat (manejado UI).");
      } else {
        console.error(error); // Solo loguear errores reales inesperados
        mensajeError =
          "No pudimos conectar con el asistente. Intenta nuevamente.";
      }

      // Añadir mensaje de sistema al chat también para que sea visible
      mensajes = [
        ...mensajes,
        {
          id: crypto.randomUUID(),
          emisor: "siga",
          tipo: "texto",
          contenido: "⚠️ " + mensajeError,
        },
      ];
    } finally {
      if (!insightData) estaPensando = false; // Only stop if we haven't already (for the mock path)
      setTimeout(() => {
        if (inputMensaje) inputMensaje.focus();
      }, 100);
    }
  };

  const manejarEnvio = async (evento) => {
    evento.preventDefault();
    await enviarMensaje();
  };

  // Drag Logic General
  const iniciarArrastre = (e, tipo) => {
    if (e.button !== 0) return;

    if (tipo === "insight") {
      draggingInsight = true;
      if (insightElement) {
        const rect = insightElement.getBoundingClientRect();
        offsetX = e.clientX - rect.left;
        offsetY = e.clientY - rect.top;
      }
    }
  };

  const manejarMovimiento = (e) => {
    if (draggingInsight && insightElement) {
      posInsightX = e.clientX - offsetX;
      posInsightY = e.clientY - offsetY;
    }
  };

  const finalizarArrastre = () => {
    draggingInsight = false;
  };

  // ... (Speech logic kept simplified for length, assuming user has it or copy previous if critical,
  // but re-implementing core parts here to ensure it works) ...
  const inicializarVoz = () => {
    const SpeechRecognition =
      typeof window !== "undefined" &&
      (window.webkitSpeechRecognition || window.SpeechRecognition);
    if (!SpeechRecognition) return;

    try {
      reconocimiento = new SpeechRecognition();
      reconocimiento.lang = "es-ES";
      reconocimiento.continuous = false;
      reconocimiento.interimResults = false;

      reconocimiento.onstart = () => {
        estamosUsandoVoz = true;
      };
      reconocimiento.onend = () => {
        estamosUsandoVoz = false;
      };
      reconocimiento.onresult = (e) => {
        const transcript = e.results[0][0].transcript;
        if (transcript) {
          mensajeUsuario = transcript;
          setTimeout(() => {
            if (mensajeUsuario) enviarMensaje();
          }, 1500);
        }
      };
    } catch (e) {
      console.error(e);
    }
  };

  const iniciarGrabacion = () => {
    if (!reconocimiento) inicializarVoz();
    if (reconocimiento) reconocimiento.start();
  };

  onMount(() => {
    inicializarVoz();
  });
  $: if (mensajesContainer && mensajes.length) {
    // Wait for DOM update
    setTimeout(() => {
      if (mensajesContainer) {
        mensajesContainer.scrollTo({
          top: mensajesContainer.scrollHeight,
          behavior: "smooth",
        });
      }
    }, 100);
  }
</script>

<svelte:window
  on:mousemove={manejarMovimiento}
  on:mouseup={finalizarArrastre}
/>

<div class="asistente-contextual">
  <!-- Botón Flotante Principal -->
  <button
    bind:this={botonToggle}
    class="toggle-asistente"
    class:abierto={estaAbierto}
    on:click={abrirAsistente}
    aria-label={estaAbierto ? "Cerrar asistente" : "Abrir asistente"}
    title="Asistente contextual de SIGA"
  >
    <img src="/S.png" alt="SIGA" class="siga-logo" />
  </button>

  <!-- Panel de Chat (Glass Style) -->
  {#if estaAbierto}
    <div class="panel-chat glass-panel" bind:this={panelElement}>
      <div class="panel-header">
        <h3>✨ Asistente SIGA</h3>
      </div>

      <div class="mensajes-area" bind:this={mensajesContainer}>
        {#if mensajes.length === 0}
          <div class="mensaje-bienvenida">
            <p>Hola, analizamos el negocio hoy?</p>
            <p class="hint">Prueba: "Muestrame un grafico de ganancias"</p>
          </div>
        {/if}
        {#each mensajes as mensaje (mensaje.id)}
          <div class={`mensaje ${mensaje.emisor}`}>
            <p>{mensaje.contenido}</p>
          </div>
        {/each}
        {#if estaPensando}
          <div class="mensaje siga"><p>...</p></div>
        {/if}
      </div>

      <form on:submit={manejarEnvio} class="input-area">
        <div class="input-wrapper">
          <input
            type="text"
            bind:this={inputMensaje}
            bind:value={mensajeUsuario}
            placeholder="Escribe aquí..."
            class="mensaje-input"
          />
          <button
            type="button"
            class="btn-mic"
            on:click={iniciarGrabacion}
            class:recording={estamosUsandoVoz}
          >
            <Microphone
              size={20}
              weight={estamosUsandoVoz ? "fill" : "regular"}
            />
          </button>
        </div>
      </form>
    </div>
  {/if}

  <!-- Ventana de Insights (Glass Window Flotante) -->
  {#if mostrarInsight && insightData}
    <div
      class="insight-window glass-window"
      bind:this={insightElement}
      style="left: {posInsightX}px; top: {posInsightY}px;"
    >
      <div
        class="window-header"
        on:mousedown={(e) => iniciarArrastre(e, "insight")}
      >
        <div class="window-title">
          <ChartBar size={20} color="#333" />
          <span>{insightData.titulo}</span>
        </div>
        <div class="window-controls">
          <button class="btn-control close" on:click={cerrarInsight}
            ><X size={16} /></button
          >
        </div>
      </div>

      <div class="window-content">
        <div class="chart-section">
          <div class="chart-container">
            {#if insightData.tipo === "barras"}
              <GraficoBarras
                titulo=""
                etiquetas={insightData.etiquetas}
                valores={insightData.valores}
              />
            {:else}
              <GraficoBarras etiquetas={[]} valores={[]} />
            {/if}
          </div>
        </div>

        <div class="report-section">
          <div class="report-header">
            <FileText size={18} color="#555" />
            <span>Informe Ejecutivo</span>
          </div>
          <div class="report-body">
            {@html insightData.explicacion}
          </div>
        </div>
      </div>
    </div>
  {/if}
</div>

<style>
  .asistente-contextual {
    /* No layout impact, just container for fixed elements */
  }

  /* --- Toggle Button (Fixed Bottom Right) --- */
  .toggle-asistente {
    position: fixed;
    bottom: 24px;
    right: 24px;
    width: 75px;
    height: 75px;
    border-radius: 50%;
    /* Grey Glass (WebComercial Style) */
    background: rgba(140, 140, 140, 0.25);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    box-shadow:
      0 8px 32px 0 rgba(3, 4, 94, 0.3),
      0 0 0 1px rgba(255, 255, 255, 0.1) inset;
    cursor: pointer;
    z-index: 10000;
    transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
    padding: 0;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .toggle-asistente:hover {
    transform: scale(1.1) translateY(-4px);
    background: rgba(140, 140, 140, 0.4);
    box-shadow: 0 12px 32px rgba(3, 4, 94, 0.4);
  }
  .toggle-asistente.abierto {
    box-shadow: 0 0 0 4px rgba(0, 180, 216, 0.3);
  }

  .siga-logo {
    width: 60%;
    height: 60%;
    object-fit: contain;
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2)) brightness(1.2); /* Make logo pop on dark bg */
  }

  /* --- Glass Panel (Chat) --- */
  .panel-chat {
    position: fixed;
    bottom: 100px; /* Above button */
    right: 24px;
    width: 320px;
    height: 450px;
    display: flex;
    flex-direction: column;
    z-index: 9999;

    /* Lighter, tinted Glassmorphism (Restored) */
    /* Greyish/Smoked Glass (User Request) */
    background: rgba(230, 235, 240, 0.85);
    backdrop-filter: blur(24px) saturate(180%);
    -webkit-backdrop-filter: blur(24px) saturate(180%);
    border-radius: 24px;
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 15px 50px -10px rgba(0, 180, 216, 0.15);
    overflow: hidden;
    animation: slideIn 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  }

  @keyframes slideIn {
    from {
      opacity: 0;
      transform: translateY(40px) scale(0.95);
    }
    to {
      opacity: 1;
      transform: translateY(0) scale(1);
    }
  }

  .panel-header {
    padding: 1.2rem;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(
      to right,
      rgba(255, 255, 255, 0.4),
      rgba(255, 255, 255, 0.1)
    );
  }
  .panel-header h3 {
    margin: 0;
    font-size: 1.1rem;
    font-weight: 700;
    background: linear-gradient(135deg, var(--color-primario), #023e8a);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    letter-spacing: -0.02em;
  }

  .mensajes-area {
    flex: 1;
    padding: 1rem;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 0.8rem;
    scroll-behavior: smooth;

    /* Custom Scrollbar for better UX */
    scrollbar-width: thin;
    scrollbar-color: rgba(0, 180, 216, 0.5) transparent;
  }

  .mensajes-area::-webkit-scrollbar {
    width: 6px;
  }
  .mensajes-area::-webkit-scrollbar-track {
    background: transparent;
  }
  .mensajes-area::-webkit-scrollbar-thumb {
    background-color: rgba(0, 180, 216, 0.5);
    border-radius: 10px;
  }

  .mensaje {
    padding: 0.8rem 1rem;
    border-radius: 18px;
    font-size: 0.92rem;
    max-width: 85%;
    line-height: 1.45;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    position: relative;
  }
  .mensaje.usuario {
    align-self: flex-end;
    /* Cyan Gradient for user */
    background: linear-gradient(135deg, #00b4d8, #90e0ef);
    color: #03045e; /* Dark text on light cyan */
    font-weight: 500;
    border-bottom-right-radius: 4px;
    box-shadow: 0 4px 15px rgba(0, 180, 216, 0.2);
  }
  .mensaje.siga {
    align-self: flex-start;
    background: rgba(255, 255, 255, 0.8); /* Solid white bubble */
    color: #03045e; /* Dark text */
    border-bottom-left-radius: 4px;
    border: 1px solid rgba(255, 255, 255, 0.5);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  }

  .mensaje-bienvenida {
    text-align: center;
    color: #555; /* Darker grey */
    margin-top: 2rem;
  }
  .hint {
    font-size: 0.8rem;
    opacity: 0.8;
    color: #777;
    margin-top: 0.5rem;
  }

  .input-area {
    padding: 1rem;
    background: rgba(255, 255, 255, 0.4); /* Lighter input area */
    border-top: 1px solid rgba(255, 255, 255, 0.4);
  }
  .input-wrapper {
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.6);
    border: 1px solid rgba(255, 255, 255, 0.5);
    border-radius: 30px;
    padding: 0.2rem 0.2rem 0.2rem 1rem;
    transition: all 0.2s;
  }
  .input-wrapper:focus-within {
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 4px 12px rgba(0, 180, 216, 0.1);
    border-color: #00b4d8;
  }

  .mensaje-input {
    flex: 1;
    border: none;
    background: transparent;
    outline: none;
    font-size: 0.9rem;
    color: #333; /* Dark input text */
    padding: 0.6rem 0;
  }

  .mensaje-input::placeholder {
    color: #888;
  }

  .btn-mic {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    border: none;
    background: transparent;
    color: #00b4d8; /* Cyan */
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
  }
  .btn-mic:hover {
    background: rgba(0, 180, 216, 0.1);
    color: #90e0ef;
  }
  .btn-mic.recording {
    color: #ff4d4d;
    animation: pulseRed 1.5s infinite;
  }

  /* ... Insight Window partials ... */
  .chart-section {
    flex: 1.5;
    padding: 1.5rem;
    border-right: 1px solid rgba(0, 0, 0, 0.04);
    display: flex;
    flex-direction: column;
    justify-content: center;
    background: rgba(
      255,
      255,
      255,
      0.5
    ); /* Keep chart area lighter for readability */
  }
  .chart-container {
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.6);
    border-radius: 16px;
    padding: 1rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
    border: 1px solid rgba(255, 255, 255, 0.8);
  }

  .report-section {
    flex: 1;
    padding: 1.5rem;
    background: rgba(250, 250, 252, 0.3);
    overflow-y: auto;
  }
  .report-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 1rem;
    color: #0077b6;
    font-weight: 700;
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
  .report-body {
    font-size: 0.95rem;
    line-height: 1.7;
    color: #2c3e50;
  }

  /* Mobile Responsive */
  @media (max-width: 768px) {
    .insight-window {
      width: 92vw;
      left: 4vw !important;
      top: 80px !important;
      max-height: 80vh;
    }
    .window-content {
      flex-direction: column;
      height: auto;
    }
    .chart-section {
      height: 300px;
      border-right: none;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    }
    .panel-chat {
      width: 90vw;
      right: 5vw;
      bottom: 110px;
    }
  }
</style>
