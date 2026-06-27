package game.components;

import game.objects.spaceBodies.SpaceBody;
import org.joml.Vector3f;

public class OrbitComponent {
	private final SpaceBody parent;
	private final float distance;
	private final float speed;
	public float angle;

	public OrbitComponent(float distance, float speed, float initialAngle) {
		this.parent = null;
		this.distance = distance;
		this.speed = speed;
		this.angle = initialAngle;
	}

	public OrbitComponent(SpaceBody parent, float distance, float speed, float initialAngle) {
		this.parent = parent;
		this.distance = distance;
		this.speed = speed;
		this.angle = initialAngle;
	}

	public void update(Vector3f position, float deltaTime) {
		angle += speed * deltaTime;
		float offsetX = (float) Math.cos(angle) * distance;
		float offsetZ = (float) Math.sin(angle) * distance;
		if (parent != null) {
			updateRelativeToParent(position, offsetX, offsetZ);
		}
		else {
			updateAbsolutePosition(position, offsetX, offsetZ);
		}
	}

	private void updateRelativeToParent(Vector3f position, float offsetX, float offsetZ) {
		assert parent != null;
		Vector3f parentPos = parent.getPosition();
		position.set(parentPos.x + offsetX, parentPos.y, parentPos.z + offsetZ);
	}

	private static void updateAbsolutePosition(Vector3f position, float offsetX, float offsetZ) {
		position.set(offsetX, 0, offsetZ);
	}
}
