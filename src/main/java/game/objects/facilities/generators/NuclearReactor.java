package game.objects.facilities.generators;

import game.geometry.BuildingGeometry;
import game.items.ItemType;
import game.items.ProcessedItem;
import game.items.RawResource;
import game.objects.celestialBodies.Planet;

import java.util.Map;

public class NuclearReactor extends PowerGenerator {
	public static final Map<ItemType, Integer> COST = Map.of(RawResource.METAL,
															 150,
															 ProcessedItem.ALLOY,
															 50);
	public static final float POWER_DEMAND = 0.0f;

	public NuclearReactor(Planet planet) {
		super(planet);
		this.mesh = BuildingGeometry.getNuclearReactorMesh();
		this.color = new org.joml.Vector3f(0.3f, 0.5f, 0.8f);
		this.powerOutput = 50.0f;
	}

	@Override
	public void tick(Planet planet, float efficiencyMultiplier) {
		isActive = planet.getStorage().attemptWithdraw(RawResource.HYDROGEN, 1);
	}
}
