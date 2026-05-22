package game.objects;

import engine.graphics.ShaderProgram;
import engine.ui.Describable;
import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import org.joml.Vector3f;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class Planet extends GameObject implements Describable {
    private static final int   PLANET_RESOLUTION = 4;
    private static final float MAX_ORBIT_ANGLE   = (float) (Math.PI * 2.0);

    private String name;
    private final Star     homeStar;
    private final PlanetInfo planetInfo;

    private final Vector3f colorA;
    private final Vector3f colorB;
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

        this.name       = planetInfo.name();
        this.planetInfo = planetInfo;
        this.homeStar   = planetInfo.homeStar();
        this.orbitAngle = planetInfo.initialOrbitAngle();
        this.colorA     = planetInfo.colorA();
        this.colorB     = planetInfo.colorB();
    }

    public void setName(String name) { this.name = name; }
    public String getName()          { return name; }
    public PlanetInfo getPlanetInfo(){ return planetInfo; }

    @Override
    public String getDisplayName() {
        return "Planet: " + name;
    }

    @Override
    public List<Map.Entry<String, String>> getDisplayProperties() {
        return List.of(
                new AbstractMap.SimpleEntry<>("Home Star",    planetInfo.homeStar().getName()),
                new AbstractMap.SimpleEntry<>("Radius",       String.format("%.1f", planetInfo.planetRadius())),
                new AbstractMap.SimpleEntry<>("Orbit Dist.",  String.format("%.1f", planetInfo.orbitDistance())),
                new AbstractMap.SimpleEntry<>("Orbit Speed",  String.format("%.5f", planetInfo.orbitSpeed()))
        );
    }

    @Override
    public void render(ShaderProgram shader) {
        shader.setUniform("isLightSource", 0);
        shader.setUniform("model", modelMatrix);
        shader.setUniform("normalMatrix", computeNormalMatrix());
        shader.setUniform("colorA", colorA);
        shader.setUniform("colorB", colorB);
        shader.setUniform("noiseScale", 2.0f);
        mesh.render();

        if (isSelected) {
            renderSelectionShell(shader);
        }
    }

    /** Advances the orbital position by the given time delta. */
    public void orbit(float deltaTime) {
        float orbitDistance = planetInfo.orbitDistance();
        float orbitSpeed    = planetInfo.orbitSpeed();

        orbitAngle += orbitSpeed * deltaTime;
        if (orbitAngle >= MAX_ORBIT_ANGLE) orbitAngle -= MAX_ORBIT_ANGLE;

        modelMatrix.identity();
        modelMatrix.translate(homeStar.getPosition());
        modelMatrix.rotateY(orbitAngle);
        modelMatrix.translate(orbitDistance, 0, 0);
    }
}
