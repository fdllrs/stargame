package engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
	private static final float BASE_FOV = 45.0f;
	private static final float SIDE_VELOCITY_DOWNSCALE_FACTOR = 0.35f;
	private static final float FOV_SPEED_FACTOR = 0.07f;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 100_000.0f;
	private static final float mouseSensitivity = 0.15f;
	private static final float distanceFromPlayer = 3.75f;
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

	public void addRotation(float deltaX, float deltaY) {
		rotation.x += deltaY * mouseSensitivity;
		rotation.y += deltaX * mouseSensitivity;

		rotation.x = Math.clamp(rotation.x, -89.0f, 89.0f);
	}

	public void applyMovement(float deltaTime, float maxSpeed) {
		if (velocity.lengthSquared() > maxSpeed * maxSpeed) {
			velocity.normalize(maxSpeed);
		}
		position.fma(deltaTime, velocity);
	}

	public Vector3f getLocalForwardDirection() {
		float yawRad = (float) Math.toRadians(rotation.y);
		float pitchRad = (float) Math.toRadians(rotation.x);
		return new Vector3f((float) ( Math.sin(yawRad) * Math.cos(pitchRad) ),
							(float) ( -Math.sin(pitchRad) ),
							(float) ( -Math.cos(yawRad) * Math.cos(pitchRad) )).normalize();
	}

	public Vector3f getPosition() {
		return position;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	private Vector3f localRightDirection() {
		return new Vector3f(getLocalForwardDirection().cross(new Vector3f(0, 1, 0))
													  .normalize()).mul(
				SIDE_VELOCITY_DOWNSCALE_FACTOR);
	}

	public void moveTo(Vector3f position) {
		this.position = position;
		zeroAcceleration(0);
	}

	/**
	 * Call when the framebuffer is resized to keep the projection matrix in sync.
	 */
	public void onResize(int width, int height) {
		this.aspectRatio = (float) width / height;
		rebuildProjection(cachedFov);
	}

	private void rebuildProjection(float fov) {
		cachedFov = fov;
		projectionMatrix.setPerspective((float) Math.toRadians(fov),
										aspectRatio,
										NEAR_PLANE,
										FAR_PLANE);
	}

	/**
	 * Shift the camera position by a world-space delta without affecting velocity.
	 */
	public void translate(float dx, float dy, float dz) {
		position.add(dx, dy, dz);
	}

	public void updateVelocityBack(float acceleration) {
		velocity.sub(getLocalForwardDirection().mul(acceleration));
	}

	public void updateVelocityForwards(float acceleration) {
		velocity.add(getLocalForwardDirection().mul(acceleration));
	}

	public void updateVelocityLeft(float acceleration) {
		velocity.sub(localRightDirection().mul(acceleration));
	}

	public void updateVelocityRight(float acceleration) {
		velocity.add(localRightDirection().mul(acceleration));
	}

	public void updateViewMatrix() {
		viewMatrix.identity();
		viewMatrix.translate(0, 0, -distanceFromPlayer);
		viewMatrix.rotateX((float) Math.toRadians(rotation.x));
		viewMatrix.rotateY((float) Math.toRadians(rotation.y));
		viewMatrix.translate(-position.x, -position.y, -position.z);

		// Only rebuild the projection when FOV actually changes (driven by speed).
		float targetFov = BASE_FOV + velocity.length() * FOV_SPEED_FACTOR;
		if (Math.abs(targetFov - cachedFov) > 0.01f) {
			rebuildProjection(targetFov);
		}
	}

	public void zeroAcceleration(float brakeStrength) {

		if (velocity.length() < 0.5f) {
			velocity.zero();
		}
		else {
			velocity.mul(brakeStrength);
		}
	}
}
