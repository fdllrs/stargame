package engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {


    public Vector3f position;
    public Vector3f rotation;
    Matrix4f viewMatrix;
    private final float cameraSpeed = 15f;
    private float mouseSensitivity = 0.15f;
    private float distanceFromPlayer = 15f;

    public Camera() {
        position = new Vector3f(0, 0, 100f); // Start backed away from the planet
        rotation = new Vector3f();
        viewMatrix = new Matrix4f();
    }
    public void updateViewMatrix(){
        viewMatrix = new Matrix4f().identity();

        viewMatrix.translate(0, 0, -distanceFromPlayer);

        viewMatrix.rotateX((float) Math.toRadians(rotation.x));
        viewMatrix.rotateY((float) Math.toRadians(rotation.y));

        viewMatrix.translate(-position.x, -position.y, -position.z);
    }

    public void addRotation(float deltaX, float deltaY) {
        rotation.x += deltaY * mouseSensitivity;
        rotation.y += deltaX * mouseSensitivity;

    }


    public void moveBackwards() {
        position.sub(localForwardDirection().mul(cameraSpeed));
    }

    public void moveForwards(){
        position.add(localForwardDirection().mul(cameraSpeed));
    }

    public void moveLeft(){
        position.sub(localRightDirection().mul(cameraSpeed));
    }
    public void moveRight(){
        position.add(localRightDirection().mul(cameraSpeed));
    }

    public void moveUp() {
        position.y += cameraSpeed;
    }

    public void moveDown() {
        position.y -= cameraSpeed;
    }

    public Matrix4f getViewMatrix(){
        return viewMatrix;
    }

    private Vector3f localRightDirection() {
        return new Vector3f(localForwardDirection().cross(new Vector3f(0, 1, 0)).normalize());
    }

    private Vector3f localForwardDirection() {
        Vector3f forward = new Vector3f();

        // Convert degrees to radians for Java's Math class
        float yawRad = (float) Math.toRadians(rotation.y);
        float pitchRad = (float) Math.toRadians(rotation.x);

        // Calculate the X, Y, and Z components using standard spherical coordinates.
        // Note: OpenGL looks down the -Z axis, so the Z and Y signs are inverted from standard math.
        forward.x = (float) (Math.sin(yawRad) * Math.cos(pitchRad));
        forward.y = (float) -Math.sin(pitchRad);
        forward.z = (float) (-Math.cos(yawRad) * Math.cos(pitchRad));

        return forward.normalize();
    }

}
