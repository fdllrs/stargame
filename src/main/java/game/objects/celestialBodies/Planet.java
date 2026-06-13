package game.objects.celestialBodies;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import game.ui.Describable;
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
import game.objects.facilities.StorageSilo;
import game.objects.facilities.generators.PowerGenerator;
import game.objects.facilities.producers.ProducerFacility;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Planet extends SpaceBody implements Describable {
	public final List<SpaceBody> satellites = new ArrayList<>();
	protected final List<ProducerFacility> producerFacilities = new ArrayList<>();
	protected final List<PowerGenerator> generators = new ArrayList<>();
	protected final Star homeStar;
	protected final PlanetInfo planetInfo;
	protected final StorageComponent planetStorage;
	private final OrbitComponent orbit;
	private boolean wasInBrownout = false;

	public abstract PlanetType getType();

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

		this.rotation.x = 15.0f;
		this.rotation.z = 5.0f;

		this.orbit = new OrbitComponent(homeStar,
										planetInfo.orbitDistance(),
										planetInfo.orbitSpeed(),
										planetInfo.initialOrbitAngle());
		this.orbit.update(this, 0f);
	}

	public void addCapacity(int capacity) {
		planetStorage.addCapacity(capacity);
	}

	public void addPowerGenerator(PowerGenerator powerGenerator) {
		generators.add(powerGenerator);
	}

	public void addProducer(ProducerFacility producerFacility) {
		producerFacilities.add(producerFacility);
	}

	public void addStorageSilo(StorageSilo storageSilo) {
		addCapacity(storageSilo.getCapacity());
	}

	@Override
	public void cleanup() {
		super.cleanup();
		for (SpaceBody orbiter : satellites) {
			orbiter.cleanup();
		}
	}

	@Override
	public void render(ShaderProgram shader) {
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

	public float distanceToStar() {
		return homeStar.getPosition().distance(getPosition());
	}

	@Override
	public String getDisplayName() {
		return "Planet: " + name;
	}

	@Override
	public List<Map.Entry<String, String>> getDisplayProperties() {
		List<Map.Entry<String, String>> props = new ArrayList<>();
		props.add(Map.entry("Home Star", planetInfo.homeStar().getName()));
		props.add(Map.entry("Radius", String.format("%.1f", planetInfo.planetRadius())));
		props.add(Map.entry("Orbit Dist.", String.format("%.1f", planetInfo.orbitDistance())));
		props.add(Map.entry("Orbit Speed", String.format("%.5f", planetInfo.orbitSpeed())));
		props.add(Map.entry("Type", getType().name()));
		props.add(Map.entry("Rings", planetInfo.hasRings() ? "Yes" : "No"));

		// Add Power Grid stats
		props.add(Map.entry("Power Grid",
							String.format("%.1f / %.1f MW (%.1f%%)",
										  getPowerDemand(),
										  getPowerCapacity(),
										  getEnergyEfficiency() * 100.0f)));

		return props;
	}

	@Override
	public void renderBody(Renderer renderer, Camera camera, Light starLight) {
		ShaderProgram planetShader = renderer.getShaderForType(getType());
		setupPlanetShader(renderer, camera, starLight, planetShader);

		render(planetShader);
		planetShader.unbind();

		if (!producerFacilities.isEmpty() || !generators.isEmpty()) {
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

	public float getEnergyEfficiency() {
		float demand = getPowerDemand();
		if (demand <= 0) {
			return 1.0f;
		}
		return Math.min(1.0f, getPowerCapacity() / demand);
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

	public float getPowerCapacity() {
		return (float) generators.stream().mapToDouble(PowerGenerator::getPowerOutput).sum();
	}

	public float getPowerDemand() {
		float producersDemand = producerFacilities.stream()
												  .mapToInt(Facility::getPowerDemand)
												  .sum();
		float generatorsDemand =
				generators.stream().mapToInt(PowerGenerator::getPowerDemand).sum();
		return producersDemand + generatorsDemand;
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
		for (Facility facility : producerFacilities) {
			facility.render(shader, this.modelMatrix);
		}
		for (PowerGenerator generator : generators) {
			generator.render(shader, this.modelMatrix);
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
		planetShader.setUniform("lightSpaceMatrix", renderer.getCurrentLightSpaceMatrix());
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
		for (PowerGenerator generator : generators) {
			generator.tick(this, 1.0f);
		}
		float efficiencyMultiplier = getEnergyEfficiency();

		if (efficiencyMultiplier < 1.0f) {
			if (!wasInBrownout) {
				System.out.println("[WARNING] Planet " + name +
								   " has entered a BROWNOUT! Energy grid efficiency is at " +
								   String.format("%.1f%%", efficiencyMultiplier * 100.0f) + ".");
				wasInBrownout = true;
			}
		}
		else {
			if (wasInBrownout) {
				System.out.println("[INFO] Planet " + name +
								   " energy grid has RECOVERED. Efficiency is back to 100.0%.");
				wasInBrownout = false;
			}
		}

		for (Facility facility : producerFacilities) {
			facility.tick(this, efficiencyMultiplier);
		}
	}
}
