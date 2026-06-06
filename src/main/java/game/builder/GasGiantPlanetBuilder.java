package game.builder;

import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.celestialBodies.GasGiantPlanet;
import game.objects.celestialBodies.Star;
import org.joml.Vector3f;

public class GasGiantPlanetBuilder extends PlanetBuilder {
    public GasGiantPlanetBuilder(Star homeStar) {
        super(homeStar);
    }

    @Override public GasGiantPlanet build() {
        basicPlanetInfo basicPlanetInfo = buildBasicPlanetInfo();

        PlanetInfo info = new PlanetInfo(homeStar,
                                         basicPlanetInfo.finalSpeed(),
                                         basicPlanetInfo.finalAngle(),
                                         basicPlanetInfo.finalRadius(),
                                         basicPlanetInfo.finalDistance(),
                                         basicPlanetInfo.finalColorA(),
                                         basicPlanetInfo.finalColorB(),
                                         null,
                                         PlanetType.GAS_GIANT);

        return new GasGiantPlanet(info);
    }

    @Override protected void generateColors() {
        int gasStyle = RANDOM.nextInt(2);
        if (gasStyle == 0) {
            setJupiterColors();
        } else {
            setNeptuneColors();
        }
    }

    private void setNeptuneColors() {
        colorA = new Vector3f(0.1f,
                              0.5f + RANDOM.nextFloat() * 0.2f,
                              0.6f + RANDOM.nextFloat() * 0.2f);
        colorB = new Vector3f(0.05f,
                              0.25f + RANDOM.nextFloat() * 0.1f,
                              0.45f + RANDOM.nextFloat() * 0.15f);
    }

    private void setJupiterColors() {
        colorA = new Vector3f(0.75f + RANDOM.nextFloat() * 0.15f,
                              0.5f + RANDOM.nextFloat() * 0.15f,
                              0.3f);
        colorB = new Vector3f(0.4f + RANDOM.nextFloat() * 0.1f,
                              0.2f + RANDOM.nextFloat() * 0.08f,
                              0.1f);
    }
}
