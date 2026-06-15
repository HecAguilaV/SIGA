<script lang="ts">
	import Button from '$lib/components/ui/Button.svelte';
	import Input from '$lib/components/ui/Input.svelte';
	import Card from '$lib/components/ui/Card.svelte';

	type PageData = {
		user: {
			email: string;
			name: string;
			id: string;
		};
	};

	type FormData = {
		error?: string;
	};

	let {
		data,
		form
	}: {
		data: PageData;
		form?: FormData | null;
	} = $props();

	let name = $state('');
	let companyName = $state('');
	let skipLoading = $state(false);

	const emailPrefix = data.user.email?.split('@')[0] || '';
</script>

<svelte:head>
	<title>Completa tu perfil — SIGA</title>
</svelte:head>

<Card variant="glass" padding="lg">
	{#snippet header()}
		<div class="onboarding-header">
			<h1 class="onboarding-title">¡Bienvenido a SIGA!</h1>
			<p class="onboarding-subtitle">
				Solo unos pasos más para completar tu perfil
			</p>
		</div>
	{/snippet}

	{#snippet children()}
		<form method="POST">
			<!-- Error message -->
			{#if form?.error}
				<div class="onboarding-error" role="alert">
					<span class="material-symbols-outlined" style="font-size: 18px;">error</span>
					{form.error}
				</div>
			{/if}

			<!-- Welcome -->
			<div class="onboarding-welcome">
				<p>
					Hola <strong>{emailPrefix}</strong>, cuéntanos más sobre ti para
					personalizar tu experiencia.
				</p>
			</div>

			<!-- Fields -->
			<div class="onboarding-fields">
				<Input
					type="text"
					name="name"
					label="Nombre completo"
					placeholder="Ej: María García"
					bind:value={name}
					required
					autocomplete="name"
				/>

				<Input
					type="text"
					name="companyName"
					label="Nombre de tu empresa (opcional)"
					placeholder="Ej: Mi Empresa SRL"
					bind:value={companyName}
				/>
			</div>

			<!-- Submit -->
			<div class="onboarding-actions">
				<Button type="submit" variant="primary" style="width: 100%; padding: 14px;">
					Guardar y continuar
				</Button>

				<a href="/dashboard" class="onboarding-skip" role="button">
					Omitir por ahora
				</a>
			</div>
		</form>
	{/snippet}
</Card>

<style>
	.onboarding-header {
		text-align: center;
		padding: var(--spacing-lg) 0;
	}

	.onboarding-title {
		font-size: var(--font-size-3xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-accent);
		letter-spacing: -0.02em;
	}

	.onboarding-subtitle {
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
		margin-top: var(--spacing-xs);
	}

	.onboarding-error {
		padding: 10px 14px;
		background: var(--color-error-bg);
		color: var(--color-error-text);
		border-radius: var(--radius-md);
		font-size: var(--font-size-sm);
		margin-bottom: var(--spacing-md);
		border: 1px solid var(--color-error);
	}

	.onboarding-welcome {
		font-size: var(--font-size-body-md);
		color: var(--color-text-secondary);
		margin-bottom: var(--spacing-lg);
		padding: 12px 16px;
		background: var(--color-surface-container-low);
		border-radius: var(--radius-md);
		line-height: 1.5;
	}

	.onboarding-fields {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
		margin-bottom: var(--spacing-lg);
	}

	.onboarding-actions {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
	}

	.onboarding-skip {
		display: block;
		text-align: center;
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
		text-decoration: none;
		padding: var(--spacing-sm);
		border-radius: var(--radius-md);
		transition: color var(--transition-fast);
	}

	.onboarding-skip:hover {
		color: var(--color-accent);
		text-decoration: underline;
	}
</style>
