package game.objects.celestialBodies;

import engine.graphics.ShaderProgram;
import game.ui.Describable;
import game.geometry.PlanetGeometry;
import game.info.StarInfo;
import game.objects.entities.Light;
import org.joml.Vector3f;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class Star extends SpaceBody implements Describable {
    private static final float NOISE_SCALE_MIN = 4.0f;
    private static final float NOISE_SCALE_MAX = 8.0f;
    private static final float NOISE_PULSE_SPEED = 0.01f; // full oscillations per second
    private final StarInfo starInfo;
    private final Light light;
    private float elapsedTime = 0f;

    public Star(StarInfo info) {
        super(PlanetGeometry.generate(32, info.radius(), null),
              info.colorA(),
              new Vector3f(0, 0, 0));
        this.starInfo = info;
        this.name = info.name();
        this.colorA = info.colorA();
        this.colorB = info.colorB();
        this.radius = info.radius();
        Vector3f lightColor = new Vector3f(0.7f, 0.7f, 0.7f).add(colorA.mul(0.3f,
                                                                            new Vector3f()));
        this.light = new Light(new Vector3f(0, 0, 0), lightColor);
    }

    @Override public String getDisplayName() {
        return "Star: " + name;
    }

    @Override public List<Map.Entry<String, String>> getDisplayProperties() {
        return List.of(new AbstractMap.SimpleEntry<>("Type", starInfo.type().name()),
                       new AbstractMap.SimpleEntry<>("Radius",
                                                     String.format("%.1f",
                                                                   starInfo.radius())),
                       new AbstractMap.SimpleEntry<>("Mass",
                                                     String.format("%.1f",
                                                                   starInfo.mass())));
    }

    public float getRadius() {
        return starInfo.radius();
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        this.rotate(deltaTime);
        updateModelMatrix();
    }

    public Light getLight() {
        return light;
    }

    @Override public void render(ShaderProgram shader) {
        float t = (float) Math.sin(elapsedTime * NOISE_PULSE_SPEED * Math.PI);
        float noiseScale = NOISE_SCALE_MIN +
                           (t * 0.5f + 0.5f) * (NOISE_SCALE_MAX - NOISE_SCALE_MIN);

        // Pass standard uniforms
        shader.setUniform("colorA", colorA);
        shader.setUniform("colorB", colorB);
        shader.setUniform("noiseScale", noiseScale);
        shader.setUniform("time", elapsedTime);

        // 1. Render the main solid star body
        shader.setUniform("isGlowShell", 0);
        shader.setUniform("model", modelMatrix);
        setupStencilForSelection();
        mesh.render();

        // 2. Render the outer glow/corona shell
        // Scale model matrix to make a nice volumetric halo
        org.joml.Matrix4f glowModelMatrix = new org.joml.Matrix4f(modelMatrix);
        glowModelMatrix.scale(1.1f);

        shader.setUniform("isGlowShell", 1);
        shader.setUniform("model", glowModelMatrix);

        // Configure OpenGL for transparent additive blending
        org.lwjgl.opengl.GL11C.glDepthMask(false);
        org.lwjgl.opengl.GL11C.glStencilMask(0x00);
        org.lwjgl.opengl.GL11C.glEnable(org.lwjgl.opengl.GL11C.GL_BLEND);
        org.lwjgl.opengl.GL11C.glBlendFunc(org.lwjgl.opengl.GL11C.GL_SRC_ALPHA,
                                           org.lwjgl.opengl.GL11C.GL_ONE);

        mesh.render();

        // Restore OpenGL state
        org.lwjgl.opengl.GL11C.glDepthMask(true);
        org.lwjgl.opengl.GL11C.glStencilMask(0xFF);
        org.lwjgl.opengl.GL11C.glDisable(org.lwjgl.opengl.GL11C.GL_BLEND);
    }
}