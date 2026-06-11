package game.core;

import engine.graphics.Camera;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import game.objects.Player;
import game.objects.StarSystem;
import game.objects.Starfield;
import game.objects.celestialBodies.Planet;
import game.objects.celestialBodies.SpaceBody;
import game.objects.celestialBodies.Star;
import game.objects.entities.Light;
import org.joml.*;

import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL13C.*;

public class Scene {
    private static final float MAX_SELECTION_DISTANCE = 6000.0f;
    private static final float DOCK_RANGE_MULTIPLIER = 4.0f;
    private static final float DOCK_SPEED_THRESHOLD = 30.0f;
    private static final float UNDOCK_SPEED_THRESHOLD = 80.0f;
    private final Player player;
    private final Starfield starfield;
    private final Vector3f dockedBodyLastPos = new Vector3f();
    private StarSystem starSystem;
    private SpaceBody selectedObject;
    private SpaceBody dockedBody;

    public Scene() {
        player = new Player();
        starSystem = new StarSystem(10);
        starfield = new Starfield(500, 10000);
    }

    private SpaceBody calculateClosestObject(Vector3f rayOrigin, Vector3f rayDirection) {
        float closestDistance = Float.MAX_VALUE;
        SpaceBody closestObject = null;
        for (SpaceBody body : starSystem.getAllBodies()) {
            Vector3f center = body.getPosition();
            float radius = body.getRadius();

            Vector2f result = new Vector2f();
            boolean hit = Intersectionf.intersectRaySphere(rayOrigin,
                                                           rayDirection,
                                                           center,
                                                           radius * radius,
                                                           result);

            if (hit && result.x >= 0 && result.x < closestDistance) {
                closestDistance = result.x;
                closestObject = body;
            }
        }
        if (closestDistance >= MAX_SELECTION_DISTANCE) {
            return null;
        }

        return closestObject;
    }

    private Vector3f calculateMouseRay(float mouseX,
                                       float mouseY,
                                       long windowHandle,
                                       Camera camera) {
        Vector2i screenSize = Window.getWindowSize(windowHandle);

        float ndcX = (2.0f * mouseX) / screenSize.x - 1.0f;
        float ndcY = 1.0f - (2.0f * mouseY) / screenSize.y;

        Matrix4f inverseViewProjection = new Matrix4f(camera.getProjectionMatrix()).mul(
                camera.getViewMatrix()).invert();

        Vector4f nearPoint = new Vector4f(ndcX, ndcY, -1.0f, 1.0f);
        Vector4f farPoint = new Vector4f(ndcX, ndcY, 1.0f, 1.0f);

        inverseViewProjection.transform(nearPoint);
        inverseViewProjection.transform(farPoint);

        nearPoint.div(nearPoint.w);
        farPoint.div(farPoint.w);

        return new Vector3f(farPoint.x - nearPoint.x,
                            farPoint.y - nearPoint.y,
                            farPoint.z - nearPoint.z).normalize();
    }

    private Vector3f calculateRayOrigin(Camera camera) {
        return new Matrix4f(camera.getViewMatrix()).invert()
                                                   .transformPosition(new Vector3f());
    }

    private void checkCollisions(Camera camera) {
        Vector3f playerPos = camera.getPosition();
        Vector3f velocity = camera.getVelocity();
        float playerRadius = player.getRadius();

        for (SpaceBody body : starSystem.getAllBodies()) {
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

            Vector3f correctedPos = new Vector3f(bodyPos).add(new Vector3f(normal).mul(
                    minDistance));
            camera.position.set(correctedPos);

            float velocityDotNormal = velocity.dot(normal);
            if (velocityDotNormal < 0.0f) {
                float restitution = 0.3f;
                velocity.sub(new Vector3f(normal).mul(
                        (1.0f + restitution) * velocityDotNormal)).mul(0.7f);
            }
        }
    }

    public void cleanup() {
        starSystem.cleanupAll();
        player.cleanup();
    }

    public Star closestStarToPlayer() {
        Star closest = null;
        float minDist = Float.MAX_VALUE;
        Vector3f playerPos = player.getPosition();
        for (Star star : starSystem.getStars()) {
            float d = star.getPosition().distance(playerPos);
            if (d < minDist) {
                minDist = d;
                closest = star;
            }
        }
        return closest;

    }

    public List<Planet> getPlanets() {
        return starSystem.getPlanets();
    }

    public Player getPlayer() {
        return player;
    }

    public SpaceBody getSelectedObject() {
        return selectedObject;
    }

    public StarSystem getStarSystem() {
        return starSystem;
    }

    public Starfield getStarfield() {
        return starfield;
    }

    public SpaceBody objectClicked(float mouseX,
                                   float mouseY,
                                   long windowHandle,
                                   Camera camera) {
        return pickObject(mouseX, mouseY, windowHandle, camera);
    }

    private SpaceBody pickObject(float mouseX,
                                 float mouseY,
                                 long windowHandle,
                                 Camera camera) {
        Vector3f rayOrigin = calculateRayOrigin(camera);
        Vector3f rayDirection = calculateMouseRay(mouseX, mouseY, windowHandle, camera);
        return calculateClosestObject(rayOrigin, rayDirection);
    }

    public void recreateStarSystem() {
        starSystem.cleanupAll();
        starSystem = new StarSystem(10);
    }

    public void render(Renderer renderer, ShaderProgram shaderStar, Camera camera) {
        for (Planet planet : starSystem.getPlanets()) {
            Light starLight = planet.getHomeStar().getLight();
            planet.renderBody(renderer, camera, starLight);

            for (SpaceBody satellites : planet.satellites) {
                satellites.renderBody(renderer, camera, starLight);
            }
        }

        // Bind the primary star's light for the player rendering using the default shader
        ShaderProgram defaultShader = renderer.getDefaultShader();
        defaultShader.bind();
        defaultShader.setUniform("view", camera.getViewMatrix());
        defaultShader.setUniform("projection", camera.getProjectionMatrix());
        defaultShader.setUniform("viewPos", camera.getPosition());
        defaultShader.setUniform("lightSpaceMatrix",
                                 renderer.getCurrentLightSpaceMatrix());

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, renderer.getShadowDepthTex());
        defaultShader.setUniform("shadowMap", 1);
        glActiveTexture(GL_TEXTURE0);

        Light playerLight = starSystem.getStar() != null
                            ? starSystem.getStar().getLight()
                            : null;
        if (playerLight != null) {
            defaultShader.setUniform("lightPosition", playerLight.getPosition());
            defaultShader.setUniform("lightColor", playerLight.getColor());
        }
        player.render(defaultShader);
        defaultShader.unbind();

        // Render all unlit stars
        shaderStar.bind();
        shaderStar.setUniform("view", camera.getViewMatrix());
        shaderStar.setUniform("projection", camera.getProjectionMatrix());
        for (Star star : starSystem.getStars()) {
            star.render(shaderStar);
        }
        shaderStar.unbind();
    }

    public void tick() {
        starSystem.tickAllFacilities();
    }

    public void update(Camera camera, boolean isMoving, float deltaTime) {
        starSystem.updateAll(deltaTime);

        updateDocking(camera);
        checkCollisions(camera);
        player.syncWithCamera(camera, isMoving);

    }

    /**
     * Automatically dock to a nearby body when the player slows down,
     * and undock when they accelerate away.
     */
    private void updateDocking(Camera camera) {
        float speed = camera.getVelocity().length();

        if (dockedBody != null) {
            Vector3f currentPos = dockedBody.getPosition();
            camera.translate(currentPos.x - dockedBodyLastPos.x,
                             currentPos.y - dockedBodyLastPos.y,
                             currentPos.z - dockedBodyLastPos.z);
            dockedBodyLastPos.set(currentPos);

            if (speed > UNDOCK_SPEED_THRESHOLD) {
                dockedBody = null;
            }
            return;
        }

        if (speed >= DOCK_SPEED_THRESHOLD)
            return;

        Vector3f camPos = camera.getPosition();
        for (SpaceBody body : starSystem.getAllBodies()) {
            if (body instanceof Star)
                continue;
            float dockRange = body.getRadius() * DOCK_RANGE_MULTIPLIER;
            if (camPos.distance(body.getPosition()) < dockRange) {
                dockedBody = body;
                dockedBodyLastPos.set(dockedBody.getPosition());
                return;
            }
        }
    }

    public void updateSelectedObject(SpaceBody clicked) {
        if (selectedObject != null) {
            selectedObject.setSelected(false);
        }
        selectedObject = clicked;
        if (selectedObject != null) {
            selectedObject.setSelected(true);
        }
    }
}