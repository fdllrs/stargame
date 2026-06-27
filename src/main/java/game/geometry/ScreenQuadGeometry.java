package game.geometry;

import engine.graphics.Mesh;

public class ScreenQuadGeometry {
	public static Mesh generateScreenQuad() {
		float[] vertices = {
				-1.0f, 1.0f, // Top Left
				-1.0f, -1.0f, // Bottom Left
				1.0f, -1.0f, // Bottom Right
				1.0f, 1.0f  // Top Right
		};

		int[] indices = { 0, 1, 2, 0, 2, 3 };

		float[] uvs = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f };

		return Mesh.create2DUI(vertices, indices, uvs);
	}

	public static Mesh generateUIRect() {
		float[] vertices = {
				0.0f, 0.0f, // Vertex 0: Top Left
				0.0f, 1.0f, // Vertex 1: Bottom Left
				1.0f, 1.0f, // Vertex 2: Bottom Right
				1.0f, 0.0f  // Vertex 3: Top Right
		};
		int[] indices = { 0, 1, 2, 0, 2, 3 };
		float[] uvs = {
				0.0f, 1.0f, // Top Left UV
				0.0f, 0.0f, // Bottom Left UV
				1.0f, 0.0f, // Bottom Right UV
				1.0f, 1.0f  // Top Right UV
		};

		// Pass to your Mesh constructor (Normals are empty)
		return Mesh.create2DUI(vertices, indices, uvs);
	}
}