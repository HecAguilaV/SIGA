import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/svelte';
import BentoGrid from '$lib/components/layout/BentoGrid.svelte';

describe('BentoGrid', () => {
    it('renders with default column count', () => {
        const { container } = render(BentoGrid);
        const grid = container.querySelector('.bento-grid') as HTMLElement;
        expect(grid?.style.getPropertyValue('--grid-cols')).toBe('12');
    });

    it('applies custom column count', () => {
        const { container } = render(BentoGrid, { props: { cols: 6 } });
        const grid = container.querySelector('.bento-grid') as HTMLElement;
        expect(grid?.style.getPropertyValue('--grid-cols')).toBe('6');
    });
});
