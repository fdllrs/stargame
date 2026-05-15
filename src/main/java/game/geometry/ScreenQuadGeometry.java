package game.geometry;

import engine.graphics.Mesh;

public class ScreenQuadGeometry {

    public static Mesh generateScreenQuad() {
        float[] vertices = {
                -1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 0.0f
        };

        int[] indices = {0, 1, 2, 0, 2, 3};

        float[] normals = new float[vertices.length];

        float[] uvs = {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        return new Mesh(vertices, indices, normals, uvs);
    }

    public static Mesh generateUIRect() {
        float[] vertices = {
                // X, Y (Bottom left is 0,0)
                0.0f, 1.0f, // Top Left
                0.0f, 0.0f, // Bottom Left
                1.0f, 0.0f, // Bottom Right
                1.0f, 1.0f  // Top Right
        };
        int[] indices = {0, 1, 2, 0, 2, 3};
        float[] uvs = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f};

        // Pass to your Mesh constructor (Normals are empty)
        return new Mesh(vertices, indices, new float[0], uvs);
    }
}