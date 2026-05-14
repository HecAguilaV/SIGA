<script lang="ts" generics="T extends Record<string, unknown>>">
	import Button from '@siga/ui-kit/Button.svelte';
	import Input from '@siga/ui-kit/Input.svelte';

	export interface FieldDef<T> {
		key: string;
		label: string;
		type: 'text' | 'number' | 'email' | 'select' | 'textarea' | 'password';
		options?: { value: string; label: string }[];
		required?: boolean;
		placeholder?: string;
		validate?: (value: string) => string | undefined;
	}

	let {
		fields = [] as FieldDef<T>[],
		onSubmit,
		initialValues = {} as Partial<T>,
		mode = 'create' as 'create' | 'edit',
		serverErrors = {} as Record<string, string>,
		children
	}: {
		fields: FieldDef<T>[];
		onSubmit: (data: Record<string, string>) => Promise<void>;
		initialValues?: Partial<T>;
		mode?: 'create' | 'edit';
		serverErrors?: Record<string, string>;
		children?: import('svelte').Snippet;
	} = $props();

	let formData = $state<Record<string, string>>({});
	let errors = $state<Record<string, string>>({});
	let submitting = $state(false);
	let formSubmitted = $state(false);

	// Initialize form data from initialValues
	$effect(() => {
		const init: Record<string, string> = {};
		for (const field of fields) {
			const val = initialValues[field.key as keyof T];
			init[field.key] = val != null ? String(val) : '';
		}
		formData = init;
	});

	function validateField(field: FieldDef<T>): string | undefined {
		const value = formData[field.key] || '';
		if (field.required && !value.trim()) {
			return `${field.label} es requerido`;
		}
		if (field.validate) {
			return field.validate(value);
		}
		if (field.type === 'email' && value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
			return 'Email inválido';
		}
		if (field.type === 'number' && value && isNaN(Number(value))) {
			return 'Debe ser un número';
		}
		return undefined;
	}

	function validateAll(): boolean {
		const newErrors: Record<string, string> = {};
		for (const field of fields) {
			const err = validateField(field);
			if (err) newErrors[field.key] = err;
		}
		errors = newErrors;
		return Object.keys(newErrors).length === 0;
	}

	function handleFieldChange(key: string, value: string) {
		formData[key] = value;
		if (formSubmitted) {
			const field = fields.find((f) => f.key === key);
			if (field) {
				const err = validateField(field);
				errors = { ...errors, [key]: err || '' };
				if (!err) {
					const { [key]: _, ...rest } = errors;
					errors = rest;
				}
			}
		}
	}

	async function handleSubmit(e: Event) {
		e.preventDefault();
		formSubmitted = true;
		if (!validateAll()) return;

		submitting = true;
		try {
			await onSubmit(formData);
		} catch {
			// Error handling is done by the parent
		} finally {
			submitting = false;
		}
	}

	// Merge server errors
	const allErrors = $derived.by(() => {
		const merged = { ...errors };
		for (const [key, msg] of Object.entries(serverErrors)) {
			if (msg) merged[key] = msg;
		}
		return merged;
	});
</script>

<form class="crud-form" onsubmit={handleSubmit} novalidate>
	<div class="form-fields">
		{#each fields as field}
			<div class="form-field">
				{#if field.type === 'select'}
					<div class="input-group">
						<label class="input-label" for="field-{field.key}">
							{field.label}
							{#if field.required}
								<span class="required" aria-hidden="true">*</span>
							{/if}
						</label>
						<select
							id="field-{field.key}"
							class="input-field"
							class:input-error={!!allErrors[field.key]}
							value={formData[field.key] || ''}
							onchange={(e) => handleFieldChange(field.key, (e.currentTarget as HTMLSelectElement).value)}
							aria-invalid={!!allErrors[field.key]}
							aria-describedby={allErrors[field.key] ? `error-${field.key}` : undefined}
						>
							<option value="">Seleccionar...</option>
							{#each field.options || [] as opt}
								<option value={opt.value}>{opt.label}</option>
							{/each}
						</select>
						{#if allErrors[field.key]}
							<p id="error-{field.key}" class="field-error" role="alert">{allErrors[field.key]}</p>
						{/if}
					</div>
				{:else if field.type === 'textarea'}
					<div class="input-group">
						<label class="input-label" for="field-{field.key}">
							{field.label}
							{#if field.required}
								<span class="required" aria-hidden="true">*</span>
							{/if}
						</label>
						<textarea
							id="field-{field.key}"
							class="input-field textarea-field"
							class:input-error={!!allErrors[field.key]}
							value={formData[field.key] || ''}
							oninput={(e) => handleFieldChange(field.key, (e.currentTarget as HTMLTextAreaElement).value)}
							aria-invalid={!!allErrors[field.key]}
							aria-describedby={allErrors[field.key] ? `error-${field.key}` : undefined}
							rows={3}
						></textarea>
						{#if allErrors[field.key]}
							<p id="error-{field.key}" class="field-error" role="alert">{allErrors[field.key]}</p>
						{/if}
					</div>
				{:else}
					<Input
						type={field.type}
						label={field.label}
						placeholder={field.placeholder}
						value={formData[field.key] || ''}
						error={allErrors[field.key]}
						required={field.required}
						oninput={(e: Event) => handleFieldChange(field.key, (e.currentTarget as HTMLInputElement).value)}
					/>
				{/if}
			</div>
		{/each}
	</div>

	{#if children}
		{@render children()}
	{/if}

	<div class="form-actions">
		<Button type="submit" variant="primary" loading={submitting} disabled={submitting}>
			{mode === 'create' ? 'Guardar' : 'Actualizar'}
		</Button>
	</div>
</form>

<style>
	.crud-form {
		max-width: 640px;
	}

	.form-fields {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
		margin-bottom: var(--spacing-lg);
	}

	.form-field {
		width: 100%;
	}

	.input-group {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-xs);
	}

	.input-label {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		color: var(--color-text);
	}

	.required {
		color: var(--color-error);
		margin-left: 2px;
	}

	.input-field {
		width: 100%;
		padding: 10px 14px;
		font-size: var(--font-size-base);
		font-family: var(--font-sans);
		color: var(--color-text);
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		transition: border-color var(--transition-fast), box-shadow var(--transition-fast);
		outline: none;
	}

	.input-field:focus {
		border-color: var(--color-accent);
		box-shadow: 0 0 0 3px var(--color-accent-light);
	}

	.input-field.input-error {
		border-color: var(--color-error);
	}

	.input-field.input-error:focus {
		box-shadow: 0 0 0 3px var(--color-error-bg);
	}

	.textarea-field {
		resize: vertical;
		min-height: 80px;
	}

	.field-error {
		font-size: var(--font-size-sm);
		color: var(--color-error);
		margin-top: 2px;
	}

	.form-actions {
		display: flex;
		justify-content: flex-end;
		gap: var(--spacing-sm);
		padding-top: var(--spacing-md);
		border-top: 1px solid var(--color-border-light);
	}
</style>
