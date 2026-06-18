package game.items;

import engine.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class ItemIconRegistry {
	private static final Map<ItemType, Texture> icons = new HashMap<>();

	public static Texture getIcon(ItemType type) {
		return icons.computeIfAbsent(type, t -> {
			String path = "src/main/resources/textures/items/" + t.name().toLowerCase() + ".png";
			try {
				return new Texture(path);
			} catch (Exception e) {
				System.err.println("Failed to load icon for " + t.name() + " at " + path + ": " +
								   e.getMessage());
				return null;
			}
		});
	}
}
