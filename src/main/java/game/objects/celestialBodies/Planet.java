package game.objects.celestialBodies;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.Describable;
import engine.ui.panels.InfoPanelController;
import engine.ui.panels.PlanetPanelController;
import engine.ui.text.FontAtlas;
import game.components.OrbitComponent;
import game.components.StorageComponent;
import game.core.Renderer;
import game.info.PlanetInfo;
import game.info.PlanetType;
import game.items.ItemType;
import game.objects.entities.Light;
import game.objects.facilities.Facility;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Planet extends SpaceBody implements Describable {
    public final List<SpaceBody> satellites = new ArrayList<>();
    protected final Star homeStar;
    protected final PlanetInfo planetInfo;
    protected final StorageComponent planetStorage;
    protected final List<Facility> facilities;
    private final OrbitComponent orbit;

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
        this.colorA = planetInfo.colorA();
        this.colorB = planetInfo.colorB();
        this.radius = planetInfo.planetRadius();
        this.facilities = new ArrayList<>();

        this.rotation.x = 15.0f;
        this.rotation.z = 5.0f;

        this.orbit = new OrbitComponent(homeStar,
                                        planetInfo.orbitDistance(),
                                        planetInfo.orbitSpeed(),
                                        planetInfo.initialOrbitAngle());
        this.orbit.update(this, 0f);
    }

    public abstract PlanetType getType();

    public void addCapacity(int capacity) {
        planetStorage.addCapacity(capacity);
    }

    public void addFacility(Facility facility) {
        facilities.add(facility);
    }

    @Override public void cleanup() {
        super.cleanup();
        for (SpaceBody orbiter : satellites) {
            orbiter.cleanup();
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

    public void deposit(ItemType resourceType, int amount) {
        planetStorage.deposit(resourceType, amount);
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

    @Override public void renderBody(Renderer renderer, Camera camera, Light starLight) {
        ShaderProgram planetShader = renderer.getShaderForType(getType());
        setupPlanetShader(renderer, camera, starLight, planetShader);

        render(planetShader);
        planetShader.unbind();

        if (!facilities.isEmpty()) {
            ShaderProgram defaultShader = setupDefaultShader(renderer, camera, starLight);

            renderFacilities(defaultShader);
            defaultShader.unbind();
        }

        renderExtra(renderer, camera);
    }

    public void update(float deltaTime) {
        orbit.update(this, deltaTime);

        float spinSpeed = 0.5f;
        this.rotation.y += spinSpeed * deltaTime;

        rotate(deltaTime);
        updateModelMatrix();

        for (SpaceBody orbiter : satellites) {
            orbiter.update(deltaTime);
        }
    }

    public Star getHomeStar() {
        return homeStar;
    }

    public Hub getHub() {
        for (SpaceBody orbiter : satellites) {
            if (orbiter instanceof Hub hub) {
                return hub;
            }
        }
        return null;
    }

    public List<Moon> getMoons() {
        List<Moon> moons = new ArrayList<>();
        for (SpaceBody orbiter : satellites) {
            if (orbiter instanceof Moon moon) {
                moons.add(moon);
            }
        }
        return moons;
    }

    @Override
    public InfoPanelController getPanelController(StorageComponent playerStorage,
                                                  FontAtlas font,
                                                  float width,
                                                  Runnable onRebuild,
                                                  Consumer<SpaceBody> onSelectTarget) {
        return new PlanetPanelController(this,
                                         playerStorage,
                                         font,
                                         width,
                                         onRebuild,
                                         onSelectTarget);
    }

    public PlanetInfo getPlanetInfo() {
        return planetInfo;
    }

    public StorageComponent getStorage() {
        return planetStorage;
    }

    public boolean hasHub() {
        return getHub() != null;
    }

    public void renderExtra(Renderer renderer, Camera camera) {
        // Default implementation does nothing
    }

    public void renderFacilities(ShaderProgram shader) {
        for (Facility facility : facilities) {
            facility.render(shader, this.modelMatrix);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    static void setupPlanetShader(Renderer renderer,
                                  Camera camera,
                                  Light starLight,
                                  ShaderProgram planetShader) {
        planetShader.bind();
        planetShader.setUniform("view", camera.getViewMatrix());
        planetShader.setUniform("projection", camera.getProjectionMatrix());
        planetShader.setUniform("viewPos", camera.getPosition());
        planetShader.setUniform("lightSpaceMatrix",
                                renderer.getCurrentLightSpaceMatrix());
        if (starLight != null) {
            planetShader.setUniform("lightPosition", starLight.getPosition());
            planetShader.setUniform("lightColor", starLight.getColor());
        }

        org.lwjgl.opengl.GL13C.glActiveTexture(org.lwjgl.opengl.GL13C.GL_TEXTURE1);
        org.lwjgl.opengl.GL11C.glBindTexture(org.lwjgl.opengl.GL11C.GL_TEXTURE_2D,
                                             renderer.getShadowDepthTex());
        planetShader.setUniform("shadowMap", 1);
        org.lwjgl.opengl.GL13C.glActiveTexture(org.lwjgl.opengl.GL13C.GL_TEXTURE0);
    }

    public void tickFacilities() {
        for (Facility facility : facilities) {
            facility.tick(this);
        }
    }
}
