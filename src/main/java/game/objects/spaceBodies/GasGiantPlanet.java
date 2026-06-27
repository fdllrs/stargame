package game.objects.spaceBodies;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import game.core.Renderer;
import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.entities.Light;
import org.joml.Vector3f;

public class GasGiantPlanet extends Planet {
	private static final int PLANET_RESOLUTION = 80;
	private final Mesh ringMesh;
	private final Vector3f ringColor;

	public GasGiantPlanet(PlanetInfo planetInfo) {
		super(PlanetGeometry.generate(PLANET_RESOLUTION,
									  planetInfo.planetRadius(),
									  PlanetType.GAS_GIANT), planetInfo);
		if (planetInfo.hasRings()) {
			this.ringMesh = PlanetGeometry.generateRing(radius * 1.4f, radius * 2.3f, 64);
			this.ringColor = new Vector3f(colorA).lerp(colorB, 0.5f);
		}
		else {
			this.ringMesh = null;
			this.ringColor = null;
		}
	}

	@Override
	public void cleanup() {
		super.cleanup();
		if (ringMesh != null) {
			ringMesh.cleanup();
		}
	}

	@Override
	public PlanetType getType() {
		return PlanetType.GAS_GIANT;
	}

	@Override
	public void renderExtra(Renderer renderer, Camera camera) {
		if (ringMesh != null) {
			ShaderProgram shaderRing = renderer.getShaderRing();
			shaderRing.bind();

			shaderRing.setUniform("view", camera.getViewMatrix());
			shaderRing.setUniform("projection", camera.getProjectionMatrix());
			shaderRing.setUniform("model", modelMatrix);
			shaderRing.setUniform("normalMatrix", computeNormalMatrix());

			Light starLight = homeStar.getLight();
			shaderRing.setUniform("lightPosition", starLight.getPosition());
			shaderRing.setUniform("lightColor", starLight.getColor());

			shaderRing.setUniform("colorA", ringColor);
			shaderRing.setUniform("colorB", ringColor);
			shaderRing.setUniform("radius", radius);

			ringMesh.render();
			shaderRing.unbind();
		}
	}
}
