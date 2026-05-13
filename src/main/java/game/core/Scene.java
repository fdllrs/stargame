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

        createSolarSystem();

        player = new Player();
    }

    public void update(Camera camera, Boolean isMoving) {
        player.updateFromCamera(camera, isMoving);


        for (Planet planet : planets) {
            planet.orbit();

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


}