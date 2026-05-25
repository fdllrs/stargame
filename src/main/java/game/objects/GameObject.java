package game.objects;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.*;

public class GameObject {

    protected final Vector3f position;
    protected final Vector3f rotation; // Stored in Euler angles (Degrees or Radians)
    protected final Vector3f scale;

    protected final Mesh mesh;
    protected final Matrix4f modelMatrix;
    protected final Vector3f color;
    protected boolean isSelected = false;

    public GameObject(Mesh mesh, Vector3f color, Vector3f position) {
        this.mesh = mesh;
        this.color = color;

        this.position = new Vector3f(position);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.modelMatrix = new Matrix4f();
        updateModelMatrix();
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
    public Vector3f getPosition() { return position; }
    public Vector3f getRotation() { return rotation; }
    public Vector3f getScale() { return scale; }

    public void setPosition(Vector3f position) {
        this.position.set(position);
        updateModelMatrix();
    }
    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
        updateModelMatrix();
    }
    public void setScale(Vector3f scale) {
        this.scale.set(scale);
        updateModelMatrix();
    }


    protected Matrix3f computeNormalMatrix() {
        return new Matrix3f(modelMatrix).invert().transpose();
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void updateModelMatrix() {
        modelMatrix.identity();
        modelMatrix.translate(position);

        // Convert degrees to radians for JOML!
        modelMatrix.rotateX((float) Math.toRadians(rotation.x));
        modelMatrix.rotateY((float) Math.toRadians(rotation.y));
        modelMatrix.rotateZ((float) Math.toRadians(rotation.z));

        modelMatrix.scale(scale);
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

}