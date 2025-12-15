# Instrucciones: Sistema de Permisos Granular - WebApp

**Fecha**: Diciembre 2024  
**Equipo**: WebApp (siga-appweb)  
**Objetivo**: Implementar gestión de usuarios y permisos en WebApp

> ⚠️ **IMPORTANTE**: Antes de implementar, lee `RESUMEN_EJECUTIVO_SISTEMA_PERMISOS.md` y `VISION_COMPLETA_ECOSISTEMA.md` para entender el panorama completo.

---

## 🎯 VISIÓN COMPLETA DEL ECOSISTEMA

### WebApp: El Corazón del Sistema

**WebApp es el actor padre del ecosistema SIGA**:
- ✅ Es donde se toman **todas las decisiones de negocio**
- ✅ Es donde el dueño (admin) gestiona su empresa completa
- ✅ Es donde se crean y gestionan usuarios operativos
- ✅ Es donde se asignan permisos según confianza y necesidad
- ✅ Es donde se usa el asistente IA para operaciones diarias
- ✅ Es donde se generan reportes y análisis estratégicos

**WebApp NO es una página de prueba**, es el **sistema operativo completo** donde el dueño orquesta su negocio.

### Flujo del Ecosistema

```
1. Usuario se registra en Web Comercial → Adquiere suscripción
2. Backend crea automáticamente usuario operativo ADMINISTRADOR
3. Admin accede a WebApp desde Web Comercial (SSO, flujo fluido)
4. Admin gestiona su negocio desde WebApp:
   - Crea usuarios operativos (OPERADOR, CAJERO)
   - Asigna permisos según confianza
   - Opera el negocio día a día
   - Usa asistente IA para facilitar operaciones
5. Empleados acceden a WebApp/App Móvil con sus credenciales
6. Cada usuario opera según sus permisos
```

### Filosofía del Sistema

**SIGA está diseñado para PYMEs** donde:
- Los empleados son **multifuncionales**
- La confianza se gana con el tiempo
- No hay tiempo para trabajo de oficina complejo
- El asistente IA es el **orquestador** que facilita todo

**El sistema de permisos granular** permite:
- Flexibilidad real para PYMEs
- Seguridad sin perder funcionalidad
- Adaptación según confianza y necesidad
- OPERADOR puede crear productos (según experiencia real)
- CAJERO de confianza puede ver reportes (si admin se lo asigna)

---

## 📋 RESUMEN DE CAMBIOS EN BACKEND

### Nuevo Sistema de Permisos Granular

El backend ahora implementa un sistema flexible de permisos:

1. **Roles base**: ADMINISTRADOR, OPERADOR, CAJERO (plantillas)
2. **Permisos adicionales**: Admin puede asignar permisos extra a usuarios específicos
3. **Validación granular**: Cada operación valida permisos específicos

### Nuevos Endpoints Disponibles

#### Gestión de Usuarios Operativos
- `GET /api/saas/usuarios` - Listar usuarios (solo admin)
- `GET /api/saas/usuarios/{id}` - Obtener usuario (solo admin)
- `POST /api/saas/usuarios` - Crear usuario operativo (solo admin)
- `PUT /api/saas/usuarios/{id}` - Actualizar usuario (solo admin)
- `DELETE /api/saas/usuarios/{id}` - Desactivar usuario (solo admin)

#### Gestión de Permisos
- `GET /api/saas/usuarios/{id}/permisos` - Obtener permisos de usuario (solo admin)
- `POST /api/saas/usuarios/{id}/permisos` - Asignar permiso (solo admin)
- `DELETE /api/saas/usuarios/{id}/permisos/{codigoPermiso}` - Revocar permiso (solo admin)
- `GET /api/saas/usuarios/permisos/disponibles` - Listar todos los permisos (solo admin)

### Cambios en Validación de Permisos

Los endpoints ahora validan permisos específicos:

**Antes**:
```javascript
// Solo admin podía crear productos
if (rol !== 'ADMINISTRADOR') {
  // Error
}
```

**Ahora**:
```javascript
// OPERADOR también puede crear productos (tiene permiso por defecto)
// CAJERO puede tener permiso si admin se lo asigna
if (!tienePermiso('PRODUCTOS_CREAR')) {
  // Error
}
```

---

## 🎯 CAMBIOS REQUERIDOS EN WEBAPP

### 1. Pantalla de Gestión de Usuarios (Solo Admin)

**Ubicación**: Nueva sección en el menú de administración

**Funcionalidades**:
- Listar usuarios operativos
- Crear nuevos usuarios (OPERADOR, CAJERO)
- Editar usuarios
- Desactivar usuarios
- Asignar/revocar permisos adicionales

#### Componente: GestionUsuarios.jsx

```jsx
import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';

const GestionUsuarios = () => {
  const { token, user } = useAuth();
  const [usuarios, setUsuarios] = useState([]);
  const [permisosDisponibles, setPermisosDisponibles] = useState([]);
  const [mostrarFormulario, setMostrarFormulario] = useState(false);
  const [usuarioSeleccionado, setUsuarioSeleccionado] = useState(null);

  // Solo admin puede ver esta pantalla
  if (user?.rol !== 'ADMINISTRADOR') {
    return <div>No tienes permiso para acceder a esta sección</div>;
  }

  useEffect(() => {
    cargarUsuarios();
    cargarPermisosDisponibles();
  }, []);

  const cargarUsuarios = async () => {
    try {
      const response = await fetch(`${API_URL}/api/saas/usuarios`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error('Error al cargar usuarios');
      }

      const data = await response.json();
      setUsuarios(data.usuarios);
    } catch (error) {
      console.error('Error:', error);
      showError('Error al cargar usuarios');
    }
  };

  const cargarPermisosDisponibles = async () => {
    try {
      const response = await fetch(`${API_URL}/api/saas/usuarios/permisos/disponibles`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error('Error al cargar permisos');
      }

      const data = await response.json();
      setPermisosDisponibles(data.permisos);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const crearUsuario = async (formData) => {
    try {
      const response = await fetch(`${API_URL}/api/saas/usuarios`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          nombre: formData.nombre,
          apellido: formData.apellido,
          email: formData.email,
          password: formData.password,
          rol: formData.rol // OPERADOR o CAJERO
        })
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error al crear usuario');
      }

      const data = await response.json();
      showSuccess('Usuario creado exitosamente');
      cargarUsuarios();
      setMostrarFormulario(false);
    } catch (error) {
      console.error('Error:', error);
      showError(error.message);
    }
  };

  const asignarPermiso = async (usuarioId, codigoPermiso) => {
    try {
      const response = await fetch(
        `${API_URL}/api/saas/usuarios/${usuarioId}/permisos`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            codigoPermiso
          })
        }
      );

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error al asignar permiso');
      }

      showSuccess('Permiso asignado exitosamente');
      cargarUsuarios();
    } catch (error) {
      console.error('Error:', error);
      showError(error.message);
    }
  };

  const revocarPermiso = async (usuarioId, codigoPermiso) => {
    try {
      const response = await fetch(
        `${API_URL}/api/saas/usuarios/${usuarioId}/permisos/${codigoPermiso}`,
        {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error al revocar permiso');
      }

      showSuccess('Permiso revocado exitosamente');
      cargarUsuarios();
    } catch (error) {
      console.error('Error:', error);
      showError(error.message);
    }
  };

  return (
    <div className="gestion-usuarios">
      <h2>Gestión de Usuarios Operativos</h2>
      
      <button onClick={() => setMostrarFormulario(true)}>
        Crear Usuario
      </button>

      {mostrarFormulario && (
        <FormularioCrearUsuario
          onSubmit={crearUsuario}
          onCancel={() => setMostrarFormulario(false)}
        />
      )}

      <table>
        <thead>
          <tr>
            <th>Email</th>
            <th>Nombre</th>
            <th>Rol</th>
            <th>Estado</th>
            <th>Permisos</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {usuarios.map(usuario => (
            <tr key={usuario.id}>
              <td>{usuario.email}</td>
              <td>{usuario.nombre} {usuario.apellido}</td>
              <td>{usuario.rol}</td>
              <td>{usuario.activo ? 'Activo' : 'Inactivo'}</td>
              <td>
                <PermisosUsuario
                  usuario={usuario}
                  permisosDisponibles={permisosDisponibles}
                  onAsignar={asignarPermiso}
                  onRevocar={revocarPermiso}
                />
              </td>
              <td>
                <button onClick={() => editarUsuario(usuario)}>Editar</button>
                <button onClick={() => desactivarUsuario(usuario.id)}>
                  Desactivar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default GestionUsuarios;
```

#### Componente: PermisosUsuario.jsx

```jsx
import React, { useState } from 'react';

const PermisosUsuario = ({ usuario, permisosDisponibles, onAsignar, onRevocar }) => {
  const [mostrarPermisos, setMostrarPermisos] = useState(false);

  // Permisos que el usuario tiene
  const permisosUsuario = usuario.permisos || [];
  
  // Permisos que NO tiene (disponibles para asignar)
  const permisosDisponiblesParaAsignar = permisosDisponibles.filter(
    permiso => !permisosUsuario.includes(permiso.codigo)
  );

  // Agrupar permisos por categoría
  const permisosPorCategoria = permisosDisponibles.reduce((acc, permiso) => {
    if (!acc[permiso.categoria]) {
      acc[permiso.categoria] = [];
    }
    acc[permiso.categoria].push(permiso);
    return acc;
  }, {});

  return (
    <div className="permisos-usuario">
      <button onClick={() => setMostrarPermisos(!mostrarPermisos)}>
        Ver Permisos ({permisosUsuario.length})
      </button>

      {mostrarPermisos && (
        <div className="permisos-detalle">
          <h4>Permisos de {usuario.nombre}</h4>
          
          {/* Permisos actuales */}
          <div className="permisos-actuales">
            <h5>Permisos Asignados</h5>
            {Object.entries(permisosPorCategoria).map(([categoria, permisos]) => (
              <div key={categoria}>
                <strong>{categoria}</strong>
                <ul>
                  {permisos.map(permiso => {
                    const tienePermiso = permisosUsuario.includes(permiso.codigo);
                    return (
                      <li key={permiso.codigo}>
                        {permiso.nombre}
                        {tienePermiso && (
                          <button
                            onClick={() => onRevocar(usuario.id, permiso.codigo)}
                            className="btn-revocar"
                          >
                            Revocar
                          </button>
                        )}
                      </li>
                    );
                  })}
                </ul>
              </div>
            ))}
          </div>

          {/* Asignar nuevos permisos */}
          <div className="asignar-permisos">
            <h5>Asignar Permisos Adicionales</h5>
            {permisosDisponiblesParaAsignar.length === 0 ? (
              <p>El usuario ya tiene todos los permisos disponibles</p>
            ) : (
              <ul>
                {permisosDisponiblesParaAsignar.map(permiso => (
                  <li key={permiso.codigo}>
                    {permiso.nombre} ({permiso.codigo})
                    <button
                      onClick={() => onAsignar(usuario.id, permiso.codigo)}
                      className="btn-asignar"
                    >
                      Asignar
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default PermisosUsuario;
```

### 2. Validación de Permisos en UI

**Ocultar/Mostrar elementos según permisos**:

```jsx
import { useAuth } from '../context/AuthContext';

const ProductosPage = () => {
  const { user, permisos } = useAuth();

  // Verificar si puede crear productos
  const puedeCrear = permisos?.includes('PRODUCTOS_CREAR') || user?.rol === 'ADMINISTRADOR';

  return (
    <div>
      <h2>Productos</h2>
      
      {puedeCrear && (
        <button onClick={crearProducto}>
          Crear Producto
        </button>
      )}

      {/* Lista de productos */}
    </div>
  );
};
```

### 3. Manejo de Errores 403 Mejorado

```javascript
// En tu servicio de API
async function crearProducto(producto) {
  try {
    const response = await fetch(`${API_URL}/api/saas/productos`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(producto)
    });

    if (!response.ok) {
      const error = await response.json();
      
      if (response.status === 403) {
        // Error de permisos
        throw new Error(error.message || 'No tienes permiso para crear productos');
      }
      
      throw new Error(error.message || 'Error al crear producto');
    }

    return await response.json();
  } catch (error) {
    console.error('Error:', error);
    throw error;
  }
}
```

### 4. Actualizar Contexto de Autenticación

```javascript
// AuthContext.js
import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [permisos, setPermisos] = useState([]);

  useEffect(() => {
    // Cargar token desde localStorage o URL
    const tokenFromStorage = localStorage.getItem('operational_token');
    const tokenFromURL = new URLSearchParams(window.location.search).get('token');
    const tokenToUse = tokenFromURL || tokenFromStorage;

    if (tokenToUse) {
      setToken(tokenToUse);
      localStorage.setItem('operational_token', tokenToUse);
      
      // Decodificar token para obtener usuario
      const decoded = decodeJWT(tokenToUse);
      setUser({
        id: decoded.userId,
        email: decoded.email,
        rol: decoded.rol
      });

      // Cargar permisos del usuario
      cargarPermisos(decoded.userId, tokenToUse);
    }
  }, []);

  const cargarPermisos = async (userId, token) => {
    try {
      const response = await fetch(`${API_URL}/api/saas/usuarios/${userId}/permisos`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const data = await response.json();
        setPermisos(data.permisos || []);
      }
    } catch (error) {
      console.error('Error al cargar permisos:', error);
    }
  };

  const tienePermiso = (codigoPermiso) => {
    if (user?.rol === 'ADMINISTRADOR') {
      return true; // Admin tiene todos los permisos
    }
    return permisos.includes(codigoPermiso);
  };

  return (
    <AuthContext.Provider value={{ user, token, permisos, tienePermiso }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
```

---

## 📋 CHECKLIST DE IMPLEMENTACIÓN

### ⚠️ IMPORTANTE: Cambios Necesarios vs Recomendados

#### 🔴 CAMBIOS NECESARIOS (Obligatorios)

1. **Gestión de Usuarios Operativos** (Solo Admin)
   - ✅ **NECESARIO**: Crear pantalla de gestión de usuarios
   - ✅ **NECESARIO**: Permitir crear usuarios OPERADOR y CAJERO
   - ✅ **NECESARIO**: Permitir asignar/revocar permisos adicionales
   - **Razón**: WebApp es el centro de decisiones, admin debe poder gestionar usuarios

2. **Cargar Permisos del Usuario**
   - ✅ **NECESARIO**: Cargar permisos al iniciar sesión o recibir token
   - ✅ **NECESARIO**: Guardar permisos en contexto/estado global
   - **Razón**: Necesario para validar permisos en UI y API

3. **Manejo de Errores 403**
   - ✅ **NECESARIO**: Mostrar mensajes específicos del backend cuando hay error 403
   - **Razón**: El backend ahora retorna mensajes específicos sobre qué permiso falta

#### 🟡 CAMBIOS RECOMENDADOS (Mejoran UX pero no son críticos)

1. **Validación de Permisos en UI**
   - 🟡 **RECOMENDADO**: Ocultar botones/acciones si el usuario no tiene permiso
   - 🟡 **RECOMENDADO**: Mostrar mensajes informativos cuando falta permiso
   - **Razón**: Mejora UX, pero el backend ya valida y retorna errores claros

2. **Indicadores Visuales**
   - 🟡 **RECOMENDADO**: Mostrar badges o indicadores de permisos del usuario
   - **Razón**: Ayuda a entender qué puede hacer el usuario, pero no es crítico

### Fase 1: Cambios Necesarios (Prioridad Alta)

- [ ] **NECESARIO**: Crear componente GestionUsuarios (solo visible para admin)
- [ ] **NECESARIO**: Crear componente PermisosUsuario (para asignar/revocar permisos)
- [ ] **NECESARIO**: Crear formulario de creación de usuarios (OPERADOR, CAJERO)
- [ ] **NECESARIO**: Implementar asignar/revocar permisos
- [ ] **NECESARIO**: Actualizar contexto de autenticación para cargar permisos
- [ ] **NECESARIO**: Actualizar manejo de errores 403 para mostrar mensajes específicos
- [ ] **NECESARIO**: Agregar ruta en el router para gestión de usuarios
- [ ] **NECESARIO**: Agregar enlace en menú de administración

### Fase 2: Cambios Recomendados (Prioridad Media)

- [ ] **RECOMENDADO**: Ocultar/mostrar botones según permisos
- [ ] **RECOMENDADO**: Agregar validación de permisos antes de ejecutar acciones
- [ ] **RECOMENDADO**: Mostrar indicadores visuales de permisos

### Fase 3: Testing

- [ ] Probar creación de usuarios (OPERADOR, CAJERO)
- [ ] Probar asignación de permisos
- [ ] Probar revocación de permisos
- [ ] Probar que OPERADOR puede crear productos
- [ ] Probar que CAJERO sin permiso no puede crear productos
- [ ] Probar que CAJERO con permiso puede crear productos
- [ ] Probar manejo de errores 403 con mensajes específicos

---

## 🎯 CASOS DE USO

### Caso 1: Admin Crea Usuario OPERADOR

1. Admin accede a "Gestión de Usuarios"
2. Click en "Crear Usuario"
3. Completa formulario:
   - Nombre: "Juan Pérez"
   - Email: "juan@empresa.cl"
   - Password: "password123"
   - Rol: "OPERADOR"
4. Click en "Crear"
5. Usuario creado con permisos por defecto de OPERADOR

### Caso 2: Admin Asigna Permiso a CAJERO

1. Admin accede a "Gestión de Usuarios"
2. Click en "Ver Permisos" del CAJERO
3. En "Asignar Permisos Adicionales", busca "REPORTES_VER"
4. Click en "Asignar"
5. CAJERO ahora puede ver reportes

### Caso 3: OPERADOR Crea Producto

1. OPERADOR accede a "Productos"
2. Ve botón "Crear Producto" (tiene permiso PRODUCTOS_CREAR por defecto)
3. Click en "Crear Producto"
4. Completa formulario y guarda
5. Producto creado exitosamente

### Caso 4: CAJERO Intenta Crear Producto (Sin Permiso)

1. CAJERO accede a "Productos"
2. NO ve botón "Crear Producto" (no tiene permiso)
3. Si intenta crear por API, recibe error 403

---

## 🎯 CONCLUSIÓN

### Cambios Necesarios (Obligatorios)

**WebApp DEBE implementar**:

1. ✅ **Pantalla de gestión de usuarios** (solo admin)
   - **Por qué**: WebApp es el centro de decisiones, admin debe poder gestionar usuarios
   - **Dónde**: Nueva sección en menú de administración

2. ✅ **Cargar permisos del usuario**
   - **Por qué**: Necesario para validar permisos en UI y mostrar errores claros
   - **Cuándo**: Al iniciar sesión o recibir token (SSO)

3. ✅ **Manejo mejorado de errores 403**
   - **Por qué**: El backend ahora retorna mensajes específicos sobre qué permiso falta
   - **Cómo**: Mostrar el mensaje del backend al usuario

### Cambios Recomendados (Opcionales pero Mejoran UX)

**WebApp DEBERÍA implementar** (pero no es crítico):

1. 🟡 **Validación de permisos en UI** (ocultar/mostrar elementos)
   - **Por qué**: Mejora UX, evita que el usuario intente acciones sin permiso
   - **Nota**: El backend ya valida y retorna errores claros, esto es solo para mejor UX

2. 🟡 **Indicadores visuales de permisos**
   - **Por qué**: Ayuda a entender qué puede hacer el usuario
   - **Nota**: No es crítico, pero mejora la experiencia

### No Requiere

- ❌ Cambios en autenticación (SSO sigue igual)
- ❌ Cambios en flujo de login
- ❌ Cambios en otros componentes existentes (solo agregar gestión de usuarios)

### Resumen

**WebApp es el corazón del sistema**, donde el admin toma todas las decisiones de negocio. La gestión de usuarios y permisos es **fundamental** para que el sistema funcione correctamente según la visión del ecosistema.

---

## 📚 REFERENCIAS

Para entender mejor la visión completa del ecosistema, consulta:
- `VISION_COMPLETA_ECOSISTEMA.md` - Visión completa del ecosistema SIGA
- `SISTEMA_PERMISOS_GRANULAR.md` - Diseño detallado del sistema de permisos
- `SOLUCION_SEGURIDAD_CORRECTA.md` - Análisis de seguridad y decisiones

---

**Instrucciones completas para WebApp - Sistema de Permisos**
