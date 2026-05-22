package game.objects;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.*;

public class GameObject {

    protected final Mesh mesh;
    protected final Matrix4f modelMatrix;
    protected final Vector3f color;
    protected boolean isSelected = false;

    public GameObject(Mesh mesh, Vector3f color, Vector3f position) {
        this.mesh = mesh;
        this.color = color;
        this.modelMatrix = new Matrix4f().identity().translate(position);
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    /** Computes the correct normal matrix (inverse-transpose of the model's 3x3). */
    protected Matrix3f computeNormalMatrix() {
        return new Matrix3f(modelMatrix).invert().transpose();
    }

    public void render(ShaderProgram shader) {
        shader.setUniform("model", modelMatrix);
        shader.setUniform("normalMatrix", computeNormalMatrix());
        shader.setUniform("colorA", color);
        shader.setUniform("colorB", color);
        shader.setUniform("noiseScale", 0.0f);
        mesh.render();
    }

    /** Renders a selection wireframe shell around this object. Call from subclasses when isSelected. */
    protected void renderSelectionShell(ShaderProgram shader) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glLineWidth(2.0f);

        Matrix4f shellMatrix = new Matrix4f(modelMatrix).scale(1.05f);
        shader.setUniform("model", shellMatrix);
        shader.setUniform("normalMatrix", new Matrix3f(shellMatrix).invert().transpose());
        shader.setUniform("colorA", new Vector3f(0.0f, 1.0f, 1.0f));
        shader.setUniform("colorB", new Vector3f(0.0f, 1.0f, 1.0f));
        mesh.render();

        // CRITICAL: Reset OpenGL state so the rest of the scene doesn't turn into a wireframe.
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glLineWidth(1.0f);
    }

    public void cleanup() {
        mesh.cleanup();
    }

    public Vector3f getPosition() {
        return modelMatrix.getTranslation(new Vector3f());
    }
}