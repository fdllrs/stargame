package game.objects;

import game.builder.PlanetBuilder;
import game.builder.StarBuilder;
import game.objects.celestialBodies.Moon;
import game.objects.celestialBodies.Planet;
import game.objects.celestialBodies.SpaceBody;
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

    private org.joml.Vector3f[] generateColorsForType(game.info.PlanetType type) {
        org.joml.Vector3f colorA;
        org.joml.Vector3f colorB;
        java.util.Random RANDOM = new java.util.Random();
        if (type == game.info.PlanetType.ROCKY) {
            int archetype = RANDOM.nextInt(3);
            if (archetype == 0) {
                float h1 = (RANDOM.nextFloat() * 0.15f + 0.95f) % 1.0f;
                float h2 = (h1 + (RANDOM.nextFloat() * 0.08f - 0.04f) + 1.0f) % 1.0f;
                float s1 = 0.3f + RANDOM.nextFloat() * 0.5f;
                float s2 = Math.max(0.2f,
                                    Math.min(0.9f,
                                             s1 + (RANDOM.nextFloat() * 0.2f - 0.1f)));
                float l1 = 0.15f + RANDOM.nextFloat() * 0.35f;
                float l2 = Math.max(0.1f,
                                    Math.min(0.7f,
                                             l1 + (RANDOM.nextFloat() * 0.2f - 0.1f)));
                colorA = game.builder.PlanetBuilder.hslToRgb(h1, s1, l1);
                colorB = game.builder.PlanetBuilder.hslToRgb(h2, s2, l2);
            } else if (archetype == 1) {
                float h1 = RANDOM.nextFloat();
                float h2 = RANDOM.nextFloat();
                float s1 = RANDOM.nextFloat() * 0.12f;
                float s2 = RANDOM.nextFloat() * 0.12f;
                float l1 = 0.15f + RANDOM.nextFloat() * 0.25f;
                float l2 = 0.35f + RANDOM.nextFloat() * 0.35f;
                colorA = game.builder.PlanetBuilder.hslToRgb(h1, s1, l1);
                colorB = game.builder.PlanetBuilder.hslToRgb(h2, s2, l2);
            } else {
                float h1 = 0.5f + RANDOM.nextFloat() * 0.15f;
                float h2 = (h1 + (RANDOM.nextFloat() * 0.06f - 0.03f) + 1.0f) % 1.0f;
                float s1 = 0.05f + RANDOM.nextFloat() * 0.25f;
                float s2 = 0.05f + RANDOM.nextFloat() * 0.25f;
                float l1 = 0.4f + RANDOM.nextFloat() * 0.35f;
                float l2 = Math.max(0.3f,
                                    Math.min(0.85f,
                                             l1 + (RANDOM.nextFloat() * 0.2f - 0.1f)));
                colorA = game.builder.PlanetBuilder.hslToRgb(h1, s1, l1);
                colorB = game.builder.PlanetBuilder.hslToRgb(h2, s2, l2);
            }
        } else { // ORGANIC
            boolean alien = RANDOM.nextFloat() < 0.4f;
            if (!alien) {
                float h1 = 0.5f + RANDOM.nextFloat() * 0.15f;
                float s1 = 0.4f + RANDOM.nextFloat() * 0.35f;
                float l1 = 0.15f + RANDOM.nextFloat() * 0.25f;
                colorA = game.builder.PlanetBuilder.hslToRgb(h1, s1, l1);
                float h2 = RANDOM.nextBoolean()
                           ? 0.22f + RANDOM.nextFloat() * 0.15f
                           : 0.08f + RANDOM.nextFloat() * 0.08f;
                float s2 = RANDOM.nextBoolean()
                           ? 0.35f + RANDOM.nextFloat() * 0.35f
                           : 0.25f + RANDOM.nextFloat() * 0.25f;
                float l2 = 0.2f + RANDOM.nextFloat() * 0.25f;
                colorB = game.builder.PlanetBuilder.hslToRgb(h2, s2, l2);
            } else {
                float h1 = RANDOM.nextFloat();
                float s1 = 0.4f + RANDOM.nextFloat() * 0.4f;
                float l1 = 0.2f + RANDOM.nextFloat() * 0.25f;
                colorA = game.builder.PlanetBuilder.hslToRgb(h1, s1, l1);
                float h2 = (h1 + 0.3f + RANDOM.nextFloat() * 0.4f) % 1.0f;
                float s2 = 0.4f + RANDOM.nextFloat() * 0.4f;
                float l2 = 0.2f + RANDOM.nextFloat() * 0.3f;
                colorB = game.builder.PlanetBuilder.hslToRgb(h2, s2, l2);
            }
        }
        return new org.joml.Vector3f[]{colorA, colorB};
    }

    private void generateRandomPlanets(int planetAmount) {
        java.util.Random RANDOM = new java.util.Random();
        float currentDistance = star.getRadius() + 400f;

        for (int i = 0; i < planetAmount; i++) {
            currentDistance += 800f + RANDOM.nextFloat() * 1000f;
            Planet planet = PlanetBuilder.createRandom(star, currentDistance).build();
            this.planets.add(planet);

            // Attach moons procedurally
            boolean isGiant = (planet.getType() == game.info.PlanetType.GAS_GIANT ||
                               planet.getType() == game.info.PlanetType.ICE_GIANT);
            int moonCount = 0;
            if (isGiant) {
                if (RANDOM.nextFloat() < 0.75f) {
                    moonCount = 1 + RANDOM.nextInt(3); // 1 to 3 moons
                }
            } else {
                if (RANDOM.nextFloat() < 0.25f) {
                    moonCount = 1; // 0 or 1 moon
                }
            }

            for (int m = 0; m < moonCount; m++) {
                // Giants can have organic or rocky moons, terrestrial only rocky
                game.info.PlanetType moonType = game.info.PlanetType.ROCKY;
                if (isGiant && RANDOM.nextFloat() < 0.20f) {
                    moonType = game.info.PlanetType.ORGANIC;
                }

                float moonRadius;
                if (isGiant) {
                    moonRadius = planet.getRadius() *
                                 (0.04f + RANDOM.nextFloat() * 0.04f); // 4% to 8%
                } else {
                    moonRadius = planet.getRadius() *
                                 (0.20f + RANDOM.nextFloat() * 0.07f); // 20% to 27%
                }

                // Spacing
                float baseStart = planet.getPlanetInfo().hasRings()
                                  ? planet.getRadius() * 8f
                                  : planet.getRadius() * 6f;
                float moonDistance = baseStart + (m * planet.getRadius() * 0.8f);

                float moonSpeed = (0.05f + RANDOM.nextFloat() * 0.05f);
                float moonAngle = RANDOM.nextFloat() * (float) (Math.PI * 2.0);

                org.joml.Vector3f[] colors = generateColorsForType(moonType);
                String moonName = planet.getName() + "-" + (char) ('A' + m);

                game.info.PlanetInfo moonInfo = new game.info.PlanetInfo(star,
                                                                         moonSpeed,
                                                                         moonAngle,
                                                                         moonRadius,
                                                                         moonDistance,
                                                                         colors[0],
                                                                         colors[1],
                                                                         moonName,
                                                                         moonType,
                                                                         false);

                new Moon(moonInfo, planet);
            }
        }
    }

    public ArrayList<SpaceBody> getAllBodies() {
        ArrayList<SpaceBody> celestialBodies = new ArrayList<>(planets);
        for (Planet planet : planets) {
            celestialBodies.addAll(planet.satellites);
        }
        celestialBodies.addAll(stars);
        return celestialBodies;
    }

    public List<Planet> getPlanets() {
        return planets;
    }

    public List<Planet> getPlanetsOrbitingStar(Star star) {
        return planets.stream().filter(p -> p.getHomeStar() == star).toList();
    }

    public Star getStar() {
        return star;
    }

    public List<Star> getStars() {
        return stars;
    }

    public float maxOrbitDistance(Star star) {
        return planets.stream()
                      .filter(p -> p.getHomeStar() == star)
                      .map(p -> p.getPlanetInfo().orbitDistance())
                      .max(Float::compare)
                      .orElse(star.getRadius() + 1000.0f);
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

    public void tickAllFacilities() {
        for (Planet planet : planets) {
            planet.tickFacilities();
        }
    }

    public void updateAll(float deltaTime) {
        for (Star s : stars) {
            s.update(deltaTime);
        }

        for (Planet planet : planets) {
            planet.update(deltaTime);
        }
    }
}
