package game.builder;

import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.spaceBodies.IceGiantPlanet;
import game.objects.spaceBodies.Star;

public class IceGiantPlanetBuilder extends PlanetBuilder {
	public IceGiantPlanetBuilder(Star homeStar) {
		super(homeStar);
	}

	@Override
	public IceGiantPlanet build() {
		basicPlanetInfo basicPlanetInfo = buildBasicPlanetInfo();
		float finalRadius = ( this.radius != null ) ? this.radius : 15f + RANDOM.nextFloat() * 15f;

		PlanetInfo info = new PlanetInfo(homeStar,
										 basicPlanetInfo.finalSpeed(),
										 basicPlanetInfo.finalAngle(),
										 finalRadius,
										 basicPlanetInfo.finalDistance(),
										 basicPlanetInfo.finalColorA(),
										 basicPlanetInfo.finalColorB(),
										 null,
										 PlanetType.ICE_GIANT,
										 false);

		return new IceGiantPlanet(info);
	}

	@Override
	protected void generateColors() {
		// Cold hues: green-cyan (0.45) to purple-magenta (0.8)
		float h1 = 0.45f + RANDOM.nextFloat() * 0.35f;
		float hueShift = ( RANDOM.nextFloat() * 0.1f ) - 0.05f;
		float h2 = ( h1 + hueShift + 1f ) % 1f;

		// Ice giants are highly reflective/icy, so lightness should be higher
		float s1 = 0.35f + RANDOM.nextFloat() * 0.45f;
		float s2 = Math.max(0.2f, Math.min(0.9f, s1 + ( RANDOM.nextFloat() * 0.2f - 0.1f )));

		float l1 = 0.45f + RANDOM.nextFloat() * 0.35f; // bright/icy
		float l2 = Math.max(0.3f,
							Math.min(0.9f,
									 l1 - 0.1f -
									 RANDOM.nextFloat() * 0.2f)); // color B is typically darker
		// ice/features

		colorA = hslToRgb(h1, s1, l1);
		colorB = hslToRgb(h2, s2, l2);
	}
}
