package engine.graphics;

import engine.utils.ArrayUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelLoader {
    public static Mesh loadModelObj(String modelPath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(modelPath));
            ArrayList<Float> vertices = new ArrayList<>();
            ArrayList<Float> texture = new ArrayList<>();
            ArrayList<Float> normals = new ArrayList<>();

            parseLines(lines, vertices, normals, texture);

            // Parse materials if specified
            Map<String, float[]> materials = new HashMap<>();
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.startsWith("mtllib ")) {
                    String[] split = trimmed.split("\\s+");
                    if (split.length > 1) {
                        String mtlFileName = split[1];
                        java.nio.file.Path parent = Paths.get(modelPath).getParent();
                        java.nio.file.Path mtlPath = parent != null ? parent.resolve(mtlFileName) : Paths.get(mtlFileName);
                        materials.putAll(loadMtl(mtlPath.toString()));
                    }
                }
            }

            List<Float> finalVertices = new ArrayList<>();
            List<Float> finalTextures = new ArrayList<>();
            List<Float> finalNormals = new ArrayList<>();
            List<Float> finalColors = new ArrayList<>();
            List<Float> finalEmissive = new ArrayList<>();
            List<Integer> finalIndices = new ArrayList<>();
            Map<String, Integer> uniqueVertices = new HashMap<>();

            float[] activeColor = new float[]{1.0f, 1.0f, 1.0f}; // Default to white
            float[] activeEmissive = new float[]{0.0f, 0.0f, 0.0f}; // Default to black

            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.startsWith("usemtl ")) {
                    String[] split = trimmed.split("\\s+");
                    if (split.length > 1) {
                        String matName = split[1];
                        activeColor = materials.getOrDefault(matName + "_Kd", new float[]{1.0f, 1.0f, 1.0f});
                        activeEmissive = materials.getOrDefault(matName + "_Ke", new float[]{0.0f, 0.0f, 0.0f});
                    }
                } else if (trimmed.startsWith("f ")) {
                    String[] split = trimmed.split("\\s+");
                    for (int i = 1; i < split.length; i++) {
                        String token = split[i];
                        if (!uniqueVertices.containsKey(token)) {
                            String[] face = token.split("/");
                            int vIdx = Integer.parseInt(face[0]) - 1;
                            int vtIdx = Integer.parseInt(face[1]) - 1;
                            int vnIdx = Integer.parseInt(face[2]) - 1;

                            // Add position (3 floats)
                            finalVertices.add(vertices.get(vIdx * 3));
                            finalVertices.add(vertices.get(vIdx * 3 + 1));
                            finalVertices.add(vertices.get(vIdx * 3 + 2));

                            // Add UV coordinate (2 floats)
                            finalTextures.add(texture.get(vtIdx * 2));
                            finalTextures.add(texture.get(vtIdx * 2 + 1));

                            // Add normal (3 floats)
                            finalNormals.add(normals.get(vnIdx * 3));
                            finalNormals.add(normals.get(vnIdx * 3 + 1));
                            finalNormals.add(normals.get(vnIdx * 3 + 2));

                            // Add color (3 floats)
                            finalColors.add(activeColor[0]);
                            finalColors.add(activeColor[1]);
                            finalColors.add(activeColor[2]);

                            // Add emissive color (3 floats)
                            finalEmissive.add(activeEmissive[0]);
                            finalEmissive.add(activeEmissive[1]);
                            finalEmissive.add(activeEmissive[2]);

                            uniqueVertices.put(token, uniqueVertices.size());
                        }
                        finalIndices.add(uniqueVertices.get(token));
                    }
                }
            }

            float[] vertexArray = ArrayUtils.convertToFloatArray(finalVertices);
            float[] textureArray = ArrayUtils.convertToFloatArray(finalTextures);
            float[] normalArray = ArrayUtils.convertToFloatArray(finalNormals);
            float[] colorArray = ArrayUtils.convertToFloatArray(finalColors);
            float[] emissiveArray = ArrayUtils.convertToFloatArray(finalEmissive);
            int[] indicesArray = ArrayUtils.convertToIntArray(finalIndices);

            return Mesh.create3D(vertexArray, indicesArray, normalArray, textureArray, colorArray, emissiveArray);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load obj file: " + modelPath, e);
        }
    }

    private static Map<String, float[]> loadMtl(String mtlPath) {
        Map<String, float[]> materialColors = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(mtlPath));
            String currentMaterial = null;
            for (String line : lines) {
                String trimmed = line.trim();
                String[] split = trimmed.split("\\s+");
                if (trimmed.startsWith("newmtl ") && split.length > 1) {
                    currentMaterial = split[1];
                } else if (trimmed.startsWith("Kd ") && currentMaterial != null && split.length > 3) {
                    float r = Float.parseFloat(split[1]);
                    float g = Float.parseFloat(split[2]);
                    float b = Float.parseFloat(split[3]);
                    materialColors.put(currentMaterial + "_Kd", new float[]{r, g, b});
                } else if (trimmed.startsWith("Ke ") && currentMaterial != null && split.length > 3) {
                    float r = Float.parseFloat(split[1]);
                    float g = Float.parseFloat(split[2]);
                    float b = Float.parseFloat(split[3]);
                    materialColors.put(currentMaterial + "_Ke", new float[]{r, g, b});
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load mtl file: " + mtlPath + ". " + e.getMessage());
        }
        return materialColors;
    }

    private static void parseLines(List<String> lines,
                                   ArrayList<Float> vertices,
                                   ArrayList<Float> normals,
                                   ArrayList<Float> texture) {
        for (String line : lines) {
            String trimmed = line.trim();
            String[] split = trimmed.split("\\s+");
            if (trimmed.startsWith("v ")) {
                vertices.add(Float.parseFloat(split[1]));
                vertices.add(Float.parseFloat(split[2]));
                vertices.add(Float.parseFloat(split[3]));
            } else if (trimmed.startsWith("vn ")) {
                normals.add(Float.parseFloat(split[1]));
                normals.add(Float.parseFloat(split[2]));
                normals.add(Float.parseFloat(split[3]));
            } else if (trimmed.startsWith("vt ")) {
                texture.add(Float.parseFloat(split[1]));
                texture.add(Float.parseFloat(split[2]));
            }
        }
    }
}
