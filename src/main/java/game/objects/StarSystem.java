package game.objects;

import engine.graphics.ShaderProgram;
import game.builder.PlanetBuilder;
import game.builder.StarBuilder;
import game.objects.celestialBodies.CelestialBody;
import game.objects.celestialBodies.Planet;
import game.objects.celestialBodies.Star;

import java.util.ArrayList;
import java.util.List;

public class StarSystem {
    private final Star star;
    private final ArrayList<Planet> planets;

    public StarSystem(int planetAmount) {
        this(new StarBuilder().build(), new ArrayList<>());

        generateRandomPlanets(planetAmount);
        nameAllPlanets();
    }

    public StarSystem(Star star, ArrayList<Planet> planets) {
        this.star = star;
        this.planets = planets;
        nameAllPlanets();

    }

    private void nameAllPlanets() {
        int nameSufix = 1;
        for (Planet planet : planets) {
            planet.setName(star.getName().split(" ")[1] + " " + nameSufix);
            nameSufix++;
        }
    }

    private void generateRandomPlanets(int planetAmount) {
        for (int i = 0; i < planetAmount; i++) {
            this.planets.add(new PlanetBuilder(star).build());
        }
    }

    public void renderAll(ShaderProgram shader) {
        for (Planet planet : planets) {
            planet.render(shader);
        }
        star.render(shader);
    }

    public void cleanupAll() {
        for (Planet planet : planets) {
            planet.cleanup();
        }

        star.cleanup();
    }

    public ArrayList<CelestialBody> getAllBodies() {

        ArrayList<CelestialBody> celestialBodies = new ArrayList<>(planets);
        celestialBodies.add(star);
        return celestialBodies;

    }

    public void update(float deltaTime) {
        star.update(deltaTime);

        for (Planet planet : planets) {
            planet.update(deltaTime);
        }

    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public Star getStar() {
        return star;
    }
}
