package game.objects;

import engine.graphics.ShaderProgram;
import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import org.joml.Vector3f;

public class Planet extends GameObject {
    private static final int PLANET_RESOLUTION = 4;
    private static final float MAX_ORBIT_ANGLE = (float) (Math.PI * 2.0);

    private Star homeStar;
    private float orbitSpeed;
    private float orbitAngle;
    private float planetRadius;
    private float orbitDistance;
    private Vector3f colorA;
    private Vector3f colorB;


    public float getPlanetRadius() {
        return planetRadius;
    }


    public Planet(PlanetInfo planetInfo) {

        super(PlanetGeometry.generate(
                PLANET_RESOLUTION,
                planetInfo.planetRadius()),
                planetInfo.colorA(),
                planetInfo.homeStar().getPosition().add(planetInfo.orbitDistance(), 0, 0));

        this.homeStar = planetInfo.homeStar();
        this.orbitSpeed = planetInfo.orbitSpeed();
        this.orbitAngle = planetInfo.initialOrbitAngle();
        this.orbitDistance = planetInfo.orbitDistance();
        this.colorA = planetInfo.colorA();
        this.colorB = planetInfo.colorB();
        this.planetRadius = planetInfo.planetRadius();

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
        modelMatrix.translate(orbitDistance, 0, 0);
    }


}
