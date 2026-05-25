package game.objects;

import engine.graphics.ShaderProgram;
import game.builder.PlanetBuilder;
import game.builder.StarBuilder;
import org.joml.Vector3f;

import java.util.ArrayList;

public class StarSystem {

    private final Star star;
    private final ArrayList<Planet> planets;

    public StarSystem(Star star, ArrayList<Planet> planets) {
        this.star = star;
        this.planets = planets;
    }

    public StarSystem(int planetAmount) {
        this.star = new StarBuilder().build();
        this.planets = new ArrayList<>();

        placePlanets(planetAmount);
        namePlanets();
    }

    private void placePlanets(int planetAmount) {
        for (int i = 0; i < planetAmount; i++) {
            this.planets.add(new PlanetBuilder(star).build());
        }
    }

    private void namePlanets() {
        int nameSufix = 1;
        for (Planet planet : planets) {
            planet.setName(star.getName().split(" ")[1] + " " + nameSufix);
            nameSufix++;
        }
    }

    public ArrayList<Planet> getPlanets() {
        return planets;
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

    public Star getStar() {
        return star;
    }

    public ArrayList<CelestialBody> getAllBodies() {

        ArrayList<CelestialBody> celestialBodies = new ArrayList<CelestialBody>(planets);
        celestialBodies.add(star);
        return celestialBodies;

    }

    public void update(float deltaTime) {
        star.update(deltaTime);

        for (Planet planet: planets){
            planet.update(deltaTime);
        }

    }
}
