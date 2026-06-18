package game.objects;

import game.builder.PlanetBuilder;
import game.builder.StarBuilder;
import game.info.PlanetType;
import game.info.StarInfo.StarType;
import game.objects.spaceBodies.Planet;
import game.objects.spaceBodies.SpaceBody;
import game.objects.spaceBodies.Star;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	private void generateRandomPlanets(int planetAmount) {
		java.util.Random RANDOM = new java.util.Random();
		float currentDistance = star.getRadius() + 400f;

		for (int i = 0; i < planetAmount; i++) {
			currentDistance += 800f + RANDOM.nextFloat() * 1000f;
			Planet planet = PlanetBuilder.createRandom(star, currentDistance)
										 .withRandomMoons()
										 .build();
			this.planets.add(planet);
		}
	}

	public static StarSystem generateStartingSystem() {
		Star star = new StarBuilder().withType(StarType.F).build();
		ArrayList<Planet> planets = new ArrayList<>();

		Random RANDOM = new Random();
		PlanetType[] planetTypes = {
				PlanetType.ROCKY,
				PlanetType.ROCKY,
				PlanetType.ORGANIC,
				PlanetType.GAS_GIANT,
				PlanetType.GAS_GIANT,
				PlanetType.ICE_GIANT,
				PlanetType.GAS_GIANT };

		int[] moonCounts = { 1, 0, 2, 0, 1, 3, 2 };
		Planet planet;
		float currentDistance = star.getRadius() + 1000f + RANDOM.nextFloat() * 1000;

		for (int i = 0; i < planetTypes.length; i++) {
			PlanetType type = planetTypes[ i ];
			currentDistance += 1500f + RANDOM.nextFloat() * 10000f;

			if (type == PlanetType.ICE_GIANT || type == PlanetType.GAS_GIANT) {
				currentDistance += 10000f;
			}
			planet = PlanetBuilder.create(star, type)
								  .withOrbitDistance(currentDistance)
								  .withMoonCount(moonCounts[ i ])
								  .build();
			planets.add(planet);
		}

		return new StarSystem(star, planets);
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
					  .map(p -> p.getPlanetInfo()
								 .orbitDistance())
					  .max(Float::compare)
					  .orElse(star.getRadius() + 1000.0f);
	}

	private void nameAllPlanets() {
		int nameSufix = 1;
		for (Planet planet : planets) {
			String starName = planet.getHomeStar().getName();
			String[] parts = starName.split(" ");
			String baseName = parts.length > 1 ? parts[ 1 ] : starName;
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
