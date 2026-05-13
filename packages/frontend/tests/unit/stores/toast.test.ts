import { describe, it, expect, beforeEach, vi } from 'vitest';
import { get } from 'svelte/store';
import { toast } from '../../../src/lib/stores/toast.svelte';

describe('Toast Store', () => {
	beforeEach(() => {
		toast.clear();
		vi.useFakeTimers();
	});

	afterEach(() => {
		vi.useRealTimers();
	});

	it('starts with empty toasts', () => {
		expect(get(toast)).toEqual([]);
	});

	it('adds a toast', () => {
		toast.add({ type: 'info', message: 'Test message' });
		const toasts = get(toast);
		expect(toasts).toHaveLength(1);
		expect(toasts[0].type).toBe('info');
		expect(toasts[0].message).toBe('Test message');
	});

	it('adds multiple toasts', () => {
		toast.add({ type: 'info', message: 'First' });
		toast.add({ type: 'success', message: 'Second' });
		expect(get(toast)).toHaveLength(2);
	});

	it('removes a toast by id', () => {
		const id = toast.add({ type: 'info', message: 'To remove' });
		expect(get(toast)).toHaveLength(1);

		toast.remove(id);
		expect(get(toast)).toHaveLength(0);
	});

	it('removes only the specified toast', () => {
		const id1 = toast.add({ type: 'info', message: 'First' });
		const id2 = toast.add({ type: 'success', message: 'Second' });

		toast.remove(id1);
		const remaining = get(toast);
		expect(remaining).toHaveLength(1);
		expect(remaining[0].id).toBe(id2);
	});

	it('auto-dismisses toast after default duration', () => {
		vi.useFakeTimers();
		toast.add({ type: 'info', message: 'Auto dismiss' });
		expect(get(toast)).toHaveLength(1);

		vi.advanceTimersByTime(5000 + 100);
		expect(get(toast)).toHaveLength(0);
	});

	it('auto-dismisses toast with custom duration', () => {
		vi.useFakeTimers();
		toast.add({ type: 'warning', message: 'Custom duration', duration: 2000 });
		expect(get(toast)).toHaveLength(1);

		vi.advanceTimersByTime(2000 + 100);
		expect(get(toast)).toHaveLength(0);
	});

	it('does not auto-dismiss when autoDismiss is false', () => {
		vi.useFakeTimers();
		toast.add({ type: 'error', message: 'Manual dismiss', autoDismiss: false });
		expect(get(toast)).toHaveLength(1);

		vi.advanceTimersByTime(10000);
		expect(get(toast)).toHaveLength(1);
	});

	it('clears all toasts', () => {
		toast.add({ type: 'info', message: 'First' });
		toast.add({ type: 'success', message: 'Second' });
		toast.add({ type: 'error', message: 'Third' });
		expect(get(toast)).toHaveLength(3);

		toast.clear();
		expect(get(toast)).toHaveLength(0);
	});

	it('assigns unique ids to toasts', () => {
		const id1 = toast.add({ type: 'info', message: 'First' });
		const id2 = toast.add({ type: 'info', message: 'Second' });
		expect(id1).not.toBe(id2);
	});
});
