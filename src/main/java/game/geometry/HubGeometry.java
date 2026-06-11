package game.geometry;

import engine.graphics.Mesh;
import engine.graphics.ModelLoader;

public class HubGeometry {
    public static Mesh getHubMesh() {

        return ModelLoader.loadModelObj("src/main/resources/models/hub.obj", 0.5f);

    }

}
