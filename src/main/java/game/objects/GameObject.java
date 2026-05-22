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

    public Mesh getMesh() {
        return mesh;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    protected void setupStencilForSelection() {
        if (isSelected) {
            glStencilFunc(GL_ALWAYS, 1, 0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        } else {
            glStencilFunc(GL_ALWAYS, 0, 0xFF);
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        }
    }

    public void render(ShaderProgram shader) {
        shader.setUniform("model", modelMatrix);
        shader.setUniform("normalMatrix", computeNormalMatrix());
        shader.setUniform("colorA", color);
        shader.setUniform("colorB", color);
        shader.setUniform("noiseScale", 0.0f);
        setupStencilForSelection();
        mesh.render();
    }

    public void cleanup() {
        mesh.cleanup();
    }

    public Vector3f getPosition() {
        return modelMatrix.getTranslation(new Vector3f());
    }
}