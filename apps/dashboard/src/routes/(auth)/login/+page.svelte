<script lang="ts">
	import Button from '$lib/components/ui/Button.svelte';
	import Input from '$lib/components/ui/Input.svelte';
	import Card from '$lib/components/ui/Card.svelte';
	import Badge from '$lib/components/ui/Badge.svelte';

	type FormData = {
		error?: string;
		email?: string;
		missing?: string;
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
		<form method="POST">
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
				<Button
					type="submit"
					variant="primary"
					size="lg"
					style="width: 100%"
				>
					Iniciar sesión
				</Button>
			</div>
		</form>
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
