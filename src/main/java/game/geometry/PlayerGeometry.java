package game.geometry;

import engine.graphics.Mesh;
import engine.graphics.ModelLoader;

public class PlayerGeometry {
    public static Mesh generatePlayerMesh() {

        return ModelLoader.loadModelObj("src/main/resources/models/spaceship.obj");
    }
}
