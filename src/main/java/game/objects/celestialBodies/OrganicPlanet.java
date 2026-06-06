package game.objects.celestialBodies;

import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import game.info.PlanetType;

public class OrganicPlanet extends Planet {
    private static final int PLANET_RESOLUTION = 80;

    public OrganicPlanet(PlanetInfo planetInfo) {
        super(PlanetGeometry.generate(PLANET_RESOLUTION,
                                      planetInfo.planetRadius(),
                                      PlanetType.ORGANIC),
              planetInfo);
    }

    @Override
    public PlanetType getType() {
        return PlanetType.ORGANIC;
    }
}
