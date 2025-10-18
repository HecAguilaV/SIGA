// Estado compartido para todos los endpoints
// Esto permite que los cambios en un endpoint se reflejen en los otros

export let datosGlobales = {
  locales: [
    { id: 1, nombre: 'ITR' },
    { id: 2, nombre: 'Presidente Ibañez' },
    { id: 3, nombre: 'Serena' }
  ],
  productos: [
    { id: 101, nombre: 'Leche con Chocolate 1L', sku: 'LCH-001', categoria: 'Lácteos', stock: { 1: 18, 2: 24, 3: 96 } },
    { id: 102, nombre: 'Bebida Fantasía 1.5L', sku: 'BFA-002', categoria: 'Bebidas', stock: { 1: 32, 2: 28, 3: 210 } },
    { id: 103, nombre: 'Papas Fritas Grandes', sku: 'PFG-003', categoria: 'Snacks', stock: { 1: 14, 2: 21, 3: 88 } },
    { id: 104, nombre: 'Galletas de Avena', sku: 'GAV-004', categoria: 'Galletas', stock: { 1: 27, 2: 24, 3: 140 } },
    { id: 105, nombre: 'Sándwich Integral Mixto', sku: 'SIM-005', categoria: 'Sándwiches', stock: { 1: 12, 2: 16, 3: 64 } },
    { id: 106, nombre: 'Barra Energética Natural', sku: 'BEN-006', categoria: 'Snacks', stock: { 1: 20, 2: 18, 3: 92 } },
    { id: 107, nombre: 'Café Frío Listo 350ml', sku: 'CFL-007', categoria: 'Bebidas', stock: { 1: 26, 2: 34, 3: 118 } }
  ]
};

export function obtenerDatos() {
  return datosGlobales;
}

export function actualizarDatos(nuevosDatos) {
  datosGlobales = nuevosDatos;
}
