<script lang="ts">
	let {
		type = 'text',
		label,
		value = $bindable(''),
		error,
		placeholder = '',
		name,
		required = false,
		disabled = false,
		autocomplete,
		...rest
	}: {
		type?: string;
		label?: string;
		value?: string;
		error?: string;
		placeholder?: string;
		name?: string;
		required?: boolean;
		disabled?: boolean;
		autocomplete?: any;
	} & Record<string, unknown> = $props();

	const inputId = `input-${Math.random().toString(36).slice(2, 9)}`;
	const errorId = `${inputId}-error`;

	function handleInput(e: Event) {
		const target = e.currentTarget as HTMLInputElement;
		value = target.value;
	}
</script>

<div class="input-group">
	{#if label}
		<label for={inputId} class="input-label">
			{label}
			{#if required}
				<span class="required" aria-hidden="true">*</span>
			{/if}
		</label>
	{/if}
	<input
		{type}
		{name}
		id={inputId}
		value={value}
		{placeholder}
		{required}
		{disabled}
		{autocomplete}
		class="input-field"
		class:input-error={!!error}
		aria-invalid={!!error}
		aria-describedby={error ? errorId : undefined}
		oninput={handleInput}
		{...rest}
	/>
	{#if error}
		<p id={errorId} class="input-error-msg" role="alert">
			{error}
		</p>
	{/if}
</div>

<style>
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

	.input-field::placeholder {
		color: var(--color-text-muted);
	}

	.input-field:focus {
		border-color: var(--color-accent);
		box-shadow: 0 0 0 3px var(--color-accent-light);
	}

	.input-field:disabled {
		opacity: 0.5;
		cursor: not-allowed;
		background: var(--color-bg-alt);
	}

	.input-field.input-error {
		border-color: var(--color-error);
	}

	.input-field.input-error:focus {
		box-shadow: 0 0 0 3px var(--color-error-bg);
	}

	.input-error-msg {
		font-size: var(--font-size-sm);
		color: var(--color-error);
		margin-top: 2px;
	}
</style>
