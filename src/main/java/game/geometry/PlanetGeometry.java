package game.geometry;

import engine.graphics.Mesh;
import engine.utils.ArrayUtils;
import game.info.PlanetType;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PlanetGeometry {
    private static Vector3f calculateDisplacedNormal(Vector3f dir,
                                                     float radius,
                                                     PlanetType type,
                                                     float height) {
        if (type != PlanetType.ROCKY && type != PlanetType.ORGANIC) {
            return new Vector3f(dir).normalize();
        }

        Vector3f T = new Vector3f();
        if (Math.abs(dir.x) < 0.9f) {
            new Vector3f(1.0f, 0.0f, 0.0f).cross(dir, T).normalize();
        } else {
            new Vector3f(0.0f, 1.0f, 0.0f).cross(dir, T).normalize();
        }
        Vector3f B = new Vector3f(dir).cross(T, new Vector3f()).normalize();

        float epsilon = 0.005f;
        Vector3f dirT = new Vector3f(dir).add(new Vector3f(T).mul(epsilon)).normalize();
        Vector3f dirB = new Vector3f(dir).add(new Vector3f(B).mul(epsilon)).normalize();

        float heightT = getPlanetHeight(dirT, radius, type);
        float heightB = getPlanetHeight(dirB, radius, type);

        Vector3f P = new Vector3f(dir).mul(radius + height);
        Vector3f PT = new Vector3f(dirT).mul(radius + heightT);
        Vector3f PB = new Vector3f(dirB).mul(radius + heightB);

        Vector3f vT = PT.sub(P, new Vector3f());
        Vector3f vB = PB.sub(P, new Vector3f());

        Vector3f normal = vT.cross(vB, new Vector3f()).normalize();
        if (normal.dot(dir) < 0.0f) {
            normal.negate();
        }
        return normal;
    }

    private static void constructIndices(int resolution,
                                         int x,
                                         int y,
                                         int vertexOffset,
                                         List<Integer> indices) {
        int i = x + y * (resolution + 1) + vertexOffset;

        indices.add(i);
        indices.add(i + resolution + 1);
        indices.add(i + resolution + 2);

        indices.add(i);
        indices.add(i + resolution + 2);
        indices.add(i + 1);
    }

    private static void constructVertex(int resolution,
                                        float radius,
                                        PlanetType type,
                                        Vector3f localUp,
                                        int x,
                                        int y,
                                        Vector3f axisA,
                                        Vector3f axisB,
                                        List<Float> vertices,
                                        List<Float> normals,
                                        List<Float> colors) {
        float percentX = (float) x / resolution;
        float percentY = (float) y / resolution;

        Vector3f scaledA = axisA.mul((percentX - 0.5f) * 2, new Vector3f());
        Vector3f scaledB = axisB.mul((percentY - 0.5f) * 2, new Vector3f());
        Vector3f displacement = scaledA.add(scaledB, new Vector3f());
        Vector3f dir = new Vector3f(localUp.add(displacement, new Vector3f())
                                           .normalize());

        float height = getPlanetHeight(dir, radius, type);
        Vector3f spherePoint = dir.mul(radius + height, new Vector3f());

        vertices.add(spherePoint.x);
        vertices.add(spherePoint.y);
        vertices.add(spherePoint.z);

        Vector3f normal = calculateDisplacedNormal(dir, radius, type, height);
        normals.add(normal.x);
        normals.add(normal.y);
        normals.add(normal.z);

        float noiseVal = 0.0f;
        if (type == PlanetType.ROCKY || type == PlanetType.ORGANIC) {
            noiseVal = Noise3D.fbm(dir.x * 3.0f, dir.y * 3.0f, dir.z * 3.0f);
        }
        colors.add(noiseVal);
        colors.add(noiseVal);
        colors.add(noiseVal);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Mesh generate(int resolution, float radius, PlanetType type) {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> colors = new ArrayList<>();

        Vector3f[] directions = {new Vector3f(0, 1, 0), // Up
                                 new Vector3f(0, -1, 0), // Down
                                 new Vector3f(-1, 0, 0), // Left
                                 new Vector3f(1, 0, 0), // Right
                                 new Vector3f(0, 0, 1), // Forward
                                 new Vector3f(0, 0, -1)  // Back
        };

        int vertexOffset = 0;
        for (Vector3f localUp : directions) {
            Vector3f axisA = new Vector3f(localUp.z, localUp.x, localUp.y);
            Vector3f axisB = localUp.cross(axisA, new Vector3f());

            for (int y = 0; y <= resolution; y++) {
                for (int x = 0; x <= resolution; x++) {
                    constructVertex(resolution,
                                    radius,
                                    type,
                                    localUp,
                                    x,
                                    y,
                                    axisA,
                                    axisB,
                                    vertices,
                                    normals,
                                    colors);

                    if (x < resolution && y < resolution) {
                        constructIndices(resolution, x, y, vertexOffset, indices);
                    }
                }
            }
            vertexOffset += (resolution + 1) * (resolution + 1);
        }

        float[] vertArray = ArrayUtils.convertToFloatArray(vertices);
        int[] indArray = ArrayUtils.convertToIntArray(indices);
        float[] normArray = ArrayUtils.convertToFloatArray(normals);
        float[] colorArray = ArrayUtils.convertToFloatArray(colors);

        return Mesh.create3D(vertArray, indArray, normArray, new float[0], colorArray);
    }

    public static Mesh generateRing(float innerRadius,
                                    float outerRadius,
                                    int resolution) {
        List<Float> positions = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i <= resolution; i++) {
            float angle = (float) (2.0 * Math.PI * i / resolution);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);

            positions.add(cos * innerRadius);
            positions.add(0.0f);
            positions.add(sin * innerRadius);
            normals.add(0.0f);
            normals.add(1.0f);
            normals.add(0.0f);

            positions.add(cos * outerRadius);
            positions.add(0.0f);
            positions.add(sin * outerRadius);
            normals.add(0.0f);
            normals.add(1.0f);
            normals.add(0.0f);

            if (i < resolution) {
                int base = i * 2;
                indices.add(base);
                indices.add(base + 1);
                indices.add(base + 2);
                indices.add(base + 1);
                indices.add(base + 3);
                indices.add(base + 2);

                indices.add(base);
                indices.add(base + 2);
                indices.add(base + 1);
                indices.add(base + 1);
                indices.add(base + 2);
                indices.add(base + 3);
            }
        }

        float[] posArray = ArrayUtils.convertToFloatArray(positions);
        float[] normArray = ArrayUtils.convertToFloatArray(normals);
        int[] indArray = ArrayUtils.convertToIntArray(indices);

        return Mesh.create3D(posArray, indArray, normArray, new float[0]);
    }

    private static float getPlanetHeight(Vector3f dir, float radius, PlanetType type) {
        if (type == PlanetType.ROCKY) {
            float noiseVal = Noise3D.fbm(dir.x * 3.0f, dir.y * 3.0f, dir.z * 3.0f);
            float heightAmplitude = radius * 0.16f;
            return (noiseVal - 0.45f) * heightAmplitude;
        } else if (type == PlanetType.ORGANIC) {
            float noiseVal = Noise3D.fbm(dir.x * 3.0f, dir.y * 3.0f, dir.z * 3.0f);
            float heightAmplitude = radius * 0.16f;
            return (Math.max(noiseVal, 0.45f) - 0.45f) * heightAmplitude;
        }
        return 0.0f;
    }
}
