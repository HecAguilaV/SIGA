<script lang="ts">
	/**
	 * LandingPage.svelte — Página de presentación pública del ecosistema SIGA Pro.
	 * 
	 * Diseño: Glassmorphism, Hanken Grotesk y paleta de colores oficial.
	 */
	import ArrowRight from 'phosphor-svelte/lib/ArrowRight';
	import Check from 'phosphor-svelte/lib/Check';
	import Sparkle from 'phosphor-svelte/lib/Sparkle';
	import Package from 'phosphor-svelte/lib/Package';
	import Storefront from 'phosphor-svelte/lib/Storefront';
	import ShieldCheck from 'phosphor-svelte/lib/ShieldCheck';
	import Laptop from 'phosphor-svelte/lib/Laptop';
	import A11yToolbar from '$lib/components/layout/A11yToolbar.svelte';
</script>

<svelte:head>
	<title>SIGA Pro — Sistema Inteligente de Gestión de Activos</title>
</svelte:head>

<div class="landing-container">
	<!-- NAVBAR -->
	<header class="navbar">
		<div class="navbar-content">
			<div class="logo-area">
				<img src="/Logo_SIGA.png" alt="SIGA Pro Logo" class="logo-img" />
			</div>
			<nav class="nav-links">
				<a href="#caracteristicas" class="nav-link">Características</a>
				<a href="#ia-copilot" class="nav-link">IA Copilot</a>
				<a href="#precios" class="nav-link">SaaS</a>
			</nav>
			<div class="nav-actions">
				<A11yToolbar />
				<a href="/login" class="btn btn-login">Iniciar Sesión</a>
			</div>
		</div>
	</header>

	<!-- HERO SECTION -->
	<section class="hero-section">
		<div class="hero-content">
			<div class="hero-badge">
				<span class="badge-sparkle">✨</span>
				<span>Nueva versión SIGA Pro activa</span>
			</div>
			<h1 class="hero-title">
				El Ecosistema Inteligente de <span class="gradient-text">Gestión de Activos</span>
			</h1>
			<p class="hero-subtitle">
				SIGA Pro combina el control de inventario multi-sucursal tradicional con Agentes de IA Operativos autónomos (A2UI) para llevar la administración de tu Pyme al siguiente nivel de eficiencia.
			</p>
			<div class="hero-cta-group">
				<a href="/login" class="btn btn-hero-primary">
					Comenzar Ahora <span class="arrow-icon"><ArrowRight size={16} weight="bold" /></span>
				</a>
				<a href="#caracteristicas" class="btn btn-hero-secondary">Ver Características</a>
			</div>
		</div>

		<!-- HERO VISUAL: Giant glowing Isotipo S -->
		<div class="hero-visual">
			<div class="glow-orb"></div>
			<div class="glass-preview-card">
				<img src="/S.png" alt="SIGA S" class="hero-isotipo" />
				<div class="live-indicator">
					<span class="live-dot"></span>
					<span>Agente IA En Línea</span>
				</div>
			</div>
		</div>
	</section>

	<!-- FEATURES SECTION -->
	<section id="caracteristicas" class="features-section">
		<div class="section-header">
			<h2 class="section-title">Control total, decisiones guiadas por IA</h2>
			<p class="section-subtitle">
				Arquitectura Zero-Trust y aislamiento de datos estructurados para cumplir con la Ley Chilena 21.719 de protección de privacidad.
			</p>
		</div>

		<div class="features-grid">
			<!-- Feature 1 -->
			<div class="feature-card">
				<div class="feature-icon">📦</div>
				<h3 class="feature-name">Stock Consolidado</h3>
				<p class="feature-desc">
					Monitorea el inventario de todas tus bodegas y puntos de venta en tiempo real. Conciliación automática y alertas inteligentes de stock bajo.
				</p>
			</div>
			<!-- Feature 2 -->
			<div class="feature-card">
				<div class="feature-icon">🏪</div>
				<h3 class="feature-name">POS Integrado</h3>
				<p class="feature-desc">
					Venta rápida al cliente final con sincronización y descuento inmediato de stock. Métodos de pago flexibles y comprobantes internos.
				</p>
			</div>
			<!-- Feature 3 -->
			<div class="feature-card">
				<div class="feature-icon">🛡️</div>
				<h3 class="feature-name">Cumplimiento Legal</h3>
				<p class="feature-desc">
					Diseño de privacidad nativo (Art. 14 quáter). Seudonimización obligatoria mediante UUID v4 para proteger los datos financieros y comerciales.
				</p>
			</div>
		</div>
	</section>

	<!-- AI COPILOT SHOWCASE -->
	<section id="ia-copilot" class="ai-showcase-section">
		<div class="showcase-content">
			<div class="showcase-badge">
				<Sparkle size={14} weight="fill" /> IA AGENTIVA
			</div>
			<h2 class="showcase-title">Interactúa con tu negocio usando Lenguaje Natural</h2>
			<p class="showcase-subtitle">
				Nuestro Agente IA no solo responde preguntas, sino que ejecuta acciones operativas en tu inventario a través del protocolo seguro A2UI (Agent-to-UI).
			</p>
			
			<ul class="showcase-list">
				<li><span class="check-icon"><Check size={14} weight="bold" /></span> Ajuste de stock rápido mediante comandos de voz o texto.</li>
				<li><span class="check-icon"><Check size={14} weight="bold" /></span> Detección predictiva de anomalías en precios o movimientos.</li>
				<li><span class="check-icon"><Check size={14} weight="bold" /></span> Respuestas inteligentes contextualizadas por local y SKU.</li>
			</ul>
		</div>

		<!-- AI Agent Chat Mockup -->
		<div class="ai-chat-mockup">
			<div class="chat-header">
				<img src="/S.png" alt="S" class="chat-agent-logo" />
				<div class="chat-agent-info">
					<span class="agent-name">Asistente SIGA</span>
					<span class="agent-status">Escribiendo...</span>
				</div>
			</div>
			<div class="chat-body">
				<div class="chat-bubble user-bubble">
					<p>¿Qué sucursales tienen stock crítico de Harina de Trigo?</p>
				</div>
				<div class="chat-bubble assistant-bubble">
					<div class="assistant-content">
						<p>Detecté que la **Sucursal Norte** está sin unidades de *Harina de Trigo 1kg* (SKU: HAR-001).</p>
						<div class="suggestion-glow-box">
							<span class="suggestion-badge">Sugerencia</span>
							<p>¿Deseas transferir 20 unidades desde el Depósito Central?</p>
							<button class="action-btn-mock">Transferir Stock</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>

	<!-- PRECIOS / SAAS -->
	<section id="precios" class="pricing-section">
		<div class="section-header">
			<h2 class="section-title">Planes SaaS que escalan contigo</h2>
			<p class="section-subtitle">Facturación de la plataforma SIGA estructurada para Pymes. Pruébalo gratis por 14 días.</p>
		</div>

		<div class="pricing-grid">
			<div class="pricing-card">
				<span class="plan-badge">PYME INICIO</span>
				<h3 class="plan-price">$29.990<span>/mes</span></h3>
				<p class="plan-desc">Ideal para negocios individuales y almacenes de barrio.</p>
				<ul class="plan-features">
					<li>Hasta 2 sucursales</li>
					<li>500 productos únicos</li>
					<li>POS integrado</li>
					<li>Soporte vía email</li>
				</ul>
				<a href="/login" class="btn btn-pricing">Probar Plan</a>
			</div>
			
			<div class="pricing-card featured">
				<span class="plan-badge featured-badge">PRO ENTERPRISE</span>
				<h3 class="plan-price">$59.990<span>/mes</span></h3>
				<p class="plan-desc">Perfecto para empresas en expansión con múltiples sucursales.</p>
				<ul class="plan-features">
					<li>Sucursales ilimitadas</li>
					<li>Productos ilimitados</li>
					<li>**Copiloto IA (A2UI)** incluido</li>
					<li>Auditorías de Ley 21.719</li>
					<li>Soporte 24/7 de alta prioridad</li>
				</ul>
				<a href="/login" class="btn btn-pricing-featured">Probar Plan Pro</a>
			</div>
		</div>
	</section>

	<!-- FOOTER -->
	<footer class="footer">
		<div class="footer-content">
			<div class="footer-left">
				<img src="/Logo_SIGA.png" alt="SIGA Logo" class="footer-logo" />
				<p class="footer-desc">Sistema Inteligente de Gestión de Activos para Pymes de retail multi-tenant.</p>
			</div>
			<div class="footer-right">
				<div class="footer-signature">
					Desarrollado por <strong>Héctor Aguila</strong> <span class="sep">/</span> SIGA &copy; 2026
				</div>
				<div class="signature-quote">
					&gt; Un Soñador con Poca RAM 👨🏻‍💻
				</div>
			</div>
		</div>
	</footer>
</div>

<style>
	/* === ESTILOS BASE DE LANDING PAGE === */
	:global(html) {
		scroll-behavior: smooth;
	}

	.landing-container {
		font-family: 'Hanken Grotesk', system-ui, -apple-system, sans-serif;
		background-color: var(--color-bg, #fdf7ff);
		color: var(--color-text, #0f172a);
		min-height: 100vh;
		overflow-x: hidden;
	}

	.btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		padding: 10px 20px;
		font-weight: var(--font-weight-semibold, 600);
		font-size: var(--font-size-sm, 0.875rem);
		border-radius: var(--radius-md, 8px);
		text-decoration: none;
		transition: all var(--transition-fast, 150ms ease);
		cursor: pointer;
		border: none;
	}

	/* === NAVBAR === */
	.navbar {
		position: sticky;
		top: 0;
		z-index: 100;
		background: rgba(253, 247, 255, 0.75);
		backdrop-filter: blur(12px);
		border-bottom: 1px solid var(--color-border-light, #f1f5f9);
	}

	.navbar-content {
		max-width: 1200px;
		margin: 0 auto;
		padding: 1rem 1.5rem;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.logo-img {
		height: 32px;
		object-fit: contain;
	}

	.nav-links {
		display: flex;
		gap: 2rem;
	}

	.nav-link {
		color: var(--color-text-secondary, #3b3c5e);
		font-weight: var(--font-weight-medium, 500);
		font-size: var(--font-size-sm, 0.875rem);
		text-decoration: none;
		transition: color var(--transition-fast, 150ms ease);
	}

	.nav-link:hover {
		color: var(--color-primary, #03045e);
	}

	.nav-actions {
		display: flex;
		align-items: center;
		gap: 1.25rem;
	}

	.btn-login {
		background: var(--color-primary, #03045e);
		color: #fff;
	}

	.btn-login:hover {
		background: var(--color-primary-hover, #0084d8);
		transform: translateY(-1px);
	}

	/* === HERO SECTION === */
	.hero-section {
		max-width: 1200px;
		margin: 0 auto;
		padding: 5rem 1.5rem;
		display: grid;
		grid-template-columns: 1.2fr 1fr;
		gap: 4rem;
		align-items: center;
	}

	@media (max-width: 900px) {
		.hero-section {
			grid-template-columns: 1fr;
			padding: 3rem 1.5rem;
			gap: 3rem;
			text-align: center;
		}
	}

	.hero-content {
		display: flex;
		flex-direction: column;
		gap: 1.5rem;
		align-items: flex-start;
	}

	@media (max-width: 900px) {
		.hero-content {
			align-items: center;
		}
	}

	.hero-badge {
		background: var(--color-primary-light, #e0f2fe);
		color: var(--color-primary, #03045e);
		font-size: 0.75rem;
		font-weight: var(--font-weight-bold, 700);
		padding: 6px 12px;
		border-radius: var(--radius-full, 9999px);
		display: flex;
		align-items: center;
		gap: 6px;
	}

	.hero-title {
		font-size: 3rem;
		font-weight: 800;
		color: var(--color-primary, #03045e);
		line-height: 1.15;
		letter-spacing: -0.02em;
	}

	.gradient-text {
		background: linear-gradient(135deg, var(--color-primary, #03045e), var(--color-primary-hover, #0084d8));
		-webkit-background-clip: text;
		-webkit-text-fill-color: transparent;
	}

	.hero-subtitle {
		font-size: 1.125rem;
		line-height: 1.6;
		color: var(--color-text-secondary, #3b3c5e);
	}

	.hero-cta-group {
		display: flex;
		gap: 1rem;
		flex-wrap: wrap;
	}

	.btn-hero-primary {
		background: var(--color-primary, #03045e);
		color: #fff;
		padding: 14px 28px;
		font-size: 0.95rem;
		box-shadow: var(--shadow-md);
	}

	.btn-hero-primary:hover {
		background: var(--color-primary-hover, #0084d8);
		transform: translateY(-2px);
		box-shadow: var(--shadow-lg);
	}

	.btn-hero-primary:hover .arrow-icon {
		transform: translateX(4px);
	}

	.arrow-icon {
		display: inline-flex;
		transition: transform var(--transition-fast);
		margin-left: 6px;
	}

	.btn-hero-secondary {
		background: var(--color-surface, #ffffff);
		color: var(--color-text-secondary);
		border: 1px solid var(--color-border);
		padding: 14px 28px;
		font-size: 0.95rem;
	}

	.btn-hero-secondary:hover {
		background: var(--color-border-light);
		color: var(--color-text);
		transform: translateY(-1px);
	}

	/* Hero visual (Isotipo S) */
	.hero-visual {
		position: relative;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.glow-orb {
		position: absolute;
		width: 300px;
		height: 300px;
		background: radial-gradient(circle, var(--color-accent, #80ffdb) 0%, rgba(128, 255, 219, 0) 70%);
		filter: blur(20px);
		opacity: 0.45;
		z-index: 1;
		animation: pulse-glow 6s infinite ease-in-out;
	}

	@keyframes pulse-glow {
		0%, 100% { transform: scale(1); opacity: 0.45; }
		50% { transform: scale(1.15); opacity: 0.6; }
	}

	.glass-preview-card {
		position: relative;
		z-index: 2;
		background: var(--color-surface, #ffffff);
		border: 1px solid rgba(255, 255, 255, 0.4);
		border-radius: var(--radius-xl, 12px);
		padding: 3rem;
		box-shadow: var(--shadow-lg), var(--shadow-glow);
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 1.5rem;
		transition: transform 0.4s ease;
	}

	.glass-preview-card:hover {
		transform: translateY(-5px) rotate(1deg);
	}

	.hero-isotipo {
		width: 140px;
		height: 140px;
		object-fit: contain;
		filter: drop-shadow(0 0 10px rgba(3, 4, 94, 0.08));
	}

	.live-indicator {
		background: var(--color-bg, #fdf7ff);
		border: 1px solid var(--color-border, #e2e8f0);
		border-radius: var(--radius-full, 9999px);
		padding: 6px 16px;
		display: flex;
		align-items: center;
		gap: 8px;
		font-size: 0.75rem;
		font-weight: 600;
	}

	.live-dot {
		width: 8px;
		height: 8px;
		background: var(--color-success, #00f5d4);
		border-radius: 50%;
		animation: pulse 2s infinite;
	}

	/* === FEATURES SECTION === */
	.features-section {
		background: var(--color-surface, #ffffff);
		border-top: 1px solid var(--color-border-light, #f1f5f9);
		border-bottom: 1px solid var(--color-border-light, #f1f5f9);
		padding: 6rem 1.5rem;
	}

	.section-header {
		max-width: 700px;
		margin: 0 auto 4rem auto;
		text-align: center;
		display: flex;
		flex-direction: column;
		gap: 1rem;
	}

	.section-title {
		font-size: 2.25rem;
		font-weight: 800;
		color: var(--color-primary, #03045e);
		letter-spacing: -0.02em;
	}

	.section-subtitle {
		font-size: 1rem;
		color: var(--color-text-muted, #7d7e9e);
		line-height: 1.6;
	}

	.features-grid {
		max-width: 1200px;
		margin: 0 auto;
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
		gap: 2rem;
	}

	.feature-card {
		background: var(--color-bg, #fdf7ff);
		border: 1px solid var(--color-border, #e2e8f0);
		border-radius: var(--radius-lg, 8px);
		padding: 2.25rem;
		display: flex;
		flex-direction: column;
		gap: 1rem;
		transition: all var(--transition-base);
	}

	.feature-card:hover {
		transform: translateY(-4px);
		border-color: var(--color-primary-hover, #0084d8);
		box-shadow: var(--shadow-md);
	}

	.feature-icon {
		font-size: 2rem;
		background: #fff;
		width: 50px;
		height: 50px;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: var(--radius-md);
		border: 1px solid var(--color-border-light);
	}

	.feature-name {
		font-size: 1.15rem;
		font-weight: var(--font-weight-bold, 700);
		color: var(--color-primary, #03045e);
	}

	.feature-desc {
		font-size: 0.925rem;
		line-height: 1.6;
		color: var(--color-text-secondary);
	}

	/* === AI SHOWCASE SECTION === */
	.ai-showcase-section {
		max-width: 1200px;
		margin: 0 auto;
		padding: 6rem 1.5rem;
		display: grid;
		grid-template-columns: 1fr 1.1fr;
		gap: 5rem;
		align-items: center;
	}

	@media (max-width: 900px) {
		.ai-showcase-section {
			grid-template-columns: 1fr;
			gap: 3rem;
		}
	}

	.showcase-content {
		display: flex;
		flex-direction: column;
		gap: 1.25rem;
	}

	.showcase-badge {
		align-self: flex-start;
		background: var(--color-accent-light, #effffb);
		color: var(--color-success-text, #0077b6);
		border: 1px solid rgba(128, 255, 219, 0.5);
		font-size: 0.75rem;
		font-weight: 700;
		padding: 4px 10px;
		border-radius: 4px;
		letter-spacing: 0.05em;
		display: inline-flex;
		align-items: center;
		gap: 6px;
	}

	.showcase-title {
		font-size: 2.25rem;
		font-weight: 800;
		color: var(--color-primary, #03045e);
		letter-spacing: -0.02em;
		line-height: 1.2;
	}

	.showcase-subtitle {
		font-size: 1rem;
		color: var(--color-text-secondary);
		line-height: 1.6;
	}

	.showcase-list {
		display: flex;
		flex-direction: column;
		gap: 10px;
		margin-top: 1rem;
	}

	.showcase-list li {
		font-size: 0.925rem;
		color: var(--color-text);
		display: flex;
		align-items: center;
		gap: 10px;
	}

	.check-icon {
		background: var(--color-accent-light);
		color: var(--color-primary);
		border: 1px solid var(--color-accent);
		width: 20px;
		height: 20px;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	/* AI Chat Mockup Card */
	.ai-chat-mockup {
		background: #fff;
		border: 1px solid var(--color-border, #e2e8f0);
		border-radius: var(--radius-xl, 12px);
		box-shadow: var(--shadow-lg), var(--shadow-glow);
		overflow: hidden;
		display: flex;
		flex-direction: column;
		height: 340px;
		transition: transform 0.3s;
	}

	.ai-chat-mockup:hover {
		transform: translateY(-2px);
	}

	.chat-header {
		background: var(--color-bg);
		border-bottom: 1px solid var(--color-border-light);
		padding: 12px 16px;
		display: flex;
		align-items: center;
		gap: 12px;
	}

	.chat-agent-logo {
		width: 24px;
		height: 24px;
		object-fit: contain;
	}

	.chat-agent-info {
		display: flex;
		flex-direction: column;
	}

	.agent-name {
		font-size: 0.85rem;
		font-weight: 700;
		color: var(--color-primary);
	}

	.agent-status {
		font-size: 0.7rem;
		color: var(--color-text-muted);
	}

	.chat-body {
		flex: 1;
		padding: 16px;
		display: flex;
		flex-direction: column;
		gap: 14px;
		overflow-y: auto;
		background: var(--color-bg);
	}

	.chat-bubble {
		max-width: 80%;
		padding: 10px 14px;
		font-size: 0.825rem;
		line-height: 1.4;
	}

	.user-bubble {
		align-self: flex-end;
		background: var(--color-primary-light);
		color: var(--color-primary);
		border-radius: var(--radius-md) var(--radius-md) 0 var(--radius-md);
	}

	.assistant-bubble {
		align-self: flex-start;
		background: #fff;
		color: var(--color-text);
		border: 1px solid var(--color-accent);
		border-radius: var(--radius-md) var(--radius-md) var(--radius-md) 0;
		box-shadow: var(--shadow-glow);
		display: flex;
		flex-direction: column;
		gap: 8px;
	}

	.suggestion-glow-box {
		margin-top: 8px;
		border-top: 1px solid var(--color-border-light);
		padding-top: 8px;
		display: flex;
		flex-direction: column;
		gap: 6px;
	}

	.suggestion-badge {
		align-self: flex-start;
		background: var(--color-accent-light);
		color: var(--color-success-text);
		font-size: 9px;
		font-weight: 700;
		padding: 1px 6px;
		border-radius: 4px;
		text-transform: uppercase;
	}

	.action-btn-mock {
		background: var(--color-accent);
		color: var(--color-primary);
		font-weight: 700;
		font-size: 0.75rem;
		padding: 6px 12px;
		border: none;
		border-radius: 4px;
		cursor: pointer;
		text-align: center;
		transition: background var(--transition-fast);
	}

	.action-btn-mock:hover {
		background: var(--color-accent-hover);
	}

	/* === PRICING SECTION === */
	.pricing-section {
		background: var(--color-surface, #ffffff);
		border-top: 1px solid var(--color-border-light);
		padding: 6rem 1.5rem;
	}

	.pricing-grid {
		max-width: 900px;
		margin: 0 auto;
		display: grid;
		grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
		gap: 2.5rem;
		align-items: stretch;
	}

	.pricing-card {
		background: var(--color-bg);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-xl, 12px);
		padding: 3rem 2rem;
		display: flex;
		flex-direction: column;
		gap: 1.5rem;
		position: relative;
		transition: transform var(--transition-base), box-shadow var(--transition-base);
	}

	.pricing-card:hover {
		transform: translateY(-4px);
		box-shadow: var(--shadow-md);
	}

	.pricing-card.featured {
		border-color: var(--color-primary-hover);
		background: var(--color-surface);
		box-shadow: var(--shadow-lg), var(--shadow-glow);
	}

	.pricing-card.featured:hover {
		box-shadow: var(--shadow-lg), 0 0 20px rgba(128, 255, 219, 0.45);
	}

	.plan-badge {
		font-size: 0.75rem;
		font-weight: 800;
		color: var(--color-text-muted);
		letter-spacing: 0.05em;
	}

	.featured-badge {
		color: var(--color-primary-hover);
	}

	.plan-price {
		font-size: 2.5rem;
		font-weight: 800;
		color: var(--color-primary);
	}

	.plan-price span {
		font-size: 1rem;
		color: var(--color-text-muted);
		font-weight: 400;
	}

	.plan-desc {
		font-size: 0.875rem;
		color: var(--color-text-secondary);
		line-height: 1.5;
	}

	.plan-features {
		display: flex;
		flex-direction: column;
		gap: 10px;
		font-size: 0.875rem;
		list-style: none;
		margin: 1rem 0;
	}

	.plan-features li {
		color: var(--color-text);
		display: flex;
		align-items: center;
		gap: 8px;
	}

	.plan-features li::before {
		content: '✓';
		color: var(--color-success-text);
		font-weight: bold;
	}

	.btn-pricing {
		background: var(--color-surface);
		color: var(--color-primary);
		border: 1px solid var(--color-primary);
		padding: 12px;
		text-align: center;
		font-weight: 700;
	}

	.btn-pricing:hover {
		background: var(--color-primary-light);
	}

	.btn-pricing-featured {
		background: var(--color-primary);
		color: #fff;
		padding: 12px;
		text-align: center;
		font-weight: 700;
	}

	.btn-pricing-featured:hover {
		background: var(--color-primary-hover);
		box-shadow: var(--shadow-md);
	}

	/* === FOOTER === */
	.footer {
		background: #020329 !important; /* Force a consistent dark navy blue for premium, high contrast in both themes */
		color: #e2e8f0 !important; /* Ensure the primary text is a highly readable silver-white */
		padding: 4rem 1.5rem 3rem 1.5rem;
		border-top: 1px solid rgba(255, 255, 255, 0.1);
	}

	.footer-content {
		max-width: 1200px;
		margin: 0 auto;
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		flex-wrap: wrap;
		gap: 2.5rem;
	}

	.footer-left {
		display: flex;
		flex-direction: column;
		gap: 1rem;
		max-width: 320px;
	}

	.footer-logo {
		height: 28px;
		object-fit: contain;
		filter: brightness(0) invert(1); /* white out logo */
		align-self: flex-start;
	}

	.footer-desc {
		font-size: 0.85rem;
		line-height: 1.5;
		color: #cbd5e1 !important; /* Soft gray for description, high contrast */
	}

	.footer-right {
		display: flex;
		flex-direction: column;
		align-items: flex-end;
		gap: 8px;
	}

	@media (max-width: 600px) {
		.footer-content {
			flex-direction: column;
			align-items: center;
			text-align: center;
		}
		
		.footer-right {
			align-items: center;
		}
	}

	.footer-signature {
		font-size: 0.8rem;
		color: #94a3b8 !important;
	}

	.footer-signature strong {
		color: #ffffff !important;
	}

	.sep {
		color: rgba(255, 255, 255, 0.3);
		margin: 0 4px;
	}

	.signature-quote {
		font-family: 'JetBrains Mono', monospace;
		color: var(--color-accent) !important;
		font-size: 0.75rem;
	}
</style>
