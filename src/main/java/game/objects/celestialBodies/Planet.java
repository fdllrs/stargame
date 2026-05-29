package game.objects.celestialBodies;

import engine.graphics.ShaderProgram;
import engine.ui.Describable;
import game.components.StorageComponent;
import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import game.info.PlanetType;
import game.items.ItemType;
import game.objects.facilities.Facility;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Planet extends CelestialBody implements Describable {
    private static final int PLANET_RESOLUTION = 4;
    private final Star homeStar;
    private final PlanetInfo planetInfo;
    private final StorageComponent storageComponent;
    List<Facility> facilities;
    private float orbitAngle;

    public Planet(PlanetInfo planetInfo) {
        this(planetInfo, new StorageComponent(1000));
    }

    public Planet(PlanetInfo planetInfo, StorageComponent storageComponent) {
        super(PlanetGeometry.generate(PLANET_RESOLUTION, planetInfo.planetRadius()),
              planetInfo.colorA(),
              planetInfo.homeStar()
                        .getPosition()
                        .add(planetInfo.orbitDistance(), 0, 0, new Vector3f()));
        this.storageComponent = storageComponent;

        this.name = planetInfo.name();
        this.planetInfo = planetInfo;
        this.homeStar = planetInfo.homeStar();
        this.orbitAngle = planetInfo.initialOrbitAngle();
        this.colorA = planetInfo.colorA();
        this.colorB = planetInfo.colorB();
        this.radius = planetInfo.planetRadius();
        this.facilities = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public String getDisplayName() {
        return "Planet: " + name;
    }

    @Override public List<Map.Entry<String, String>> getDisplayProperties() {
        return List.of(Map.entry("Home Star", planetInfo.homeStar().getName()),
                       Map.entry("Radius",
                                 String.format("%.1f", planetInfo.planetRadius())),
                       Map.entry("Orbit Dist.",
                                 String.format("%.1f", planetInfo.orbitDistance())),
                       Map.entry("Orbit Speed",
                                 String.format("%.5f", planetInfo.orbitSpeed())),
                       Map.entry("Type", planetInfo.type().name()),
                       Map.entry("Storage", storageComponent.getFillForDisplay()));
    }

    @Override public void render(ShaderProgram shader) {
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

        tickFacilities(deltaTime);
    }

    private void tickFacilities(float deltaTime) {
        for (Facility facility : facilities) {
            facility.tick(this, deltaTime);
        }
    }

    public boolean deposit(ItemType resourceType, int amount) {
        return storageComponent.deposit(resourceType, amount);
    }

    public boolean withdraw(ItemType resourceType, int amount) {
        return storageComponent.withdraw(resourceType, amount);
    }

    public StorageComponent getStorage() {
        return storageComponent;
    }

    public void addCapacity(int capacity) {
        storageComponent.addCapacity(capacity);
    }

    public PlanetType getType() {
        return planetInfo.type();
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

}
