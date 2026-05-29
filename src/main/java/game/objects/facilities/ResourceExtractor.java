package game.objects.facilities;

import game.items.RawResource;
import game.objects.celestialBodies.Planet;

public class ResourceExtractor extends Facility {
    private final RawResource resourceType;
    private final int extractionAmount = 1;

    public ResourceExtractor(RawResource resourceType) {
        super();
        this.resourceType = resourceType;
    }

    @Override public void tick(Planet planet, float deltaTime) {
        planet.deposit(resourceType,
                       Math.round(extractionAmount * this.efficiencyMultiplier));
    }
}
