<script lang="ts">
	type Size = 'sm' | 'md' | 'lg';
	type Variant = 'default' | 'light';

	let {
		size = 'md',
		variant = 'default'
	}: {
		size?: Size;
		variant?: Variant;
	} = $props();
</script>

<div
	class="spinner spinner-{size} spinner-{variant}"
	role="status"
	aria-label="Cargando"
>
	<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
		<circle
			cx="12"
			cy="12"
			r="10"
			stroke="currentColor"
			stroke-width="3"
			stroke-linecap="round"
			stroke-dasharray="31.4 31.4"
			opacity="0.25"
		/>
		<circle
			cx="12"
			cy="12"
			r="10"
			stroke="currentColor"
			stroke-width="3"
			stroke-linecap="round"
			stroke-dasharray="31.4 31.4"
			stroke-dashoffset="-15.7"
			class="spinner-arc"
		/>
	</svg>
</div>

<style>
	.spinner {
		display: inline-flex;
		animation: spin 1s linear infinite;
	}

	.spinner-sm {
		width: 16px;
		height: 16px;
	}

	.spinner-md {
		width: 24px;
		height: 24px;
	}

	.spinner-lg {
		width: 36px;
		height: 36px;
	}

	.spinner-default {
		color: var(--color-accent);
	}

	.spinner-light {
		color: rgba(255, 255, 255, 0.8);
	}

	.spinner-arc {
		animation: spin-arc 1.5s ease-in-out infinite;
	}

	@keyframes spin {
		to {
			transform: rotate(360deg);
		}
	}

	@keyframes spin-arc {
		0% {
			stroke-dashoffset: -15.7;
		}
		50% {
			stroke-dashoffset: 0;
		}
		100% {
			stroke-dashoffset: 15.7;
		}
	}
</style>
