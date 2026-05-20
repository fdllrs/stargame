package game.builder;

import game.info.PlanetInfo;
import game.objects.Planet;
import game.objects.Star;
import org.joml.Vector3f;

import java.util.Random;

public class PlanetBuilder {
    private static final Random RANDOM = new Random();

    private static final float MIN_ORBIT_SPEED = 0.001f;
    private static final float MAX_EXTRA_ORBIT_SPEED = 0.4f;
    private static final float MAX_POSITION_DISTANCE = 6000f;
    private static final float MIN_RADIUS = 10f;
    private static final float MAX_EXTRA_RADIUS = 20f;

    private final Star homeStar;
    private Float orbitSpeed = null;
    private Float orbitAngle = null;
    private Float radius = null;
    private Float orbitDistance = null;
    private Vector3f colorA = null;
    private Vector3f colorB = null;

    /**
     * The Constructor requires the absolute bare minimum data for a planet to exist.
     */
    public PlanetBuilder(Star homeStar) {
        this.homeStar = homeStar;
    }

    public PlanetBuilder withRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public PlanetBuilder withOrbitDistance(float distance) {
        this.orbitDistance = distance;
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

    // --- THE BUILD TRIGGER ---

    public Planet build() {
        // Fallback to random generation for anything the user didn't explicitly set
        float finalRadius = (this.radius != null) ? this.radius : randomRadius();
        float finalDistance = (this.orbitDistance != null) ? this.orbitDistance : randomOrbitDistance();
        float finalSpeed = (this.orbitSpeed != null) ? this.orbitSpeed : randomOrbitSpeed();
        float finalAngle = (this.orbitAngle != null) ? this.orbitAngle : randomOrbitAngle();
        Vector3f finalColorA = (this.colorA != null) ? this.colorA : randomColor();
        Vector3f finalColorB = (this.colorB != null) ? this.colorB : randomColor();

        // Pack the Data Transfer Object
        PlanetInfo info = new PlanetInfo(homeStar, finalSpeed, finalAngle, finalRadius, finalDistance, finalColorA,
                finalColorB, null);

        // Return the active entity
        return new Planet(info);
    }

    // --- PRIVATE RANDOMIZERS ---

    private float randomOrbitDistance() {
        return (RANDOM.nextFloat() * MAX_POSITION_DISTANCE) - (MAX_POSITION_DISTANCE / 2);
    }

    private float randomRadius() {
        return RANDOM.nextFloat() * MAX_EXTRA_RADIUS + MIN_RADIUS;
    }

    private Vector3f randomColor() {
        return new Vector3f(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
    }

    private float randomOrbitSpeed() {
        return (RANDOM.nextFloat() * MAX_EXTRA_ORBIT_SPEED + MIN_ORBIT_SPEED) * 0.001f;
    }

    private float randomOrbitAngle() {
        return RANDOM.nextFloat() * (float) (Math.PI * 2.0);
    }
}