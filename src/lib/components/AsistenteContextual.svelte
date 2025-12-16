<script>
  import { page } from "$app/stores";
  import { derived } from "svelte/store";
  import { onMount } from "svelte";
  import { datosNegocio } from "$lib/datosSimulados.js";
  import GraficoTorta from "./GraficoTorta.svelte";
  import GraficoBarras from "./GraficoBarras.svelte";
  import GraficoLineas from "./GraficoLineas.svelte";

  let estaAbierto = false;
  let mensajeUsuario = "";
  let estaPensando = false;
  let mensajeError = "";
  let posX = 0;
  let posY = 0;
  let estaArrastrando = false;
  let offsetX = 0;
  let offsetY = 0;
  /** @type {HTMLButtonElement | undefined} */
  let botonToggle;
  /** @type {HTMLDivElement | undefined} */
  let panelElement;
  /** @type {HTMLDivElement | undefined} */
  let mensajesContainer;
  /** @type {HTMLInputElement | undefined} */
  let inputMensaje;
  let estamosUsandoVoz = false;
  /** @type {any} */
  let reconocimiento;

  /** @typedef {{ id: string; emisor: 'usuario' | 'siga'; tipo: 'texto' | 'grafico'; contenido?: string; grafico?: { tipo: 'torta' | 'barras' | 'lineas'; titulo: string; etiquetas: string[]; valores: number[] } }} Mensaje */
  /** @type {Mensaje[]} */
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

  /**
   * Posiciona el panel al lado del botón al abrir
   */
  const posicionarPanelAlLado = () => {
    if (!botonToggle) return;

    const rect = botonToggle.getBoundingClientRect();
    const anchoPanel = 380;
    const altoPanel = 500;

    let x = rect.left - anchoPanel - 20;
    let y = rect.top;

    if (x < 0) {
      x = rect.right + 20;
    }

    if (typeof window !== "undefined" && y + altoPanel > window.innerHeight) {
      y = window.innerHeight - altoPanel - 20;
    }

    posX = Math.max(0, x);
    posY = Math.max(0, y);
  };

  const abrirAsistente = () => {
    estaAbierto = !estaAbierto;
    if (estaAbierto) {
      setTimeout(posicionarPanelAlLado, 0);
    }
  };

  /**
   * Procesa comandos CRUD enviados por el asistente
   * @param {any} crud
   */
  const procesarCRUD = async (crud) => {
    try {
      if (crud.accion === "crear_producto") {
        const res = await fetch("/api/productos/crear", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            nombre: crud.nombre,
            categoria: crud.categoria,
            sku: crud.sku,
          }),
        });
        const datos = await res.json();
        if (datos.success && datos.datos) {
          console.log("✅ Producto creado:", datos.producto);
          // Actualizar la store con los datos nuevos
          datosNegocio.set(datos.datos);
        }
      } else if (
        crud.accion === "agregar_stock" ||
        crud.accion === "reducir_stock"
      ) {
        const res = await fetch("/api/inventario/actualizar", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            producto: crud.producto,
            local: crud.local,
            cantidad: crud.cantidad,
            accion: crud.accion === "agregar_stock" ? "agregar" : "reducir",
          }),
        });
        const datos = await res.json();
        if (datos.success && datos.datos) {
          console.log("✅ Stock actualizado:", datos.mensaje);
          // Actualizar la store con los datos nuevos
          datosNegocio.set(datos.datos);
        }
      }
    } catch (error) {
      console.error("Error procesando CRUD:", error);
    }
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
      const respuesta = await fetch("/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          mensaje: contextoActual + contenido,
        }),
      });

      if (!respuesta.ok) {
        throw new Error("No fue posible conectar con SIGA.");
      }

      const datos = await respuesta.json();
      const textoIA = datos.respuesta ?? "Estoy aquí para ayudarte.";

      // Detectar si pide un gráfico
      if (textoIA.includes("[GRAFICO_TORTA]")) {
        mensajes = [
          ...mensajes,
          {
            id: crypto.randomUUID(),
            emisor: "siga",
            tipo: "grafico",
            grafico: {
              tipo: "torta",
              titulo: "Distribución de mermas por categoría",
              etiquetas: ["Lácteos", "Bebidas", "Snacks", "Sándwiches"],
              valores: [12, 5, 4, 3],
            },
          },
        ];
      } else if (textoIA.includes("[GRAFICO_BARRAS]")) {
        mensajes = [
          ...mensajes,
          {
            id: crypto.randomUUID(),
            emisor: "siga",
            tipo: "grafico",
            grafico: {
              tipo: "barras",
              titulo: "Ventas por categoría",
              etiquetas: ["Lácteos", "Bebidas", "Snacks", "Sándwiches"],
              valores: [45, 62, 38, 24],
            },
          },
        ];
      } else if (textoIA.includes("[GRAFICO_LINEAS]")) {
        mensajes = [
          ...mensajes,
          {
            id: crypto.randomUUID(),
            emisor: "siga",
            tipo: "grafico",
            grafico: {
              tipo: "lineas",
              titulo: "Tendencia de ventas por día",
              etiquetas: ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"],
              valores: [124, 132, 118, 156, 189, 241, 203],
            },
          },
        ];
      }

      const textoLimpio = textoIA
        .replace(/\[CRUD_START\][\s\S]*?\[CRUD_END\]/g, "")
        .replace("[GRAFICO_TORTA]", "")
        .replace("[GRAFICO_BARRAS]", "")
        .replace("[GRAFICO_LINEAS]", "")
        .trim();

      // Detectar y procesar MÚLTIPLES CRUD
      const crudMatches = textoIA.match(
        /\[CRUD_START\]([\s\S]*?)\[CRUD_END\]/g,
      );
      if (crudMatches && crudMatches.length > 0) {
        for (const match of crudMatches) {
          const crudContent = match
            .replace(/\[CRUD_START\]|\[CRUD_END\]/g, "")
            .trim();
          try {
            const crudJSON = JSON.parse(crudContent);
            await procesarCRUD(crudJSON);
            console.log("✅ CRUD procesado:", crudJSON.accion);
          } catch (error) {
            console.error(
              "Error parsando CRUD:",
              error,
              "Contenido:",
              crudContent,
            );
          }
        }
      }

      if (textoLimpio) {
        mensajes = [
          ...mensajes,
          {
            id: crypto.randomUUID(),
            emisor: "siga",
            tipo: "texto",
            contenido: textoLimpio,
          },
        ];
      }
    } catch (error) {
      console.error(error);
      mensajeError = "No pudimos obtener la respuesta. Intenta nuevamente.";
    } finally {
      estaPensando = false;
      // Devolver el focus al input después de enviar
      setTimeout(() => {
        if (inputMensaje) {
          inputMensaje.focus();
        }
      }, 100);
    }
  };

  /**
   * @param {SubmitEvent} evento
   */
  const manejarEnvio = async (evento) => {
    evento.preventDefault();
    await enviarMensaje();
  };

  /**
   * @param {MouseEvent} evento
   */
  const iniciarArrastre = (evento) => {
    if (evento.button !== 0) return; // Solo click izquierdo
    estaArrastrando = true;
    if (!panelElement) return;

    const rect = panelElement.getBoundingClientRect();
    offsetX = evento.clientX - rect.left;
    offsetY = evento.clientY - rect.top;
    console.log("✋ Arrastrando iniciado en:", offsetX, offsetY);
  };

  /**
   * @param {MouseEvent} evento
   */
  const manejarMovimiento = (evento) => {
    if (!estaArrastrando || !panelElement) return;

    const nuevaX = evento.clientX - offsetX;
    const nuevaY = evento.clientY - offsetY;

    const maxX =
      (typeof window !== "undefined" ? window.innerWidth : 1000) -
      panelElement.offsetWidth;
    const maxY =
      (typeof window !== "undefined" ? window.innerHeight : 1000) -
      panelElement.offsetHeight;

    posX = Math.max(0, Math.min(nuevaX, maxX));
    posY = Math.max(0, Math.min(nuevaY, maxY));
  };

  const finalizarArrastre = () => {
    estaArrastrando = false;
  };

  /**
   * Inicializa Web Speech API para entrada de voz
   */
  const inicializarVoz = () => {
    // @ts-ignore
    const SpeechRecognition =
      typeof window !== "undefined" &&
      (window.webkitSpeechRecognition || window.SpeechRecognition);
    if (!SpeechRecognition) {
      mensajeError =
        "❌ Tu navegador no soporta entrada de voz. Usa Chrome, Edge o Safari.";
      return;
    }

    try {
      reconocimiento = new SpeechRecognition();
      reconocimiento.lang = "es-ES";
      reconocimiento.continuous = false;
      reconocimiento.interimResults = false;

      reconocimiento.onstart = () => {
        estamosUsandoVoz = true;
        mensajeError = "";
      };

      // @ts-ignore
      reconocimiento.onresult = (evento) => {
        let textoTranscrito = "";
        for (let i = evento.resultIndex; i < evento.results.length; i++) {
          if (evento.results[i].isFinal) {
            textoTranscrito += evento.results[i][0].transcript;
          }
        }
        if (textoTranscrito) {
          mensajeUsuario = textoTranscrito;
          estamosUsandoVoz = false;
          // Enviar automáticamente después de detectar voz final (con delay de 2 segundos)
          setTimeout(() => {
            if (mensajeUsuario.trim()) {
              enviarMensaje();
            }
          }, 2000);
        }
      };

      // @ts-ignore
      reconocimiento.onerror = (evento) => {
        console.error("Error de voz:", evento.error);
        estamosUsandoVoz = false;

        let msgError = "❌ Error desconocido. Intenta de nuevo.";
        if (evento.error === "network")
          msgError = "🌐 Error de conexión. Verifica tu internet.";
        else if (evento.error === "audio")
          msgError = "🎤 No se detectó audio. Verifica tu micrófono.";
        else if (evento.error === "not-allowed")
          msgError = "🔒 Permiso negado. Autoriza el micrófono.";
        else if (evento.error === "no-speech")
          msgError = "🤐 No se detectó voz. Intenta de nuevo.";
        else if (evento.error === "bad-grammar")
          msgError = "📝 Error en el reconocimiento. Intenta de nuevo.";
        else if (evento.error === "service-not-allowed")
          msgError = "⛔ El servicio de voz no está disponible.";

        mensajeError = msgError;
      };

      reconocimiento.onend = () => {
        estamosUsandoVoz = false;
      };
    } catch (err) {
      console.error("Error al inicializar voz:", err);
      mensajeError = "❌ Error al inicializar el micrófono.";
    }
  };

  /**
   * Inicia grabación de voz
   */
  const iniciarGrabacion = () => {
    mensajeError = "";

    if (!reconocimiento) {
      inicializarVoz();
    }

    if (reconocimiento) {
      try {
        mensajeError = "🎤 Escuchando... habla ahora";
        reconocimiento.start();
      } catch (err) {
        console.error("Error al iniciar grabación:", err);
        mensajeError = "❌ No se pudo iniciar la grabación. Intenta de nuevo.";
      }
    }
  };

  // Inicializar Web Speech API cuando se monta el componente
  onMount(() => {
    inicializarVoz();
  });

  // Auto-scroll a mensajes nuevos
  $: if (mensajesContainer && mensajes.length) {
    setTimeout(() => {
      if (mensajesContainer) {
        mensajesContainer.scrollTop = mensajesContainer.scrollHeight;
      }
    }, 50);
  }
</script>

<svelte:window
  on:mousemove={manejarMovimiento}
  on:mouseup={finalizarArrastre}
/>

<div class="asistente-contextual {estaAbierto ? 'is-open' : ''}">
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

  {#if estaAbierto}
    <div
      class="panel-asistente"
      bind:this={panelElement}
      style="left: {posX}px; top: {posY}px; cursor: {estaArrastrando
        ? 'grabbing'
        : 'default'}"
    >
      <div
        class="panel-header"
        on:mousedown={iniciarArrastre}
        role="application"
      >
        <h3>🤖 SIGA Asistente</h3>
        <button
          type="button"
          class="btn-voz-header"
          class:activo={estamosUsandoVoz}
          on:click={iniciarGrabacion}
          disabled={estaPensando}
          title="Usar micrófono"
        >
          🎤
        </button>
      </div>

      <div class="mensajes-area" bind:this={mensajesContainer}>
        {#if mensajes.length === 0}
          <div class="mensaje-bienvenida">
            <p><strong>¡Hola! 👋</strong></p>
            <p>Soy tu asistente SIGA. Puedo ayudarte con:</p>
            <ul class="bienvenida-lista">
              <li>📊 Análisis de ventas y tendencias</li>
              <li>📦 Consultas sobre inventario</li>
              <li>⚠️ Alertas de stock crítico</li>
              <li>💡 Recomendaciones de gestión</li>
            </ul>
            <p class="hint">Escribe tu pregunta o usa el micrófono 🎤</p>
          </div>
        {/if}

        {#each mensajes as mensaje (mensaje.id)}
          {#if mensaje.tipo === "texto"}
            <div class={`mensaje ${mensaje.emisor}`}>
              <p>{mensaje.contenido}</p>
            </div>
          {:else if mensaje.tipo === "grafico" && mensaje.grafico}
            <div class="mensaje siga grafico-mensaje">
              {#if mensaje.grafico.tipo === "torta"}
                <div class="grafico-contenedor">
                  <GraficoTorta
                    titulo={mensaje.grafico.titulo}
                    etiquetas={mensaje.grafico.etiquetas}
                    valores={mensaje.grafico.valores}
                  />
                </div>
              {:else if mensaje.grafico.tipo === "barras"}
                <div class="grafico-contenedor">
                  <GraficoBarras
                    titulo={mensaje.grafico.titulo}
                    etiquetas={mensaje.grafico.etiquetas}
                    valores={mensaje.grafico.valores}
                  />
                </div>
              {:else if mensaje.grafico.tipo === "lineas"}
                <div class="grafico-contenedor">
                  <GraficoLineas
                    titulo={mensaje.grafico.titulo}
                    etiquetas={mensaje.grafico.etiquetas}
                    valores={mensaje.grafico.valores}
                  />
                </div>
              {/if}
            </div>
          {/if}
        {/each}

        {#if estaPensando}
          <div class="mensaje siga">
            <p class="pensando">
              <span></span>
              <span></span>
              <span></span>
            </p>
          </div>
        {/if}

        {#if mensajeError}
          <div class="mensaje error">
            <p>{mensajeError}</p>
          </div>
        {/if}
      </div>

      <form on:submit={manejarEnvio} class="input-area">
        <div class="input-group">
          <input
            type="text"
            bind:this={inputMensaje}
            bind:value={mensajeUsuario}
            placeholder="Escribe o usa micrófono..."
            disabled={estaPensando}
            class="mensaje-input"
            on:keydown={(e) => {
              if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                manejarEnvio(e);
              }
            }}
          />
          <button
            type="submit"
            disabled={estaPensando || !mensajeUsuario.trim()}
            class="enviar-btn"
            aria-label="Enviar mensaje"
          >
            →
          </button>
        </div>
      </form>
    </div>
  {/if}
</div>

<style>
  .asistente-contextual {
    position: fixed;
    bottom: 2rem;
    right: 2rem;
    z-index: 999;
    font-family: "Inter", "Segoe UI", Roboto, sans-serif;
  }

  .toggle-asistente {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    background: linear-gradient(
      135deg,
      rgba(100, 100, 100, 0.6),
      rgba(140, 140, 140, 0.5)
    );
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    color: #ffffff;
    padding: 0;
    overflow: hidden;
  }

  .siga-logo {
    width: 85%;
    height: 85%;
    object-fit: contain;
    padding: 0px;
  }

  .icono-cerrar {
    position: absolute;
    font-size: 28px;
    animation: rotarX 0.3s ease;
  }

  @keyframes rotarX {
    from {
      transform: rotateY(90deg);
      opacity: 0;
    }
    to {
      transform: rotateY(0);
      opacity: 1;
    }
  }

  .toggle-asistente:hover {
    transform: scale(1.1);
    box-shadow: 0 12px 32px rgba(3, 4, 94, 0.4);
  }

  .toggle-asistente:active {
    transform: scale(0.95);
  }

  .toggle-asistente.abierto {
    background: linear-gradient(
      135deg,
      rgba(0, 180, 216, 0.7),
      rgba(128, 255, 219, 0.6)
    );
    color: #ffffff;
  }

  .panel-asistente {
    position: fixed;
    width: 320px;
    height: 450px;
    background: #ffffff;
    border: 1px solid var(--color-borde);
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(3, 4, 94, 0.2);
    display: flex;
    flex-direction: column;
    overflow: hidden;
    animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    user-select: none;
  }

  @keyframes slideUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .panel-header {
    background: linear-gradient(
      135deg,
      var(--color-primario),
      var(--color-secundario)
    );
    color: #ffffff;
    padding: 1rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;
    cursor: grab;
    transition: all 0.2s ease;
  }

  .panel-header:active {
    cursor: grabbing;
  }

  .panel-header h3 {
    margin: 0;
    font-size: 1rem;
    font-weight: 700;
    flex: 1;
  }

  .btn-voz-header {
    background: none;
    border: none;
    font-size: 1.2rem;
    cursor: pointer;
    color: #ffffff;
    padding: 0.5rem;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
    flex-shrink: 0;
  }

  .btn-voz-header:hover:not(:disabled) {
    transform: scale(1.15);
    filter: brightness(1.1);
  }

  .btn-voz-header.activo {
    animation: grabarPulsoWhite 1s infinite;
  }

  @keyframes grabarPulsoWhite {
    0%,
    100% {
      box-shadow: 0 0 0 0 rgba(255, 255, 255, 0.7);
      transform: scale(1);
    }
    50% {
      box-shadow: 0 0 0 6px rgba(255, 255, 255, 0);
      transform: scale(1.1);
    }
  }

  .btn-voz-header:disabled {
    cursor: not-allowed;
    opacity: 0.5;
  }

  .close-btn {
    background: none;
    border: none;
    color: #ffffff;
    cursor: pointer;
    font-size: 1.25rem;
    padding: 0;
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.2s ease;
    flex-shrink: 0;
  }

  .close-btn:hover {
    transform: scale(1.2);
  }

  .mensajes-area {
    flex: 1;
    overflow-y: auto;
    padding: 0.5rem;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    background: #ffffff;
    min-height: 180px;
    max-height: 280px;
  }

  .mensaje-bienvenida {
    text-align: center;
    color: var(--color-primario);
    font-size: 13px;
    padding: 0.5rem;
  }

  .mensaje-bienvenida p {
    margin: 0.25rem 0;
  }

  .bienvenida-lista {
    list-style: none;
    padding: 0.5rem 0;
    margin: 0.5rem 0;
    text-align: left;
    font-size: 12px;
  }

  .bienvenida-lista li {
    padding: 0.2rem 0;
    color: #555;
  }

  .hint {
    margin-top: 0.5rem;
    font-size: 11px;
    color: #999;
    font-style: italic;
  }

  .mensaje {
    display: flex;
    animation: fadeIn 0.3s ease;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  .mensaje.usuario {
    justify-content: flex-end;
  }

  .mensaje.usuario p {
    background-color: var(--color-secundario);
    color: #ffffff;
    padding: 0.75rem 1rem;
    border-radius: 12px 12px 0 12px;
    font-size: 0.9rem;
    line-height: 1.5;
    max-width: 85%;
    margin: 0;
    font-weight: 500;
  }

  .mensaje.siga p {
    background-color: #e8f4f8;
    color: var(--color-primario);
    padding: 0.75rem 1rem;
    border-radius: 12px 12px 12px 0;
    font-size: 0.9rem;
    line-height: 1.5;
    border-left: 4px solid var(--color-secundario);
    max-width: 85%;
    margin: 0;
    font-weight: 500;
  }

  .mensaje.error p {
    background-color: rgba(255, 71, 87, 0.1);
    color: #ff4757;
    padding: 0.75rem 1rem;
    border-radius: 12px;
    font-size: 0.9rem;
    border-left: 3px solid #ff4757;
    margin: 0;
  }

  .pensando {
    display: flex;
    gap: 4px;
    align-items: center;
    height: 1.2rem;
    margin: 0;
  }

  .pensando span {
    width: 4px;
    height: 4px;
    border-radius: 50%;
    background-color: var(--color-secundario);
    animation: pulse 1.4s infinite;
  }

  .pensando span:nth-child(2) {
    animation-delay: 0.2s;
  }

  .pensando span:nth-child(3) {
    animation-delay: 0.4s;
  }

  @keyframes pulse {
    0%,
    60%,
    100% {
      opacity: 0.3;
      transform: scale(0.8);
    }
    30% {
      opacity: 1;
      transform: scale(1);
    }
  }

  .input-area {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    padding: 0.75rem;
    border-top: 1px solid var(--color-borde);
    background-color: #ffffff;
    flex-shrink: 0;
  }

  .input-group {
    display: flex;
    gap: 0.5rem;
  }

  .mensaje-input {
    flex: 1;
    border: 2px solid var(--color-borde);
    border-radius: 8px;
    padding: 0.6rem 0.8rem;
    font-size: 0.85rem;
    font-family: inherit;
    color: var(--color-texto);
    background-color: #ffffff;
    transition: all 0.2s ease;
    font-weight: 500;
    max-height: 80px;
    overflow-y: auto;
  }

  .mensaje-input:focus {
    outline: none;
    border-color: var(--color-secundario);
    box-shadow: 0 0 0 3px rgba(0, 180, 216, 0.2);
  }

  .mensaje-input::placeholder {
    color: rgba(27, 35, 63, 0.6);
    font-weight: 400;
  }

  .mensaje-input:disabled {
    background-color: #f5f5f5;
    cursor: not-allowed;
    opacity: 0.7;
  }

  .enviar-btn {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    background-color: var(--color-primario);
    color: #ffffff;
    border: none;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.1rem;
    transition: all 0.2s ease;
    font-weight: 700;
    flex-shrink: 0;
  }

  .enviar-btn:hover:not(:disabled) {
    background-color: #051a8f;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(3, 4, 94, 0.2);
  }

  .enviar-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  .grafico-mensaje {
    flex-direction: column;
    width: 100%;
    max-width: 100%;
  }

  .grafico-contenedor {
    width: 100%;
    height: 280px;
    background: #f0f7ff;
    border-radius: 8px;
    padding: 0.5rem;
    margin-top: 0.5rem;
    border: 1px solid var(--color-borde);
  }

  @media screen and (max-width: 768px) {
    .asistente-contextual {
      bottom: 1rem;
      right: 1rem;
    }

    .toggle-asistente {
      width: 52px;
      height: 52px;
      font-size: 1.75rem;
    }

    .panel-asistente {
      width: calc(100vw - 2rem);
      max-width: 380px;
      max-height: 500px;
    }
  }
</style>
