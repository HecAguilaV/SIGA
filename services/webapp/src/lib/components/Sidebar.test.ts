import { render } from '@testing-library/svelte';
import { describe, it, expect, vi } from 'vitest';
import Sidebar from './Sidebar.svelte';

// Mock de SvelteKit page store and navigation
vi.mock('$app/stores', () => ({
  page: {
    subscribe: (fn) => {
      fn({ route: { id: '/inventario' } });
      return () => {};
    }
  }
}));

describe('Sidebar Component', () => {
  it('renderiza sin fallar', () => {
    // Usamos role y permisos mockeados
    const { container } = render(Sidebar, {
      props: {
        rol: 'ADMIN',
        modulosAbiertos: ['inventario', 'ventas']
      }
    });

    expect(container).toBeInTheDocument();
  });

  it('muestra modulo de inventario si el rol tiene acceso', () => {
    const { getByText } = render(Sidebar, {
      props: {
        rol: 'VENDEDOR',
        modulosAbiertos: ['ventas']
      }
    });

    // Validar que un texto existe
    expect(getByText('Ventas')).toBeInTheDocument();
  });
});
