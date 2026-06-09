<script lang="ts">
	import Button from '$lib/components/ui/Button.svelte';
	import Input from '$lib/components/ui/Input.svelte';
	import Card from '$lib/components/ui/Card.svelte';
	import Badge from '$lib/components/ui/Badge.svelte';

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
		{#if form?.success}
			<div style="text-align: center; padding: 20px; background: #e6fffa; border: 1px solid #38b2ac; border-radius: 8px;">
				<h2 style="color: #2c7a7b; margin-bottom: 10px;">¡LOGIN EXITOSO!</h2>
				<p style="margin-bottom: 20px;">Las cookies se setearon correctamente.</p>
				<a 
					href={form.redirectTo} 
					style="display: block; padding: 14px; background: #38b2ac; color: white; text-decoration: none; border-radius: 8px; font-weight: 600;"
				>
					ENTRAR AL DASHBOARD
				</a>
			</div>
		{:else}
			<form 
				method="POST" 
				onsubmit={() => {
					console.log('Formulario enviado!');
					// alert('Enviando login...'); // Descomentar si no ves la consola
				}}
			>
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
					<button
						type="submit"
						style="width: 100%; padding: 14px; background: #0070f3; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: 600;"
					>
						Iniciar sesión (TEST NATIVO)
					</button>
				</div>
			</form>
		{/if}
	{/snippet}

	{#snippet footer()}
		<div class="login-footer">
			<Badge variant="info">Demo: demo@siga.cl / demo1234</Badge>
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
