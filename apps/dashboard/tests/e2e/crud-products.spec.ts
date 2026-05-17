import { test, expect } from '@playwright/test';

test.describe('Products CRUD (E2E)', () => {
	test.beforeEach(async ({ page }) => {
		// Login as admin before each test
		await page.goto('/login');
		await page.getByRole('tab', { name: /usuario del sistema/i }).click();
		await page.getByLabel('Correo electrónico').fill('admin@siga.cl');
		await page.getByLabel('Contraseña').fill('admin1234');
		await page.getByRole('button', { name: /iniciar sesión/i }).click();
		await page.waitForURL(/\/(?!login)/);
	});

	test('products page shows list of products', async ({ page }) => {
		await page.goto('/products');
		await expect(page.locator('h1')).toContainText('Productos');
		// Should have a table or search bar
		await expect(page.getByPlaceholder(/buscar/i)).toBeVisible();
	});

	test('search bar filters products', async ({ page }) => {
		await page.goto('/products');

		const searchInput = page.getByPlaceholder(/buscar/i);
		await expect(searchInput).toBeVisible();

		// Type in search
		await searchInput.fill('Harina');
		// Wait for debounce + navigation
		await page.waitForTimeout(400);
		await expect(page).toHaveURL(/search=Harina/);
	});

	test('navigates to create product page', async ({ page }) => {
		await page.goto('/products');
		await page.getByRole('button', { name: /nuevo producto/i }).click();
		await expect(page).toHaveURL(/\/products\/new/);
		await expect(page.locator('h1')).toContainText('Nuevo Producto');
	});

	test('create product form has required fields', async ({ page }) => {
		await page.goto('/products/new');

		// Should have form fields
		await expect(page.getByLabel('Nombre')).toBeVisible();
		await expect(page.getByLabel('SKU')).toBeVisible();
		await expect(page.getByLabel('Precio')).toBeVisible();
		await expect(page.getByLabel('Stock')).toBeVisible();
	});

	test('edit product page loads with data', async ({ page }) => {
		await page.goto('/products/1');
		await expect(page.locator('h1')).toContainText('Editar Producto');
	});

	test('back navigation from edit goes to products list', async ({ page }) => {
		await page.goto('/products/1');
		await page.getByRole('button', { name: /volver/i }).click();
		await expect(page).toHaveURL(/\/products$/);
	});
});
