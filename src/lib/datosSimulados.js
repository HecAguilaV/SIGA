// Archivo de datos simulados para el prototipo de SIGA
// Este archivo contiene el store writable de Svelte con todos los datos de prueba del negocio
// Propósito: Proporcionar datos simulados para demostrar las funcionalidades de inventario, análisis y asistente sin necesidad de una base de datos real

import { writable } from 'svelte/store';

// Store writable que contiene todos los datos del negocio
// Se utiliza writable para permitir actualizaciones reactivas en la aplicación
export const datosNegocio = writable({
  // Lista de locales disponibles en el negocio
  // Cada local tiene un id único y un nombre descriptivo
  locales: [
    { id: 1, nombre: 'ITR' },
    { id: 2, nombre: 'Presidente Ibañez' },
    { id: 3, nombre: 'Serena' }
  ],

  // Lista de productos disponibles
  // Cada producto tiene id, nombre, sku, categoria y stock por local
  // El stock es un objeto donde la clave es el id del local y el valor es la cantidad
  productos: [
    { id: 101, nombre: 'Leche con Chocolate 1L', sku: 'LCH-001', categoria: 'Lácteos', stock: { 1: 18, 2: 24, 3: 96 } },
    { id: 102, nombre: 'Bebida Fantasía 1.5L', sku: 'BFA-002', categoria: 'Bebidas', stock: { 1: 32, 2: 28, 3: 210 } },
    { id: 103, nombre: 'Papas Fritas Grandes', sku: 'PFG-003', categoria: 'Snacks', stock: { 1: 14, 2: 21, 3: 88 } },
    { id: 104, nombre: 'Galletas de Avena', sku: 'GAV-004', categoria: 'Galletas', stock: { 1: 27, 2: 24, 3: 140 } },
    { id: 105, nombre: 'Sándwich Integral Mixto', sku: 'SIM-005', categoria: 'Sándwiches', stock: { 1: 12, 2: 16, 3: 64 } },
    { id: 106, nombre: 'Barra Energética Natural', sku: 'BEN-006', categoria: 'Snacks', stock: { 1: 20, 2: 18, 3: 92 } },
    { id: 107, nombre: 'Café Frío Listo 350ml', sku: 'CFL-007', categoria: 'Bebidas', stock: { 1: 26, 2: 34, 3: 118 } }
  ],

  // Datos de ventas semanales por local
  // Cada registro indica cuánto vendió un local de un producto específico
  ventasSemana: [
    // ITR - Vende mucho café y bebidas
    { localId: 1, productoId: 107, cantidad: 58 },
    { localId: 1, productoId: 102, cantidad: 52 },
    { localId: 1, productoId: 103, cantidad: 38 },
    { localId: 1, productoId: 105, cantidad: 28 },
    { localId: 1, productoId: 104, cantidad: 22 },
    { localId: 1, productoId: 101, cantidad: 15 },
    { localId: 1, productoId: 106, cantidad: 18 },
    // Presidente Ibañez - Vende mucho sándwich e integralidad
    { localId: 2, productoId: 105, cantidad: 52 },
    { localId: 2, productoId: 104, cantidad: 48 },
    { localId: 2, productoId: 101, cantidad: 42 },
    { localId: 2, productoId: 102, cantidad: 35 },
    { localId: 2, productoId: 107, cantidad: 28 },
    { localId: 2, productoId: 106, cantidad: 32 },
    { localId: 2, productoId: 103, cantidad: 22 },
    // Serena - Vende de todo mucho volumen
    { localId: 3, productoId: 102, cantidad: 68 },
    { localId: 3, productoId: 107, cantidad: 62 },
    { localId: 3, productoId: 103, cantidad: 52 },
    { localId: 3, productoId: 106, cantidad: 48 },
    { localId: 3, productoId: 104, cantidad: 42 },
    { localId: 3, productoId: 105, cantidad: 38 },
    { localId: 3, productoId: 101, cantidad: 32 }
  ],

  // Datos de mermas mensuales por categoria
  // Array de objetos con categoria y cantidad de merma
  mermasMes: [
    { categoria: 'Lácteos', cantidad: 12 },
    { categoria: 'Bebidas', cantidad: 5 },
    { categoria: 'Snacks', cantidad: 4 },
    { categoria: 'Sándwiches', cantidad: 3 }
  ],

  // Datos de ventas diarias para los últimos 7 días
  // Permite visualizar tendencias de venta por día
  ventasPorDia: [
    { dia: 'Lunes', totalVentas: 145 },
    { dia: 'Martes', totalVentas: 174 },
    { dia: 'Miércoles', totalVentas: 134 },
    { dia: 'Jueves', totalVentas: 188 },
    { dia: 'Viernes', totalVentas: 205 },
    { dia: 'Sábado', totalVentas: 177 },
    { dia: 'Domingo', totalVentas: 150 }
  ],

  // Datos de ventas diarias por local (últimos 7 días)
  // Permite comparar desempeño entre kioskos
  ventasPorDiaYLocal: [
    // Lunes - todos trabajan bien
    { dia: 'Lunes', local: 1, ventas: 52 },
    { dia: 'Lunes', local: 2, ventas: 35 },
    { dia: 'Lunes', local: 3, ventas: 58 },
    // Martes - Presidente Ibañez tiene su mejor día
    { dia: 'Martes', local: 1, ventas: 48 },
    { dia: 'Martes', local: 2, ventas: 72 },
    { dia: 'Martes', local: 3, ventas: 54 },
    // Miércoles - normalito
    { dia: 'Miércoles', local: 1, ventas: 44 },
    { dia: 'Miércoles', local: 2, ventas: 38 },
    { dia: 'Miércoles', local: 3, ventas: 52 },
    // Jueves - Presidente Ibañez vuelve a pegar fuerte
    { dia: 'Jueves', local: 1, ventas: 56 },
    { dia: 'Jueves', local: 2, ventas: 68 },
    { dia: 'Jueves', local: 3, ventas: 64 },
    // Viernes - buen día para todos
    { dia: 'Viernes', local: 1, ventas: 68 },
    { dia: 'Viernes', local: 2, ventas: 62 },
    { dia: 'Viernes', local: 3, ventas: 75 },
    // Sábado - ITR y Serena trabajan, Presidente Ibañez CERRADO
    { dia: 'Sábado', local: 1, ventas: 85 },
    { dia: 'Sábado', local: 2, ventas: 0 },
    { dia: 'Sábado', local: 3, ventas: 92 },
    // Domingo - ITR y Serena trabajan menos, Presidente Ibañez CERRADO
    { dia: 'Domingo', local: 1, ventas: 72 },
    { dia: 'Domingo', local: 2, ventas: 0 },
    { dia: 'Domingo', local: 3, ventas: 78 }
  ]
});