package game.builder;

import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.celestialBodies.GasGiantPlanet;
import game.objects.celestialBodies.Star;

public class GasGiantPlanetBuilder extends PlanetBuilder {
    public GasGiantPlanetBuilder(Star homeStar) {
        super(homeStar);
    }

    @Override public GasGiantPlanet build() {
        basicPlanetInfo basicPlanetInfo = buildBasicPlanetInfo();
        float finalRadius = (this.radius != null)
                            ? this.radius
                            : 25f + RANDOM.nextFloat() * 25f;
        boolean finalHasRings = (this.hasRings != null)
                                ? this.hasRings
                                : (RANDOM.nextFloat() < 0.6f);

        PlanetInfo info = new PlanetInfo(homeStar,
                                         basicPlanetInfo.finalSpeed(),
                                         basicPlanetInfo.finalAngle(),
                                         finalRadius,
                                         basicPlanetInfo.finalDistance(),
                                         basicPlanetInfo.finalColorA(),
                                         basicPlanetInfo.finalColorB(),
                                         null,
                                         PlanetType.GAS_GIANT,
                                         finalHasRings);

        return new GasGiantPlanet(info);
    }

    @Override protected void generateColors() {
        float h1 = RANDOM.nextFloat();
        // Shift hue slightly for color B to create an analogous palette
        float hueShift = (RANDOM.nextFloat() * 0.16f) - 0.08f;
        float h2 = (h1 + hueShift + 1f) % 1f;

        float s1 = 0.4f + RANDOM.nextFloat() * 0.4f;
        float s2 = Math.max(0.2f,
                            Math.min(1.0f, s1 + (RANDOM.nextFloat() * 0.3f - 0.15f)));

        float l1 = 0.25f + RANDOM.nextFloat() * 0.4f;
        float l2 = Math.max(0.15f,
                            Math.min(0.85f, l1 + (RANDOM.nextFloat() * 0.25f - 0.125f)));

        colorA = hslToRgb(h1, s1, l1);
        colorB = hslToRgb(h2, s2, l2);
    }
}
