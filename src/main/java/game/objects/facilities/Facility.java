package game.objects.facilities;

import engine.graphics.ShaderProgram;
import game.objects.GameObject;
import game.objects.spaceBodies.Planet;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Facility extends GameObject {
	protected Vector3f localPosition;
	protected final Vector3f surfaceNormal = new Vector3f();
	protected final Quaternionf alignmentRotation = new Quaternionf();
	private final Matrix4f buildingModelMatrix = new Matrix4f();
	private final Matrix3f normalMatrix = new Matrix3f();
	private final Vector3f colorA = new Vector3f();
	private final Vector3f colorB = new Vector3f();
	private static final Vector3f UP_VECTOR = new Vector3f(0.0f, 1.0f, 0.0f);
	protected float efficiency = 1.0f;
	protected float progressAccumulator = 0.0f;
	protected int level = 1;

	public abstract int getPowerDemand();

	public abstract void tick(Planet planet, float resourceMultiplier);

	public abstract void upgrade();

	public Facility(Planet planet) {
		super(planet.getPosition());
		this.localPosition = new Vector3f(0, 0, 0);
		this.color = new Vector3f(1.0f, 1.0f, 1.0f);
		this.initPosition(planet);
	}

	@Override
	public void cleanup() {
		// Do not clean up the shared static mesh
	}

	public void initPosition(Planet planet) {
		Vector3f randomDir = new Vector3f();
		int attempts = 0;
		do {
			randomDir.set((float) ( Math.random() - 0.5f ),
						  (float) ( Math.random() - 0.5f ),
						  (float) ( Math.random() - 0.5f )).normalize();
			attempts++;
		} while (planet.isWater(randomDir) && attempts < 500);

		float height = planet.getTerrainHeight(randomDir);
		this.localPosition = randomDir.mul(planet.getRadius() + height - 0.1f);

		surfaceNormal.set(localPosition).normalize();
		alignmentRotation.identity().rotateTo(UP_VECTOR, surfaceNormal);
	}

	public void render(ShaderProgram shader, Matrix4f planetModelMatrix) {
		if (mesh == null) return;

		buildingModelMatrix.set(planetModelMatrix).translate(localPosition)
												  .rotate(alignmentRotation);

		shader.setUniform("isLightSource", 0);
		shader.setUniform("model", buildingModelMatrix);

		normalMatrix.set(buildingModelMatrix).invert().transpose();
		shader.setUniform("normalMatrix", normalMatrix);

		colorA.set(color);
		colorB.set(color).mul(0.7f);
		shader.setUniform("colorA", colorA);
		shader.setUniform("colorB", colorB);
		shader.setUniform("noiseScale", 0.0f);
		shader.setUniform("useVertexColor", mesh.hasVertexColors() ? 1 : 0);

		mesh.render(shader);
	}

	protected void setLevel(int level) {
		this.level = level;
	}

	public void update(float deltaTime) {
		// Can be overridden by subclasses to update animations, orbits, or state every frame
	}
}
