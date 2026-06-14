package game.objects.spaceBodies;

import engine.ui.panels.DefaultPanelController;
import engine.ui.panels.InfoPanelController;
import engine.ui.text.FontAtlas;
import game.components.OrbitComponent;
import game.components.StorageComponent;
import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import game.info.PlanetType;

import java.util.function.Consumer;

public class Moon extends Planet {
	private static final int MOON_RESOLUTION = 40;
	private final Planet parentPlanet;
	private final OrbitComponent orbit;

	public Moon(PlanetInfo planetInfo, Planet parentPlanet) {
		super(PlanetGeometry.generate(MOON_RESOLUTION,
									  planetInfo.planetRadius(),
									  planetInfo.type()), planetInfo);
		this.parentPlanet = parentPlanet;
		this.orbit = new OrbitComponent(parentPlanet,
										planetInfo.orbitDistance(),
										planetInfo.orbitSpeed(),
										planetInfo.initialOrbitAngle());
		this.orbit.update(this.getPosition(), 0f);
		parentPlanet.satellites.add(this);
	}

	public Planet getParentPlanet() {
		return parentPlanet;
	}

	@Override
	public PlanetType getType() {
		return planetInfo.type();
	}

	@Override
	public void update(float deltaTime) {
		orbit.update(this.getPosition(), deltaTime);

		float spinSpeed = 0.5f;
		this.rotation.y += spinSpeed * deltaTime;
		rotate(deltaTime);
		updateModelMatrix();
	}

	@Override
	public InfoPanelController getPanelController(StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Consumer<SpaceBody> onSelectTarget) {
		return new DefaultPanelController();
	}
}
