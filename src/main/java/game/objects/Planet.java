package game.objects;

import engine.graphics.ShaderProgram;
import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import org.joml.Vector3f;

public class Planet extends CelestialBody {
    private static final int PLANET_RESOLUTION = 4;
    private static final float MAX_ORBIT_ANGLE = (float) (Math.PI * 2.0);


    private String name;
    private Star homeStar;
    private PlanetInfo planetInfo;

    private Vector3f colorA;
    private Vector3f colorB;
    private float orbitAngle;


    public float getPlanetRadius() {
        return planetInfo.planetRadius();
    }


    public Planet(PlanetInfo planetInfo) {
        super(PlanetGeometry.generate(
                PLANET_RESOLUTION,
                planetInfo.planetRadius()),
                planetInfo.colorA(),
                planetInfo.homeStar().getPosition().add(planetInfo.orbitDistance(), 0, 0));

        this.name = planetInfo.name();
        this.planetInfo = planetInfo;
        this.homeStar = planetInfo.homeStar();
        this.orbitAngle = planetInfo.initialOrbitAngle();
        this.colorA = planetInfo.colorA();
        this.colorB = planetInfo.colorB();

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public PlanetInfo getPlanetInfo() {
        return planetInfo;
    }

    public void render(ShaderProgram shader) {
        super.render(shader);

        shader.setUniform("isLightSource", 0);
        shader.setUniform("model", modelMatrix);
        shader.setUniform("colorA", colorA);
        shader.setUniform("colorB", colorB);
        shader.setUniform("noiseScale", 2.0f);
        mesh.render();

    }

    public void orbit() {
        float orbitDistance = planetInfo.orbitDistance();
        float orbitSpeed = planetInfo.orbitSpeed();

        orbitAngle += orbitSpeed;
        if (orbitAngle >= MAX_ORBIT_ANGLE) orbitAngle -= MAX_ORBIT_ANGLE; // Keep it from overflowing

        modelMatrix.identity();
        modelMatrix.translate(homeStar.getPosition());
        modelMatrix.rotateY(orbitAngle);
        modelMatrix.translate(orbitDistance, 0, 0);
    }


}
