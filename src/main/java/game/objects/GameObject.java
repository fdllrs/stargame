package game.objects;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class GameObject {

    protected final Mesh mesh;
    protected final Matrix4f modelMatrix;
    protected final Vector3f color;

    public GameObject(Mesh mesh, Vector3f color, Vector3f position) {
        this.mesh = mesh;
        this.color = color;
        this.modelMatrix = new Matrix4f().identity().translate(position);
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    public void render(ShaderProgram shader) {
        shader.setUniform("model", modelMatrix);
        shader.setUniform("objectColor", color);
        mesh.render();
    }

    public void cleanup() {
        mesh.cleanup();
    }
}