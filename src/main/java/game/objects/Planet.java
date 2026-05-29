package game.objects;

import engine.graphics.ShaderProgram;
import engine.ui.Describable;
import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import org.joml.Vector3f;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class Planet extends CelestialBody implements Describable {
    private static final int PLANET_RESOLUTION = 4;

    private final Star homeStar;
    private final PlanetInfo planetInfo;

    private float orbitAngle;



    public Planet(PlanetInfo planetInfo) {
        super(PlanetGeometry.generate(PLANET_RESOLUTION, planetInfo.planetRadius()), planetInfo.colorA(), planetInfo.homeStar().getPosition().add(planetInfo.orbitDistance(), 0, 0, new Vector3f()));

        this.name = planetInfo.name();
        this.planetInfo = planetInfo;
        this.homeStar = planetInfo.homeStar();
        this.orbitAngle = planetInfo.initialOrbitAngle();

        this.colorA = planetInfo.colorA();
        this.colorB = planetInfo.colorB();
        this.radius = planetInfo.planetRadius();
    }



    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return "Planet: " + name;
    }

    @Override
    public List<Map.Entry<String, String>> getDisplayProperties() {
        return List.of(new AbstractMap.SimpleEntry<>("Home Star", planetInfo.homeStar().getName()), new AbstractMap.SimpleEntry<>("Radius", String.format("%.1f", planetInfo.planetRadius())), new AbstractMap.SimpleEntry<>("Orbit Dist.", String.format("%.1f", planetInfo.orbitDistance())), new AbstractMap.SimpleEntry<>("Orbit Speed", String.format("%.5f", planetInfo.orbitSpeed())));
    }

    @Override
    public void render(ShaderProgram shader) {
        shader.setUniform("isLightSource", 0);
        shader.setUniform("model", modelMatrix);
        shader.setUniform("normalMatrix", computeNormalMatrix());
        shader.setUniform("colorA", colorA);
        shader.setUniform("colorB", colorB);
        shader.setUniform("noiseScale", 2.0f);
        shader.setUniform("useVertexColor", 0);
        setupStencilForSelection();
        mesh.render();
    }


    public void update(float deltaTime) {
        float orbitSpeed = planetInfo.orbitSpeed();
        orbitAngle += orbitSpeed * deltaTime;

        float offsetX = (float) Math.cos(orbitAngle) * planetInfo.orbitDistance();
        float offsetZ = (float) Math.sin(orbitAngle) * planetInfo.orbitDistance();

        Vector3f starPos = homeStar.getPosition();
        this.position.set(starPos.x + offsetX, starPos.y, starPos.z + offsetZ);

        float spinSpeed = 0.5f;
        this.rotation.y += spinSpeed * deltaTime;

        rotate(deltaTime);
        updateModelMatrix();
    }
}
