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
    { id: 101, nombre: 'Leche con Chocolate 1L', sku: 'LCH-001', categoria: 'Lácteos', stock: { 1: 15, 2: 20, 3: 100 } },
    { id: 102, nombre: 'Bebida Fantasía 1.5L', sku: 'BFA-002', categoria: 'Bebidas', stock: { 1: 30, 2: 25, 3: 200 } },
    { id: 103, nombre: 'Papas Fritas Grandes', sku: 'PFG-003', categoria: 'Snacks', stock: { 1: 12, 2: 18, 3: 80 } },
    { id: 104, nombre: 'Galletas de Avena', sku: 'GAV-004', categoria: 'Galletas', stock: { 1: 25, 2: 22, 3: 150 } }
  ],

  // Datos de ventas semanales
  // Array de objetos con productoId y cantidad vendida en la semana
  ventasSemana: [
    { productoId: 102, cantidad: 50 },
    { productoId: 103, cantidad: 30 },
    { productoId: 101, cantidad: 25 },
    { productoId: 104, cantidad: 15 }
  ],

  // Datos de mermas mensuales por categoria
  // Array de objetos con categoria y cantidad de merma
  mermasMes: [
    { categoria: 'Lácteos', cantidad: 10 },
    { categoria: 'Bebidas', cantidad: 3 },
    { categoria: 'Snacks', cantidad: 2 }
  ]
});