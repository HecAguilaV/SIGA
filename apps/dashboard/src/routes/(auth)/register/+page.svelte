<script lang="ts">
	import Button from '$lib/components/ui/Button.svelte';
	import Input from '$lib/components/ui/Input.svelte';
	import Card from '$lib/components/ui/Card.svelte';

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
	let name = $state('');
	let companyName = $state('');
</script>

<svelte:head>
	<title>Crear cuenta — SIGA</title>
</svelte:head>

<Card variant="glass" padding="lg">
	{#snippet header()}
		<div class="register-header">
			<h1 class="register-title">Crear cuenta</h1>
			<p class="register-subtitle">Completa tu registro en SIGA</p>
		</div>
	{/snippet}

	{#snippet children()}
		<form method="POST">
			<!-- Error message -->
			{#if form?.error}
				<div class="register-error" role="alert">
					<span class="material-symbols-outlined" style="font-size: 18px;">error</span>
					{form.error}
				</div>
			{/if}

			<!-- Fields -->
			<div class="register-fields">
				<Input
					type="email"
					name="email"
					label="Correo electrónico"
					placeholder="tu@correo.cl"
					bind:value={email}
					required
					autocomplete="email"
					error={form?.missing === 'email' ? form?.error : undefined}
				/>

				<Input
					type="password"
					name="password"
					label="Contraseña"
					placeholder="Mínimo 6 caracteres"
					bind:value={password}
					required
					autocomplete="new-password"
					error={form?.missing === 'password' ? form?.error : undefined}
				/>

				<Input
					type="text"
					name="name"
					label="Nombre (opcional)"
					placeholder="Tu nombre completo"
					bind:value={name}
					autocomplete="name"
				/>

				<Input
					type="text"
					name="companyName"
					label="Empresa (opcional)"
					placeholder="Nombre de tu empresa"
					bind:value={companyName}
				/>
			</div>

			<div class="register-info">
				<span class="material-symbols-outlined" style="font-size: 16px;">info</span>
				<span>Recibirás un correo para verificar tu dirección de email.</span>
			</div>

			<!-- Submit -->
			<div class="register-submit">
				<Button type="submit" variant="primary" style="width: 100%; padding: 14px;">
					Crear cuenta
				</Button>
			</div>
		</form>
	{/snippet}

	{#snippet footer()}
		<div class="register-footer">
			<span>¿Ya tienes cuenta?</span>
			<a href="/login" class="register-link">Iniciar sesión</a>
		</div>
	{/snippet}
</Card>

<style>
	.register-header {
		text-align: center;
		padding: var(--spacing-lg) 0;
	}

	.register-title {
		font-size: var(--font-size-3xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-accent);
		letter-spacing: -0.02em;
	}

	.register-subtitle {
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
		margin-top: var(--spacing-xs);
	}

	.register-error {
		padding: 10px 14px;
		background: var(--color-error-bg);
		color: var(--color-error-text);
		border-radius: var(--radius-md);
		font-size: var(--font-size-sm);
		margin-bottom: var(--spacing-md);
		border: 1px solid var(--color-error);
	}

	.register-fields {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
		margin-bottom: var(--spacing-md);
	}

	.register-info {
		display: flex;
		align-items: center;
		gap: var(--spacing-xs);
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
		margin-bottom: var(--spacing-lg);
		padding: 8px 12px;
		background: var(--color-surface-container-low);
		border-radius: var(--radius-md);
	}

	.register-submit {
		margin-bottom: var(--spacing-md);
	}

	.register-footer {
		display: flex;
		justify-content: center;
		align-items: center;
		gap: var(--spacing-xs);
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
	}

	.register-link {
		color: var(--color-accent);
		font-weight: var(--font-weight-medium);
	}

	.register-link:hover {
		text-decoration: underline;
	}
</style>
