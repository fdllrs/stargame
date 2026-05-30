package game.objects.facilities;

import game.objects.celestialBodies.Planet;

public abstract class Facility {
    protected final float efficiencyMultiplier = 1f;

    public abstract void tick(Planet planet);
}
