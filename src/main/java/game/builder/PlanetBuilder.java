package game.builder;

import game.info.PlanetType;
import game.objects.celestialBodies.Planet;
import game.objects.celestialBodies.Star;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Random;

public abstract class PlanetBuilder {
    protected static final Random RANDOM = new Random();
    protected static final float MIN_ORBIT_SPEED = 0.01f;
    protected static final float MAX_EXTRA_ORBIT_SPEED = 0.4f;
    protected static final float MIN_RADIUS = 10f;
    protected static final float MAX_EXTRA_RADIUS = 20f;
    protected static final float ORBIT_DISTANCE_PADDING = 50f;
    protected static final float MAX_ORBIT_DISTANCE = 12000f;
    protected final Star homeStar;
    protected Float orbitSpeed = null;
    protected Float orbitAngle = null;
    protected Float radius = null;
    protected Float orbitDistance = null;
    protected Vector3f colorA = null;
    protected Vector3f colorB = null;

    public PlanetBuilder(Star homeStar) {
        this.homeStar = homeStar;
    }

    public abstract Planet build();

    public PlanetBuilder withRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public PlanetBuilder withOrbitSpeed(float speed) {
        this.orbitSpeed = speed;
        return this;
    }

    public PlanetBuilder withOrbitAngle(float angle) {
        this.orbitAngle = angle;
        return this;
    }

    public PlanetBuilder withColors(Vector3f colorA, Vector3f colorB) {
        this.colorA = colorA;
        this.colorB = colorB;
        return this;
    }

    public static PlanetBuilder create(Star homeStar, PlanetType type) {
        return switch (type) {
            case ROCKY -> new RockyPlanetBuilder(homeStar);
            case GAS_GIANT -> new GasGiantPlanetBuilder(homeStar);
            case ICE_GIANT -> new IceGiantPlanetBuilder(homeStar);
            case ORGANIC -> new OrganicPlanetBuilder(homeStar);
        };
    }

    public static PlanetBuilder createRandom(Star homeStar) {
        float minDistance = homeStar.getRadius() + MAX_EXTRA_RADIUS +
                            ORBIT_DISTANCE_PADDING;
        float distance = minDistance +
                         RANDOM.nextFloat() * (MAX_ORBIT_DISTANCE - minDistance);

        PlanetBuilder builder;
        if (distance <= MAX_ORBIT_DISTANCE / 2.5f) {
            builder = RANDOM.nextBoolean()
                      ? new RockyPlanetBuilder(homeStar)
                      : new OrganicPlanetBuilder(homeStar);
        } else {
            builder = RANDOM.nextBoolean()
                      ? new GasGiantPlanetBuilder(homeStar)
                      : new IceGiantPlanetBuilder(homeStar);
        }
        builder.withOrbitDistance(distance);
        return builder;
    }

    public PlanetBuilder withOrbitDistance(float distance) {
        this.orbitDistance = distance;
        return this;
    }

    @NotNull protected basicPlanetInfo buildBasicPlanetInfo() {
        float finalDistance = (this.orbitDistance != null)
                              ? this.orbitDistance
                              : randomOrbitDistance();
        float finalSpeed = (this.orbitSpeed != null)
                           ? this.orbitSpeed
                           : randomOrbitSpeed();
        float finalAngle = (this.orbitAngle != null)
                           ? this.orbitAngle
                           : randomOrbitAngle();

        if (this.colorA == null || this.colorB == null) {
            generateColors();
        }

        Vector3f finalColorA = (this.colorA != null) ? this.colorA : randomColor();
        Vector3f finalColorB = (this.colorB != null) ? this.colorB : randomColor();
        return new basicPlanetInfo(finalDistance,
                                   finalSpeed,
                                   finalAngle,
                                   finalColorA,
                                   finalColorB);
    }

    protected float randomOrbitDistance() {
        float minDistance = homeStar.getRadius() + MAX_EXTRA_RADIUS +
                            ORBIT_DISTANCE_PADDING;
        return minDistance + RANDOM.nextFloat() * (MAX_ORBIT_DISTANCE - minDistance);
    }

    abstract protected void generateColors();

    protected Vector3f randomColor() {
        return new Vector3f(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
    }

    protected float randomOrbitSpeed() {
        return (RANDOM.nextFloat() * MAX_EXTRA_ORBIT_SPEED + MIN_ORBIT_SPEED) * 0.001f;
    }

    protected float randomOrbitAngle() {
        return RANDOM.nextFloat() * (float) (Math.PI * 2.0);
    }

    protected float randomRadius() {
        return RANDOM.nextFloat() * MAX_EXTRA_RADIUS + MIN_RADIUS;
    }

    protected record basicPlanetInfo(float finalDistance,
                                     float finalSpeed,
                                     float finalAngle,
                                     Vector3f finalColorA,
                                     Vector3f finalColorB) {}
}