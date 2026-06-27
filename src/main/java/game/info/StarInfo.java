package game.info;

import org.joml.Vector3f;

public record StarInfo(
		String name, StarType type, float radius, float mass, Vector3f colorA, Vector3f colorB

) {
	public enum StarType {O, B, A, F, G, K, M}
}