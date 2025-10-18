import { json } from '@sveltejs/kit';
import { datosGlobales } from '$lib/estado-compartido.js';

export const POST = async ({ request }) => {
  try {
    const body = await request.json();
    const { producto, local, cantidad, accion } = body;

    if (!producto || !local || !cantidad || !accion) {
      return json(
        { error: 'Faltan parámetros: producto, local, cantidad, accion' },
        { status: 400 }
      );
    }

    // Buscar el producto
    const prod = datosGlobales.productos.find(
      (p) => p.nombre.toLowerCase() === producto.toLowerCase() || p.sku === producto
    );

    if (!prod) {
      return json(
        { error: `Producto "${producto}" no encontrado. Crea primero con /api/productos/crear` },
        { status: 404 }
      );
    }

    // Buscar el local
    const loc = datosGlobales.locales.find(
      (l) => l.nombre.toLowerCase() === local.toLowerCase() || l.id === Number(local)
    );

    if (!loc) {
      return json(
        { error: `Local "${local}" no encontrado` },
        { status: 404 }
      );
    }

    // Actualizar stock
    const stockActual = prod.stock[String(loc.id)] || 0;
    let nuevoStock = stockActual;

    if (accion === 'agregar') {
      nuevoStock = stockActual + cantidad;
    } else if (accion === 'reducir') {
      nuevoStock = Math.max(0, stockActual - cantidad);
    }

    prod.stock[String(loc.id)] = nuevoStock;

    return json({
      success: true,
      producto: prod.nombre,
      local: loc.nombre,
      stockAnterior: stockActual,
      stockNuevo: nuevoStock,
      datos: datosGlobales,
      mensaje: `✅ Stock de "${prod.nombre}" en ${loc.nombre}: ${stockActual} → ${nuevoStock}`
    });
  } catch (error) {
    console.error('Error en stock:', error);
    return json(
      { error: 'Error al actualizar stock' },
      { status: 500 }
    );
  }
};
