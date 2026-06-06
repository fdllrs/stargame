package game.builder;

import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.celestialBodies.IceGiantPlanet;
import game.objects.celestialBodies.Star;
import org.joml.Vector3f;

public class IceGiantPlanetBuilder extends PlanetBuilder {
    public IceGiantPlanetBuilder(Star homeStar) {
        super(homeStar);
    }

    @Override public IceGiantPlanet build() {
        basicPlanetInfo basicPlanetInfo = buildBasicPlanetInfo();

        PlanetInfo info = new PlanetInfo(homeStar,
                                         basicPlanetInfo.finalSpeed(),
                                         basicPlanetInfo.finalAngle(),
                                         basicPlanetInfo.finalRadius(),
                                         basicPlanetInfo.finalDistance(),
                                         basicPlanetInfo.finalColorA(),
                                         basicPlanetInfo.finalColorB(),
                                         null,
                                         PlanetType.ICE_GIANT);

        return new IceGiantPlanet(info);
    }

    @Override protected void generateColors() {
        int iceStyle = RANDOM.nextInt(2);
        if (iceStyle == 0) {
            setBlueColors();
        } else {
            setCyanColors();
        }
    }

    private void setCyanColors() {
        colorA = new Vector3f(0.55f + RANDOM.nextFloat() * 0.15f,
                              0.75f + RANDOM.nextFloat() * 0.15f,
                              0.9f);
        colorB = new Vector3f(0.45f + RANDOM.nextFloat() * 0.1f,
                              0.45f + RANDOM.nextFloat() * 0.1f,
                              0.7f + RANDOM.nextFloat() * 0.15f);
    }

    private void setBlueColors() {
        colorA = new Vector3f(0.6f + RANDOM.nextFloat() * 0.2f,
                              0.8f + RANDOM.nextFloat() * 0.15f,
                              0.95f);
        colorB = new Vector3f(0.35f + RANDOM.nextFloat() * 0.1f,
                              0.55f + RANDOM.nextFloat() * 0.15f,
                              0.75f);
    }
}
