package game.objects.celestialBodies;

import game.geometry.PlanetGeometry;
import game.info.PlanetInfo;
import game.info.PlanetType;
import org.joml.Vector3f;

public class Moon extends Planet {
    private static final int MOON_RESOLUTION = 40;
    private final Planet parentPlanet;

    public Moon(PlanetInfo planetInfo, Planet parentPlanet) {
        super(PlanetGeometry.generate(MOON_RESOLUTION,
                                      planetInfo.planetRadius(),
                                      planetInfo.type()), planetInfo);
        this.parentPlanet = parentPlanet;
        updatePosition();
    }

    private void updatePosition() {
        float offsetX = (float) Math.cos(orbitAngle) * planetInfo.orbitDistance();
        float offsetZ = (float) Math.sin(orbitAngle) * planetInfo.orbitDistance();
        Vector3f parentPos = parentPlanet.getPosition();
        this.position.set(parentPos.x + offsetX, parentPos.y, parentPos.z + offsetZ);
        updateModelMatrix();
    }

    @Override public PlanetType getType() {
        return planetInfo.type();
    }

    @Override public void update(float deltaTime) {
        float orbitSpeed = planetInfo.orbitSpeed();
        orbitAngle += orbitSpeed * deltaTime;
        updatePosition();

        float spinSpeed = 0.5f;
        this.rotation.y += spinSpeed * deltaTime;
        rotate(deltaTime);
    }
}
