package game.objects;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import game.geometry.PlayerGeometry;
import org.joml.Vector3f;

public class Player extends GameObject{

    Vector3f playerRotation = new Vector3f(0,0,0);
    private static final Mesh playerMesh = PlayerGeometry.generatePlayerMesh();

    public Player(){
        super(playerMesh, new Vector3f(1.0f,0.0f,0.0f), new Vector3f(0,0,0));
    }

    public void syncWithCamera(Camera camera, boolean isMoving) {
        getModelMatrix().identity();
        getModelMatrix().translate(camera.position);

        if (isMoving) {
            float turnSpeed = 0.15f;

            playerRotation.x += (camera.rotation.x - playerRotation.x) * turnSpeed;
            playerRotation.y += (camera.rotation.y - playerRotation.y) * turnSpeed;
        }

        getModelMatrix().rotateY( - (float) Math.toRadians(playerRotation.y));
        getModelMatrix().rotateX( - (float) Math.toRadians(playerRotation.x));

    }


}
