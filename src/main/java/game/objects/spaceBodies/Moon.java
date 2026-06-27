package game.objects.spaceBodies;

import engine.ui.text.FontAtlas;
import game.components.OrbitComponent;
import game.components.StorageComponent;
import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import game.info.PlanetType;
import game.ui.panel.controller.DefaultPanelController;
import game.ui.panel.controller.InfoPanelController;

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

	@Override
	public String getName() {
		if (parentPlanet != null) {
			String parentName = parentPlanet.getName();
			if (parentName != null && !parentName.equals("null") &&
				( super.getName() == null || super.getName().startsWith("null-") )) {
				int idx = parentPlanet.getMoons().indexOf(this);
				char letter = (char) ( 'A' + ( Math.max(idx, 0) ) );
				return parentName + "-" + letter;
			}
		}
		return super.getName();
	}

	public Planet getParentPlanet() {
		return parentPlanet;
	}

	@Override
	public PlanetType getType() {
		return planetInfo.type();
	}

	@Override
	public String getDisplayName() {
		return getName();
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
	public InfoPanelController getPanelController(FontAtlas font,
			float width,
			Consumer<SpaceBody> onSelectTarget) {
		return super.getPanelController(font, width, onSelectTarget);
	}
}
