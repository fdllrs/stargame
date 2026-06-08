package game.objects.celestialBodies;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.Describable;
import game.components.StorageComponent;
import game.core.Renderer;
import game.info.PlanetInfo;
import game.info.PlanetType;
import game.items.ItemType;
import game.objects.facilities.Facility;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Planet extends CelestialBody implements Describable {
    protected final Star homeStar;
    protected final PlanetInfo planetInfo;
    protected final StorageComponent planetStorage;
    protected final List<Facility> facilities;
    protected final List<Moon> moons = new ArrayList<>();
    protected float orbitAngle;

    public Planet(Mesh mesh, PlanetInfo planetInfo) {
        this(mesh, planetInfo, new StorageComponent(1000));
    }

    public Planet(Mesh mesh, PlanetInfo planetInfo, StorageComponent planetStorage) {
        super(mesh,
              planetInfo.colorA(),
              planetInfo.homeStar()
                        .getPosition()
                        .add(planetInfo.orbitDistance(), 0, 0, new Vector3f()));
        this.planetStorage = planetStorage;

        this.name = planetInfo.name();
        this.planetInfo = planetInfo;
        this.homeStar = planetInfo.homeStar();
        this.orbitAngle = planetInfo.initialOrbitAngle();
        this.colorA = planetInfo.colorA();
        this.colorB = planetInfo.colorB();
        this.radius = planetInfo.planetRadius();
        this.facilities = new ArrayList<>();

        this.rotation.x = 15.0f;
        this.rotation.z = 5.0f;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Star getHomeStar() {
        return homeStar;
    }

    public PlanetInfo getPlanetInfo() {
        return planetInfo;
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
                       Map.entry("Type", getType().name()),
                       Map.entry("Rings", planetInfo.hasRings() ? "Yes" : "No"));
    }

    public abstract PlanetType getType();

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

        for (Moon moon : moons) {
            moon.update(deltaTime);
        }
    }

    public void tickFacilities() {
        for (Facility facility : facilities) {
            facility.tick(this);
        }
    }

    public void deposit(ItemType resourceType, int amount) {
        planetStorage.deposit(resourceType, amount);
    }

    public StorageComponent getStorage() {
        return planetStorage;
    }

    public void addCapacity(int capacity) {
        planetStorage.addCapacity(capacity);
    }

    public void addFacility(Facility facility) {
        facilities.add(facility);
    }

    public List<Moon> getMoons() {
        return moons;
    }

    public void addMoon(Moon moon) {
        moons.add(moon);
    }

    @Override public void cleanup() {
        super.cleanup();
        for (Moon moon : moons) {
            moon.cleanup();
        }
    }

    @Override public void render(ShaderProgram shader) {
        shader.setUniform("isLightSource", 0);
        shader.setUniform("model", modelMatrix);
        shader.setUniform("normalMatrix", computeNormalMatrix());
        shader.setUniform("colorA", colorA);
        shader.setUniform("colorB", colorB);
        shader.setUniform("radius", radius);
        setupStencilForSelection();
        mesh.render();
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void renderFacilities(ShaderProgram shader) {
        for (Facility facility : facilities) {
            facility.render(shader, this.modelMatrix);
        }
    }

    public void renderExtra(Renderer renderer, Camera camera) {
        // Default implementation does nothing
    }

}
