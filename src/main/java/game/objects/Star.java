package game.objects;

import engine.graphics.ShaderProgram;
import game.geometry.PlanetGeometry;
import game.info.StarInfo;
import org.joml.Vector3f;

public class Star extends GameObject {

    private float noiseScale = 5.0f;
    private float noiseScaleOffset = 0.001f;

    private final Vector3f colorA;
    private final Vector3f colorB;
    private final String name;
    private final StarInfo starInfo;

    public Star(StarInfo info) {
        super(
                PlanetGeometry.generate(6, info.radius()),
                info.colorA(),
                new Vector3f(0, 0, 0)
        );
        this.starInfo = info;
        this.name = info.name();
        this.colorA = info.colorA();
        this.colorB = info.colorB();
    }

    public void render(ShaderProgram shader) {
        shader.setUniform("isLightSource", 1);

        // Feed the dynamic colors to the shader instead of hardcoding orange
        shader.setUniform("colorA", this.colorA);
        shader.setUniform("colorB", this.colorB);

        shader.setUniform("noiseScale", noiseScale);

        noiseScale += noiseScaleOffset;
        if (noiseScale > 8.0f || noiseScale < 4.0f) noiseScaleOffset *= -1.0f;

        shader.setUniform("model", modelMatrix);
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
}