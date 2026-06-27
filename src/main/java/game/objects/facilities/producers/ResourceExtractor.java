package game.objects.facilities.producers;

import game.objects.items.ItemType;
import game.objects.items.RawResource;
import game.objects.spaceBodies.Planet;

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
	protected void process(Planet planet) {
		planet.deposit(resourceType, extractionAmount);

		org.joml.Vector3f worldPos = new org.joml.Vector3f();
		org.joml.Vector4f localPos4 = new org.joml.Vector4f(this.localPosition, 1.0f);
		planet.getModelMatrix().transform(localPos4);
		worldPos.set(localPos4.x, localPos4.y, localPos4.z);

		org.joml.Vector4f textCol = new org.joml.Vector4f(0.2f, 1.0f, 0.2f, 1.0f);
		engine.events.EventBus.publish(new game.events.SpawnFloatingTextEvent("+" + extractionAmount, worldPos, planet.getPosition(), textCol));
	}

	@Override
	public void upgrade() {
		extractionAmount += 1;
	}
}
