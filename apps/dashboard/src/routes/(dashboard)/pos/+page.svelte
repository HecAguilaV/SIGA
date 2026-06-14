<script lang="ts">
	import './pos.css';
	import { fade, fly, scale } from 'svelte/transition';
	import { cubicOut } from 'svelte/easing';
	import Card from '@siga/ui-kit/Card.svelte';
	import Badge from '@siga/ui-kit/Badge.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import Input from '@siga/ui-kit/Input.svelte';
	
	// Phosphor icons
	import MagnifyingGlass from 'phosphor-svelte/lib/MagnifyingGlass';
	import ShoppingCart from 'phosphor-svelte/lib/ShoppingCart';
	import Trash from 'phosphor-svelte/lib/Trash';
	import Plus from 'phosphor-svelte/lib/Plus';
	import Minus from 'phosphor-svelte/lib/Minus';
	import CreditCard from 'phosphor-svelte/lib/CreditCard';
	import Bank from 'phosphor-svelte/lib/Bank';
	import Money from 'phosphor-svelte/lib/Money';
	import Receipt from 'phosphor-svelte/lib/Receipt';

	import { deserialize, applyAction } from '$app/forms';
	import { invalidateAll } from '$app/navigation';
	import Modal from '@siga/ui-kit/Modal.svelte';
	import './pos.css';

	let { data }: { data: any } = $props();

	// State
	let searchQuery = $state('');
	let selectedCategory = $state('Todos');
	let cart = $state<any[]>([]);
	let isProcessing = $state(false);
	let isOpeningShift = $state(false);
	let paymentMethod = $state<'CASH' | 'DEBIT' | 'CREDIT'>('CASH');
	
	// Shift State
	let showShiftModal = $derived(!data.activeShift);
	let initialBalance = $state(0);

	// Derived Categories from real data
	const categories = $derived(['Todos', ...new Set(data.products.map((p: any) => p.category || 'General'))]);

	// Filtered products
	const filteredProducts = $derived(
		data.products.filter((p: any) => {
			const matchesSearch = p.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
								 p.sku?.toLowerCase().includes(searchQuery.toLowerCase());
			const matchesCategory = selectedCategory === 'Todos' || p.category === selectedCategory;
			return matchesSearch && matchesCategory;
		})
	);

	// Cart totals
	const subtotal = $derived(cart.reduce((sum, item) => sum + (item.price * item.quantity), 0));
	const tax = $derived(subtotal * 0.19); // IVA 19%
	const total = $derived(subtotal + tax);

	// Actions
	async function handleOpenShift() {
		isOpeningShift = true;
		const formData = new FormData();
		formData.append('initialBalance', initialBalance.toString());

		const response = await fetch('?/openShift', {
			method: 'POST',
			body: formData
		});

		const result = deserialize(await response.text());
		if (result.type === 'success') {
			await invalidateAll();
		} else {
			alert('Error al abrir caja: ' + (result as any).data?.message);
		}
		isOpeningShift = false;
	}

	function addToCart(product: any) {
		if (!data.activeShift) return;
		const existing = cart.find(item => item.id === product.id);
		if (existing) {
			existing.quantity += 1;
		} else {
			cart = [...cart, { ...product, quantity: 1 }];
		}
	}

	function removeFromCart(productId: string) {
		cart = cart.filter(item => item.id !== productId);
	}

	function updateQuantity(productId: string, delta: number) {
		const index = cart.findIndex(i => i.id === productId);
		if (index !== -1) {
			cart[index].quantity = Math.max(1, cart[index].quantity + delta);
		}
	}

	async function handleCheckout() {
		if (cart.length === 0 || !data.activeShift) return;
		isProcessing = true;
		
		const formData = new FormData();
		formData.append('cart', JSON.stringify(cart));
		formData.append('paymentMethod', paymentMethod);

		try {
			const response = await fetch('?/checkout', {
				method: 'POST',
				body: formData
			});

			const result = deserialize(await response.text());

			if (result.type === 'success') {
				setTimeout(() => {
					alert('¡Venta realizada con éxito!');
					cart = [];
					isProcessing = false;
					invalidateAll();
				}, 2000);
			} else {
				isProcessing = false;
				alert('Error: ' + (result as any).data?.message || 'Error desconocido');
			}
		} catch (e) {
			isProcessing = false;
			alert('Error de red al procesar la venta');
		}
	}
</script>

<svelte:head>
	<title>SIGA — Terminal de Ventas (POS)</title>
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@300;400;500;700;800&display=swap" rel="stylesheet">
</svelte:head>

<!-- Shift Modal (Apertura de Caja) -->
{#if showShiftModal}
	<div class="modal-overlay" transition:fade>
		<div class="shift-modal pos-scope" in:scale>
			<div class="modal-header">
				<Bank size={32} weight="duotone" class="text-primary" />
				<h2>Apertura de Caja</h2>
				<p>Es necesario iniciar un turno para comenzar a vender.</p>
			</div>
			
			<div class="modal-body">
				<div class="input-group">
					<label for="balance">Saldo Inicial (Efectivo)</label>
					<input 
						type="number" 
						id="balance" 
						bind:value={initialBalance} 
						placeholder="0"
					/>
				</div>
			</div>

			<div class="modal-footer">
				<Button 
					variant="primary" 
					style="width: 100%; height: 50px;"
					disabled={isOpeningShift}
					onclick={handleOpenShift}
				>
					{isOpeningShift ? 'Abriendo...' : 'Abrir Turno de Venta'}
				</Button>
			</div>
		</div>
	</div>
{/if}

<div class="pos-container pos-scope" class:blurred={showShiftModal} in:fade={{ duration: 400 }}>
	{#if isProcessing}
		<div class="scan-animation"></div>
	{/if}
	<!-- Main POS Area -->
	<main class="pos-main">
		<header class="pos-header">
			<div class="search-bar">
				<MagnifyingGlass size={20} class="search-icon" />
				<input 
					type="text" 
					placeholder="Buscar por nombre o SKU..." 
					bind:value={searchQuery}
				/>
			</div>
			<div class="category-filters">
				{#each categories as category}
					<button 
						onclick={() => selectedCategory = category}
						class="filter-btn"
						class:active={selectedCategory === category}
					>
						<Badge variant={selectedCategory === category ? 'info' : 'default'}>
							{category}
						</Badge>
					</button>
				{/each}
			</div>
		</header>

		<div class="products-grid">
			{#each filteredProducts as product (product.id)}
				<button 
					class="product-card" 
					onclick={() => addToCart(product)}
					in:scale={{ duration: 300, start: 0.9, easing: cubicOut }}
				>
					<div class="product-img-wrapper">
						<img src="/S.png" alt={product.name} />
						<div class="product-overlay"><Plus size={24} weight="bold" /></div>
					</div>
					<div class="product-info">
						<p class="name">{product.name}</p>
						<p class="sku">{product.sku}</p>
						<p class="price">${product.price.toLocaleString('es-CL')}</p>
					</div>
					{#if product.stock < 10}
						<div class="low-stock-badge">Stock: {product.stock}</div>
					{/if}
				</button>
			{:else}
				<div class="empty-results">
					<p>No se encontraron productos.</p>
				</div>
			{/each}
		</div>
	</main>

	<!-- Sidebar Cart -->
	<aside class="pos-cart">
		<Card variant="glass" padding="none">
			{#snippet children()}
				<div class="cart-container">
					<header class="cart-header">
						<div class="title-group">
							<ShoppingCart size={24} weight="duotone" />
							<h2>Carrito</h2>
						</div>
						<Badge variant="primary">{cart.length} items</Badge>
					</header>

					<div class="cart-items">
						{#each cart as item (item.id)}
							<div class="cart-item" in:fly={{ x: 20, duration: 300 }}>
								<div class="item-info">
									<p class="item-name">{item.name}</p>
									<p class="item-price">${(item.price * item.quantity).toLocaleString('es-CL')}</p>
								</div>
								<div class="item-actions">
									<div class="qty-controls">
										<button onclick={() => updateQuantity(item.id, -1)}><Minus size={12} /></button>
										<span>{item.quantity}</span>
										<button onclick={() => updateQuantity(item.id, 1)}><Plus size={12} /></button>
									</div>
									<button class="btn-delete" onclick={() => removeFromCart(item.id)}>
										<Trash size={16} />
									</button>
								</div>
							</div>
						{:else}
							<div class="empty-cart">
								<ShoppingCart size={48} weight="thin" />
								<p>El carrito está vacío</p>
							</div>
						{/each}
					</div>

					<footer class="cart-footer">
						<div class="totals">
							<div class="total-row">
								<span>Subtotal</span>
								<span>${subtotal.toLocaleString('es-CL')}</span>
							</div>
							<div class="total-row">
								<span>IVA (19%)</span>
								<span>${tax.toLocaleString('es-CL')}</span>
							</div>
							<div class="total-row grand-total">
								<span>TOTAL</span>
								<span>${total.toLocaleString('es-CL')}</span>
							</div>
						</div>

						<div class="payment-methods">
							<p class="section-label">Método de Pago</p>
							<div class="method-grid">
								<button 
									class:active={paymentMethod === 'CASH'} 
									onclick={() => paymentMethod = 'CASH'}
								>
									<Money size={20} /> Efectivo
								</button>
								<button 
									class:active={paymentMethod === 'DEBIT'} 
									onclick={() => paymentMethod = 'DEBIT'}
								>
									<Bank size={20} /> Débito
								</button>
								<button 
									class:active={paymentMethod === 'CREDIT'} 
									onclick={() => paymentMethod = 'CREDIT'}
								>
									<CreditCard size={20} /> Crédito
								</button>
							</div>
						</div>

						<Button 
							variant="primary" 
							disabled={cart.length === 0 || isProcessing}
							onclick={handleCheckout}
							style="width: 100%; height: 60px; font-size: 1.1rem;"
						>
							{#if isProcessing}
								Procesando...
							{:else}
								Finalizar Venta <Receipt size={20} style="margin-left: 8px" />
							{/if}
						</Button>
					</footer>
				</div>
			{/snippet}
		</Card>
	</aside>
</div>

<style>
	.pos-container {
		display: grid;
		grid-template-columns: 1fr 380px;
		gap: var(--spacing-lg);
		height: calc(100vh - 100px);
		margin: -var(--spacing-md);
		padding: var(--spacing-md);
		background: var(--color-bg-alt);
	}

	.pos-main {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
		overflow: hidden;
	}

	.pos-header {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-sm);
		padding: var(--spacing-md);
		background: var(--color-surface);
		border-radius: var(--radius-xl);
		border: 1px solid var(--color-border-light);
	}

	.search-bar {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		background: var(--color-bg-alt);
		padding: var(--spacing-sm) var(--spacing-md);
		border-radius: var(--radius-md);
		border: 1px solid var(--color-border);
	}

	.search-bar input {
		flex: 1;
		background: transparent;
		border: none;
		outline: none;
		color: var(--color-text);
		font-size: var(--font-size-base);
	}

	.category-filters {
		display: flex;
		gap: var(--spacing-sm);
	}

	.filter-btn {
		background: none;
		border: none;
		padding: 0;
		cursor: pointer;
		transition: opacity 0.2s;
	}

	.filter-btn:hover {
		opacity: 0.8;
	}

	.products-grid {
		flex: 1;
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
		gap: var(--spacing-md);
		overflow-y: auto;
		padding-right: 4px;
	}

	.product-card {
		background: var(--color-surface);
		border: 1px solid var(--color-border-light);
		border-radius: var(--radius-lg);
		padding: var(--spacing-md);
		display: flex;
		flex-direction: column;
		gap: var(--spacing-sm);
		text-align: left;
		cursor: pointer;
		transition: all var(--transition-fast);
		position: relative;
		overflow: hidden;
	}

	.product-card:hover {
		transform: translateY(-4px);
		border-color: var(--color-accent);
		box-shadow: var(--shadow-md);
	}

	.product-img-wrapper {
		aspect-ratio: 1;
		background: var(--color-bg-alt);
		border-radius: var(--radius-md);
		display: flex;
		align-items: center;
		justify-content: center;
		position: relative;
	}

	.product-img-wrapper img {
		width: 60%;
		opacity: 0.5;
	}

	.product-overlay {
		position: absolute;
		inset: 0;
		background: rgba(0, 245, 212, 0.2);
		display: flex;
		align-items: center;
		justify-content: center;
		opacity: 0;
		transition: opacity 0.2s;
		color: var(--color-primary-dark);
	}

	.product-card:hover .product-overlay {
		opacity: 1;
	}

	.name {
		font-weight: var(--font-weight-bold);
		font-size: var(--font-size-sm);
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.sku {
		font-size: 10px;
		font-family: var(--font-mono);
		color: var(--color-text-muted);
	}

	.price {
		font-weight: 800;
		color: var(--color-primary);
		margin-top: auto;
	}

	.low-stock-badge {
		position: absolute;
		top: 8px;
		right: 8px;
		background: var(--color-error-bg);
		color: var(--color-error-text);
		font-size: 9px;
		font-weight: bold;
		padding: 2px 6px;
		border-radius: var(--radius-full);
	}

	/* Cart Styles */
	.pos-cart {
		height: 100%;
	}

	:global(.pos-cart .card) { height: 100%; }

	.cart-container {
		height: 100%;
		display: flex;
		flex-direction: column;
	}

	.cart-header {
		padding: var(--spacing-lg);
		border-bottom: 1px solid var(--color-border-light);
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.title-group {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		color: var(--color-primary);
	}

	.cart-items {
		flex: 1;
		overflow-y: auto;
		display: flex;
		flex-direction: column;
		padding: var(--spacing-md);
		gap: var(--spacing-sm);
	}

	.cart-item {
		display: flex;
		justify-content: space-between;
		padding: var(--spacing-sm);
		background: var(--color-surface);
		border-radius: var(--radius-md);
		border: 1px solid var(--color-border-light);
	}

	.item-name {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-semibold);
	}

	.item-price {
		font-size: var(--font-size-xs);
		color: var(--color-text-secondary);
	}

	.item-actions {
		display: flex;
		align-items: center;
		gap: var(--spacing-md);
	}

	.qty-controls {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		background: var(--color-bg-alt);
		padding: 2px;
		border-radius: var(--radius-sm);
	}

	.qty-controls button {
		width: 20px;
		height: 20px;
		border: none;
		background: var(--color-surface);
		border-radius: 4px;
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
	}

	.btn-delete {
		color: var(--color-error);
		background: transparent;
		border: none;
		cursor: pointer;
		opacity: 0.6;
	}

	.btn-delete:hover { opacity: 1; }

	.empty-cart {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		color: var(--color-text-muted);
		gap: var(--spacing-md);
	}

	.cart-footer {
		padding: var(--spacing-lg);
		background: var(--color-bg-alt);
		border-top: 1px solid var(--color-border-light);
		display: flex;
		flex-direction: column;
		gap: var(--spacing-lg);
	}

	.totals {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-xs);
	}

	.total-row {
		display: flex;
		justify-content: space-between;
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
	}

	.grand-total {
		margin-top: var(--spacing-sm);
		padding-top: var(--spacing-sm);
		border-top: 2px dashed var(--color-border);
		font-size: var(--font-size-xl);
		font-weight: 800;
		color: var(--color-text);
	}

	.section-label {
		font-size: 10px;
		font-weight: 700;
		color: var(--color-text-muted);
		text-transform: uppercase;
		letter-spacing: 0.1em;
		margin-bottom: var(--spacing-sm);
	}

	.method-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: var(--spacing-xs);
	}

	.method-grid button {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 4px;
		padding: 8px;
		font-size: 10px;
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		cursor: pointer;
		transition: all var(--transition-fast);
		color: var(--color-text-secondary);
	}

	.method-grid button.active {
		border-color: var(--color-accent);
		background: var(--color-accent-light);
		color: var(--color-primary-dark);
	}

	@media (max-width: 1024px) {
		.pos-container { grid-template-columns: 1fr; }
		.pos-cart { display: none; } /* On mobile/tablet we'd use a floating FAB for cart */
	}
</style>
