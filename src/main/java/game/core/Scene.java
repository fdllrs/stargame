package game.core;

import engine.graphics.Camera;
import engine.graphics.ShaderProgram;
import game.objects.Planet;
import game.objects.Player;
import game.objects.Star;
import org.joml.Vector3f;

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
            planets.add(new Planet(stars.getFirst()));
        }
    }

    public List<Planet> getPlanets() {
        return planets;
    }


    public List<Star> getStars() {
        return stars;
    }


}