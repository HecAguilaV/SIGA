<script lang="ts">
	let {
		data = [],
		color = 'var(--color-accent)',
		width = 80,
		height = 30
	}: {
		data?: number[];
		color?: string;
		width?: number;
		height?: number;
	} = $props();

	// Generar el path curvo suave
	const pathString = $derived.by(() => {
		if (data.length < 2) return '';
		
		const minVal = Math.min(...data);
		const maxVal = Math.max(...data);
		const rangeVal = maxVal - minVal || 1;

		const minY = 3;
		const maxY = height - 3;
		const maxX = 100;

		const coords = data.map((val, index) => {
			const x = (index / (data.length - 1)) * maxX;
			const y = maxY - ((val - minVal) / rangeVal) * (maxY - minY);
			return { x, y };
		});

		let d = `M ${coords[0].x} ${coords[0].y}`;
		for (let i = 0; i < coords.length - 1; i++) {
			const p0 = coords[i];
			const p1 = coords[i + 1];
			// Puntos de control para la curva Bezier
			const cpX1 = p0.x + (p1.x - p0.x) / 2;
			const cpY1 = p0.y;
			const cpX2 = p0.x + (p1.x - p0.x) / 2;
			const cpY2 = p1.y;
			d += ` C ${cpX1} ${cpY1}, ${cpX2} ${cpY2}, ${p1.x} ${p1.y}`;
		}
		return d;
	});

	// Rellenar el área debajo de la curva para el gradiente
	const areaPathString = $derived(
		pathString ? `${pathString} L 100 ${height} L 0 ${height} Z` : ''
	);

	const uniqueId = $derived(Math.random().toString(36).substring(2, 9));
</script>

<div class="micro-trend" style="width: {width}px; height: {height}px;">
	{#if data.length >= 2}
		<svg viewBox="0 0 100 {height}" width="100%" height="100%" preserveAspectRatio="none">
			<defs>
				<linearGradient id="gradient-{uniqueId}" x1="0" y1="0" x2="0" y2="1">
					<stop offset="0%" stop-color={color} stop-opacity="0.25" />
					<stop offset="100%" stop-color={color} stop-opacity="0.0" />
				</linearGradient>
			</defs>
			
			<!-- Relleno del área -->
			{#if areaPathString}
				<path d={areaPathString} fill="url(#gradient-{uniqueId})" />
			{/if}

			<!-- Línea de tendencia -->
			{#if pathString}
				<path
					d={pathString}
					fill="none"
					stroke={color}
					stroke-width="1.8"
					stroke-linecap="round"
					stroke-linejoin="round"
				/>
			{/if}
		</svg>
	{/if}
</div>

<style>
	.micro-trend {
		display: inline-block;
		overflow: hidden;
		flex-shrink: 0;
	}

	svg {
		display: block;
	}
</style>
