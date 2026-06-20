package game.objects.facilities.producers;

import game.geometry.BuildingGeometry;
import game.objects.items.ItemType;
import game.objects.items.ProcessedItem;
import game.objects.items.RawResource;
import game.objects.spaceBodies.Planet;
import org.joml.Vector3f;

import java.util.Map;

public class EngineFabricator extends ProducerFacility {
	public static final Map<ItemType, Integer> COST = Map.of(RawResource.METAL,
															 200,
															 ProcessedItem.ALLOY,
															 80,
															 ProcessedItem.COOLANT,
															 20);
	public static final float POWER_DEMAND = 8.0f;
	public static final Map<ItemType, Integer> INPUTS = Map.of(ProcessedItem.ALLOY,
															   5,
															   ProcessedItem.COOLANT,
															   2);
	public static final Map<ItemType, Integer> OUTPUTS = Map.of(ProcessedItem.THRUSTER, 1);

	public EngineFabricator(Planet planet) {
		super(planet, POWER_DEMAND, INPUTS, OUTPUTS);
		this.mesh = BuildingGeometry.getEngineFabricatorMesh();
		this.color = new Vector3f(0.8f, 0.7f, 0.1f); // yellow-gold
	}
}
