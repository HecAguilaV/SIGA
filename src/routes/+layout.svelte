<script>
  // Importamos los estilos globales y la barra de navegación compartida
  import '../app.css';
  import Navbar from '$lib/components/Navbar.svelte';
  import AsistenteContextual from '$lib/components/AsistenteContextual.svelte';
</script>

<Navbar />

<main class="section">
  <div class="container">
    <slot />
  </div>
</main>

<AsistenteContextual />