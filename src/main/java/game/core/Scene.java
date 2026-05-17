package game.core;

import engine.graphics.Camera;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import game.factory.PlanetFactory;
import game.objects.Planet;
import game.objects.Player;
import game.objects.Star;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;


public class Scene {

    private final Player player;
    private final List<Planet> planets;
    private final List<Star> stars;

    public Scene() {
        planets = new ArrayList<>();
        stars = new ArrayList<>();
        player = new Player();

        createSolarSystem();
    }


    private Planet pickPlanet(float mouseX, float mouseY, long windowHandle, Camera camera) {
        Vector3f rayOrigin = calculateRayOrigin(camera);
        Vector3f rayDirection = calculateMouseRay(mouseX, mouseY, windowHandle, camera);

        return calculateClosestPlanet(rayOrigin, rayDirection);
    }
    private Planet calculateClosestPlanet(Vector3f rayOrigin, Vector3f rayDirection ) {
        float closestDistance = Float.MAX_VALUE;
        Planet closestPlanet = null;

        for (Planet planet : planets) {
            Vector3f planetCenter = planet.getPosition();
            float planetRadius = planet.getPlanetRadius();

            Vector2f intersectionResult = new Vector2f();

            boolean hit = Intersectionf.intersectRaySphere(
                    rayOrigin,
                    rayDirection,
                    planetCenter,
                    planetRadius * planetRadius,
                    intersectionResult
            );
            if (!hit) {
                continue;
            }

            float hitDistance = intersectionResult.x;

            if (hitDistance >= 0 && hitDistance < closestDistance) {
                closestDistance = hitDistance;
                closestPlanet = planet;
            }
        }
        return closestPlanet;
    }
    private Vector3f calculateMouseRay(float mouseX, float mouseY, long windowHandle, Camera camera) {
        Vector2i screenSize = Window.getWindowSize(windowHandle);

        float ndcX = (2.0f * mouseX) / screenSize.x - 1.0f;
        float ndcY = 1.0f - (2.0f * mouseY) / screenSize.y;

        Matrix4f inverseViewProjection = new Matrix4f(camera.getProjectionMatrix())
                .mul(camera.getViewMatrix())
                .invert();

        Vector4f nearPoint = new Vector4f(ndcX, ndcY, -1.0f, 1.0f);
        Vector4f farPoint = new Vector4f(ndcX, ndcY, 1.0f, 1.0f);

        inverseViewProjection.transform(nearPoint);
        inverseViewProjection.transform(farPoint);

        nearPoint.div(nearPoint.w);
        farPoint.div(farPoint.w);

        return new Vector3f(
                farPoint.x - nearPoint.x,
                farPoint.y - nearPoint.y,
                farPoint.z - nearPoint.z
        ).normalize();
    }
    private Vector3f calculateRayOrigin(Camera camera) {
        Matrix4f inverseView = new Matrix4f(camera.getViewMatrix()).invert();

        return inverseView.transformPosition(new Vector3f(0, 0, 0));
    }

    private void onPlanetClicked(Planet planet) {
        System.out.println("Clicked planet at: " + planet.getPosition());

        // Later you can do things like:
        // - open a UI panel
        // - select/highlight the planet
        // - move the player toward it
        // - show planet stats
    }

    public void update(Camera camera, Boolean isMoving) {
        player.syncWithCamera(camera, isMoving);

        Vector3f playerPosition = player.getPosition();
        for (Planet planet : planets) {
            planet.orbit();
            float planetRadius = planet.getPlanetRadius();
            float planetOrbitInfluence = planetRadius +5f;

            if (planet.getPosition().distance(playerPosition) < planetOrbitInfluence) {
                camera.zeroAcceleration(true);
            }
        }
    }

    public void render(ShaderProgram shader) {
        for (Planet planet : planets) {
            planet.render(shader);
        }

        for (Star star : stars) {
            star.render(shader);
        }
        player.render(shader);
    }

    public void cleanup() {
        for (Planet planet : planets) {
            planet.cleanup();
        }

        for (Star star : stars) {
            star.cleanup();
        }

        player.cleanup();
    }

    private void createSolarSystem() {
        stars.add(new Star(
                120f,
                new Vector3f(0, 0, 0),
                new Vector3f(1f, 1f, 0f)
        ));

        for (int i = 0; i < 6; i++) {
            Planet planet = new PlanetFactory().generatePlanet(stars.getFirst());
            planets.add(planet);
        }
    }


    public List<Planet> getPlanets() {
        return planets;
    }
    public List<Star> getStars() {
        return stars;
    }

    public Planet planetClicked(float mouseX, float mouseY, long windowHandle, Camera camera) {

        return pickPlanet(
                mouseX,
                mouseY,
                windowHandle,
                camera
        );
    }
}