package game.objects.spaceBodies;

import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import game.info.PlanetType;

public class RockyPlanet extends Planet {
	private static final int PLANET_RESOLUTION = 80;

	public RockyPlanet(PlanetInfo planetInfo) {
		super(PlanetGeometry.generate(PLANET_RESOLUTION,
									  planetInfo.planetRadius(),
									  PlanetType.ROCKY), planetInfo);
	}

	@Override
	public PlanetType getType() {
		return PlanetType.ROCKY;
	}
}
