import { test, expect } from '@playwright/test';

test.describe('Auth Flow (E2E)', () => {
	test('redirects to login when accessing protected route without session', async ({ page }) => {
		await page.goto('/dashboard');
		await page.waitForURL(/\/login/);
		expect(page.url()).toContain('/login');
	});

	test('shows login page with correct elements', async ({ page }) => {
		await page.goto('/login');

		// Should show the login form
		await expect(page.locator('h1')).toContainText('SIGA');
		await expect(page.getByLabel('Correo electrónico')).toBeVisible();
		await expect(page.getByLabel('Contraseña')).toBeVisible();
		await expect(page.getByRole('button', { name: /iniciar sesión/i })).toBeVisible();
	});

	test('switches between Customer and User tabs', async ({ page }) => {
		await page.goto('/login');

		// Default tab is "Cliente"
		const customerTab = page.getByRole('tab', { name: 'Cliente' });
		const userTab = page.getByRole('tab', { name: /usuario del sistema/i });

		await expect(customerTab).toHaveAttribute('aria-selected', 'true');

		// Click on User tab
		await userTab.click();
		await expect(userTab).toHaveAttribute('aria-selected', 'true');
		await expect(customerTab).toHaveAttribute('aria-selected', 'false');
	});

	test('shows error on invalid credentials', async ({ page }) => {
		await page.goto('/login');

		await page.getByLabel('Correo electrónico').fill('invalid@test.com');
		await page.getByLabel('Contraseña').fill('wrongpassword');
		await page.getByRole('button', { name: /iniciar sesión/i }).click();

		// Should show error (either server or mock validation)
		await expect(page.locator('[role="alert"]')).toBeVisible();
	});

	test('login with valid customer credentials redirects to dashboard', async ({ page }) => {
		await page.goto('/login');

		// Ensure customer tab is active
		await page.getByRole('tab', { name: 'Cliente' }).click();

		await page.getByLabel('Correo electrónico').fill('cliente@demo.com');
		await page.getByLabel('Contraseña').fill('demo1234');
		await page.getByRole('button', { name: /iniciar sesión/i }).click();

		// Should redirect to dashboard or root
		await page.waitForURL(/\/(?!login)/);
	});

	test('login with valid user credentials redirects to dashboard', async ({ page }) => {
		await page.goto('/login');

		// Switch to User tab
		await page.getByRole('tab', { name: /usuario del sistema/i }).click();

		await page.getByLabel('Correo electrónico').fill('admin@siga.cl');
		await page.getByLabel('Contraseña').fill('admin1234');
		await page.getByRole('button', { name: /iniciar sesión/i }).click();

		// Should redirect to dashboard
		await page.waitForURL(/\/(?!login)/);
	});

	test('redirect preserves original path after login', async ({ page }) => {
		const redirectUrl = '/dashboard';
		await page.goto(`/login?redirect=${encodeURIComponent(redirectUrl)}`);

		await page.getByRole('tab', { name: 'Cliente' }).click();
		await page.getByLabel('Correo electrónico').fill('cliente@demo.com');
		await page.getByLabel('Contraseña').fill('demo1234');
		await page.getByRole('button', { name: /iniciar sesión/i }).click();

		// Should redirect to the original path
		await page.waitForURL(/\/dashboard/);
	});

	test('complete auth cycle: login → dashboard → logout → redirect to login', async ({ page }) => {
		// Step 1: Login
		await page.goto('/login');
		await page.getByRole('tab', { name: 'Cliente' }).click();
		await page.getByLabel('Correo electrónico').fill('cliente@demo.com');
		await page.getByLabel('Contraseña').fill('demo1234');
		await page.getByRole('button', { name: /iniciar sesión/i }).click();
		await page.waitForURL(/\/(?!login)/);

		// Step 2: Should be on dashboard
		await expect(page.locator('h1')).toContainText('Bienvenido');

		// Step 3: Click logout
		await page.getByRole('button', { name: /cerrar sesión/i }).click();

		// Step 4: Should redirect to login
		await page.waitForURL(/\/login/);
		await expect(page.getByRole('button', { name: /iniciar sesión/i })).toBeVisible();
	});
});
