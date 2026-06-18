package game.builder;

import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.spaceBodies.RockyPlanet;
import game.objects.spaceBodies.Star;

public class RockyPlanetBuilder extends PlanetBuilder {
	public RockyPlanetBuilder(Star homeStar) {
		super(homeStar);
	}

	@Override
	public PlanetType getType() {
		return PlanetType.ROCKY;
	}

	@Override
	protected RockyPlanet buildPlanet() {

		basicPlanetInfo basicPlanetInfo = buildBasicPlanetInfo();

		float finalRadius = ( this.radius != null ) ? this.radius : 4f + RANDOM.nextFloat() * 6f;

		PlanetInfo info = new PlanetInfo(homeStar,
										 basicPlanetInfo.finalSpeed(),
										 basicPlanetInfo.finalAngle(),
										 finalRadius,
										 basicPlanetInfo.finalDistance(),
										 basicPlanetInfo.finalColorA(),
										 basicPlanetInfo.finalColorB(),
										 null,
										 PlanetType.ROCKY,
										 false);

		return new RockyPlanet(info);
	}

	@Override
	protected void generateColors() {
		int archetype = RANDOM.nextInt(3);
		if (archetype == 0) {
			// Warm: Volcanic/Desert/Dusty (Red/Orange/Brown)
			float h1 = ( RANDOM.nextFloat() * 0.15f + 0.95f ) % 1.0f; // Red to Orange/Yellow
			float h2 = ( h1 + ( RANDOM.nextFloat() * 0.08f - 0.04f ) + 1.0f ) % 1.0f;
			float s1 = 0.3f + RANDOM.nextFloat() * 0.5f;
			float s2 = Math.max(0.2f, Math.min(0.9f, s1 + ( RANDOM.nextFloat() * 0.2f - 0.1f )));
			float l1 = 0.15f + RANDOM.nextFloat() * 0.35f;
			float l2 = Math.max(0.1f, Math.min(0.7f, l1 + ( RANDOM.nextFloat() * 0.2f - 0.1f )));
			colorA = hslToRgb(h1, s1, l1);
			colorB = hslToRgb(h2, s2, l2);
		}
		else if (archetype == 1) {
			// Barren/Basalt/Grey
			float h1 = RANDOM.nextFloat();
			float h2 = RANDOM.nextFloat();
			float s1 = RANDOM.nextFloat() * 0.12f; // very low saturation
			float s2 = RANDOM.nextFloat() * 0.12f;
			float l1 = 0.15f + RANDOM.nextFloat() * 0.25f;
			float l2 = 0.35f + RANDOM.nextFloat() * 0.35f;
			colorA = hslToRgb(h1, s1, l1);
			colorB = hslToRgb(h2, s2, l2);
		}
		else {
			// Frozen Rocky: Icy/Barren/Grey-Blue
			float h1 = 0.5f + RANDOM.nextFloat() * 0.15f; // Cyan to Blue
			float h2 = ( h1 + ( RANDOM.nextFloat() * 0.06f - 0.03f ) + 1.0f ) % 1.0f;
			float s1 = 0.05f + RANDOM.nextFloat() * 0.25f; // low-to-medium saturation
			float s2 = 0.05f + RANDOM.nextFloat() * 0.25f;
			float l1 = 0.4f + RANDOM.nextFloat() * 0.35f; // bright ice
			float l2 = Math.max(0.3f, Math.min(0.85f, l1 + ( RANDOM.nextFloat() * 0.2f - 0.1f )));
			colorA = hslToRgb(h1, s1, l1);
			colorB = hslToRgb(h2, s2, l2);
		}
	}
}
