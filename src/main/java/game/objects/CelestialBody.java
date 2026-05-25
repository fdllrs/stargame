package game.objects;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public class CelestialBody extends GameObject {

    public CelestialBody(Mesh mesh, Vector3f color, Vector3f position) {
        super(mesh, color, position);
    }

    private float spinSpeedMultiplier = 0.7f;

    protected String name;
    protected Vector3f colorA;
    protected Vector3f colorB;
    protected float radius;




    public void rotate(float deltaTime) {
        this.rotation.y += (float) Math.toDegrees(deltaTime * spinSpeedMultiplier);
    }

    public float getRadius(){
        return radius;
    }
    public float orbitInfluence() {
        return radius + 5f;
    }
}
