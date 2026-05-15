package game.objects;

import engine.graphics.ShaderProgram;
import game.geometry.PlanetGeometry;
import org.joml.Vector3f;
import java.util.Random;

public class Planet extends GameObject {
    private static final int PLANET_RESOLUTION = 4;

    private static final Random RANDOM = new Random();

    private static final float MIN_ORBIT_SPEED = 0.001f;
    private static final float MAX_EXTRA_ORBIT_SPEED = 0.004f;
    private static final float MAX_ORBIT_ANGLE = (float) (Math.PI * 2.0);
    private static final float MAX_POSITION_DISTANCE = 3000f;
    private static final float MIN_RADIUS = 10f;
    private static final float MAX_EXTRA_RADIUS = 20f;

    private final Star homeStar;
    private final float orbitSpeed;
    private float orbitAngle;
    private float planetRadius;
    private final float orbitRadius;
    private final Vector3f colorA;
    private final Vector3f colorB;

    public float getPlanetRadius() {
        return planetRadius;
    }

    public Planet(Star homeStar) {
        this(
                randomRadius(),
                randomPositionAround(homeStar),
                randomColor(),
                homeStar
        );
    }

    public Planet(float radius, Vector3f position, Vector3f color, Star homeStar) {
        this(
                radius,
                position,
                color,
                homeStar,
                randomOrbitSpeed(),
                randomOrbitAngle()
        );
    }

    public Planet(
            float radius,
            Vector3f position,
            Vector3f color,
            Star homeStar,
            float orbitSpeed,
            float orbitAngle
    ) {
        super(PlanetGeometry.generate(PLANET_RESOLUTION, radius), color, position);
        this.homeStar = homeStar;
        this.orbitSpeed = orbitSpeed;
        this.orbitAngle = orbitAngle;
        this.orbitRadius = homeStar.getPosition().distance(position);
        this.colorA = color;
        this.colorB = randomColor();
        this.planetRadius = radius;

    }

    private static float randomRadius() {
        return RANDOM.nextFloat() * MAX_EXTRA_RADIUS + MIN_RADIUS;
    }

    private static Vector3f randomPositionAround(Star homeStar) {
        Vector3f starPosition = homeStar.getPosition();
        float x = starPosition.x + randomSignedDistance();
        float y = starPosition.y;
        float z = starPosition.z + randomSignedDistance();

        return new Vector3f(x, y, z);
    }

    private static float randomSignedDistance() {
        return RANDOM.nextFloat() * MAX_POSITION_DISTANCE * 2f - MAX_POSITION_DISTANCE;
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
        return RANDOM.nextFloat() * MAX_ORBIT_ANGLE;
    }


    public void render(ShaderProgram shader) {
        shader.setUniform("isLightSource", 0);
        shader.setUniform("model", modelMatrix);

        shader.setUniform("colorA", colorA);
        shader.setUniform("colorB", colorB);
        shader.setUniform("noiseScale", 2.0f);

        mesh.render();
    }

    public void orbit() {
        orbitAngle += orbitSpeed;
        if (orbitAngle >= MAX_ORBIT_ANGLE) orbitAngle -= MAX_ORBIT_ANGLE; // Keep it from overflowing

        modelMatrix.identity();
        modelMatrix.translate(homeStar.getPosition());
        modelMatrix.rotateY(orbitAngle);
        modelMatrix.translate(orbitRadius, 0, 0);
    }


}
