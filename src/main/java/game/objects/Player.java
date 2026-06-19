package game.objects;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import game.components.StorageComponent;
import game.geometry.PlayerGeometry;
import org.joml.Vector3f;

public class Player extends GameObject {
	private static final Mesh playerMesh = PlayerGeometry.getPlayerMesh();
	private static final float PLAYER_RADIUS = 0.18f;
	private final StorageComponent storage = new StorageComponent(1000);
	private float accelerationFactor = 2f;
	private float turboMultiplier = 1f;
	private float maxSpeed = 4f;

	public Player() {
		super(playerMesh, new Vector3f(0, 0, 0));
		updateModelMatrix();
	}

	public float accelerate(float deltaTime) {
		return accelerationFactor * deltaTime;
	}

	public float accelerateWithTurbo(float deltaTime) {
		return accelerate(deltaTime) * turboMultiplier;
	}

	public float getBrakeStrength() {
		return 0.95f;
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) { this.maxSpeed = maxSpeed; }

	public float getRadius() {
		return PLAYER_RADIUS;
	}

	public StorageComponent getStorage() {
		return storage;
	}

	private float lerpAngle(float current, float target, float speed) {
		float difference = target - current;

		while (difference < -180.0f) difference += 360.0f;
		while (difference > 180.0f) difference -= 360.0f;

		return current + ( difference * speed );
	}

	public void setAccelerationFactor(float accelerationFactor) {
		this.accelerationFactor = accelerationFactor;
	}

	public void setTurboMultiplier(float turboMultiplier) {
		this.turboMultiplier = turboMultiplier;
	}

	public void syncWithCamera(Camera camera, boolean isMoving) {
		this.position.set(camera.position);

		if (isMoving) {
			float turnSpeed = 0.15f;

			this.rotation.x = lerpAngle(this.rotation.x, -camera.rotation.x, turnSpeed);
			this.rotation.y = lerpAngle(this.rotation.y, -camera.rotation.y, turnSpeed);
		}
		updateModelMatrix();
	}
}