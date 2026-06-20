package game.objects.facilities.generators;

import game.components.OrbitComponent;
import game.objects.items.ItemType;
import game.objects.items.RawResource;
import game.objects.spaceBodies.Planet;

import java.util.Map;

public class SolarPanel extends PowerGenerator {
	public static final Map<ItemType, Integer> COST = Map.of(RawResource.METAL, 80);
	public static final float POWER_DEMAND = 0.0f;
	private final OrbitComponent orbitComponent;

	public SolarPanel(Planet planet) {
		super(planet);
		float distanceToStar = planet.distanceToStar();
		powerOutput = Math.clamp(20.0f * ( 1000000.0f / ( distanceToStar * distanceToStar ) ),
								 0.5f,
								 50.0f);
		isActive = true;

		this.mesh = game.geometry.BuildingGeometry.getSolarPanelMesh();
		this.color = new org.joml.Vector3f(0.2f, 0.7f, 0.9f);
		orbitComponent = new OrbitComponent(planet.getRadius() * 1.8f, 0.2f, 0.0f);
		update(0.0f);
	}

	@Override
	public void update(float deltaTime) {
		if (orbitComponent != null) {
			orbitComponent.update(this.localPosition, deltaTime);
		}
	}
}
