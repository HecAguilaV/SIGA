import { json } from '@sveltejs/kit';
import { datosGlobales } from '$lib/estado-compartido.js';
import { env } from '$env/dynamic/private';

/**
 * Construye el prompt completo que se enviará al modelo de lenguaje.
 * Propósito: encapsular la lógica de RAG y asegurar que todas las instrucciones se incluyan de manera consistente.
 * @param {string} preguntaUsuario
 * @returns {string}
 */
const construirPrompt = (preguntaUsuario) => {
  const datos = datosGlobales;
  
  // Resumen muy conciso de los datos (evitar JSON gigante)
  const locales = datos.locales?.map((l) => `${l.nombre} (ID: ${l.id})`).join(', ') || 'N/A';
  const productos = datos.productos?.map((p) => `${p.nombre} (${p.categoria})`).slice(0, 10).join(', ') || 'N/A';
  const ventasPromedio = datos.ventasSemana?.map((v) => v.cantidad).reduce((a, b) => a + b, 0) || 0;

  return `Eres SIGA, asistente inteligente de gestión de inventario. Sé conciso y amigable. NUNCA muestres JSON al usuario.

📊 CONTEXTO:
- Locales: ${locales}
- Productos existentes: ${productos}
- Total ventas semanal: ${ventasPromedio} unidades
- Todos los datos: ${JSON.stringify(datos)}

🎯 REGLAS IMPORTANTES:
1. Si piden agregar stock a un producto INEXISTENTE:
   - PRIMERO crea el producto (guesa la categoría si no la menciona - ej: "Panadería", "Bebidas")
   - LUEGO agrega el stock en 2 operaciones CRUD separadas
   
2. Responde SIEMPRE en máximo 2 líneas, amigable y natural
   - ✅ "Listo, agregué 15 rollos de canela a ITR"
   - ❌ No muestres JSON ni tecnicismos

3. CRUD: Si necesitas ejecutar operaciones, responde entre [CRUD_START] y [CRUD_END]
   - Para múltiples operaciones, usa MÚLTIPLES bloques [CRUD_START]...[CRUD_END]
   
🔄 FORMATOS CRUD:
- Crear: {"accion": "crear_producto", "nombre": "Canela", "categoria": "Panadería"}
- Agregar stock: {"accion": "agregar_stock", "producto": "Canela", "local": "ITR", "cantidad": 15}
- Reducir stock: {"accion": "reducir_stock", "producto": "Pan", "local": "Serena", "cantidad": 5}
- Gráficos: [GRAFICO_TORTA], [GRAFICO_BARRAS], [GRAFICO_LINEAS]

Pregunta del usuario: ${preguntaUsuario}`;
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
    `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=${apiKey}`,
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
      return json({ respuesta: 'La configuración del asistente no está completa. Agrega la variable GEMINI_API_KEY en tu .env.local' }, { status: 500 });
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