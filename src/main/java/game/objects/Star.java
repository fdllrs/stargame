package game.objects;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import game.geometry.PlanetGeometry;
import org.joml.Vector3f;

public class Star extends GameObject{

    private float noiseScale = 5.0f;
    private float noiseScaleOffset = 0.001f;
    public Star(float radius, Vector3f position, Vector3f color) {
        Mesh planetMesh = PlanetGeometry.generate(6, radius);
        super(planetMesh, color, position);
    }

    public void render(ShaderProgram shader) {
        shader.setUniform("isLightSource", 1);
        shader.setUniform("colorA", new Vector3f(0.9f, 0.2f, 0.0f));
        shader.setUniform("colorB", new Vector3f(1.0f, 0.8f, 0.0f));
        shader.setUniform("noiseScale", noiseScale);

        noiseScale += noiseScaleOffset;
        if (noiseScale > 8.0f || noiseScale < 4.0f) noiseScaleOffset *= -1.0f;
        shader.setUniform("model", modelMatrix);
        mesh.render();
    }

}
