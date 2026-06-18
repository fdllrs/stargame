package game.ui;

import game.ui.panel.controller.DefaultPanelController;
import game.ui.panel.controller.InfoPanelController;

import java.util.List;
import java.util.Map;

/**
 * Implemented by any game object that can display its properties in a UI info panel.
 * Keeps the engine layer free of game-specific types.
 */
public interface Describable {
	/**
	 * The title shown at the top of the panel.
	 */
	String getDisplayName();
	/**
	 * Ordered list of key-value pairs shown in the panel body.
	 */
	List<Map.Entry<String, String>> getDisplayProperties();
	/**
	 * Returns the panel controller layout manager for this describable target.
	 */
	default InfoPanelController getPanelController(game.components.StorageComponent playerStorage,
			engine.ui.text.FontAtlas font,
			float width,
			Runnable onRebuild,
			java.util.function.Consumer<game.objects.spaceBodies.SpaceBody> onSelectTarget) {
		return new DefaultPanelController();
	}
}
