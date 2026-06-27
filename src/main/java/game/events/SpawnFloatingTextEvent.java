package game.events;

import engine.events.Event;
import org.joml.Vector3f;
import org.joml.Vector4f;

public record SpawnFloatingTextEvent(
		String text,
		Float x,
		Float y,
		Vector3f worldPos,
		Vector3f planetCenter,
		Vector4f color) implements Event {

	// 2D constructor
	public SpawnFloatingTextEvent(String text, float x, float y, Vector4f color) {
		this(text, x, y, null, null, color);
	}

	// 3D constructor
	public SpawnFloatingTextEvent(String text,
			Vector3f worldPos,
			Vector3f planetCenter,
			Vector4f color) {
		this(text, null, null, worldPos, planetCenter, color);
	}
}
