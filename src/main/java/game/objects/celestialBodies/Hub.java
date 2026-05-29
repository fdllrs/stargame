package game.objects.celestialBodies;

import game.components.StorageComponent;

public class Hub {
    StorageComponent storage;
    int level = 1;

    public Hub(int capacity) {
        storage = new StorageComponent(capacity);
    }
}
