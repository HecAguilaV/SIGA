import { test, expect, type Page } from '@playwright/test';

/**
 * Emoji Audit (E2E) — verifies the three route pages render Phosphor icons and
 * contain no pictographic emoji characters in the DOM body text.
 *
 * Pictographic ranges per spec (U+1F000–U+1FAFF covers the listed sub-ranges:
 * U+1F300–U+1F5FF, U+1F600–U+1F64F, U+1F680–U+1F6FF, U+1F900–U+1F9FF).
 * The `u` flag enables Unicode code-point escapes.
 *
 * Spec: ui-icon-consistency R1 (eight components), R3, R8.
 */

const PICTOGRAPHIC_EMOJI = /[\u{1F000}-\u{1FAFF}]/u;

async function loginAsCustomer(page: Page): Promise<void> {
	await page.goto('/login');
	await page.getByRole('tab', { name: 'Cliente' }).click();
	await page.getByLabel('Correo electrónico').fill('cliente@demo.com');
	await page.getByLabel('Contraseña').fill('demo1234');
	await page.getByRole('button', { name: /iniciar sesión/i }).click();
	await page.waitForURL(/\/(?!login)/);
}

async function assertNoPictographicEmoji(page: Page): Promise<void> {
	const bodyText = await page.evaluate(() => document.body.textContent ?? '');
	expect(
		PICTOGRAPHIC_EMOJI.test(bodyText),
		`body text must not contain pictographic emoji, found: ${[...bodyText].filter((c) => PICTOGRAPHIC_EMOJI.test(c)).join(' ')}`
	).toBe(false);
}

test.describe('Emoji Audit (E2E)', () => {
	test.beforeEach(async ({ page }) => {
		await loginAsCustomer(page);
	});

	test('dashboard renders Phosphor SVGs and no pictographic emoji', async ({ page }) => {
		await page.goto('/dashboard');
		// At least one Phosphor SVG is present on the page
		await expect(page.locator('svg').first()).toBeVisible();
		await assertNoPictographicEmoji(page);
	});

	test('analytics renders Phosphor SVGs and no pictographic emoji', async ({ page }) => {
		await page.goto('/analytics');
		await expect(page.locator('svg').first()).toBeVisible();
		await assertNoPictographicEmoji(page);
	});

	test('predictive analytics renders Phosphor SVGs and no pictographic emoji', async ({ page }) => {
		await page.goto('/analytics/predictive');
		await expect(page.locator('svg').first()).toBeVisible();
		await assertNoPictographicEmoji(page);
	});
});
