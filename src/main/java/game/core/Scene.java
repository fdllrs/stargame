package game.core;

import engine.graphics.Camera;
import engine.graphics.ShaderProgram;
import engine.ui.Describable;
import engine.window.Window;
import game.objects.*;
import org.joml.*;

import java.util.List;

public class Scene {

    private final Player player;
    private StarSystem starSystem;

    private CelestialBody selectedObject;

    public Scene() {
        player = new Player();
        starSystem = new StarSystem(8);
    }

    public void update(Camera camera, boolean isMoving, float deltaTime) {
        player.syncWithCamera(camera, isMoving);

        Vector3f playerPosition = player.getPosition();

        starSystem.getStar().update(deltaTime);

        for (Planet planet : starSystem.getPlanets()) {
            planet.orbit(deltaTime);

            float planetRadius = planet.getPlanetRadius();
            float orbitInfluence = planetRadius + 5f;

            if (planet.getPosition().distance(playerPosition) < orbitInfluence) {
                camera.zeroAcceleration(true);
            }
        }
    }

    public void render(ShaderProgram shader) {
        starSystem.renderAll(shader);
        player.render(shader);
    }

    public void cleanup() {
        starSystem.cleanupAll();
        player.cleanup();
    }

    public List<Planet> getPlanets() {
        return starSystem.getPlanets();
    }

    public CelestialBody objectClicked(float mouseX, float mouseY, long windowHandle, Camera camera) {
        return pickObject(mouseX, mouseY, windowHandle, camera);
    }

    public void recreateStarSystem() {
        starSystem.cleanupAll();
        starSystem = new StarSystem(10);
    }


    private CelestialBody pickObject(float mouseX, float mouseY, long windowHandle, Camera camera) {
        Vector3f rayOrigin = calculateRayOrigin(camera);
        Vector3f rayDirection = calculateMouseRay(mouseX, mouseY, windowHandle, camera);
        return calculateClosestObject(rayOrigin, rayDirection);
    }

    private CelestialBody calculateClosestObject(Vector3f rayOrigin, Vector3f rayDirection) {
        float closestDistance = Float.MAX_VALUE;
        CelestialBody closestObject = null;

        for (Planet planet : starSystem.getPlanets()) {
            Vector3f planetCenter = planet.getPosition();
            float planetRadius = planet.getPlanetRadius();
            Vector2f result = new Vector2f();

            boolean hit = Intersectionf.intersectRaySphere(rayOrigin, rayDirection, planetCenter, planetRadius * planetRadius, result);

            if (hit && result.x >= 0 && result.x < closestDistance) {
                closestDistance = result.x;
                closestObject = planet;
            }
        }

        Star star = starSystem.getStar();
        Vector3f starCenter = star.getPosition();
        float starRadius = star.getRadius();
        Vector2f result = new Vector2f();

        boolean hit = Intersectionf.intersectRaySphere(rayOrigin, rayDirection, starCenter, starRadius * starRadius, result);

        if (hit && result.x >= 0 && result.x < closestDistance) {
            closestObject = star;
        }

        return closestObject;
    }

    private Vector3f calculateMouseRay(float mouseX, float mouseY, long windowHandle, Camera camera) {
        Vector2i screenSize = Window.getWindowSize(windowHandle);

        float ndcX = (2.0f * mouseX) / screenSize.x - 1.0f;
        float ndcY = 1.0f - (2.0f * mouseY) / screenSize.y;

        Matrix4f inverseViewProjection = new Matrix4f(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).invert();

        Vector4f nearPoint = new Vector4f(ndcX, ndcY, -1.0f, 1.0f);
        Vector4f farPoint = new Vector4f(ndcX, ndcY, 1.0f, 1.0f);

        inverseViewProjection.transform(nearPoint);
        inverseViewProjection.transform(farPoint);

        nearPoint.div(nearPoint.w);
        farPoint.div(farPoint.w);

        return new Vector3f(farPoint.x - nearPoint.x, farPoint.y - nearPoint.y, farPoint.z - nearPoint.z).normalize();
    }

    private Vector3f calculateRayOrigin(Camera camera) {
        return new Matrix4f(camera.getViewMatrix()).invert().transformPosition(new Vector3f());
    }

    public CelestialBody getSelectedObject() {
        return selectedObject;
    }

    public void updateSelectedObject(CelestialBody clicked) {
        if (selectedObject != null) {
            selectedObject.setSelected(false);
        }
        selectedObject = clicked;
        if (selectedObject != null) {
            selectedObject.setSelected(true);
        }
    }
}