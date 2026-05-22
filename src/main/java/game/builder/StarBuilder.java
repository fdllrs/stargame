package game.builder;

import game.info.StarInfo;
import game.info.StarInfo.StarType;
import game.objects.Star;
import org.joml.Vector3f;

import java.util.Random;

public class StarBuilder {
    private static final Random RANDOM = new Random();

    private String name = null;
    private StarType type = null;
    private Float radius = null;
    private Float mass = null;
    private Vector3f colorA = null;
    private Vector3f colorB = null;

    public StarBuilder withName(String name) { this.name = name; return this; }
    public StarBuilder withType(StarType type) { this.type = type; return this; }
    public StarBuilder withRadius(float radius) { this.radius = radius; return this; }
    public StarBuilder withMass(float mass) { this.mass = mass; return this; }
    public StarBuilder withColors(Vector3f colorA, Vector3f colorB) {
        this.colorA = colorA;
        this.colorB = colorB;
        return this;
    }


    public Star build() {
        String finalName = (this.name != null) ? this.name : generateRandomName();

        // 2. Resolve Type (Pick a random spectral class if none was provided)
        StarType finalType = (this.type != null) ? this.type :
                StarType.values()[RANDOM.nextInt(StarType.values().length)];

        // 3. Resolve Physics & Colors based on Spectral Class (Main Sequence)
        float finalRadius = (this.radius != null) ? this.radius : generateRadiusForType(finalType);
        float finalMass = (this.mass != null) ? this.mass : generateMassForType(finalType);
        Vector3f finalColorA = (this.colorA != null) ? this.colorA : generateBaseColor(finalType);
        Vector3f finalColorB = (this.colorB != null) ? this.colorB : generateCoronaColor(finalType);

        StarInfo info = new StarInfo(finalName, finalType, finalRadius, finalMass, finalColorA, finalColorB);
        return new Star(info);
    }

    // --- PROCEDURAL GENERATORS ---

    private String generateRandomName() {
        String[] greekLetters = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Sigma", "Omega"};
        String[] constellations = {"Andromedae", "Centauri", "Cygni", "Draconis", "Orionis", "Pegasi", "Lyrae"};
        return greekLetters[RANDOM.nextInt(greekLetters.length)] + " " + constellations[RANDOM.nextInt(constellations.length)];
    }

    private float generateRadiusForType(StarType type) {
        return switch (type) {
            case O -> 800f + RANDOM.nextFloat() * 40f;
            case B -> 500f + RANDOM.nextFloat() * 30f;
            case A -> 350f + RANDOM.nextFloat() * 15f;
            case F -> 250f + RANDOM.nextFloat() * 10f;
            case G -> 200f + RANDOM.nextFloat() * 5f;   // Sun-like
            case K -> 150f + RANDOM.nextFloat() * 5f;
            case M -> 80f + RANDOM.nextFloat() * 7f;    // Red Dwarf
        };
    }

    private float generateMassForType(StarType type) {
        return generateRadiusForType(type) * 1.5f; // Simplified mass calculation
    }

    private Vector3f generateBaseColor(StarType type) {
        return switch (type) {
            case O, B -> new Vector3f(0.5f, 0.7f, 1.0f); // Blue-white
            case A, F -> new Vector3f(1.0f, 1.0f, 1.0f); // Pure white
            case G -> new Vector3f(1.0f, 0.9f, 0.2f);    // Yellow
            case K -> new Vector3f(1.0f, 0.5f, 0.0f);    // Orange
            case M -> new Vector3f(0.8f, 0.1f, 0.0f);    // Red
        };
    }

    private Vector3f generateCoronaColor(StarType type) {
        return switch (type) {
            case O, B -> new Vector3f(0.2f, 0.4f, 1.0f);
            case A, F -> new Vector3f(0.9f, 0.9f, 1.0f);
            case G -> new Vector3f(1.0f, 0.6f, 0.0f);
            case K -> new Vector3f(0.8f, 0.2f, 0.0f);
            case M -> new Vector3f(0.5f, 0.0f, 0.0f);
        };
    }
}