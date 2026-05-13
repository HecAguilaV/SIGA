<script lang="ts">
	import { enhance } from '$app/forms';
	import Button from '$lib/components/ui/Button.svelte';
	import Input from '$lib/components/ui/Input.svelte';
	import Card from '$lib/components/ui/Card.svelte';
	import Badge from '$lib/components/ui/Badge.svelte';
	import { page } from '$app/stores';

	type Tab = 'customer' | 'user';
	type FormData = {
		error?: string;
		email?: string;
		missing?: string;
	};

	let {
		form,
		data
	}: {
		form?: FormData | null;
		data?: Record<string, unknown>;
	} = $props();

	let activeTab: Tab = $state('customer');
	let email = $state('');
	let password = $state('');
	let submitting = $state(false);

	const redirectParam = $derived($page.url.searchParams.get('redirect') || '');
	const placeholderEmail = $derived(
		activeTab === 'customer' ? 'cliente@demo.com' : activeTab === 'user' ? 'admin@siga.com' : 'correo@ejemplo.com'
	);
	const placeholderPass = $derived(
		activeTab === 'customer' ? 'demo1234' : activeTab === 'user' ? 'admin1234' : '••••••••'
	);

	function switchTab(tab: Tab) {
		activeTab = tab;
		email = '';
		password = '';
	}
</script>

<svelte:head>
	<title>Iniciar sesión — SIGA</title>
</svelte:head>

<Card variant="glass" padding="lg">
	{#snippet header()}
		<div class="login-header">
			<h1 class="login-title">SIGA</h1>
			<p class="login-subtitle">Sistema de Gestión</p>
		</div>
	{/snippet}

	{#snippet content()}
		<form
			method="POST"
			action="?/login"
			use:enhance={() => {
				submitting = true;
				return async ({ update }) => {
					submitting = false;
					await update();
				};
			}}
		>
			<input type="hidden" name="redirect" value={redirectParam} />

			<!-- Tabs -->
			<div class="login-tabs" role="tablist">
				<button
					type="button"
					role="tab"
					aria-selected={activeTab === 'customer'}
					class="tab"
					class:tab-active={activeTab === 'customer'}
					onclick={() => switchTab('customer')}
				>
					Cliente
				</button>
				<button
					type="button"
					role="tab"
					aria-selected={activeTab === 'user'}
					class="tab"
					class:tab-active={activeTab === 'user'}
					onclick={() => switchTab('user')}
				>
					Usuario del Sistema
				</button>
			</div>

			<!-- Error message -->
			{#if form?.error}
				<div class="login-error" role="alert">
					{form.error}
				</div>
			{/if}

			<!-- Fields -->
			<div class="login-fields">
				<Input
					type="email"
					name="email"
					label="Correo electrónico"
					placeholder={placeholderEmail}
					bind:value={email}
					required
					autocomplete="email"
					error={form?.missing === 'email' ? form?.error : undefined}
				/>

				<Input
					type="password"
					name="password"
					label="Contraseña"
					placeholder={placeholderPass}
					bind:value={password}
					required
					autocomplete="current-password"
					error={form?.missing === 'password' ? form?.error : undefined}
				/>
			</div>

			<!-- Submit -->
			<div class="login-submit">
				<Button
					type="submit"
					variant="primary"
					size="lg"
					loading={submitting}
					style="width: 100%"
				>
					Iniciar sesión
				</Button>
			</div>
		</form>
	{/snippet}

	{#snippet footer()}
		<div class="login-footer">
			{#if activeTab === 'customer'}
				<Badge variant="info">Demo: cliente@demo.com / demo1234</Badge>
			{:else}
				<Badge variant="info">Demo: admin@siga.com / admin1234</Badge>
			{/if}
		</div>
	{/snippet}
</Card>

<style>
	.login-header {
		text-align: center;
		padding: var(--spacing-lg) 0;
	}

	.login-title {
		font-size: var(--font-size-3xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-accent);
		letter-spacing: -0.02em;
	}

	.login-subtitle {
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
		margin-top: var(--spacing-xs);
	}

	.login-tabs {
		display: flex;
		gap: var(--spacing-xs);
		margin-bottom: var(--spacing-lg);
		background: var(--color-bg-alt);
		padding: 4px;
		border-radius: var(--radius-md);
	}

	.tab {
		flex: 1;
		padding: 10px 16px;
		border: none;
		background: transparent;
		color: var(--color-text-secondary);
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		border-radius: var(--radius-sm);
		cursor: pointer;
		transition: all var(--transition-fast);
	}

	.tab-active {
		background: var(--color-surface);
		color: var(--color-text);
		box-shadow: var(--shadow-sm);
	}

	.login-error {
		padding: 10px 14px;
		background: var(--color-error-bg);
		color: var(--color-error-text);
		border-radius: var(--radius-md);
		font-size: var(--font-size-sm);
		margin-bottom: var(--spacing-md);
		border: 1px solid var(--color-error);
	}

	.login-fields {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
		margin-bottom: var(--spacing-lg);
	}

	.login-submit {
		margin-bottom: var(--spacing-md);
	}

	.login-footer {
		display: flex;
		justify-content: center;
	}
</style>
