package game.core;

import engine.graphics.Camera;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import game.objects.Player;
import game.objects.StarSystem;
import game.objects.Starfield;
import game.objects.celestialBodies.CelestialBody;
import game.objects.celestialBodies.Planet;
import game.objects.celestialBodies.Star;
import game.objects.entities.Light;
import org.joml.*;

import java.util.List;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL13C.*;

public class Scene {
    private static final float MAX_SELECTION_DISTANCE = 6000.0f;
    private final Player player;
    private final Starfield starfield;
    private StarSystem starSystem;
    private CelestialBody selectedObject;

    public Scene() {
        player = new Player();
        starSystem = new StarSystem(10);
        starfield = new Starfield(500, 10000);
    }

    public void update(Camera camera, boolean isMoving, float deltaTime) {
        starSystem.updateAll(deltaTime);

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

    public void render(Renderer renderer, ShaderProgram shaderStar, Camera camera) {
        for (Planet planet : starSystem.getPlanets()) {
            Light starLight = planet.getHomeStar().getLight();
            ShaderProgram planetShader = renderer.getShaderForType(planet.getType());
            planetShader.bind();
            planetShader.setUniform("view", camera.getViewMatrix());
            planetShader.setUniform("projection", camera.getProjectionMatrix());
            planetShader.setUniform("viewPos", camera.getPosition());
            planetShader.setUniform("lightSpaceMatrix",
                                    renderer.getCurrentLightSpaceMatrix());
            planetShader.setUniform("lightPosition", starLight.getPosition());
            planetShader.setUniform("lightColor", starLight.getColor());

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, renderer.getShadowDepthTex());
            planetShader.setUniform("shadowMap", 1);
            glActiveTexture(GL_TEXTURE0);

            planet.render(planetShader);
            planetShader.unbind();

            // Render facilities on this planet using the default shader
            if (!planet.getFacilities().isEmpty()) {
                ShaderProgram defaultShader = renderer.getDefaultShader();
                defaultShader.bind();
                defaultShader.setUniform("view", camera.getViewMatrix());
                defaultShader.setUniform("projection", camera.getProjectionMatrix());
                defaultShader.setUniform("viewPos", camera.getPosition());
                defaultShader.setUniform("lightSpaceMatrix",
                                         renderer.getCurrentLightSpaceMatrix());
                defaultShader.setUniform("lightPosition", starLight.getPosition());
                defaultShader.setUniform("lightColor", starLight.getColor());

                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, renderer.getShadowDepthTex());
                defaultShader.setUniform("shadowMap", 1);
                glActiveTexture(GL_TEXTURE0);

                planet.renderFacilities(defaultShader);
                defaultShader.unbind();
            }

            // Render extra components (like Gas Giant rings)
            planet.renderExtra(renderer, camera);

            // Render moons of this planet
            for (game.objects.celestialBodies.Moon moon : planet.getMoons()) {
                ShaderProgram moonShader = renderer.getShaderForType(moon.getType());
                moonShader.bind();
                moonShader.setUniform("view", camera.getViewMatrix());
                moonShader.setUniform("projection", camera.getProjectionMatrix());
                moonShader.setUniform("viewPos", camera.getPosition());
                moonShader.setUniform("lightSpaceMatrix",
                                      renderer.getCurrentLightSpaceMatrix());
                moonShader.setUniform("lightPosition", starLight.getPosition());
                moonShader.setUniform("lightColor", starLight.getColor());

                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, renderer.getShadowDepthTex());
                moonShader.setUniform("shadowMap", 1);
                glActiveTexture(GL_TEXTURE0);

                moon.render(moonShader);
                moonShader.unbind();

                // Render facilities on this moon using the default shader
                if (!moon.getFacilities().isEmpty()) {
                    ShaderProgram defaultShaderForMoon = renderer.getDefaultShader();
                    defaultShaderForMoon.bind();
                    defaultShaderForMoon.setUniform("view", camera.getViewMatrix());
                    defaultShaderForMoon.setUniform("projection",
                                                    camera.getProjectionMatrix());
                    defaultShaderForMoon.setUniform("viewPos", camera.getPosition());
                    defaultShaderForMoon.setUniform("lightSpaceMatrix",
                                                    renderer.getCurrentLightSpaceMatrix());
                    defaultShaderForMoon.setUniform("lightPosition",
                                                    starLight.getPosition());
                    defaultShaderForMoon.setUniform("lightColor", starLight.getColor());

                    glActiveTexture(GL_TEXTURE1);
                    glBindTexture(GL_TEXTURE_2D, renderer.getShadowDepthTex());
                    defaultShaderForMoon.setUniform("shadowMap", 1);
                    glActiveTexture(GL_TEXTURE0);

                    moon.renderFacilities(defaultShaderForMoon);
                    defaultShaderForMoon.unbind();
                }

                // Render extra components (like rings, if any)
                moon.renderExtra(renderer, camera);
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

    public void cleanup() {
        starSystem.cleanupAll();
        player.cleanup();
    }

    public List<Planet> getPlanets() {
        return starSystem.getPlanets();
    }

    public CelestialBody objectClicked(float mouseX,
                                       float mouseY,
                                       long windowHandle,
                                       Camera camera) {
        return pickObject(mouseX, mouseY, windowHandle, camera);
    }

    private CelestialBody pickObject(float mouseX,
                                     float mouseY,
                                     long windowHandle,
                                     Camera camera) {
        Vector3f rayOrigin = calculateRayOrigin(camera);
        Vector3f rayDirection = calculateMouseRay(mouseX, mouseY, windowHandle, camera);
        return calculateClosestObject(rayOrigin, rayDirection);
    }

    private CelestialBody calculateClosestObject(Vector3f rayOrigin,
                                                 Vector3f rayDirection) {
        float closestDistance = Float.MAX_VALUE;
        CelestialBody closestObject = null;
        for (CelestialBody body : starSystem.getAllBodies()) {
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

    public Player getPlayer() {
        return player;
    }

    public StarSystem getStarSystem() {
        return starSystem;
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

    public void tick() {
        starSystem.tickAllFacilities();
    }
}