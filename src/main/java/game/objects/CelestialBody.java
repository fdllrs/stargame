package game.objects;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public class CelestialBody extends GameObject {

    public CelestialBody(Mesh mesh, Vector3f color, Vector3f position) {
        super(mesh, color, position);
    }
}
