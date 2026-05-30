package game.objects.entities;

import engine.graphics.ShaderProgram;
import game.objects.GameEntity;
import org.joml.Vector3f;

public class Light extends GameEntity {
    public Light(Vector3f position, Vector3f color) {
        super(position, color);
    }

    @Override public void cleanup() {
    }

    @Override public void render(ShaderProgram shader) {
        // Uniforms are bound dynamic-array style via Scene/Renderer
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color.set(color);
    }
}
