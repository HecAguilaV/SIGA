# Spec: ui-crud

**Change**: frontend-desde-cero
**Status**: Draft
**Depends on**: ui-bff, ui-auth-flow, ui-theme

## Requirements

### Functional

- REQ-CRUD-01: El sistema DEBE implementar un componente genérico `CrudTable` que renderice una tabla con columnas configurables, ordenamiento por columna, paginación server-side, y acciones por fila (ver, editar, eliminar).
- REQ-CRUD-02: El sistema DEBE implementar un componente genérico `CrudForm` que renderice un formulario con campos configurables, validación client-side antes de enviar, y estado de carga/enviado/error.
- REQ-CRUD-03: El sistema DEBE proporcionar un buscador/filtro que actualice la query string (`?search=`) y recargue los datos server-side.
- REQ-CRUD-04: La paginación DEBE ser server-side: `CrudTable` pasa `page` y `pageSize` como URL params, la load function los mapea al gateway.
- REQ-CRUD-05: El sistema DEBE mostrar confirmación antes de eliminar un recurso (modal/dialog de confirmación).
- REQ-CRUD-06: El sistema DEBE validar datos en cliente (formato) y en servidor (reglas de negocio) — los errores del servidor DEBEN mostrarse en el formulario.
- REQ-CRUD-07: El patrón CRUD DEBE aplicarse a: Products, Stores, Categories, Users.

### Contrato de componentes

| Prop | CrudTable | CrudForm |
|------|-----------|----------|
| Props | `columns: ColumnDef[]`, `data: T[]`, `total: number`, `page: number`, `pageSize: number`, `actions?: ActionDef[]` | `fields: FieldDef[]`, `onSubmit: (data) => Promise`, `initialValues?: Partial<T>`, `mode: 'create' \| 'edit'` |
| Output | Emite eventos de acción (edit, delete, page change, sort) | `onSubmit` recibe datos validados |
| Slots | `empty` (sin datos), `loading` (skeleton) | - |

### Non-functional

- REQ-CRUD-08: CrudTable DEBE renderizar 20 filas en < 200ms (sin incluir fetch).
- REQ-CRUD-09: La búsqueda DEBE hacerse server-side con debounce de 300ms en cliente.

## Scenarios (GWT)

### Scenario: Listar con paginación
Given un usuario autenticado en `/products`
When la load function retorna 50 productos (página 1 de 3)
Then `CrudTable` muestra 20 productos con columnas: nombre, categoría, stock, precio, acciones
Y el paginador muestra "Página 1 de 3"

### Scenario: Buscar por texto
Given el usuario escribe "harina" en el buscador
When transcurren 300ms de debounce
Then la URL cambia a `?search=harina&page=1`
Y la load function recibe `search=harina` y filtra en gateway
Y `CrudTable` muestra solo productos que contienen "harina"

### Scenario: Crear producto exitoso
Given el usuario completa el formulario de nuevo producto
When hace clic en "Guardar"
Then `CrudForm` valida en cliente (nombre requerido, precio > 0)
Y envía `POST` al gateway vía BFF
Y redirige a la lista con mensaje "Producto creado exitosamente"

### Scenario: Error de validación del servidor
Given el usuario intenta crear un producto con SKU duplicado
When `CrudForm` envía al gateway y recibe 409 Conflict
Then el error del servidor se muestra en el campo `sku` del formulario
Y el formulario permanece abierto con los datos ingresados

### Scenario: Eliminar con confirmación
Given el usuario hace clic en eliminar en una fila de `CrudTable`
When se muestra el modal de confirmación
Then si confirma → `DELETE` al gateway → fila eliminada → tabla refrescada
Y si cancela → no ocurre nada

### Scenario: Eliminar recurso en uso
Given un usuario intenta eliminar una categoría que tiene productos asociados
When el gateway retorna 409 (categoría en uso)
Then se muestra mensaje de error "No se puede eliminar: tiene productos asociados"
Y la lista no se modifica

## Edge Cases
- REQ-CRUD-10: Inyección XSS en búsqueda — los términos de búsqueda DEBEN escaparse antes de mostrar en la UI.
- REQ-CRUD-11: Doble clic en "Guardar" — el botón DEBE deshabilitarse mientras el submit está en curso.

## Acceptance Criteria
- [ ] `CrudTable` y `CrudForm` implementados como componentes Svelte reutilizables
- [ ] CRUD funcional para Products, Stores, Categories, Users
- [ ] Búsqueda server-side con debounce
- [ ] Paginación server-side
- [ ] Confirmación de eliminación
- [ ] Validación cliente + servidor con feedback visual
