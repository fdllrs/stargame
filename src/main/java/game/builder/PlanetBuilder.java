package game.builder;

import game.info.PlanetInfo;
import game.info.PlanetType;
import game.objects.spaceBodies.Moon;
import game.objects.spaceBodies.Planet;
import game.objects.spaceBodies.Star;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public abstract class PlanetBuilder {
	protected static final Random RANDOM = new Random();
	protected static final float MIN_ORBIT_SPEED = 0.01f;
	protected static final float MAX_EXTRA_ORBIT_SPEED = 0.4f;
	protected static final float MIN_RADIUS = 10f;
	protected static final float MAX_EXTRA_RADIUS = 20f;
	protected static final float ORBIT_DISTANCE_PADDING = 50f;
	protected static final float MAX_ORBIT_DISTANCE = 12000f;
	protected final Star homeStar;
	protected final List<MoonConfig> moonConfigs = new ArrayList<>();
	protected Float orbitSpeed = null;
	protected Float orbitAngle = null;
	protected Float radius = null;
	protected Float orbitDistance = null;
	protected Vector3f colorA = null;
	protected Vector3f colorB = null;
	protected Boolean hasRings = null;

	protected abstract Planet buildPlanet();

	abstract protected void generateColors();

	public abstract PlanetType getType();

	public PlanetBuilder(Star homeStar) {
		this.homeStar = homeStar;
	}

	public final Planet build() {
		Planet planet = buildPlanet();
		buildMoons(planet);
		return planet;
	}

	@NotNull
	protected basicPlanetInfo buildBasicPlanetInfo() {
		float finalDistance = ( this.orbitDistance != null )
							  ? this.orbitDistance
							  : randomOrbitDistance();
		float finalSpeed = ( this.orbitSpeed != null ) ? this.orbitSpeed : randomOrbitSpeed();
		float finalAngle = ( this.orbitAngle != null ) ? this.orbitAngle : randomOrbitAngle();

		if (this.colorA == null || this.colorB == null) {
			generateColors();
		}

		Vector3f finalColorA = ( this.colorA != null ) ? this.colorA : randomColor();
		Vector3f finalColorB = ( this.colorB != null ) ? this.colorB : randomColor();
		return new basicPlanetInfo(finalDistance, finalSpeed, finalAngle, finalColorA,
								   finalColorB);
	}

	protected void buildMoons(Planet planet) {
		boolean isGiant = ( planet.getType() == PlanetType.GAS_GIANT ||
							planet.getType() == PlanetType.ICE_GIANT );

		for (int m = 0; m < moonConfigs.size(); m++) {
			MoonConfig config = moonConfigs.get(m);

			PlanetType moonType = config.type != null
								  ? config.type
								  : ( isGiant && RANDOM.nextFloat() < 0.20f
									  ? PlanetType.ORGANIC
									  : PlanetType.ROCKY );

			float moonRadius;
			if (config.radius != null) {
				moonRadius = config.radius;
			}
			else {
				if (isGiant) {
					moonRadius = planet.getRadius() * ( 0.04f + RANDOM.nextFloat() * 0.04f );
				}
				else {
					moonRadius = planet.getRadius() * ( 0.20f + RANDOM.nextFloat() * 0.07f );
				}
			}

			float moonDistance;
			if (config.orbitDistance != null) {
				moonDistance = config.orbitDistance;
			}
			else {
				float baseStart = planet.getPlanetInfo().hasRings()
								  ? planet.getRadius() * 8f
								  : planet.getRadius() * 6f;
				moonDistance = baseStart + ( m * planet.getRadius() * 0.8f );
			}

			float moonSpeed = config.orbitSpeed != null
							  ? config.orbitSpeed
							  : ( 0.05f + RANDOM.nextFloat() * 0.05f );
			float moonAngle = config.orbitAngle != null
							  ? config.orbitAngle
							  : RANDOM.nextFloat() * (float) ( Math.PI * 2.0 );

			Vector3f finalColorA = config.colorA;
			Vector3f finalColorB = config.colorB;
			if (finalColorA == null || finalColorB == null) {
				Vector3f[] colors = generateColorsForType(homeStar, moonType);
				if (finalColorA == null) finalColorA = colors[ 0 ];
				if (finalColorB == null) finalColorB = colors[ 1 ];
			}

			String moonName = config.name != null
							  ? config.name
							  : ( planet.getName() != null ? planet.getName() : "null" ) + "-" +
								(char) ( 'A' + m );

			PlanetInfo moonInfo = new PlanetInfo(homeStar,
												 moonSpeed,
												 moonAngle,
												 moonRadius,
												 moonDistance,
												 finalColorA,
												 finalColorB,
												 moonName,
												 moonType,
												 false);

			new Moon(moonInfo, planet);
		}
	}

	public static PlanetBuilder create(Star homeStar, PlanetType type) {
		return switch (type) {
			case ROCKY -> new RockyPlanetBuilder(homeStar);
			case GAS_GIANT -> new GasGiantPlanetBuilder(homeStar);
			case ICE_GIANT -> new IceGiantPlanetBuilder(homeStar);
			case ORGANIC -> new OrganicPlanetBuilder(homeStar);
		};
	}

	public static PlanetBuilder createRandom(Star homeStar) {
		float minDistance = homeStar.getRadius() + MAX_EXTRA_RADIUS + ORBIT_DISTANCE_PADDING;
		float distance = minDistance + RANDOM.nextFloat() * ( MAX_ORBIT_DISTANCE - minDistance );
		return createRandom(homeStar, distance);
	}

	public static PlanetBuilder createRandom(Star homeStar, float distance) {
		PlanetBuilder builder;
		if (distance <= homeStar.getRadius() * 25f) {
			builder = RANDOM.nextBoolean()
					  ? new RockyPlanetBuilder(homeStar)
					  : new OrganicPlanetBuilder(homeStar);
		}
		else {
			builder = RANDOM.nextBoolean()
					  ? new GasGiantPlanetBuilder(homeStar)
					  : new IceGiantPlanetBuilder(homeStar);
		}
		builder.withOrbitDistance(distance);
		return builder;
	}

	public static Vector3f[] generateColorsForType(Star homeStar, PlanetType type) {
		PlanetBuilder tempBuilder = create(homeStar, type);
		tempBuilder.generateColors();
		return new Vector3f[] { tempBuilder.colorA, tempBuilder.colorB };
	}

	public static Vector3f hslToRgb(float h, float s, float l) {
		float r, g, b;
		if (s == 0f) {
			r = g = b = l; // achromatic
		}
		else {
			float q = l < 0.5f ? l * ( 1f + s ) : l + s - l * s;
			float p = 2f * l - q;
			r = hueToRgb(p, q, h + 1f / 3f);
			g = hueToRgb(p, q, h);
			b = hueToRgb(p, q, h - 1f / 3f);
		}
		return new Vector3f(r, g, b);
	}

	private static float hueToRgb(float p, float q, float t) {
		if (t < 0f) t += 1f;
		if (t > 1f) t -= 1f;
		if (t < 1f / 6f) return p + ( q - p ) * 6f * t;
		if (t < 1f / 2f) return q;
		if (t < 2f / 3f) return p + ( q - p ) * ( 2f / 3f - t ) * 6f;
		return p;
	}

	protected Vector3f randomColor() {
		return new Vector3f(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
	}

	protected float randomOrbitAngle() {
		return RANDOM.nextFloat() * (float) ( Math.PI * 2.0 );
	}

	protected float randomOrbitDistance() {
		float minDistance = homeStar.getRadius() + MAX_EXTRA_RADIUS + ORBIT_DISTANCE_PADDING;
		return minDistance + RANDOM.nextFloat() * ( MAX_ORBIT_DISTANCE - minDistance );
	}

	protected float randomOrbitSpeed() {
		return ( RANDOM.nextFloat() * MAX_EXTRA_ORBIT_SPEED + MIN_ORBIT_SPEED ) * 0.001f;
	}

	protected float randomRadius() {
		return RANDOM.nextFloat() * MAX_EXTRA_RADIUS + MIN_RADIUS;
	}

	public PlanetBuilder withColors(Vector3f colorA, Vector3f colorB) {
		this.colorA = colorA;
		this.colorB = colorB;
		return this;
	}

	public PlanetBuilder withMoon() {
		this.moonConfigs.add(new MoonConfig());
		return this;
	}

	public PlanetBuilder withMoon(MoonConfig config) {
		this.moonConfigs.add(config);
		return this;
	}

	public PlanetBuilder withMoon(Consumer<MoonConfig> configurator) {
		MoonConfig config = new MoonConfig();
		configurator.accept(config);
		this.moonConfigs.add(config);
		return this;
	}

	public PlanetBuilder withMoonCount(int count) {
		for (int i = 0; i < count; i++) {
			this.moonConfigs.add(new MoonConfig());
		}
		return this;
	}

	public PlanetBuilder withOrbitAngle(float angle) {
		this.orbitAngle = angle;
		return this;
	}

	public PlanetBuilder withOrbitDistance(float distance) {
		this.orbitDistance = distance;
		return this;
	}

	public PlanetBuilder withOrbitSpeed(float speed) {
		this.orbitSpeed = speed;
		return this;
	}

	public PlanetBuilder withRadius(float radius) {
		this.radius = radius;
		return this;
	}

	public PlanetBuilder withRandomMoons() {
		boolean isGiant = ( getType() == PlanetType.GAS_GIANT ||
							getType() == PlanetType.ICE_GIANT );
		int moonCount = 0;
		if (isGiant) {
			if (RANDOM.nextFloat() < 0.75f) {
				moonCount = 1 + RANDOM.nextInt(3); // 1 to 3 moons
			}
		}
		else {
			if (RANDOM.nextFloat() < 0.25f) {
				moonCount = RANDOM.nextInt(3);
			}
		}

		return withMoonCount(moonCount);
	}

	public PlanetBuilder withRings(boolean hasRings) {
		this.hasRings = hasRings;
		return this;
	}

	public static class MoonConfig {
		private PlanetType type = null;
		private Float radius = null;
		private Float orbitDistance = null;
		private Float orbitSpeed = null;
		private Float orbitAngle = null;
		private Vector3f colorA = null;
		private Vector3f colorB = null;
		private String name = null;

		public MoonConfig() { }

		public Vector3f getColorA() { return colorA; }

		public Vector3f getColorB() { return colorB; }

		public String getName() { return name; }

		public Float getRadius() { return radius; }

		public PlanetType getType() { return type; }

		public MoonConfig withColors(Vector3f colorA, Vector3f colorB) {
			this.colorA = colorA;
			this.colorB = colorB;
			return this;
		}

		public MoonConfig withName(String name) {
			this.name = name;
			return this;
		}

		public MoonConfig withOrbitAngle(float orbitAngle) {
			this.orbitAngle = orbitAngle;
			return this;
		}

		public MoonConfig withOrbitDistance(float orbitDistance) {
			this.orbitDistance = orbitDistance;
			return this;
		}

		public MoonConfig withOrbitSpeed(float orbitSpeed) {
			this.orbitSpeed = orbitSpeed;
			return this;
		}

		public MoonConfig withRadius(float radius) {
			this.radius = radius;
			return this;
		}

		public MoonConfig withType(PlanetType type) {
			this.type = type;
			return this;
		}
	}

	protected record basicPlanetInfo(
			float finalDistance,
			float finalSpeed,
			float finalAngle,
			Vector3f finalColorA,
			Vector3f finalColorB) { }
}