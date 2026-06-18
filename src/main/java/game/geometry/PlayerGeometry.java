package game.geometry;

import engine.graphics.Mesh;
import engine.graphics.ModelLoader;

public class PlayerGeometry {
	public static Mesh getPlayerMesh() {

		return ModelLoader.loadModelObj("src/main/resources/models/spaceship.obj", 0.12f);
	}
}
