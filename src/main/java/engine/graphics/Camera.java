package engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static final float BASE_FOV = 45.0f;
    private static final float SIDE_VELOCITY_DOWNSCALE_FACTOR = 0.35f;
    private static final float FOV_SPEED_FACTOR = 0.07f;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 100_000.0f;
    private static final float turboMultiplier = 3f;
    private static final float mouseSensitivity = 0.15f;
    private static final float distanceFromPlayer = 3.75f;
    private static final float maxSpeed = 800f;
    private static final float brakeStrength = 0.95f;
    private final float acceleration = 100f;
    private final Vector3f velocity = new Vector3f();
    private final Matrix4f projectionMatrix;
    public Vector3f position;
    public Vector3f rotation;
    Matrix4f viewMatrix;
    private float aspectRatio;
    private float cachedFov = -1f;

    public Camera(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        position = new Vector3f(0, 0, 100f);
        rotation = new Vector3f();
        viewMatrix = new Matrix4f();
        projectionMatrix = new Matrix4f();
        rebuildProjection(BASE_FOV);
    }

    private void rebuildProjection(float fov) {
        cachedFov = fov;
        projectionMatrix.setPerspective((float) Math.toRadians(fov),
                                        aspectRatio,
                                        NEAR_PLANE,
                                        FAR_PLANE);

    }

    /**
     * Call when the framebuffer is resized to keep the projection matrix in sync.
     */
    public void onResize(int width, int height) {
        this.aspectRatio = (float) width / height;
        rebuildProjection(cachedFov);
    }

    public void updateViewMatrix() {
        viewMatrix.identity();
        viewMatrix.translate(0, 0, -distanceFromPlayer);
        viewMatrix.rotateX((float) Math.toRadians(rotation.x));
        viewMatrix.rotateY((float) Math.toRadians(rotation.y));
        viewMatrix.translate(-position.x, -position.y, -position.z);

        // Only rebuild projection when FOV actually changes (driven by speed).
        float targetFov = BASE_FOV + velocity.length() * FOV_SPEED_FACTOR;
        if (Math.abs(targetFov - cachedFov) > 0.01f) {
            rebuildProjection(targetFov);
        }
    }

    public void addRotation(float deltaX, float deltaY) {
        rotation.x += deltaY * mouseSensitivity;
        rotation.y += deltaX * mouseSensitivity;

        rotation.x = Math.clamp(rotation.x, -89.0f, 89.0f);
    }

    public void accelerateWithTurbo(float deltaTime) {
        velocity.add(localForwardDirection().mul(
                acceleration * turboMultiplier * deltaTime));
    }

    private Vector3f localForwardDirection() {
        float yawRad = (float) Math.toRadians(rotation.y);
        float pitchRad = (float) Math.toRadians(rotation.x);
        return new Vector3f((float) (Math.sin(yawRad) * Math.cos(pitchRad)),
                            (float) (-Math.sin(pitchRad)),
                            (float) (-Math.cos(yawRad) * Math.cos(pitchRad))).normalize();
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

    private Vector3f localRightDirection() {
        return new Vector3f(localForwardDirection().cross(new Vector3f(0, 1, 0))
                                                   .normalize()).mul(
                SIDE_VELOCITY_DOWNSCALE_FACTOR);
    }

    public void accelerateRight(float deltaTime) {
        velocity.add(localRightDirection().mul(acceleration * deltaTime));
    }

    public void applyMovement(float deltaTime) {
        if (velocity.lengthSquared() > maxSpeed * maxSpeed) {
            velocity.normalize(maxSpeed);
        }
        position.fma(deltaTime, velocity);

    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void moveTo(Vector3f position) {
        this.position = position;
        zeroAcceleration(true);
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
}
