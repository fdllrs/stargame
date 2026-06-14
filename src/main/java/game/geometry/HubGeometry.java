package game.geometry;

import engine.graphics.Mesh;
import engine.graphics.ModelLoader;

public class HubGeometry {

	private static Mesh hubMesh;

	public static Mesh getHubMesh() {

		if (hubMesh == null) {
			hubMesh = ModelLoader.loadModelObj("src/main/resources/models/hub.obj", 0.7f);
		}
		return hubMesh;
	}
}
