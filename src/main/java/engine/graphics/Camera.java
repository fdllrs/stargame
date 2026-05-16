package engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Camera {


    public Vector3f position;
    public Vector3f rotation;
    private Vector3f velocity = new Vector3f();
    Matrix4f viewMatrix;
    private final float acceleration = 100f;
    private final float turboMultiplier = 2f;

    private float mouseSensitivity = 0.15f;
    private final float distanceFromPlayer = 15f;
    private final float maxSpeed = 800f;
    private final float brakeStrength = 0.95f;
    private Matrix4f cameraProjection;

    public Camera() {
        position = new Vector3f(0, 0, 100f); // Start backed away from the planet
        rotation = new Vector3f();
        viewMatrix = new Matrix4f();
        cameraProjection = new Matrix4f().perspective(
                (float) Math.toRadians(45.0f),
                1280f / 720f,
                10f,
                100_000.0f
        );

    }

    public void updateViewMatrix(){
        viewMatrix = new Matrix4f().identity();

        viewMatrix.translate(0, 0, -distanceFromPlayer);
        cameraProjection = new Matrix4f().perspective(
                (float) Math.toRadians(45 + (velocity.length() * 0.07f)),
                1280f / 720f,
                10f,
                100_000.0f
        );
        viewMatrix.rotateX((float) Math.toRadians(rotation.x));
        viewMatrix.rotateY((float) Math.toRadians(rotation.y));

        viewMatrix.translate(-position.x, -position.y, -position.z);
    }

    public void addRotation(float deltaX, float deltaY) {
        rotation.x += deltaY * mouseSensitivity;
        rotation.y += deltaX * mouseSensitivity;

    }

    public void accelerateWithTurbo(float deltaTime){

        velocity.add(localForwardDirection().mul(acceleration * turboMultiplier * deltaTime));
    }

    public void accelerateForwards(float deltaTime) {
        velocity.add(localForwardDirection().mul(acceleration * deltaTime));
    }

    public void accelerateBackwards(float deltaTime) {
        velocity.sub(localForwardDirection().mul(acceleration * deltaTime));
    }

    public void accelerateLeft(float deltaTime) {
        velocity.sub(localRightDirection().mul(acceleration * deltaTime));
    }

    public void accelerateRight(float deltaTime) {
        velocity.add(localRightDirection().mul(acceleration * deltaTime));
    }


    public void applyMovement(float deltaTime){
        if (velocity.length() > maxSpeed) {
            velocity.normalize(maxSpeed);
        }

        position.add(new Vector3f(velocity).mul(deltaTime));

    }

    public Matrix4f getViewMatrix(){
        return viewMatrix;
    }

    public void moveTo(Vector3f position){
        this.position = position;
        zeroAcceleration(true);
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

    public void zeroAcceleration(boolean hardBrake) {
        if (hardBrake) {
            velocity.zero();
        } else {

            if (velocity.length() < 0.1f) {
                velocity.zero();
            } else {
                velocity.mul(brakeStrength);
            }

        }
    }

    public Matrix4f getCameraProjection() {
        return cameraProjection;
    }


    public float getVelocity() {
        return velocity.length();
    }
}
