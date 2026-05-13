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
}