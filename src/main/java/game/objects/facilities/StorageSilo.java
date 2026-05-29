package game.objects.facilities;

import game.objects.celestialBodies.Planet;

public class StorageSilo {
    public static final int initialCapacity = 1000;
    int capacity = 1000;

    public StorageSilo(Planet planet) {
        planet.addCapacity(initialCapacity);
    }

    public int getCapacity() {
        return capacity;
    }
}
