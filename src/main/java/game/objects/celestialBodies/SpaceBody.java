package game.objects.celestialBodies;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.Describable;
import game.core.Renderer;
import game.objects.GameObject;
import game.objects.entities.Light;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

public class SpaceBody extends GameObject implements Describable {
    protected String name;
    protected Vector3f colorA;
    protected Vector3f colorB;
    protected float radius;

    public SpaceBody(Mesh mesh, Vector3f color, Vector3f position) {
        super(mesh, color, position);
    }

    @Override public String getDisplayName() {
        return name;
    }

    @Override public List<Map.Entry<String, String>> getDisplayProperties() {
        return List.of();
    }

    public String getName() {
        return name;
    }

    public float getRadius() {
        return radius;
    }

    /**
     * Renders this space body using the default 3D shader. Subclasses can override for
     * custom shaders.
     */
    public void renderBody(Renderer renderer, Camera camera, Light starLight) {
        ShaderProgram shader = setupDefaultShader(renderer, camera, starLight);
        render(shader);
        shader.unbind();
    }

    public void rotate(float deltaTime) {
        float spinSpeedMultiplier = 0.1f;
        this.rotation.y += (float) Math.toDegrees(deltaTime * spinSpeedMultiplier);
    }

    @NotNull
    protected static ShaderProgram setupDefaultShader(Renderer renderer,
                                                      Camera camera,
                                                      Light starLight) {
        ShaderProgram shader = renderer.getDefaultShader();
        Planet.setupPlanetShader(renderer, camera, starLight, shader);
        return shader;
    }

    public void update(float deltaTime) {
        // Override in subclasses to perform updates
    }
}
