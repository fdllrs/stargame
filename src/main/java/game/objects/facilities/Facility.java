package game.objects.facilities;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import game.objects.celestialBodies.Planet;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Facility {
	protected Vector3f localPosition;
	protected float efficiency = 1.0f;
	protected float progressAccumulator = 0.0f;
	protected Vector3f color;
	protected Mesh mesh;
	protected int level = 1;

	public abstract int getPowerDemand();

	public abstract void tick(Planet planet, float resourceMultiplier);

	public abstract void upgrade();

	public Facility(Planet planet) {
		this.localPosition = new Vector3f(0, 0, 0);
		this.color = new Vector3f(1.0f, 1.0f, 1.0f);
		this.initPosition(planet.getRadius());
	}

	public void initPosition(float planetRadius) {
		Vector3f randomDir = new Vector3f((float) ( Math.random() - 0.5f ),
										  (float) ( Math.random() - 0.5f ),
										  (float) ( Math.random() - 0.5f )).normalize();

		this.localPosition = randomDir.mul(planetRadius - 0.1f);
	}

	public void render(ShaderProgram shader, Matrix4f planetModelMatrix) {
		if (mesh == null) return;

		Vector3f surfaceNormal = new Vector3f(localPosition).normalize();
		Quaternionf alignmentRotation = new Quaternionf().rotateTo(new Vector3f(0, 1, 0),
																   surfaceNormal);

		Matrix4f buildingModelMatrix = new Matrix4f(planetModelMatrix).translate(localPosition)
																	  .rotate(alignmentRotation)
																	  .scale(1.0f);

		shader.setUniform("isLightSource", 0);
		shader.setUniform("model", buildingModelMatrix);

		Matrix3f normalMatrix = new Matrix3f(buildingModelMatrix).invert().transpose();
		shader.setUniform("normalMatrix", normalMatrix);

		shader.setUniform("colorA", new org.joml.Vector3f(color));
		shader.setUniform("colorB", new org.joml.Vector3f(color).mul(0.7f));
		shader.setUniform("noiseScale", 0.0f);
		shader.setUniform("useVertexColor", mesh.hasVertexColors() ? 1 : 0);

		mesh.render(shader);
	}

	protected void setLevel(int level) {
		this.level = level;
	}
}
