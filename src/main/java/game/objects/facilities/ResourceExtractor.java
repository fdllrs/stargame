package game.objects.facilities;

import game.geometry.BuildingGeometry;
import game.items.RawResource;
import game.objects.celestialBodies.Planet;

public class ResourceExtractor extends Facility {
    private final RawResource resourceType;
    private final int extractionAmount = 1;

    public ResourceExtractor(RawResource resourceType, Planet planet) {
        super();
        this.resourceType = resourceType;
        this.mesh = BuildingGeometry.getExtractorMesh();

        //        switch (resourceType) {
        //            case METAL -> this.color = new Vector3f(0.8f,
        //                                                    0.4f,
        //                                                    0.1f);      // Metallic
        //                                                    bronze/orange
        //            case HYDROGEN ->
        //                    this.color = new Vector3f(0.1f, 0.7f, 1.0f);   //
        //                    Electric gas blue
        //            case WATER ->
        //                    this.color = new Vector3f(0.1f, 0.3f, 0.9f);      // Deep
        //                    liquid blue
        //            case ORGANICS -> this.color = new Vector3f(0.1f,
        //                                                       0.8f,
        //                                                       0.2f);   //
        //                                                       Bio-luminescent green
        //            default -> this.color = new Vector3f(0.6f, 0.6f, 0.6f);
        //        }
        planet.addFacility(this);
        this.initPosition(planet.getRadius());

    }

    @Override public void tick(Planet planet) {
        // Cast the calculation to int to match the Planet.deposit signature
        planet.deposit(resourceType,
                       (int) (extractionAmount * this.efficiencyMultiplier));
    }
}
