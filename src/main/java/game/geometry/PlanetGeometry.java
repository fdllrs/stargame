package game.geometry;

import engine.graphics.Mesh;
import engine.utils.ArrayUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PlanetGeometry {

        private static List<Float> vertices;
        private static List<Integer> indices;
        private static List<Float> normals;

    public static Mesh generate(int resolution, float radius) {


        vertices = new ArrayList<>();
        indices = new ArrayList<>();
        normals = new ArrayList<>();

        Vector3f[] directions = {
                new Vector3f( 0,  1,  0), // Up
                new Vector3f( 0, -1,  0), // Down
                new Vector3f(-1,  0,  0), // Left
                new Vector3f( 1,  0,  0), // Right
                new Vector3f( 0,  0,  1), // Forward
                new Vector3f( 0,  0, -1)  // Back
        };
        int vertexOffset = 0;
        for (Vector3f localUp : directions){
            // build two orthogonal axes
            Vector3f axisA = new Vector3f(localUp.z, localUp.x, localUp.y);
            Vector3f axisB = localUp.cross(axisA, new Vector3f());



            for (int y = 0; y <= resolution; y++) {
                for (int x = 0; x <= resolution; x++) {

                    constructVertexes(resolution, radius, localUp, x, y, axisA, axisB);

                    if (x == resolution || y == resolution) continue;

                    constructVertexIndices(resolution, x, y, vertexOffset);
                }
            }
            vertexOffset += (resolution + 1) * (resolution + 1);
        }

        float[] vertArray = ArrayUtils.convertToFloatArray(vertices);
        int[] indArray = ArrayUtils.convertToIntArray(indices);
        float[] normArray = ArrayUtils.convertToFloatArray(normals);

        return Mesh.create3D(vertArray, indArray, normArray, new float[0]);
    }


    private static void constructVertexes(int resolution, float radius, Vector3f localUp, int x, int y, Vector3f axisA, Vector3f axisB) {
        Vector3f spherePoint = calculateSpherePoint(resolution, radius, localUp, x, y, axisA, axisB);

        vertices.add(spherePoint.x);
        vertices.add(spherePoint.y);
        vertices.add(spherePoint.z);

        Vector3f normal = new Vector3f(spherePoint).normalize();
        normals.add(normal.x);
        normals.add(normal.y);
        normals.add(normal.z);
    }

    private static void constructVertexIndices(int resolution, int x, int y, int vertexOffset) {
        int i = x + y * (resolution + 1) + vertexOffset;

        indices.add(i);
        indices.add(i + resolution + 1);
        indices.add(i + resolution + 2);

        indices.add(i);
        indices.add(i + resolution + 2);
        indices.add(i + 1);
    }

    private static Vector3f calculateSpherePoint(float resolution, float radius, Vector3f localUp, float x, float y, Vector3f axisA, Vector3f axisB) {
        float percentX = x / resolution;
        float percentY = y / resolution;

        Vector3f scaledA = axisA.mul((percentX - 0.5f) * 2, new Vector3f());
        Vector3f scaledB = axisB.mul((percentY - 0.5f) * 2, new Vector3f());
        Vector3f displacement = scaledA.add(scaledB, new Vector3f());
        Vector3f spherePoint = new Vector3f(localUp.add(displacement, new Vector3f()).normalize());


        return spherePoint.mul(radius, new Vector3f());
    }
}
