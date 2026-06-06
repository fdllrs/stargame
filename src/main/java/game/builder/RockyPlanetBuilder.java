package game.builder;

import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.celestialBodies.RockyPlanet;
import game.objects.celestialBodies.Star;
import org.joml.Vector3f;

public class RockyPlanetBuilder extends PlanetBuilder {
    public RockyPlanetBuilder(Star homeStar) {
        super(homeStar);
    }

    @Override public RockyPlanet build() {

        basicPlanetInfo basicPlanetInfo = buildBasicPlanetInfo();

        PlanetInfo info = new PlanetInfo(homeStar,
                                         basicPlanetInfo.finalSpeed(),
                                         basicPlanetInfo.finalAngle(),
                                         basicPlanetInfo.finalRadius(),
                                         basicPlanetInfo.finalDistance(),
                                         basicPlanetInfo.finalColorA(),
                                         basicPlanetInfo.finalColorB(),
                                         null,
                                         PlanetType.ROCKY);

        return new RockyPlanet(info);
    }

    @Override protected void generateColors() {
        int rockyStyle = RANDOM.nextInt(3);
        if (rockyStyle == 0) {
            setVolcanicColors();
        } else if (rockyStyle == 1) {
            setDesertColors();
        } else {
            setBasaltColors();
        }
    }

    private void setBasaltColors() {
        float val1 = 0.2f + RANDOM.nextFloat() * 0.15f;
        colorA = new Vector3f(val1, val1, val1 + RANDOM.nextFloat() * 0.05f);
        colorB = new Vector3f(0.4f + RANDOM.nextFloat() * 0.15f,
                              0.4f + RANDOM.nextFloat() * 0.15f,
                              0.4f + RANDOM.nextFloat() * 0.15f);
    }

    private void setDesertColors() {
        colorA = new Vector3f(0.6f + RANDOM.nextFloat() * 0.15f,
                              0.45f + RANDOM.nextFloat() * 0.1f,
                              0.2f);
        colorB = new Vector3f(0.35f + RANDOM.nextFloat() * 0.1f,
                              0.25f + RANDOM.nextFloat() * 0.05f,
                              0.1f);
    }

    private void setVolcanicColors() {
        colorA = new Vector3f(0.5f + RANDOM.nextFloat() * 0.2f,
                              0.15f + RANDOM.nextFloat() * 0.1f,
                              0.05f);
        colorB = new Vector3f(0.3f + RANDOM.nextFloat() * 0.1f,
                              0.1f + RANDOM.nextFloat() * 0.05f,
                              0.05f);
    }
}
