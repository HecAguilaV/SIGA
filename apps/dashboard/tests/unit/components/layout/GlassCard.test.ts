import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/svelte';
import GlassCard from '$lib/components/layout/GlassCard.svelte';

describe('GlassCard', () => {
    it('renders with surface variant by default', () => {
        const { container } = render(GlassCard);
        const card = container.querySelector('.glass-card-item');
        expect(card?.classList.contains('surface')).toBe(true);
    });

    it('renders with glass variant', () => {
        const { container } = render(GlassCard, { props: { variant: 'glass' } });
        const card = container.querySelector('.glass-card-item');
        expect(card?.classList.contains('glass')).toBe(true);
    });

    it('applies correct grid span', () => {
        const { container } = render(GlassCard, { props: { span: 4 } });
        const card = container.querySelector('.glass-card-item') as HTMLElement;
        expect(card?.style.gridColumn).toBe('span 4');
    });
});
