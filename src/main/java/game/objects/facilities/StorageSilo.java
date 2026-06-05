package game.objects.facilities;

import game.geometry.BuildingGeometry;
import game.objects.celestialBodies.Planet;
import org.joml.Vector3f;

public class StorageSilo extends Facility {
    public static final int initialCapacity = 1000;
    int capacity = initialCapacity;

    public StorageSilo(Planet planet) {
        super();
        planet.addCapacity(this.capacity);

        this.mesh = BuildingGeometry.getSiloMesh();
        this.color = new Vector3f(0.5f, 0.6f, 0.7f);

        planet.addFacility(this);
        this.initPosition(planet.getRadius());

    }

    @Override public void tick(Planet planet) {
        // Storage Silos are passive buffers and do not execute tick operations
    }

}
