package game.builder;

import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.spaceBodies.OrganicPlanet;
import game.objects.spaceBodies.Star;

public class OrganicPlanetBuilder extends PlanetBuilder {
	public OrganicPlanetBuilder(Star homeStar) {
		super(homeStar);
	}

	@Override
	public PlanetType getType() {
		return PlanetType.ORGANIC;
	}

	@Override
	protected OrganicPlanet buildPlanet() {
		basicPlanetInfo basicPlanetInfo = buildBasicPlanetInfo();

		float finalRadius = ( this.radius != null ) ? this.radius : 5f + RANDOM.nextFloat() * 7f;

		PlanetInfo info = new PlanetInfo(homeStar,
										 basicPlanetInfo.finalSpeed(),
										 basicPlanetInfo.finalAngle(),
										 finalRadius,
										 basicPlanetInfo.finalDistance(),
										 basicPlanetInfo.finalColorA(),
										 basicPlanetInfo.finalColorB(),
										 null,
										 PlanetType.ORGANIC,
										 false);

		return new OrganicPlanet(info);
	}

	@Override
	protected void generateColors() {
		boolean alien = RANDOM.nextFloat() < 0.4f; // 40% chance of alien planet colors
		if (!alien) {
			// Earth-like ocean: cyan to blue
			float h1 = 0.5f + RANDOM.nextFloat() * 0.15f;
			float s1 = 0.4f + RANDOM.nextFloat() * 0.35f;
			float l1 = 0.15f + RANDOM.nextFloat() * 0.25f;
			colorA = hslToRgb(h1, s1, l1);

			// Earth-like land: green or yellow/brown
			float h2;
			float s2;
			float l2;
			if (RANDOM.nextBoolean()) {
				// Green vegetation
				h2 = 0.22f + RANDOM.nextFloat() * 0.15f;
				s2 = 0.35f + RANDOM.nextFloat() * 0.35f;
				l2 = 0.2f + RANDOM.nextFloat() * 0.25f;
			}
			else {
				// Yellow/Brown desert/dry land
				h2 = 0.08f + RANDOM.nextFloat() * 0.08f;
				s2 = 0.25f + RANDOM.nextFloat() * 0.25f;
				l2 = 0.2f + RANDOM.nextFloat() * 0.25f;
			}
			colorB = hslToRgb(h2, s2, l2);
		}
		else {
			// Alien planet! Let's mix exotic hues.
			// Ocean hue
			float h1 = RANDOM.nextFloat();
			float s1 = 0.4f + RANDOM.nextFloat() * 0.4f;
			float l1 = 0.2f + RANDOM.nextFloat() * 0.25f;
			colorA = hslToRgb(h1, s1, l1);

			// Vegetation/Land hue - distinct from ocean to look good (e.g.
			// complementary/contrasting)
			float h2 = ( h1 + 0.3f + RANDOM.nextFloat() * 0.4f ) % 1.0f;
			float s2 = 0.4f + RANDOM.nextFloat() * 0.4f;
			float l2 = 0.2f + RANDOM.nextFloat() * 0.3f;
			colorB = hslToRgb(h2, s2, l2);
		}
	}
}
