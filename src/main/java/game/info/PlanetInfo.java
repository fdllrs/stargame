package game.info;

import game.objects.Star;
import org.joml.Vector3f;

public record PlanetInfo(
        Star homeStar,
        float orbitSpeed,
        float initialOrbitAngle,
        float planetRadius,
        float orbitDistance,
        Vector3f colorA,
        Vector3f colorB,
        String name
) {}
