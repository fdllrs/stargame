package game.components;

import game.objects.celestialBodies.SpaceBody;
import org.joml.Vector3f;

public class OrbitComponent {
    private final SpaceBody parent;
    private final float distance;
    private final float speed;
    public float angle;

    public OrbitComponent(SpaceBody parent,
                          float distance,
                          float speed,
                          float initialAngle) {
        this.parent = parent;
        this.distance = distance;
        this.speed = speed;
        this.angle = initialAngle;
    }

    public void update(SpaceBody self, float deltaTime) {
        angle += speed * deltaTime;
        float offsetX = (float) Math.cos(angle) * distance;
        float offsetZ = (float) Math.sin(angle) * distance;
        Vector3f parentPos = parent.getPosition();
        self.getPosition().set(parentPos.x + offsetX, parentPos.y, parentPos.z + offsetZ);
        self.updateModelMatrix();
    }
}
