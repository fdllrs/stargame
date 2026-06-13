package game.objects.facilities.generators;

import game.items.ItemType;
import game.items.RawResource;
import game.objects.celestialBodies.Planet;

import java.util.Map;

public class SolarArray extends PowerGenerator {
	public static final Map<ItemType, Integer> COST = Map.of(RawResource.METAL, 80);
	public static final float POWER_DEMAND = 0.0f;

	public SolarArray(Planet planet) {
		super(planet);
		float distanceToStar = planet.distanceToStar();
		powerOutput = Math.clamp(20.0f * ( 1000000.0f / ( distanceToStar * distanceToStar ) ),
								 0.5f,
								 50.0f);
		isActive = true;

		this.mesh = game.geometry.BuildingGeometry.getSolarArrayMesh();
		this.color = new org.joml.Vector3f(0.2f, 0.7f, 0.9f);
	}
}
