/**
 * ui.svelte.ts — Almacén de estado reactivo (Svelte 5) para la interfaz de usuario.
 */

class UIState {
	newMovementOpen = $state(false);
	activeStoreId = $state('store1'); // Por defecto 'store1' (Sucursal Centro)

	openNewMovement() {
		this.newMovementOpen = true;
	}

	closeNewMovement() {
		this.newMovementOpen = false;
	}
}

export const ui = new UIState();
