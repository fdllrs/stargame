package game.objects.facilities.producers;

import game.geometry.BuildingGeometry;
import game.objects.facilities.Facility;
import game.objects.items.ItemType;
import game.objects.spaceBodies.Planet;

import java.util.Map;

public abstract class ProducerFacility extends Facility {
	private final float powerDemand;
	private final Map<ItemType, Integer> inputs;
	private final Map<ItemType, Integer> outputs;

	public ProducerFacility(Planet planet,
			float powerDemand,
			Map<ItemType, Integer> inputs,
			Map<ItemType, Integer> outputs) {
		super(planet);
		this.powerDemand = powerDemand;
		this.inputs = inputs;
		this.outputs = outputs;
		this.mesh = BuildingGeometry.getExtractorMesh();
		planet.addProducer(this);
	}

	@Override
	public int getPowerDemand() {
		return (int) powerDemand;
	}

	@Override
	public void tick(Planet planet, float efficiencyMultiplier) {
		this.progressAccumulator += efficiencyMultiplier;
		if (this.progressAccumulator >= 1.0f) {
			int cycles = (int) this.progressAccumulator;
			int executedCycles = 0;
			for (int i = 0; i < cycles; i++) {
				if (canProcess(planet)) {
					process(planet);
					executedCycles++;
				}
				else {
					break;
				}
			}
			this.progressAccumulator -= executedCycles;
			if (executedCycles < cycles) {
				this.progressAccumulator = Math.min(this.progressAccumulator, 0.99f);
			}
		}
	}

	protected boolean canProcess(Planet planet) {
		// 1. Check space in storage for outputs
		if (outputs != null) {
			for (Map.Entry<ItemType, Integer> entry : outputs.entrySet()) {
				if (!planet.getStorage().canDeposit(entry.getValue())) {
					return false;
				}
			}
		}

		// 2. Check input resources availability
		if (inputs != null) {
			for (Map.Entry<ItemType, Integer> entry : inputs.entrySet()) {
				if (!planet.getStorage().canWithdraw(entry.getKey(), entry.getValue())) {
					return false;
				}
			}
		}
		return true;
	}

	protected void process(Planet planet) {
		// 1. Withdraw inputs atomically
		if (inputs != null) {
			for (Map.Entry<ItemType, Integer> entry : inputs.entrySet()) {
				planet.getStorage().attemptWithdraw(entry.getKey(), entry.getValue());
			}
		}

		// 2. Deposit outputs
		if (outputs != null) {
			for (Map.Entry<ItemType, Integer> entry : outputs.entrySet()) {
				planet.getStorage().deposit(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public void upgrade() {
	}
}
