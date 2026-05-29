package game.objects;

import engine.graphics.ShaderProgram;
import engine.ui.Describable;
import game.geometry.PlanetGeometry;
import game.info.StarInfo;
import org.joml.Vector3f;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class Star extends CelestialBody implements Describable {

    private static final float NOISE_SCALE_MIN = 4.0f;
    private static final float NOISE_SCALE_MAX = 8.0f;
    private static final float NOISE_PULSE_SPEED = 0.01f; // full oscillations per second

    private final StarInfo starInfo;
    private float elapsedTime = 0f;

    public Star(StarInfo info) {
        super(PlanetGeometry.generate(6, info.radius()), info.colorA(), new Vector3f(0, 0, 0));
        this.starInfo = info;
        this.name = info.name();
        this.colorA = info.colorA();
        this.colorB = info.colorB();
        this.radius = info.radius();
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        this.rotate(deltaTime);
        updateModelMatrix();
    }

    @Override
    public void render(ShaderProgram shader) {
        // Noise scale oscillates smoothly between MIN and MAX using sin(), independent of frame rate.
        float t = (float) Math.sin(elapsedTime * NOISE_PULSE_SPEED * Math.PI);
        float noiseScale = NOISE_SCALE_MIN + (t * 0.5f + 0.5f) * (NOISE_SCALE_MAX - NOISE_SCALE_MIN);

        shader.setUniform("isLightSource", 1);
        shader.setUniform("colorA", colorA);
        shader.setUniform("colorB", colorB);
        shader.setUniform("noiseScale", noiseScale);
        shader.setUniform("useVertexColor", 0);
        shader.setUniform("model", modelMatrix);
        shader.setUniform("normalMatrix", computeNormalMatrix());
        setupStencilForSelection();
        mesh.render();
    }

    public float getRadius() {
        return starInfo.radius();
    }

    public String getName() {
        return name;
    }

    public StarInfo getStarInfo() {
        return starInfo;
    }

    @Override
    public String getDisplayName() {
        return "Star: " + name;
    }

    @Override
    public List<Map.Entry<String, String>> getDisplayProperties() {
        return List.of(new AbstractMap.SimpleEntry<>("Type", starInfo.type().name()), new AbstractMap.SimpleEntry<>("Radius", String.format("%.1f", starInfo.radius())), new AbstractMap.SimpleEntry<>("Mass", String.format("%.1f", starInfo.mass())));
    }
}