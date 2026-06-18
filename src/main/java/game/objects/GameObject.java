package game.objects;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import game.objects.entities.GameEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.*;

public class GameObject extends GameEntity {
	protected final Vector3f rotation; // Stored in Euler angles (Degrees or Radians)
	protected final Vector3f scale;
	protected final Matrix4f modelMatrix;
	protected Mesh mesh;
	protected boolean isSelected = false;

	public GameObject(Mesh mesh, Vector3f color, Vector3f position) {
		super(position, color);
		this.mesh = mesh;

		this.rotation = new Vector3f(0, 0, 0);
		this.scale = new Vector3f(1, 1, 1);
		this.modelMatrix = new Matrix4f();
		updateModelMatrix();
	}

	public GameObject(Vector3f position) {
		this(null, new Vector3f(1, 1, 1), position);
	}

	public GameObject(Mesh mesh, Vector3f position) {
		this(mesh, new Vector3f(1, 1, 1), position);
	}

	@Override
	public void cleanup() {
		mesh.cleanup();
	}

	@Override
	public void render(ShaderProgram shader) {
		shader.setUniform("model", modelMatrix);
		shader.setUniform("normalMatrix", computeNormalMatrix());
		shader.setUniform("colorA", color);
		shader.setUniform("colorB", color);
		shader.setUniform("noiseScale", 0.0f);
		shader.setUniform("useVertexColor", 1);
		shader.setUniform("isLightSource", 0);
		setupStencilForSelection();
		mesh.render(shader);
	}

	protected Matrix3f computeNormalMatrix() {
		return new Matrix3f(modelMatrix).invert().transpose();
	}

	public Mesh getMesh() {
		return mesh;
	}

	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public Vector3f getPosition() { return position; }

	public Vector3f getRotation() { return rotation; }

	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}

	protected void setupStencilForSelection() {
		if (isSelected) {
			glStencilFunc(GL_ALWAYS, 1, 0xFF);
			glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
		}
		else {
			glStencilFunc(GL_ALWAYS, 0, 0xFF);
			glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
		}
	}

	public void updateModelMatrix() {
		modelMatrix.identity();
		modelMatrix.translate(position);

		modelMatrix.rotateY((float) Math.toRadians(rotation.y));
		modelMatrix.rotateX((float) Math.toRadians(rotation.x));
		modelMatrix.rotateZ((float) Math.toRadians(rotation.z));

		modelMatrix.scale(scale);
	}
}