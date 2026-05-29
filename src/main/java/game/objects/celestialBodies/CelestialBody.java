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

    public void rotate(float deltaTime) {
        float spinSpeedMultiplier = 0.1f;
        this.rotation.y += (float) Math.toDegrees(deltaTime * spinSpeedMultiplier);
    }

    public float getRadius() {
        return radius;
    }

    public float orbitInfluence() {
        return radius + 5f;
    }
}
