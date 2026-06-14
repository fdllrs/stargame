package game.objects.facilities.producers;

import game.geometry.BuildingGeometry;
import game.items.ItemType;
import game.items.ProcessedItem;
import game.items.RawResource;
import game.objects.spaceBodies.Planet;
import org.joml.Vector3f;

import java.util.Map;

public class ResearchLab extends ProducerFacility {
	public static final Map<ItemType, Integer> COST = Map.of(RawResource.METAL,
															 120,
															 ProcessedItem.ALLOY,
															 40);
	public static final float POWER_DEMAND = 6.0f;

	private final RawResource inputResource;
	private final ProcessedItem outputScience;

	public ResearchLab(Planet planet) {
		super(planet, POWER_DEMAND, Map.of(), Map.of());
		this.mesh = BuildingGeometry.getResearchLabMesh();
		this.color = new Vector3f(0.5f, 0.1f, 0.8f); // purple/cyan

		// Determine input and output based on a planet type
		switch (planet.getType()) {
			case ORGANIC -> {
				this.inputResource = RawResource.ORGANICS;
				this.outputScience = ProcessedItem.BIOLOGY_SCIENCE;
			}
			case GAS_GIANT -> {
				this.inputResource = RawResource.HYDROGEN;
				this.outputScience = ProcessedItem.SPACE_SCIENCE;
			}
			case ICE_GIANT -> {
				this.inputResource = RawResource.WATER;
				this.outputScience = ProcessedItem.CRYO_PHYSICS_SCIENCE;
			}
			default -> {
				this.inputResource = RawResource.METAL;
				this.outputScience = ProcessedItem.PHYSICS_SCIENCE;
			}
		}
	}

	@Override
	protected boolean canProcess(Planet planet) {
		return planet.getStorage().canDeposit(1) && planet.getStorage().canWithdraw(inputResource,
																					5);
	}

	@Override
	protected void process(Planet planet) {
		planet.getStorage().attemptWithdraw(inputResource, 5);
		planet.getStorage().deposit(outputScience, 1);
	}
}
