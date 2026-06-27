package game.upgrades;

import game.objects.Player;
import game.objects.items.ItemType;
import game.objects.items.ProcessedItem;

import java.util.Map;

public enum ShipUpgrade {
	FUSION_ENGINES_T1("Fusion Engines",
					  "Increases basic thrust and top speed.",
					  Map.of(ProcessedItem.ALLOY,
							 10)) { // Using ALLOY as a placeholder cost for now

		@Override
		public void applyTo(Player player) {
			player.setAccelerationFactor(5f);
			player.setMaxSpeed(50f);
		}
	},

	WARP_DRIVE_T2("Warp Drive", "Enables hyper-speed burst.", Map.of(ProcessedItem.ALLOY, 25)) {
		@Override
		public void applyTo(Player player) {
			player.setTurboMultiplier(1.2f);
		}
	},

	CARGO_EXPANSION_T1("Cargo Hold T1",
					   "Expands ship storage by 500.",
					   Map.of(ProcessedItem.ALLOY, 8)) {
		@Override
		public void applyTo(Player player) {
			player.getStorage().addCapacity(500);
		}
	};

	private final String displayName;
	private final String description;
	private final Map<ItemType, Integer> cost;

	ShipUpgrade(String displayName, String description, Map<ItemType, Integer> cost) {
		this.displayName = displayName;
		this.description = description;
		this.cost = cost;
	}

	public abstract void applyTo(Player player);

	public Map<ItemType, Integer> getCost() { return cost; }

	public String getDescription() { return description; }

	public String getDisplayName() { return displayName; }
}