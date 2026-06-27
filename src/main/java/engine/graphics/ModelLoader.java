package engine.graphics;

import engine.utils.ArrayUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelLoader {

	public static Mesh loadModelObj(String modelPath, float scale) {
		try {
			Path modelFilePath = Paths.get(modelPath);
			List<String> lines = Files.readAllLines(modelFilePath);
			ArrayList<Float> vertices = new ArrayList<>();
			ArrayList<Float> texture = new ArrayList<>();
			ArrayList<Float> normals = new ArrayList<>();

			parseLines(lines, vertices, normals, texture, scale);

			// Parse materials if specified
			Map<String, MtlMaterial> materials = new HashMap<>();
			for (String line : lines) {
				String trimmed = line.trim();
				if (trimmed.startsWith("mtllib ")) {
					String[] split = trimmed.split("\\s+");
					if (split.length > 1) {
						String mtlFileName = split[ 1 ];
						java.nio.file.Path parent = modelFilePath.getParent();
						java.nio.file.Path mtlPath = parent != null
													 ? parent.resolve(mtlFileName)
													 : Paths.get(mtlFileName);
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

			float[] activeColor = new float[] { 1.0f, 1.0f, 1.0f }; // Default to white
			float[] activeEmissive = new float[] { 0.0f, 0.0f, 0.0f }; // Default to black

			for (String line : lines) {
				String trimmed = line.trim();
				if (trimmed.startsWith("usemtl ")) {
					String[] split = trimmed.split("\\s+");
					if (split.length > 1) {
						String matName = split[ 1 ];
						MtlMaterial mat = materials.get(matName);
						if (mat != null) {
							activeColor = mat.Kd;
							activeEmissive = mat.Ke;
						}
						else {
							activeColor = new float[] { 1.0f, 1.0f, 1.0f };
							activeEmissive = new float[] { 0.0f, 0.0f, 0.0f };
						}
					}
				}
				else if (trimmed.startsWith("f ")) {
					String[] split = trimmed.split("\\s+");
					for (int i = 1; i < split.length; i++) {
						String token = split[ i ];
						if (!uniqueVertices.containsKey(token)) {
							String[] face = token.split("/");
							int vIdx = Integer.parseInt(face[ 0 ]) - 1;
							int vtIdx = Integer.parseInt(face[ 1 ]) - 1;
							int vnIdx = Integer.parseInt(face[ 2 ]) - 1;

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
							finalColors.add(activeColor[ 0 ]);
							finalColors.add(activeColor[ 1 ]);
							finalColors.add(activeColor[ 2 ]);

							// Add emissive color (3 floats)
							finalEmissive.add(activeEmissive[ 0 ]);
							finalEmissive.add(activeEmissive[ 1 ]);
							finalEmissive.add(activeEmissive[ 2 ]);

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

			Mesh mesh = Mesh.create3D(vertexArray,
									  indicesArray,
									  normalArray,
									  textureArray,
									  colorArray,
									  emissiveArray);

			String diffuseTexFile = null;
			String emissiveTexFile = null;
			for (MtlMaterial mat : materials.values()) {
				if (mat.map_Kd != null) {
					diffuseTexFile = mat.map_Kd;
				}
				if (mat.map_Ke != null) {
					emissiveTexFile = mat.map_Ke;
				}
			}

			if (diffuseTexFile != null) {
				String path = resolveTexturePath(modelPath, diffuseTexFile);
				if (path != null) {
					mesh.setDiffuseTexture(new Texture(path));
				}
			}
			if (emissiveTexFile != null) {
				String path = resolveTexturePath(modelPath, emissiveTexFile);
				if (path != null) {
					mesh.setEmissiveTexture(new Texture(path));
				}
			}

			return mesh;
		} catch (Exception e) {
			throw new RuntimeException("Failed to load obj file: " + modelPath, e);
		}
	}

	private static void parseLines(List<String> lines,
			ArrayList<Float> vertices,
			ArrayList<Float> normals,
			ArrayList<Float> texture,
			float scale) {
		for (String line : lines) {
			String trimmed = line.trim();
			String[] split = trimmed.split("\\s+");
			if (trimmed.startsWith("v ")) {
				vertices.add(Float.parseFloat(split[ 1 ]) * scale);
				vertices.add(Float.parseFloat(split[ 2 ]) * scale);
				vertices.add(Float.parseFloat(split[ 3 ]) * scale);
			}
			else if (trimmed.startsWith("vn ")) {
				normals.add(Float.parseFloat(split[ 1 ]));
				normals.add(Float.parseFloat(split[ 2 ]));
				normals.add(Float.parseFloat(split[ 3 ]));
			}
			else if (trimmed.startsWith("vt ")) {
				texture.add(Float.parseFloat(split[ 1 ]));
				texture.add(Float.parseFloat(split[ 2 ]));
			}
		}
	}

	private static Map<String, MtlMaterial> loadMtl(String mtlPath) {
		Map<String, MtlMaterial> materials = new HashMap<>();
		try {
			List<String> lines = Files.readAllLines(Paths.get(mtlPath));
			MtlMaterial currentMaterial = null;
			for (String line : lines) {
				String trimmed = line.trim();
				String[] split = trimmed.split("\\s+");
				if (trimmed.startsWith("newmtl ") && split.length > 1) {
					String name = split[ 1 ];
					currentMaterial = new MtlMaterial();
					materials.put(name, currentMaterial);
				}
				else if (trimmed.startsWith("Kd ") && currentMaterial != null && split.length > 3) {
					float r = Float.parseFloat(split[ 1 ]);
					float g = Float.parseFloat(split[ 2 ]);
					float b = Float.parseFloat(split[ 3 ]);
					currentMaterial.Kd = new float[] { r, g, b };
				}
				else if (trimmed.startsWith("Ke ") && currentMaterial != null && split.length > 3) {
					float r = Float.parseFloat(split[ 1 ]);
					float g = Float.parseFloat(split[ 2 ]);
					float b = Float.parseFloat(split[ 3 ]);
					currentMaterial.Ke = new float[] { r, g, b };
				}
				else if (trimmed.startsWith("map_Kd ") && currentMaterial != null &&
						 split.length > 1) {
					String fullPath = trimmed.substring("map_Kd ".length()).trim();
					currentMaterial.map_Kd = getFilename(fullPath);
				}
				else if (trimmed.startsWith("map_Ke ") && currentMaterial != null &&
						 split.length > 1) {
					String fullPath = trimmed.substring("map_Ke ".length()).trim();
					currentMaterial.map_Ke = getFilename(fullPath);
				}
			}
		} catch (Exception e) {
			System.err.println("Failed to load mtl file: " + mtlPath + ". " + e.getMessage());
		}
		return materials;
	}

	private static String resolveTexturePath(String modelPath, String textureFilename) {
		Path modelFilePath = Paths.get(modelPath);
		Path parent = modelFilePath.getParent();
		if (parent != null) {
			Path sameFolder = parent.resolve(textureFilename);
			if (Files.exists(sameFolder)) {
				return sameFolder.toString();
			}
		}

		String modelName = modelFilePath.getFileName().toString();
		int lastDot = modelName.lastIndexOf('.');
		if (lastDot != -1) {
			modelName = modelName.substring(0, lastDot);
		}
		Path texturesFolder = Paths.get("src/main/resources/textures", modelName, textureFilename);
		if (Files.exists(texturesFolder)) {
			return texturesFolder.toString();
		}

		Path fallbackTextures = Paths.get("src/main/resources/textures", textureFilename);
		if (Files.exists(fallbackTextures)) {
			return fallbackTextures.toString();
		}

		return null;
	}

	private static String getFilename(String path) {
		int lastSlash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
		if (lastSlash != -1) {
			return path.substring(lastSlash + 1);
		}
		return path;
	}

	private static class MtlMaterial {
		float[] Kd = new float[] { 1.0f, 1.0f, 1.0f };
		float[] Ke = new float[] { 0.0f, 0.0f, 0.0f };
		String map_Kd = null;
		String map_Ke = null;
	}
}
