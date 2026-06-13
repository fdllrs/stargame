package game.objects.facilities.producers;

import game.items.ItemType;
import game.items.RawResource;
import game.objects.celestialBodies.Planet;

import java.util.Map;

public class ResourceExtractor extends ProducerFacility {
	public static final Map<ItemType, Integer> COST = Map.of(RawResource.METAL, 50);
	public static final float POWER_DEMAND = 2.0f;

	private final RawResource resourceType;
	private int extractionAmount = 1;

	public ResourceExtractor(RawResource resourceType, Planet planet) {
		super(planet, POWER_DEMAND, Map.of(), Map.of());
		this.resourceType = resourceType;
	}

	@Override
	protected boolean canProcess(Planet planet) {
		return planet.getStorage().canDeposit(extractionAmount);
	}

	@Override
	public void upgrade() {
		extractionAmount += 1;
	}

	@Override
	protected void process(Planet planet) {
		planet.deposit(resourceType, extractionAmount);
	}
}
