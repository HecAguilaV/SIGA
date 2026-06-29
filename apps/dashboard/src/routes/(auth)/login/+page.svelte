<script lang="ts">
	import { page } from '$app/stores';
	import Button from '$lib/components/ui/Button.svelte';
	import Input from '$lib/components/ui/Input.svelte';
	import Card from '$lib/components/ui/Card.svelte';


	type FormData = {
		error?: string;
		email?: string;
		missing?: string;
		success?: boolean;
		redirectTo?: string;
	};

	let {
		form
	}: {
		form?: FormData | null;
	} = $props();

	let email = $state('');
	let password = $state('');

	// Show success banner if redirected from registration
	let registered = $derived($page.url.searchParams.get('registered') === 'true');
	// Show success banner if redirected from email verification
	let verified = $derived($page.url.searchParams.get('verified') === 'true');
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

	{#snippet children()}
		<form 
			method="POST" 
			onsubmit={() => {
				console.log('Formulario enviado!');
			}}
		>
			<!-- Success banner after registration -->
			{#if registered}
				<div class="login-success" role="alert">
					<span class="material-symbols-outlined" style="font-size: 18px;">check_circle</span>
					Cuenta creada exitosamente. Revisa tu correo para verificar tu dirección de email.
				</div>
			{/if}

			<!-- Success banner after email verification -->
			{#if verified}
				<div class="login-success" role="alert">
					<span class="material-symbols-outlined" style="font-size: 18px;">check_circle</span>
					¡Cuenta verificada exitosamente! Ya puedes iniciar sesión.
				</div>
			{/if}

			<!-- Error message -->
			{#if form?.error}
				<div class="login-error" role="alert">
					<span class="material-symbols-outlined" style="font-size: 18px;">error</span>
					{form.error}
				</div>
			{/if}

			<!-- Fields -->
			<div class="login-fields">
				<Input
					type="email"
					name="email"
					label="Correo electrónico"
					placeholder="demo@siga.cl"
					bind:value={email}
					required
					autocomplete="email"
					error={form?.missing === 'email' ? form?.error : undefined}
				/>

				<Input
					type="password"
					name="password"
					label="Contraseña"
					placeholder="demo1234"
					bind:value={password}
					required
					autocomplete="current-password"
					error={form?.missing === 'password' ? form?.error : undefined}
				/>
			</div>

			<!-- Submit -->
			<div class="login-submit">
				<Button type="submit" variant="primary" style="width: 100%; padding: 14px;">
					Iniciar sesión
				</Button>
			</div>
		</form>
	{/snippet}

	{#snippet footer()}
		<div class="login-footer">
			<div class="login-footer-links">
				<a href="/register" class="login-footer-link">Crear cuenta</a>
				<span class="login-footer-separator">·</span>
				<a href="/reset-password" class="login-footer-link">¿Olvidaste tu contraseña?</a>
			</div>

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

	.login-success {
		padding: 10px 14px;
		background: var(--color-success-bg, #ecfdf5);
		color: var(--color-success-text, #065f46);
		border-radius: var(--radius-md);
		font-size: var(--font-size-sm);
		margin-bottom: var(--spacing-md);
		border: 1px solid var(--color-success, #10B981);
		display: flex;
		align-items: center;
		gap: var(--spacing-xs);
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
		flex-direction: column;
		align-items: center;
		gap: var(--spacing-sm);
	}

	.login-footer-links {
		display: flex;
		align-items: center;
		gap: var(--spacing-xs);
		font-size: var(--font-size-sm);
	}

	.login-footer-link {
		color: var(--color-accent);
		font-weight: var(--font-weight-medium);
	}

	.login-footer-link:hover {
		text-decoration: underline;
	}

	.login-footer-separator {
		color: var(--color-text-muted);
	}
</style>
