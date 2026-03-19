<script>
  import GraficoTorta from "$lib/components/GraficoTorta.svelte";
  import { datosNegocio } from "$lib/stores/datosNegocio.js";
  import { api } from "$lib/services/api.js";

  let mensajeUsuario = "";
  /** @typedef {{ id: string; emisor: 'usuario' | 'siga'; tipo: 'texto' | 'grafico-mermas'; contenido?: string }} MensajeConversacion */
  /** @type {MensajeConversacion[]} */
  let mensajes = [];
  let estaPensando = false;
  let mensajeError = "";

  $: mermas = $datosNegocio.mermasMes ?? [];

  /**
   * Prepara los datos para el gráfico de mermas transformando las parejas (categoría, cantidad) en etiquetas y valores.
   * Propósito: reutilizar la lógica de transformación en múltiples renderizados sin duplicación de código.
   */
  const obtenerDatosMermas = () => {
    const etiquetas = mermas.map((item) => item.categoria);
    const valores = mermas.map((item) => item.cantidad);
    return { etiquetas, valores };
  };

  /**
   * Envía el mensaje del usuario al backend y gestiona la respuesta del asistente.
   * Propósito: centralizar la lógica de conversación, incluyendo manejo de errores y del estado de carga.
   */
  const enviarMensaje = async () => {
    const contenido = mensajeUsuario.trim();
    if (!contenido) {
      return;
    }

    mensajes = [
      ...mensajes,
      { id: crypto.randomUUID(), emisor: "usuario", tipo: "texto", contenido },
    ];
    mensajeUsuario = "";
    estaPensando = true;
    mensajeError = "";

    try {
      const data = await api.post("/api/saas/chat", {
        message: contenido,
      });

      const textoIA = data.response ?? "";

      // Si hubo acción ejecutada, recargar datos (aunque en esta página no se muestran datos vivos como producto/stock, es buena práctica)
      if (data.action && data.action.executed) {
        console.log("✅ Acción ejecutada, recargando datos...");
        datosNegocio.cargarDatos();
      }

      if (typeof textoIA === "string" && textoIA.includes("[GRAFICO_MERMAS]")) {
        mensajes = [
          ...mensajes,
          { id: crypto.randomUUID(), emisor: "siga", tipo: "grafico-mermas" },
        ];
      } else {
        mensajes = [
          ...mensajes,
          {
            id: crypto.randomUUID(),
            emisor: "siga",
            tipo: "texto",
            contenido: textoIA || "Estoy aquí para ayudarte.",
          },
        ];
      }
    } catch (error) {
      console.error(error);
      mensajeError =
        "No pudimos obtener la respuesta. Por favor, revisa tu conexión o inténtalo más tarde.";
    } finally {
      estaPensando = false;
    }
  };

  /**
   * Gestiona el envío vía formulario evitando el comportamiento por defecto del navegador.
   * Propósito: permitir el uso de Enter sin recargar la página y mantener un flujo de conversación fluido.
   * @param {SubmitEvent} evento
   */
  const manejarEnvio = async (evento) => {
    evento.preventDefault();
    await enviarMensaje();
  };
</script>

<section class="section">
  <div class="box">
    <h1 class="title has-text-weight-semibold">Asistente con IA</h1>
    <p class="subtitle">
      Pregunta lo que necesites. SIGA usa IA real para recomendaciones sin
      esperas.
    </p>

    <div class="chat-container">
      <div class="mensajes">
        {#each mensajes as mensaje (mensaje.id)}
          {#if mensaje.tipo === "texto"}
            <div
              class={`mensaje ${mensaje.emisor === "usuario" ? "es-usuario" : "es-siga"}`}
            >
              <p>{mensaje.contenido}</p>
            </div>
          {:else if mensaje.tipo === "grafico-mermas"}
            <div class="mensaje es-siga">
              <p class="mb-3">
                Aquí tienes el panorama de mermas por categoría:
              </p>
              <GraficoTorta
                titulo="Mermas mensuales por categoría"
                etiquetas={obtenerDatosMermas().etiquetas}
                valores={obtenerDatosMermas().valores}
              />
            </div>
          {/if}
        {/each}

        {#if estaPensando}
          <div class="mensaje es-siga pensando">
            <span class="loader"></span>
            <span>SIGA está pensando...</span>
          </div>
        {/if}

        {#if mensajeError}
          <div class="notification is-danger mt-4">
            {mensajeError}
          </div>
        {/if}
      </div>

      <form class="formulario" on:submit={manejarEnvio}>
        <div class="field has-addons">
          <div class="control is-expanded">
            <input
              class="input is-medium"
              type="text"
              placeholder="Escribe tu consulta..."
              bind:value={mensajeUsuario}
              on:keydown={(evento) => {
                if (evento.key === "Enter" && !evento.shiftKey) {
                  evento.preventDefault();
                  enviarMensaje();
                }
              }}
            />
          </div>
          <div class="control">
            <button
              class="button is-link is-medium"
              type="submit"
              disabled={estaPensando}
            >
              Enviar
            </button>
          </div>
        </div>
      </form>
    </div>
  </div>
</section>

<style>
  .chat-container {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }

  .mensajes {
    max-height: 480px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 1rem;
    padding-right: 0.5rem;
  }

  .mensaje {
    padding: 1rem;
    border-radius: 12px;
    max-width: 80%;
  }

  .mensaje.es-usuario {
    align-self: flex-end;
    background-color: var(--color-secundario);
    color: #fff;
  }

  .mensaje.es-siga {
    align-self: flex-start;
    background-color: #fff;
    border: 1px solid var(--color-borde);
  }

  .mensaje.pensando {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .formulario {
    position: sticky;
    bottom: 0;
    background-color: var(--color-fondo);
    padding: 1rem 0 0;
  }

  .loader {
    width: 1rem;
    height: 1rem;
    border: 2px solid var(--color-secundario);
    border-bottom-color: transparent;
    border-radius: 50%;
    display: inline-block;
    box-sizing: border-box;
    animation: rotacion 1s linear infinite;
  }

  @keyframes rotacion {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
</style>
