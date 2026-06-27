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

	public static Mesh getAlloySmelterMesh() {
		if (alloySmelterMesh == null) {
			alloySmelterMesh = loadModel("smelter", 0.05f);
		}
		return alloySmelterMesh;
	}

	@NotNull
	private static Mesh loadModel(String modelName, float scale) {
		return ModelLoader.loadModelObj("src/main/resources/models/" + modelName + ".obj", scale);
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
}
