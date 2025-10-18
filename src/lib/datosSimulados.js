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
    { id: 1, nombre: 'Kiosko Central' },
    { id: 2, nombre: 'Kiosko Ingeniería' },
    { id: 3, nombre: 'Bodega Principal' }
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
    { localId: 1, productoId: 102, cantidad: 42 },
    { localId: 1, productoId: 107, cantidad: 38 },
    { localId: 1, productoId: 103, cantidad: 24 },
    { localId: 1, productoId: 105, cantidad: 19 },
    { localId: 2, productoId: 101, cantidad: 28 },
    { localId: 2, productoId: 102, cantidad: 33 },
    { localId: 2, productoId: 106, cantidad: 25 },
    { localId: 2, productoId: 104, cantidad: 20 },
    { localId: 3, productoId: 102, cantidad: 52 },
    { localId: 3, productoId: 107, cantidad: 47 },
    { localId: 3, productoId: 103, cantidad: 36 },
    { localId: 3, productoId: 106, cantidad: 32 }
  ],

  // Datos de mermas mensuales por categoria
  // Array de objetos con categoria y cantidad de merma
  mermasMes: [
    { categoria: 'Lácteos', cantidad: 12 },
    { categoria: 'Bebidas', cantidad: 5 },
    { categoria: 'Snacks', cantidad: 4 },
    { categoria: 'Sándwiches', cantidad: 3 }
  ]
});