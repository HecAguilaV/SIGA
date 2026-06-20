<script lang="ts">
	/**
	 * LandingPage.svelte — Public presentation page for the SIGA Pro ecosystem.
	 *
	 * Built on the M3 design system (see app.css). Uses M3 color tokens directly,
	 * Phosphor Svelte icons, and SIGA branding (SIGA Pro, SIGA A2UI, SIGA Core).
	 */
	import ArrowRight from 'phosphor-svelte/lib/ArrowRight';
	import Check from 'phosphor-svelte/lib/Check';
	import CheckCircle from 'phosphor-svelte/lib/CheckCircle';
	import Lightning from 'phosphor-svelte/lib/Lightning';
	import Brain from 'phosphor-svelte/lib/Brain';
	import GearFine from 'phosphor-svelte/lib/GearFine';
	import Package from 'phosphor-svelte/lib/Package';
	import ChatCircle from 'phosphor-svelte/lib/ChatCircle';
	import ChartBar from 'phosphor-svelte/lib/ChartBar';
	import Robot from 'phosphor-svelte/lib/Robot';
	import List from 'phosphor-svelte/lib/List';
	import A11yToolbar from '$lib/components/layout/A11yToolbar.svelte';

	let mobileMenuOpen = $state(false);

	function toggleMobileMenu() {
		mobileMenuOpen = !mobileMenuOpen;
	}

	function closeMobileMenu() {
		mobileMenuOpen = false;
	}
</script>

<svelte:head>
	<title>SIGA Pro — Sistema Inteligente de Gestión de Activos</title>
</svelte:head>

<div class="landing">
	<!-- TOP NAV BAR -->
	<header class="topbar">
		<div class="topbar-inner">
			<a href="/" class="brand" onclick={closeMobileMenu}>
				<img src="/Logo_SIGA.png" alt="SIGA Pro" class="brand-logo" />
				<span class="brand-name">SIGA</span>
			</a>

			<nav class="topnav-links" class:open={mobileMenuOpen}>
				<a href="#pilares" class="topnav-link" onclick={closeMobileMenu}>Características</a>
				<a href="#historia" class="topnav-link" onclick={closeMobileMenu}>Historia</a>
				<a href="#planes" class="topnav-link" onclick={closeMobileMenu}>Planes</a>
				<a href="/login" class="topnav-link topnav-login-mobile" onclick={closeMobileMenu}>Iniciar Sesión</a>
				<a href="/login" class="btn bg-gradient-siga topnav-cta-mobile" onclick={closeMobileMenu}>
					Probar Gratis
				</a>
			</nav>

			<div class="nav-actions">
				<A11yToolbar />
				<a href="/login" class="btn btn-outline">Iniciar Sesión</a>
				<a href="/login" class="btn bg-gradient-siga">
					Probar Gratis <ArrowRight size={16} weight="bold" />
				</a>
				<button
					class="menu-btn"
					onclick={toggleMobileMenu}
					aria-label="Abrir menú"
					aria-expanded={mobileMenuOpen}
					type="button"
				>
					<List size={24} />
				</button>
			</div>
		</div>
	</header>

	<!-- HERO -->
	<section class="hero">
		<div class="hero-copy">
			<span class="hero-eyebrow">
				<Robot size={16} weight="fill" /> Nueva versión SIGA Pro activa
			</span>
			<h1 class="hero-title">
				Gestiona tu negocio con la
				<span class="text-gradient">velocidad de la IA</span>
			</h1>
			<p class="hero-subtitle">
				SIGA A2UI combina el control de inventario multi-sucursal con un agente de IA
				operativo que entiende tu negocio y ejecuta acciones por ti. Menos fricción,
				más decisiones acertadas en tiempo real.
			</p>
			<div class="hero-cta">
				<a href="/login" class="btn btn-primary">
					Comenzar Ahora <ArrowRight size={16} weight="bold" />
				</a>
				<a href="#pilares" class="btn btn-glass">
					Ver Características
				</a>
			</div>
		</div>

		<div class="hero-visual">
			<div class="glass-card hero-card">
				<img src="/S.png" alt="SIGA" class="hero-isotipo" />
				<div class="hero-card-label">SIGA Core</div>
			</div>
			<div class="a2ui-widget glass-card">
				<div class="a2ui-widget-head">
					<Robot size={18} weight="fill" />
					<span>SIGA A2UI</span>
					<span class="a2ui-dot" aria-hidden="true"></span>
				</div>
				<p class="a2ui-widget-text">"Transfiere 20 unidades al local Norte"</p>
				<span class="a2ui-widget-status">Ejecutando acción…</span>
			</div>
		</div>
	</section>

	<!-- PILARES -->
	<section id="pilares" class="pilares">
		<div class="section-head">
			<span class="section-eyebrow">Por qué SIGA</span>
			<h2 class="section-title">Tres pilares que devuelven tiempo a tu equipo</h2>
			<p class="section-subtitle">
				Diseñado para Pymes de retail multi-sucursal que necesitan operar sin fricción
				y decidir con información clara.
			</p>
		</div>

		<div class="pilares-grid">
			<article class="pillar-card">
				<div class="pillar-icon pillar-icon--secondary">
					<Lightning size={28} weight="fill" />
				</div>
				<h3 class="pillar-title">Menos Fricción</h3>
				<p class="pillar-desc">
					Flujos de inventario, ventas y conciliación en una sola pantalla.
					Reduce los pasos repetitivos que roban horas cada día.
				</p>
			</article>

			<article class="pillar-card">
				<div class="pillar-icon pillar-icon--tertiary">
					<Brain size={28} weight="fill" />
				</div>
				<h3 class="pillar-title">Más Intención</h3>
				<p class="pillar-desc">
					El agente IA entiende el contexto de cada local y SKU para sugerir
					acciones útiles, no solo respuestas genéricas.
				</p>
			</article>

			<article class="pillar-card">
				<div class="pillar-icon pillar-icon--primary">
					<GearFine size={28} weight="fill" />
				</div>
				<h3 class="pillar-title">Automatización</h3>
				<p class="pillar-desc">
					Tareas operativas configurables que se ejecutan solas: pedidos de stock,
					reportes diarios y alertas proactivas.
				</p>
			</article>
		</div>
	</section>

	<!-- A2UI CO-PILOTO -->
	<section class="copilot">
		<div class="copilot-grid">
			<div class="copilot-cards">
				<article class="mini-card glass-card">
					<div class="mini-icon"><Package size={22} weight="duotone" /></div>
					<div>
						<h4 class="mini-title">Stock Inteligente</h4>
						<p class="mini-desc">Consolidado multi-sucursal en tiempo real.</p>
					</div>
				</article>

				<article class="mini-card glass-card">
					<div class="mini-icon"><ChatCircle size={22} weight="duotone" /></div>
					<div>
						<h4 class="mini-title">Agente Chat</h4>
						<p class="mini-desc">Conversa con tu negocio en lenguaje natural.</p>
					</div>
				</article>

				<article class="mini-card glass-card">
					<div class="mini-icon"><ChartBar size={22} weight="duotone" /></div>
					<div>
						<h4 class="mini-title">Reporte Diario</h4>
						<p class="mini-desc">Resumen automático de ventas y movimientos.</p>
					</div>
				</article>

				<article class="mini-card glass-card">
					<div class="mini-icon"><Robot size={22} weight="duotone" /></div>
					<div>
						<h4 class="mini-title">Tareas Automáticas</h4>
						<p class="mini-desc">Pedidos y alertas que se ejecutan solos.</p>
					</div>
				</article>
			</div>

			<div class="copilot-copy">
				<span class="section-eyebrow">SIGA A2UI</span>
				<h2 class="copilot-title">Tu Co-piloto Inteligente para Operaciones</h2>
				<p class="copilot-desc">
					Nuestro agente IA no solo responde preguntas: ejecuta acciones operativas
					en tu inventario a través del protocolo seguro A2UI (Agent-to-UI),
					contextualizado por local y SKU.
				</p>
				<ul class="copilot-list">
					<li>
						<CheckCircle size={20} weight="fill" class="check-ok" />
						Ajuste de stock mediante comandos de voz o texto.
					</li>
					<li>
						<CheckCircle size={20} weight="fill" class="check-ok" />
						Detección predictiva de anomalías en precios y movimientos.
					</li>
					<li>
						<CheckCircle size={20} weight="fill" class="check-ok" />
						Respuestas inteligentes contextualizadas por local y SKU.
					</li>
				</ul>
			</div>
		</div>
	</section>

	<!-- NUESTRA HISTORIA -->
	<section id="historia" class="historia">
		<div class="historia-inner">
			<span class="historia-eyebrow">Nuestra Historia</span>
			<h2 class="historia-title">Nacidos para recuperar tu tiempo</h2>
			<p class="historia-text">
				SIGA nació observando a pequeños comerciantes perder horas valiosas en planillas,
				conciliaciones manuales y decisiones a ciegas. Construímos un sistema que pone la
				inteligencia operativa al alcance de cualquier Pyme, sin curvas de aprendizaje
				abruptas ni costos empresariales.
			</p>
			<p class="historia-text">
				Hoy SIGA Pro combina gestión tradicional de inventario con agentes de IA
				operativos (A2UI) para que tu equipo dedique su tiempo a crecer, no a teclear.
				Cumplimos la Ley Chilena 21.719 con seudonimización obligatoria por diseño.
			</p>

			<div class="historia-stats">
				<div class="stat">
					<span class="stat-number">500+</span>
					<span class="stat-label">Negocios</span>
				</div>
				<div class="stat">
					<span class="stat-number">15k</span>
					<span class="stat-label">Horas Ahorradas</span>
				</div>
				<div class="stat">
					<span class="stat-number">24/7</span>
					<span class="stat-label">IA Activa</span>
				</div>
			</div>
		</div>
	</section>

	<!-- PLANES -->
	<section id="planes" class="planes">
		<div class="section-head">
			<span class="section-eyebrow">Planes</span>
			<h2 class="section-title">Planes que escalan contigo</h2>
			<p class="section-subtitle">Facturación mensual, sin contratos forzosos. Pruébalo gratis 14 días.</p>
		</div>

		<div class="planes-grid">
			<article class="plan-card glass-card">
				<span class="plan-name">Emprendedor Pro</span>
				<span class="plan-price">$49<span class="plan-period">/mes</span></span>
				<p class="plan-desc">Ideal para negocios individuales y almacenes de barrio.</p>
				<ul class="plan-features">
					<li><Check size={18} weight="bold" /> Hasta 2 sucursales</li>
					<li><Check size={18} weight="bold" /> 500 productos únicos</li>
					<li><Check size={18} weight="bold" /> POS integrado</li>
					<li><Check size={18} weight="bold" /> Soporte vía email</li>
				</ul>
				<a href="/login" class="btn btn-outline plan-btn">Elegir Plan</a>
			</article>

			<article class="plan-card plan-card--featured">
				<span class="plan-badge">Recomendado</span>
				<span class="plan-name">Crecimiento</span>
				<span class="plan-price">$99<span class="plan-period">/mes</span></span>
				<p class="plan-desc">Perfecto para empresas en expansión con múltiples sucursales.</p>
				<ul class="plan-features">
					<li><Check size={18} weight="bold" /> Sucursales ilimitadas</li>
					<li><Check size={18} weight="bold" /> Productos ilimitados</li>
					<li><Check size={18} weight="bold" /> Copiloto IA (A2UI) incluido</li>
					<li><Check size={18} weight="bold" /> Auditorías de Ley 21.719</li>
					<li><Check size={18} weight="bold" /> Soporte 24/7 prioritario</li>
				</ul>
				<a href="/login" class="btn btn-primary plan-btn">Elegir Plan</a>
			</article>
		</div>
	</section>

	<!-- CTA FINAL -->
	<section class="cta-final">
		<div class="cta-card">
			<h2 class="cta-title">¿Listo para transformar tu negocio?</h2>
			<p class="cta-subtitle">Únete a los comercios que ya recuperaron su tiempo con SIGA Pro.</p>
			<a href="/login" class="btn btn-primary btn-lg">
				Empezar ahora <ArrowRight size={18} weight="bold" />
			</a>
		</div>
	</section>

	<!-- FOOTER -->
	<footer class="footer">
		<div class="footer-grid">
			<div class="footer-col footer-brand">
				<img src="/Logo_SIGA.png" alt="SIGA Pro" class="footer-logo" />
				<p class="footer-desc">
					Sistema Inteligente de Gestión de Activos para Pymes de retail multi-tenant.
				</p>
			</div>

			<div class="footer-col">
				<h4 class="footer-heading">Producto</h4>
				<a href="#pilares" class="footer-link">Características</a>
				<a href="#planes" class="footer-link">Planes</a>
				<a href="/login" class="footer-link">Iniciar Sesión</a>
			</div>

			<div class="footer-col">
				<h4 class="footer-heading">Compañía</h4>
				<a href="#historia" class="footer-link">Historia</a>
				<a href="/" class="footer-link">Inicio</a>
				<a href="/login" class="footer-link">Contacto</a>
			</div>

			<div class="footer-col">
				<h4 class="footer-heading">Legal</h4>
				<a href="/" class="footer-link">Privacidad</a>
				<a href="/" class="footer-link">Términos</a>
				<a href="/" class="footer-link">Ley 21.719</a>
			</div>
		</div>

		<div class="footer-bottom">
			<span>© 2026 SIGA Pro. Todos los derechos reservados.</span>
			<div class="footer-social">
				<a href="/" class="footer-link" aria-label="X">X</a>
				<a href="/" class="footer-link" aria-label="GitHub">GitHub</a>
				<a href="/" class="footer-link" aria-label="LinkedIn">LinkedIn</a>
			</div>
		</div>

		<!-- > Un Soñador con Poca RAM <Laptop ... /> -->
	</footer>
</div>

<style>
	:global(html) {
		scroll-behavior: smooth;
	}

	.landing {
		font-family: var(--font-sans);
		color: var(--color-on-surface);
		background-color: var(--color-background);
		min-height: 100vh;
		overflow-x: hidden;
	}

	/* ── Buttons ── */
	.btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		gap: 8px;
		padding: 10px 20px;
		font-family: var(--font-sans);
		font-weight: var(--font-weight-semibold);
		font-size: var(--font-size-body-md);
		border-radius: var(--radius-md);
		text-decoration: none;
		cursor: pointer;
		border: 1px solid transparent;
		transition: all var(--transition-fast);
	}

	.btn-primary {
		background: var(--color-secondary);
		color: var(--color-on-secondary);
	}

	.btn-primary:hover {
		box-shadow: var(--shadow-md);
		transform: translateY(-1px);
	}

	.btn-outline {
		background: transparent;
		color: var(--color-secondary);
		border-color: var(--color-secondary);
	}

	.btn-outline:hover {
		background: var(--color-secondary-container);
		color: var(--color-on-secondary-container);
	}

	/* "Probar Gratis" CTA uses the .bg-gradient-siga utility (defined in app.css).
	   Only M3 tokens are referenced here for text and hover. */
	.btn.bg-gradient-siga {
		color: var(--color-on-secondary);
		font-weight: var(--font-weight-bold);
	}

	.btn.bg-gradient-siga:hover {
		box-shadow: var(--shadow-md);
		transform: translateY(-1px);
	}

	.btn-glass {
		background: var(--color-surface-container-lowest);
		color: var(--color-on-surface);
		border-color: var(--color-outline-variant);
	}

	.btn-glass:hover {
		background: var(--color-surface-container);
	}

	.btn-lg {
		padding: 14px 32px;
		font-size: var(--font-size-body-lg);
	}

	/* ── Top Nav Bar ── */
	.topbar {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		z-index: 100;
		background: var(--color-surface-container-lowest);
		border-bottom: 1px solid var(--color-outline-variant);
	}

	.topbar-inner {
		max-width: 1200px;
		margin: 0 auto;
		padding: 12px 24px;
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 16px;
	}

	.brand {
		display: flex;
		align-items: center;
		gap: 10px;
	}

	.brand-logo {
		height: 40px;
		width: auto;
		object-fit: contain;
	}

	.brand-name {
		font-size: 1.25rem;
		font-weight: var(--font-weight-bold);
		color: var(--color-on-surface);
		letter-spacing: -0.01em;
	}

	.topnav-links {
		display: flex;
		align-items: center;
		gap: 28px;
	}

	.topnav-link {
		color: var(--color-on-surface-variant);
		font-weight: var(--font-weight-medium);
		font-size: var(--font-size-body-md);
		transition: color var(--transition-fast);
	}

	.topnav-link:hover {
		color: var(--color-on-surface);
	}

	/* Mobile-only entries inside the nav menu (hidden on desktop) */
	.topnav-login-mobile,
	.topnav-cta-mobile {
		display: none;
	}

	.nav-actions {
		display: flex;
		align-items: center;
		gap: 12px;
	}

	.menu-btn {
		display: none;
		align-items: center;
		justify-content: center;
		width: 40px;
		height: 40px;
		border: 1px solid var(--color-outline-variant);
		background: var(--color-surface-container-lowest);
		color: var(--color-on-surface);
		border-radius: var(--radius-md);
		cursor: pointer;
	}

	/* ── Hero ── */
	.hero {
		max-width: 1200px;
		margin: 0 auto;
		padding: 120px 24px 80px;
		display: grid;
		grid-template-columns: 1.15fr 1fr;
		gap: 48px;
		align-items: center;
	}

	.hero-copy {
		display: flex;
		flex-direction: column;
		gap: 24px;
		align-items: flex-start;
	}

	.hero-eyebrow {
		display: inline-flex;
		align-items: center;
		gap: 8px;
		padding: 6px 14px;
		border-radius: var(--radius-full);
		background: var(--color-secondary-container);
		color: var(--color-on-secondary-container);
		font-size: var(--font-size-label-caps);
		font-weight: var(--font-weight-semibold);
		font-family: var(--font-mono);
		letter-spacing: 0.05em;
		text-transform: uppercase;
	}

	.hero-title {
		font-size: 3rem;
		font-weight: var(--font-weight-bold);
		line-height: 1.1;
		letter-spacing: -0.02em;
		color: var(--color-on-surface);
	}

	.hero-subtitle {
		font-size: var(--font-size-body-lg);
		line-height: 1.6;
		color: var(--color-on-surface-variant);
		max-width: 540px;
	}

	.hero-cta {
		display: flex;
		gap: 12px;
		flex-wrap: wrap;
	}

	/* Hero visual */
	.hero-visual {
		position: relative;
		display: flex;
		justify-content: center;
		align-items: center;
		min-height: 360px;
	}

	.hero-card {
		position: relative;
		z-index: 2;
		width: 260px;
		padding: 32px;
		border-radius: var(--radius-xl);
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 16px;
		box-shadow: var(--shadow-lg);
	}

	.hero-isotipo {
		width: 140px;
		height: 140px;
		object-fit: contain;
	}

	.hero-card-label {
		font-family: var(--font-mono);
		font-size: var(--font-size-label-caps);
		font-weight: var(--font-weight-semibold);
		letter-spacing: 0.05em;
		text-transform: uppercase;
		color: var(--color-secondary);
	}

	.a2ui-widget {
		position: absolute;
		z-index: 3;
		right: 0;
		bottom: 12px;
		width: 220px;
		padding: 16px;
		border-radius: var(--radius-lg);
		display: flex;
		flex-direction: column;
		gap: 8px;
		box-shadow: var(--shadow-lg);
	}

	.a2ui-widget-head {
		display: flex;
		align-items: center;
		gap: 8px;
		color: var(--color-secondary);
		font-weight: var(--font-weight-semibold);
		font-size: var(--font-size-body-md);
	}

	.a2ui-dot {
		margin-left: auto;
		width: 8px;
		height: 8px;
		border-radius: 50%;
		background: var(--color-success);
	}

	.a2ui-widget-text {
		font-size: var(--font-size-body-md);
		color: var(--color-on-surface);
		line-height: 1.4;
	}

	.a2ui-widget-status {
		font-family: var(--font-mono);
		font-size: var(--font-size-label-caps);
		color: var(--color-on-surface-variant);
	}

	/* ── Section heads ── */
	.section-head {
		max-width: 720px;
		margin: 0 auto 56px;
		text-align: center;
		display: flex;
		flex-direction: column;
		gap: 12px;
		align-items: center;
	}

	.section-eyebrow {
		font-family: var(--font-mono);
		font-size: var(--font-size-label-caps);
		font-weight: var(--font-weight-semibold);
		letter-spacing: 0.08em;
		text-transform: uppercase;
		color: var(--color-secondary);
	}

	.section-title {
		font-size: 2.25rem;
		font-weight: var(--font-weight-bold);
		letter-spacing: -0.02em;
		color: var(--color-on-surface);
		line-height: 1.2;
	}

	.section-subtitle {
		font-size: var(--font-size-body-lg);
		color: var(--color-on-surface-variant);
		line-height: 1.6;
	}

	/* ── Pilares ── */
	.pilares {
		background: var(--color-surface-container-lowest);
		padding: 96px 24px;
	}

	.pilares-grid {
		max-width: 1200px;
		margin: 0 auto;
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 24px;
	}

	.pillar-card {
		background: var(--color-surface);
		border: 1px solid var(--color-outline-variant);
		border-radius: var(--radius-lg);
		padding: 32px;
		display: flex;
		flex-direction: column;
		gap: 16px;
		transition: transform var(--transition-base), box-shadow var(--transition-base);
	}

	.pillar-card:hover {
		transform: translateY(-4px);
		box-shadow: var(--shadow-md);
	}

	.pillar-icon {
		width: 56px;
		height: 56px;
		border-radius: var(--radius-md);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.pillar-icon--secondary {
		background: var(--color-secondary-container);
		color: var(--color-on-secondary-container);
	}

	.pillar-icon--tertiary {
		background: var(--color-tertiary-container);
		color: var(--color-on-tertiary-container);
	}

	.pillar-icon--primary {
		background: var(--color-primary-container);
		color: var(--color-on-primary-container);
	}

	.pillar-title {
		font-size: 1.25rem;
		font-weight: var(--font-weight-bold);
		color: var(--color-on-surface);
	}

	.pillar-desc {
		font-size: var(--font-size-body-md);
		color: var(--color-on-surface-variant);
		line-height: 1.6;
	}

	/* ── Co-piloto ── */
	.copilot {
		background: var(--color-surface);
		padding: 96px 24px;
	}

	.copilot-grid {
		max-width: 1200px;
		margin: 0 auto;
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 56px;
		align-items: center;
	}

	.copilot-cards {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 16px;
	}

	.mini-card {
		display: flex;
		gap: 12px;
		padding: 20px;
		border-radius: var(--radius-md);
	}

	.mini-icon {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 40px;
		height: 40px;
		flex-shrink: 0;
		border-radius: var(--radius-sm);
		background: var(--color-secondary-container);
		color: var(--color-on-secondary-container);
	}

	.mini-title {
		font-size: var(--font-size-body-md);
		font-weight: var(--font-weight-semibold);
		color: var(--color-on-surface);
	}

	.mini-desc {
		font-size: var(--font-size-body-md);
		color: var(--color-on-surface-variant);
		line-height: 1.5;
	}

	.copilot-copy {
		display: flex;
		flex-direction: column;
		gap: 20px;
		align-items: flex-start;
	}

	.copilot-title {
		font-size: 2rem;
		font-weight: var(--font-weight-bold);
		letter-spacing: -0.02em;
		color: var(--color-on-surface);
		line-height: 1.2;
	}

	.copilot-desc {
		font-size: var(--font-size-body-lg);
		color: var(--color-on-surface-variant);
		line-height: 1.6;
	}

	.copilot-list {
		display: flex;
		flex-direction: column;
		gap: 12px;
		margin-top: 8px;
	}

	.copilot-list li {
		display: flex;
		align-items: center;
		gap: 12px;
		font-size: var(--font-size-body-md);
		color: var(--color-on-surface);
	}

	.copilot-list :global(.check-ok) {
		color: var(--color-success);
		flex-shrink: 0;
	}

	/* ── Historia ── */
	.historia {
		background: var(--color-primary-container);
		padding: 96px 24px;
	}

	.historia-inner {
		max-width: 880px;
		margin: 0 auto;
		text-align: center;
		display: flex;
		flex-direction: column;
		gap: 20px;
		align-items: center;
	}

	.historia-eyebrow {
		font-family: var(--font-mono);
		font-size: var(--font-size-label-caps);
		font-weight: var(--font-weight-semibold);
		letter-spacing: 0.08em;
		text-transform: uppercase;
		color: var(--color-secondary-fixed);
	}

	.historia-title {
		font-size: 2.25rem;
		font-weight: var(--font-weight-bold);
		letter-spacing: -0.02em;
		color: var(--color-on-primary);
		line-height: 1.2;
	}

	.historia-text {
		font-size: var(--font-size-body-lg);
		line-height: 1.7;
		color: var(--color-on-primary-container);
	}

	.historia-stats {
		display: flex;
		justify-content: center;
		gap: 56px;
		margin-top: 24px;
		flex-wrap: wrap;
	}

	.stat {
		display: flex;
		flex-direction: column;
		gap: 4px;
	}

	.stat-number {
		font-size: 2.5rem;
		font-weight: var(--font-weight-bold);
		color: var(--color-secondary-fixed);
		line-height: 1;
	}

	.stat-label {
		font-size: var(--font-size-body-md);
		color: var(--color-on-primary-container);
		font-weight: var(--font-weight-medium);
	}

	/* ── Planes ── */
	.planes {
		background: var(--color-surface-container-low);
		padding: 96px 24px;
	}

	.planes-grid {
		max-width: 880px;
		margin: 0 auto;
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 24px;
		align-items: stretch;
	}

	.plan-card {
		display: flex;
		flex-direction: column;
		gap: 16px;
		padding: 32px;
		border-radius: var(--radius-xl);
		position: relative;
	}

	.plan-card--featured {
		background: var(--color-primary-container);
		border: 1px solid var(--color-primary-container);
		color: var(--color-on-primary);
	}

	.plan-badge {
		position: absolute;
		top: 16px;
		right: 16px;
		padding: 4px 12px;
		border-radius: var(--radius-full);
		background: var(--color-secondary);
		color: var(--color-on-secondary);
		font-size: var(--font-size-label-caps);
		font-weight: var(--font-weight-bold);
		font-family: var(--font-mono);
		text-transform: uppercase;
		letter-spacing: 0.05em;
	}

	.plan-name {
		font-size: 1.25rem;
		font-weight: var(--font-weight-bold);
		color: var(--color-on-surface);
	}

	.plan-card--featured .plan-name {
		color: var(--color-on-primary);
	}

	.plan-price {
		font-size: 2.5rem;
		font-weight: var(--font-weight-bold);
		color: var(--color-on-surface);
		line-height: 1;
	}

	.plan-card--featured .plan-price {
		color: var(--color-on-primary);
	}

	.plan-period {
		font-size: 1rem;
		font-weight: var(--font-weight-normal);
		color: var(--color-on-surface-variant);
	}

	.plan-card--featured .plan-period {
		color: var(--color-on-primary-container);
	}

	.plan-desc {
		font-size: var(--font-size-body-md);
		color: var(--color-on-surface-variant);
		line-height: 1.5;
	}

	.plan-card--featured .plan-desc {
		color: var(--color-on-primary-container);
	}

	.plan-features {
		display: flex;
		flex-direction: column;
		gap: 10px;
		margin: 8px 0 16px;
	}

	.plan-features li {
		display: flex;
		align-items: center;
		gap: 10px;
		font-size: var(--font-size-body-md);
		color: var(--color-on-surface);
	}

	.plan-card--featured .plan-features li {
		color: var(--color-on-primary-container);
	}

	.plan-features :global(svg) {
		color: var(--color-success);
		flex-shrink: 0;
	}

	.plan-card--featured .plan-features :global(svg) {
		color: var(--color-secondary-fixed);
	}

	.plan-btn {
		margin-top: auto;
		width: 100%;
	}

	/* ── CTA Final ── */
	.cta-final {
		background: var(--color-surface);
		padding: 96px 24px;
	}

	.cta-card {
		max-width: 760px;
		margin: 0 auto;
		text-align: center;
		background: var(--color-surface-tint);
		border-radius: var(--radius-xl);
		padding: 56px 32px;
		display: flex;
		flex-direction: column;
		gap: 16px;
		align-items: center;
	}

	.cta-title {
		font-size: 2rem;
		font-weight: var(--font-weight-bold);
		letter-spacing: -0.02em;
		color: var(--color-on-surface);
	}

	.cta-subtitle {
		font-size: var(--font-size-body-lg);
		color: var(--color-on-surface-variant);
	}

	/* ── Footer ── */
	.footer {
		background: var(--color-surface-container-highest);
		padding: 64px 24px 32px;
	}

	.footer-grid {
		max-width: 1200px;
		margin: 0 auto;
		display: grid;
		grid-template-columns: 1.6fr 1fr 1fr 1fr;
		gap: 40px;
	}

	.footer-brand {
		display: flex;
		flex-direction: column;
		gap: 16px;
		max-width: 320px;
	}

	.footer-logo {
		height: 36px;
		width: auto;
		object-fit: contain;
	}

	.footer-desc {
		font-size: var(--font-size-body-md);
		color: var(--color-on-surface-variant);
		line-height: 1.6;
	}

	.footer-col {
		display: flex;
		flex-direction: column;
		gap: 12px;
	}

	.footer-heading {
		font-size: var(--font-size-body-md);
		font-weight: var(--font-weight-bold);
		color: var(--color-on-surface);
	}

	.footer-link {
		font-size: var(--font-size-body-md);
		color: var(--color-on-surface-variant);
		transition: color var(--transition-fast);
	}

	.footer-link:hover {
		color: var(--color-secondary);
	}

	.footer-bottom {
		max-width: 1200px;
		margin: 40px auto 0;
		padding-top: 24px;
		border-top: 1px solid var(--color-outline-variant);
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-wrap: wrap;
		gap: 12px;
		font-size: var(--font-size-body-md);
		color: var(--color-on-surface-variant);
	}

	.footer-social {
		display: flex;
		gap: 16px;
	}

	/* ── Responsive: tablet (≤1024px) ── */
	@media (max-width: 1024px) {
		.hero {
			grid-template-columns: 1fr;
			padding-top: 104px;
			gap: 40px;
		}

		.hero-copy {
			align-items: center;
			text-align: center;
		}

		.hero-subtitle {
			margin: 0 auto;
		}

		.pilares-grid {
			grid-template-columns: 1fr;
			max-width: 560px;
		}

		.copilot-grid {
			grid-template-columns: 1fr;
			gap: 40px;
		}

		.copilot-copy {
			text-align: center;
			align-items: center;
		}

		.copilot-list {
			text-align: left;
		}

		.planes-grid {
			grid-template-columns: 1fr;
			max-width: 480px;
		}

		.footer-grid {
			grid-template-columns: 1fr 1fr;
			gap: 32px;
		}
	}

	/* ── Responsive: mobile (≤768px) ── */
	@media (max-width: 768px) {
		.topnav-links {
			display: none;
		}

		.topnav-links.open {
			display: flex;
			position: absolute;
			top: 100%;
			left: 0;
			right: 0;
			flex-direction: column;
			align-items: stretch;
			gap: 8px;
			padding: 16px 24px 24px;
			background: var(--color-surface-container-lowest);
			border-bottom: 1px solid var(--color-outline-variant);
			box-shadow: var(--shadow-md);
		}

		.topnav-link {
			padding: 10px 0;
		}

		.topnav-login-mobile,
		.topnav-cta-mobile {
			display: flex;
			justify-content: center;
			margin-top: 8px;
		}

		.menu-btn {
			display: inline-flex;
		}

		.nav-actions .btn-outline,
		.nav-actions .bg-gradient-siga {
			display: none;
		}

		.nav-actions {
			gap: 8px;
		}

		.hero-title {
			font-size: 2.25rem;
		}

		.hero-visual {
			min-height: 300px;
		}

		.a2ui-widget {
			width: 180px;
			right: 8px;
		}

		.hero-card {
			width: 220px;
		}

		.copilot-cards {
			grid-template-columns: 1fr;
		}

		.historia-stats {
			gap: 32px;
		}

		.stat-number {
			font-size: 2rem;
		}

		.section-title,
		.copilot-title,
		.cta-title,
		.historia-title {
			font-size: 1.75rem;
		}

		.footer-grid {
			grid-template-columns: 1fr;
			gap: 28px;
		}

		.footer-bottom {
			justify-content: center;
			text-align: center;
		}
	}
</style>
