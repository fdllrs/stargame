package game.objects.facilities.generators;

import game.objects.celestialBodies.Planet;
import game.objects.facilities.Facility;

public class PowerGenerator extends Facility {
	protected float powerOutput;
	protected boolean isActive;

	public PowerGenerator(Planet planet) {
		super(planet);
		planet.addPowerGenerator(this);

		this.powerOutput = 0;
		isActive = false;
	}

	@Override
	public int getPowerDemand() {
		return 0;
	}

	@Override
	public void tick(Planet planet, float efficiencyMultiplier) {
		// Generators define capacity and do not scale their own output by grid efficiency
	}

	@Override
	public void upgrade() {
		this.powerOutput *= 1.1f;
	}

	public float getPowerOutput() {
		return isActive ? powerOutput : 0f;
	}
}
