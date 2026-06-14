package game.objects.facilities.producers;

import game.geometry.BuildingGeometry;
import game.items.ItemType;
import game.items.ProcessedItem;
import game.items.RawResource;
import game.objects.spaceBodies.Planet;
import org.joml.Vector3f;

import java.util.Map;

public class AlloySmelter extends ProducerFacility {
	public static final Map<ItemType, Integer> COST = Map.of(RawResource.METAL, 150);
	public static final float POWER_DEMAND = 5.0f;
	public static final Map<ItemType, Integer> INPUTS = Map.of(RawResource.METAL, 2);
	public static final Map<ItemType, Integer> OUTPUTS = Map.of(ProcessedItem.ALLOY, 1);

	public AlloySmelter(Planet planet) {
		super(planet, POWER_DEMAND, INPUTS, OUTPUTS);
		this.mesh = BuildingGeometry.getAlloySmelterMesh();
		this.color = new Vector3f(0.6f, 0.6f, 0.6f); // steel gray
	}
}
