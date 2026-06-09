package game.objects.celestialBodies;

import engine.graphics.Mesh;
import game.objects.GameObject;
import org.joml.Vector3f;

public class CelestialBody extends GameObject {
    protected String name;
    protected Vector3f colorA;
    protected Vector3f colorB;
    protected float radius;

    public CelestialBody(Mesh mesh, Vector3f color, Vector3f position) {
        super(mesh, color, position);
    }

    /**
     * Projects this body's XZ position relative to a star origin into 2D map screen
     * coordinates, given a map center and a world-to-screen scale factor.
     */
    public org.joml.Vector2f screenPosition(Vector3f starPos,
                                            float scale,
                                            float centerX,
                                            float centerY) {
        float dx = position.x - starPos.x;
        float dz = position.z - starPos.z;
        return new org.joml.Vector2f(centerX + dx * scale, centerY + dz * scale);
    }

    public void rotate(float deltaTime) {
        float spinSpeedMultiplier = 0.1f;
        this.rotation.y += (float) Math.toDegrees(deltaTime * spinSpeedMultiplier);
    }

    public float getRadius() {
        return radius;
    }

    public String getName() {
        return name;
    }

}
