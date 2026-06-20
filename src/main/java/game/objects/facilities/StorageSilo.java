package game.objects.facilities;

import game.geometry.BuildingGeometry;
import game.objects.items.ItemType;
import game.objects.items.RawResource;
import game.objects.spaceBodies.Planet;
import org.joml.Vector3f;

import java.util.Map;

public class StorageSilo extends Facility {
	public static final Map<ItemType, Integer> COST = Map.of(RawResource.METAL, 100);
	public static final int INITIAL_CAPACITY = 1000;
	int capacity = INITIAL_CAPACITY;

	public StorageSilo(Planet planet) {
		super(planet);
		planet.addStorageSilo(this);

		this.mesh = BuildingGeometry.getSiloMesh();
		this.color = new Vector3f(0.5f, 0.6f, 0.7f);
	}

	public int getCapacity() {
		return capacity;
	}

	@Override
	public int getPowerDemand() {
		return 0;
	}

	@Override
	public void tick(Planet planet, float efficiencyMultiplier) {
	}

	@Override
	public void upgrade() {
		capacity += 1000;
	}
}