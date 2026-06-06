package game.builder;

import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.celestialBodies.OrganicPlanet;
import game.objects.celestialBodies.Star;
import org.joml.Vector3f;

public class OrganicPlanetBuilder extends PlanetBuilder {
    public OrganicPlanetBuilder(Star homeStar) {
        super(homeStar);
    }

    @Override public OrganicPlanet build() {
        basicPlanetInfo basicPlanetInfo = buildBasicPlanetInfo();

        PlanetInfo info = new PlanetInfo(homeStar,
                                         basicPlanetInfo.finalSpeed(),
                                         basicPlanetInfo.finalAngle(),
                                         basicPlanetInfo.finalRadius(),
                                         basicPlanetInfo.finalDistance(),
                                         basicPlanetInfo.finalColorA(),
                                         basicPlanetInfo.finalColorB(),
                                         null,
                                         PlanetType.ORGANIC);

        return new OrganicPlanet(info);
    }

    @Override protected void generateColors() {
        int organicStyle = RANDOM.nextInt(2);
        if (organicStyle == 0) {
            setEarthColors();
        } else {
            setAlienColors();
        }
    }

    private void setAlienColors() {
        colorA = new Vector3f(0.05f,
                              0.5f + RANDOM.nextFloat() * 0.2f,
                              0.6f + RANDOM.nextFloat() * 0.1f); // cyan ocean
        colorB = new Vector3f(0.5f + RANDOM.nextFloat() * 0.2f,
                              0.1f,
                              0.5f + RANDOM.nextFloat() * 0.2f); // purple vegetation
    }

    private void setEarthColors() {
        colorA = new Vector3f(0.05f,
                              0.2f + RANDOM.nextFloat() * 0.15f,
                              0.6f + RANDOM.nextFloat() * 0.15f); // ocean
        colorB = new Vector3f(0.15f + RANDOM.nextFloat() * 0.15f,
                              0.45f + RANDOM.nextFloat() * 0.15f,
                              0.1f); // land
    }
}
