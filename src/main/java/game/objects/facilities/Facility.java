package game.objects.facilities;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import game.objects.celestialBodies.Planet;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class Facility {
    protected final float efficiencyMultiplier = 1f;
    protected Vector3f localPosition;
    protected Vector3f color;
    protected Mesh mesh;

    public Facility() {
        this.localPosition = new Vector3f(0, 0, 0);
        this.color = new Vector3f(1.0f, 1.0f, 1.0f);
    }

    public abstract void tick(Planet planet);

    public void initPosition(float planetRadius) {
        // Pick a random direction vector and normalize it
        Vector3f randomDir = new Vector3f((float) (Math.random() - 0.5f),
                                          (float) (Math.random() - 0.5f),
                                          (float) (Math.random() - 0.5f)).normalize();

        // Scale by radius to place it exactly on the surface
        this.localPosition = randomDir.mul(planetRadius);
    }

    public void render(ShaderProgram shader, Matrix4f planetModelMatrix) {
        if (mesh == null)
            return;

        Vector3f surfaceNormal = new Vector3f(localPosition).normalize();
        Quaternionf alignmentRotation = new Quaternionf().rotateTo(new Vector3f(0, 1, 0),
                                                                   surfaceNormal);

        Matrix4f buildingModelMatrix = new Matrix4f(planetModelMatrix).translate(
                localPosition).rotate(alignmentRotation).scale(1.0f);

        shader.setUniform("isLightSource", 0);
        shader.setUniform("model", buildingModelMatrix);

        Matrix4f normalMatrix = new Matrix4f(buildingModelMatrix).invert().transpose();
        shader.setUniform("normalMatrix", normalMatrix);

        shader.setUniform("colorA", new org.joml.Vector3f(color));
        shader.setUniform("colorB", new org.joml.Vector3f(color).mul(0.7f));
        shader.setUniform("noiseScale", 0.0f);
        shader.setUniform("useVertexColor", mesh.hasVertexColors() ? 1 : 0);

        mesh.render();
    }
}
