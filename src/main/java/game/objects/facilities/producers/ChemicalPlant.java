package game.objects.facilities.producers;

import game.geometry.BuildingGeometry;
import game.objects.items.ItemType;
import game.objects.items.ProcessedItem;
import game.objects.items.RawResource;
import game.objects.spaceBodies.Planet;
import org.joml.Vector3f;

import java.util.Map;

public class ChemicalPlant extends ProducerFacility {
	public static final Map<ItemType, Integer> COST = Map.of(RawResource.METAL,
															 100,
															 ProcessedItem.ALLOY,
															 20);
	public static final Map<ItemType, Integer> INPUTS = Map.of(RawResource.HYDROGEN,
															   1,
															   RawResource.WATER,
															   1);
	public static final float POWER_DEMAND = 4.0f;

	public static final Map<ItemType, Integer> OUTPUTS = Map.of(ProcessedItem.COOLANT, 1);

	public ChemicalPlant(Planet planet) {
		super(planet, POWER_DEMAND, INPUTS, OUTPUTS);
		this.mesh = BuildingGeometry.getChemicalPlantMesh();
		this.color = new Vector3f(0.1f, 0.8f, 0.3f); // bright green
	}
}
