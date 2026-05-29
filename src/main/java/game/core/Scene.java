package game.core;

import engine.graphics.Camera;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import game.objects.*;
import org.joml.*;

import java.util.List;

public class Scene {
    private final Player player;
    private final Starfield starfield;
    private StarSystem starSystem;
    private CelestialBody selectedObject;

    public Scene() {
        player = new Player();
        starSystem = new StarSystem(8);
        starfield = new Starfield(500, 10000);
    }

    public void update(Camera camera, boolean isMoving, float deltaTime) {
        starSystem.update(deltaTime);

        checkCollisions(camera);
        player.syncWithCamera(camera, isMoving);

    }

    private void checkCollisions(Camera camera) {
        Vector3f playerPos = camera.getPosition();
        Vector3f velocity = camera.getVelocity();
        float playerRadius = player.getRadius();

        for (CelestialBody body : starSystem.getAllBodies()) {
            Vector3f bodyPos = body.getPosition();
            float bodyRadius = body.getRadius();
            float minDistance = bodyRadius + playerRadius;
            float distance = playerPos.distance(bodyPos);

            if (distance >= minDistance)
                continue;

            Vector3f normal = new Vector3f();
            if (distance > 0.001f) {
                playerPos.sub(bodyPos, normal).normalize();
            } else {
                normal.set(0.0f, 0.0f, 1.0f);
            }

            Vector3f correctedPos = new Vector3f(bodyPos).add(new Vector3f(normal).mul(minDistance));
            camera.position.set(correctedPos);

            float velocityDotNormal = velocity.dot(normal);
            if (velocityDotNormal < 0.0f) {
                float restitution = 0.3f;
                velocity.sub(new Vector3f(normal).mul((1.0f + restitution) * velocityDotNormal)).mul(0.7f);
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

    private CelestialBody pickObject(float mouseX, float mouseY, long windowHandle, Camera camera) {
        Vector3f rayOrigin = calculateRayOrigin(camera);
        Vector3f rayDirection = calculateMouseRay(mouseX, mouseY, windowHandle, camera);
        return calculateClosestObject(rayOrigin, rayDirection);
    }

    private CelestialBody calculateClosestObject(Vector3f rayOrigin, Vector3f rayDirection) {
        float closestDistance = Float.MAX_VALUE;
        CelestialBody closestObject = null;
        for (CelestialBody body : starSystem.getAllBodies()) {
            Vector3f center = body.getPosition();
            float radius = body.getRadius();

            Vector2f result = new Vector2f();
            boolean hit = Intersectionf.intersectRaySphere(rayOrigin, rayDirection, center, radius * radius, result);

            if (hit && result.x >= 0 && result.x < closestDistance) {
                closestDistance = result.x;
                closestObject = body;
            }
        }

        return closestObject;
    }

    private Vector3f calculateMouseRay(float mouseX, float mouseY, long windowHandle, Camera camera) {
        Vector2i screenSize = Window.getWindowSize(windowHandle);

        float ndcX = (2.0f * mouseX) / screenSize.x - 1.0f;
        float ndcY = 1.0f - (2.0f * mouseY) / screenSize.y;

        Matrix4f inverseViewProjection = new Matrix4f(camera.getProjectionMatrix()).mul(camera.getViewMatrix())
                                                                                   .invert();

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

    public void recreateStarSystem() {
        starSystem.cleanupAll();
        starSystem = new StarSystem(10);
    }

    public Starfield getStarfield() {
        return starfield;
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

    public Vector3f getStarPosition() {
        return starSystem.getStar().getPosition();
    }
}