package game.geometry;

import engine.graphics.Mesh;

public class PlayerGeometry {
    public static Mesh generatePlayerMesh() {
        float[] vertices = {0.0f, 0.5f, 0.0f, // 0: Top node
                -0.4f, -0.3f, 0.5f, // 1: Back left base (flipped from -0.5)
                0.4f, -0.3f, 0.5f, // 2: Back right base (flipped from -0.5)
                0.0f, -0.3f, -0.8f  // 3: Nose (front) (flipped from 0.8)
        };
        int[] indices = {3, 2, 0, // Right face (swapped from 3, 0, 2)
                3, 1, 2, // Bottom face (swapped from 3, 2, 1)
                3, 0, 1, // Left face (swapped from 3, 1, 0)
                0, 2, 1  // Back face (swapped from 0, 1, 2)
        };
        // We need simple normals so the ship is lit.
        // For a simple placeholder, calculating actual perpendicular normals
        // per face is overkill. This fake math just makes it roughly work.
        float[] normals = new float[vertices.length];
        for (int i = 0; i < vertices.length; i += 3) {
            // Normals just roughly point away from the origin
            float length =
                    (float) Math.sqrt(vertices[i] * vertices[i] + vertices[i + 1] * vertices[i + 1] + vertices[i + 2] * vertices[i + 2]);
            normals[i] = vertices[i] / length;
            normals[i + 1] = vertices[i + 1] / length;
            normals[i + 2] = vertices[i + 2] / length;
        }

        return Mesh.create3D(vertices, indices, normals, new float[0]);
    }
}
