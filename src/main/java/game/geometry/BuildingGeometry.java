package game.geometry;

import engine.graphics.Mesh;
import engine.graphics.ModelLoader;
import org.jetbrains.annotations.NotNull;

public class BuildingGeometry {
	private static Mesh extractorMesh;
	private static Mesh siloMesh;
	private static Mesh solarArrayMesh;
	private static Mesh nuclearReactorMesh;
	private static Mesh alloySmelterMesh;
	private static Mesh chemicalPlantMesh;
	private static Mesh engineFabricatorMesh;
	private static Mesh researchLabMesh;

	public static Mesh generateBox(float sizeX, float sizeY, float sizeZ) {
		float x = sizeX / 2.0f;
		float y = sizeY / 2.0f;
		float z = sizeZ / 2.0f;

		// 24 vertices (4 per face for texturing / lighting)
		float[] positions = {
				// Front face
				-x, -y, z, x, -y, z, x, y, z, -x, y, z,
				// Back face
				-x, -y, -z, -x, y, -z, x, y, -z, x, -y, -z,
				// Top face
				-x, y, -z, -x, y, z, x, y, z, x, y, -z,
				// Bottom face
				-x, -y, -z, x, -y, -z, x, -y, z, -x, -y, z,
				// Right face
				x, -y, -z, x, y, -z, x, y, z, x, -y, z,
				// Left face
				-x, -y, -z, -x, -y, z, -x, y, z, -x, y, -z, };

		// Surface normals (necessary for lighting and reflections)
		float[] normals = {
				// Front
				0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
				// Back
				0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,
				// Top
				0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
				// Bottom
				0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,
				// Right
				1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
				// Left
				-1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, };

		// Indices mapped to triangles
		int[] indices = {
				0, 1, 2, 0, 2, 3,    // front
				4, 5, 6, 4, 6, 7,    // back
				8, 9, 10, 8, 10, 11,    // top
				12, 13, 14, 12, 14, 15,    // bottom
				16, 17, 18, 16, 18, 19,    // right
				20, 21, 22, 20, 22, 23,    // left
		};

		// Standard texture coordinates (empty since we use solid colors)
		float[] uvs = new float[ 24 * 2 ];

		return Mesh.create3D(positions, indices, normals, uvs);
	}

	public static Mesh getAlloySmelterMesh() {
		if (alloySmelterMesh == null) {
			alloySmelterMesh = loadModel("smelter", 0.05f);
		}
		return alloySmelterMesh;
	}

	public static Mesh getChemicalPlantMesh() {
		if (chemicalPlantMesh == null) {
			chemicalPlantMesh = loadModel("chemical", 0.04f);
		}
		return chemicalPlantMesh;
	}

	public static Mesh getEngineFabricatorMesh() {
		if (engineFabricatorMesh == null) {
			engineFabricatorMesh = loadModel("engine_fabricator", 0.025f);
		}
		return engineFabricatorMesh;
	}

	public static Mesh getExtractorMesh() {
		if (extractorMesh == null) {
			extractorMesh = loadModel("extractor", 0.01f);
		}
		return extractorMesh;
	}

	public static Mesh getNuclearReactorMesh() {
		if (nuclearReactorMesh == null) {
			nuclearReactorMesh = loadModel("reactor", 0.05f);
		}
		return nuclearReactorMesh;
	}

	public static Mesh getResearchLabMesh() {
		if (researchLabMesh == null) {
			researchLabMesh = loadModel("lab", 0.0025f);
		}
		return researchLabMesh;
	}

	public static Mesh getSiloMesh() {
		if (siloMesh == null) {
			siloMesh = loadModel("silo", 0.05f);
		}
		return siloMesh;
	}

	public static Mesh getSolarPanelMesh() {
		if (solarArrayMesh == null) {
			solarArrayMesh = loadModel("solarPanel", 0.05f);
		}
		return solarArrayMesh;
	}

	@NotNull
	private static Mesh loadModel(String modelName, float scale) {
		return ModelLoader.loadModelObj("src/main/resources/models/" + modelName + ".obj", scale);
	}
}
