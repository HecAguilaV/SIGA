import { json } from '@sveltejs/kit';
import { get } from 'svelte/store';
import { datosNegocio } from '$lib/datosSimulados.js';
import { env } from '$env/dynamic/private';

/**
 * Construye el prompt completo que se enviará al modelo de lenguaje.
 * Propósito: encapsular la lógica de RAG y asegurar que todas las instrucciones se incluyan de manera consistente.
 * @param {string} preguntaUsuario
 * @returns {string}
 */
const construirPrompt = (preguntaUsuario) => {
  const datos = get(datosNegocio);
  const contextoSerializado = JSON.stringify(datos, null, 2);

  return `Persona: Eres SIGA, un asistente de IA experto en gestión de inventario para PYMES. Eres amable, directo y tu único objetivo es ayudar al usuario a ahorrar tiempo.
Contexto (Datos): Basa tu respuesta únicamente en los siguientes datos de inventario y ventas: ${contextoSerializado}.
Instrucción Especial: Si el usuario te pide un "gráfico de mermas", tu única respuesta debe ser la palabra [GRAFICO_MERMAS].
Instrucción de Seguridad: Si el usuario pregunta algo que no tiene que ver con la gestión del negocio (inventario, ventas, productos), recuérdale amablemente que estás aquí para ayudarle a que su negocio "siga avanzando" y no puedes responder a eso.
Pregunta del Usuario: ${preguntaUsuario}`;
};

/**
 * Llama al modelo Gemini para obtener una respuesta.
 * Propósito: mantener la interacción con la API encapsulada y fácil de testear.
 * @param {string} prompt
 * @param {string} apiKey
 * @param {typeof fetch} fetchFn
 */
const invocarGemini = async (prompt, apiKey, fetchFn) => {
  const respuesta = await fetchFn(
    `https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=${apiKey}`,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        contents: [
          {
            role: 'user',
            parts: [{ text: prompt }]
          }
        ]
      })
    }
  );

  if (!respuesta.ok) {
    const detalle = await respuesta.text();
    throw new Error(`Gemini respondió con un error: ${detalle}`);
  }

  const cuerpo = await respuesta.json();
  const texto = cuerpo?.candidates?.[0]?.content?.parts?.[0]?.text ?? '';
  return texto.trim();
};

export const POST = async ({ request, fetch }) => {
  try {
    const apiKey = env.GEMINI_API_KEY;
    if (!apiKey) {
      console.warn('GEMINI_API_KEY no está configurada en las variables de entorno.');
      return json({ respuesta: 'La configuración del asistente no está completa. Agrega la variable GEMINI_API_KEY en Vercel.' }, { status: 500 });
    }

    const body = await request.json();
    const mensaje = body?.mensaje?.trim();

    if (!mensaje) {
      return json({ respuesta: 'Necesito una pregunta para poder ayudarte.' }, { status: 400 });
    }

    const prompt = construirPrompt(mensaje);
    const respuestaIA = await invocarGemini(prompt, apiKey, fetch);

    return json({ respuesta: respuestaIA });
  } catch (error) {
    console.error('Error en la función /api/chat:', error);
    return json({ respuesta: 'En este momento no puedo responder. Por favor, inténtalo más tarde.' }, { status: 500 });
  }
};