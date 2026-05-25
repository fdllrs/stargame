package game.objects;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import game.geometry.PlayerGeometry;
import org.joml.Vector3f;

public class Player extends GameObject {

    private static final Mesh playerMesh = PlayerGeometry.generatePlayerMesh();

    public Player() {
        super(playerMesh, new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0, 0, 0));
    }

    public void syncWithCamera(Camera camera, boolean isMoving) {
        this.position.set(camera.position);

        if (isMoving) {
            float turnSpeed = 0.15f;

            this.rotation.x = lerpAngle(this.rotation.x, camera.rotation.x, turnSpeed);
            this.rotation.y = lerpAngle(this.rotation.y, camera.rotation.y, turnSpeed);
        }
        updateModelMatrix();
    }

    private float lerpAngle(float current, float target, float speed) {
        float difference = target - current;

        while (difference < -180.0f) difference += 360.0f;
        while (difference > 180.0f) difference -= 360.0f;

        return current + (difference * speed);
    }
}