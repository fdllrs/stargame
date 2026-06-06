package game.objects;

import game.builder.PlanetBuilder;
import game.builder.StarBuilder;
import game.objects.celestialBodies.CelestialBody;
import game.objects.celestialBodies.Planet;
import game.objects.celestialBodies.Star;

import java.util.ArrayList;
import java.util.List;

public class StarSystem {
    private final Star star;
    private final List<Star> stars;
    private final ArrayList<Planet> planets;

    public StarSystem(int planetAmount) {
        this.star = new StarBuilder().build();
        this.stars = new ArrayList<>(List.of(this.star));
        this.planets = new ArrayList<>();

        generateRandomPlanets(planetAmount);
        nameAllPlanets();
    }

    private void nameAllPlanets() {
        int nameSufix = 1;
        for (Planet planet : planets) {
            String starName = planet.getHomeStar().getName();
            String[] parts = starName.split(" ");
            String baseName = parts.length > 1 ? parts[1] : starName;
            planet.setName(baseName + " " + nameSufix);
            nameSufix++;
        }
    }

    private void generateRandomPlanets(int planetAmount) {
        for (int i = 0; i < planetAmount; i++) {
            this.planets.add(PlanetBuilder.createRandom(star).build());
        }
    }

    public StarSystem(Star star, ArrayList<Planet> planets) {
        this.star = star;
        this.stars = new ArrayList<>(List.of(star));
        this.planets = planets;
        nameAllPlanets();
    }

    public StarSystem(List<Star> stars, ArrayList<Planet> planets) {
        this.star = stars.isEmpty() ? null : stars.getFirst();
        this.stars = new ArrayList<>(stars);
        this.planets = planets;
        nameAllPlanets();
    }

    public void cleanupAll() {
        for (Planet planet : planets) {
            planet.cleanup();
        }

        for (Star s : stars) {
            s.cleanup();
        }
    }

    public ArrayList<CelestialBody> getAllBodies() {
        ArrayList<CelestialBody> celestialBodies = new ArrayList<>(planets);
        celestialBodies.addAll(stars);
        return celestialBodies;
    }

    public void updateAll(float deltaTime) {
        for (Star s : stars) {
            s.update(deltaTime);
        }

        for (Planet planet : planets) {
            planet.update(deltaTime);
        }
    }

    public void tickAllFacilities() {
        for (Planet planet : planets) {
            planet.tickFacilities();
        }
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public Star getStar() {
        return star;
    }

    public List<Star> getStars() {
        return stars;
    }
}
