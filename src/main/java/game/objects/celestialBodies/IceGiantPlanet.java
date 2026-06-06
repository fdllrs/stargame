package game.objects.celestialBodies;

import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import game.info.PlanetType;

public class IceGiantPlanet extends Planet {
    private static final int PLANET_RESOLUTION = 80;

    public IceGiantPlanet(PlanetInfo planetInfo) {
        super(PlanetGeometry.generate(PLANET_RESOLUTION,
                                      planetInfo.planetRadius(),
                                      PlanetType.ICE_GIANT),
              planetInfo);
    }

    @Override
    public PlanetType getType() {
        return PlanetType.ICE_GIANT;
    }
}
