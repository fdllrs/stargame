package game.objects.celestialBodies;

import engine.graphics.Mesh;
import engine.ui.Describable;
import engine.ui.panels.HubPanelController;
import engine.ui.panels.InfoPanelController;
import engine.ui.text.FontAtlas;
import game.components.OrbitComponent;
import game.components.StorageComponent;
import game.geometry.HubGeometry;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Hub extends SpaceBody implements Describable {
    private static final Mesh hubMesh = HubGeometry.getHubMesh();
    private final Planet parentPlanet;
    private final OrbitComponent orbit;
    public int level = 1;

    public Hub(Planet planet) {
        super(hubMesh, new Vector3f(1f, 1f, 1f), new Vector3f(0, 0, 0));
        this.name = planet.getName() + " Hub";
        this.parentPlanet = planet;
        this.radius = 5.0f; // Enable clickability / selection collision

        float orbitDistance = planet.getRadius() * 2.5f;
        float orbitSpeed = 0.2f;
        float initialAngle = 0.0f;

        this.orbit = new OrbitComponent(planet, orbitDistance, orbitSpeed, initialAngle);
        this.orbit.update(this, 0f);
        planet.satellites.add(this);
    }

    @Override public String getDisplayName() {
        return name;
    }

    @Override public List<Map.Entry<String, String>> getDisplayProperties() {
        return List.of(Map.entry("Parent Planet", parentPlanet.getName()),
                       Map.entry("Level", String.valueOf(level)));
    }

    public void update(float deltaTime) {
        orbit.update(this, deltaTime);

        // Tidally locked: match rotation.y to the opposite of the orbital angle (to
        // fix opposite rotation)
        this.rotation.y = -(float) Math.toDegrees(orbit.angle);

        updateModelMatrix();
    }

    @Override
    public InfoPanelController getPanelController(StorageComponent playerStorage,
                                                  FontAtlas font,
                                                  float width,
                                                  Runnable onRebuild,
                                                  Consumer<SpaceBody> onSelectTarget) {
        return new HubPanelController(this, playerStorage, font, width, onRebuild);
    }
}
