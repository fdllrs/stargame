package game.factory;

import game.info.PlanetInfo;
import game.objects.Planet;
import game.objects.Star;
import org.joml.Vector3f;

import java.util.Random;

public class PlanetFactory {
    private static final Random RANDOM = new Random();

    private static final float MIN_ORBIT_SPEED = 0.001f;
    private static final float MAX_EXTRA_ORBIT_SPEED = 0.4f;
    private static final float MAX_POSITION_DISTANCE = 6000f;
    private static final float MIN_RADIUS = 10f;
    private static final float MAX_EXTRA_RADIUS = 20f;

    public Planet generatePlanet(Star homeStar) {
        PlanetInfo planetInfo = new PlanetInfo(
                homeStar,
                randomOrbitSpeed(),
                randomOrbitAngle(),
                randomRadius(),
                randomOrbitDistance(),
                randomColor(),
                randomColor()
                );

        return new Planet(planetInfo);
    }

    private float randomOrbitDistance() {
        return (RANDOM.nextFloat() * MAX_POSITION_DISTANCE) - (MAX_POSITION_DISTANCE / 2);
    }

    public Planet generatePlanet(float radius, Vector3f color, Star homeStar, float orbitDistance) {
        PlanetInfo planetInfo = new PlanetInfo(
                homeStar,
                randomOrbitSpeed(),
                randomOrbitAngle(),
                radius,
                orbitDistance,
                color,
                randomColor()
        );

        return new Planet(planetInfo);
    }

    private static float randomRadius() {
        return RANDOM.nextFloat() * MAX_EXTRA_RADIUS + MIN_RADIUS;
    }

    private static Vector3f randomColor() {
        return new Vector3f(
                RANDOM.nextFloat(),
                RANDOM.nextFloat(),
                RANDOM.nextFloat()
        );
    }

    private static float randomOrbitSpeed() {
        return (RANDOM.nextFloat() * MAX_EXTRA_ORBIT_SPEED + MIN_ORBIT_SPEED) * 0.001f;
    }

    private static float randomOrbitAngle() {
        return RANDOM.nextFloat();
    }






}
