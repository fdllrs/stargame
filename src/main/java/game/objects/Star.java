package game.objects;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import game.geometry.PlanetGeometry;
import org.joml.Vector3f;

public class Star extends GameObject{

    public Star(float radius, Vector3f position, Vector3f color) {
        Mesh planetMesh = PlanetGeometry.generate(6, radius);
        super(planetMesh, color, position);
    }
    public Vector3f getPosition() {
        return modelMatrix.getTranslation(new Vector3f());
    }
    public void render(ShaderProgram shader) {
        shader.setUniform("isLightSource", 1);

        shader.setUniform("objectColor", color);
        shader.setUniform("model", modelMatrix);
        mesh.render();
    }

}
