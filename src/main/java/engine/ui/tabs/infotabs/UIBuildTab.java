package engine.ui.tabs.infotabs;

import engine.ui.UIElement;
import engine.ui.UIRow;
import engine.ui.buttons.UIButton;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.components.StorageComponent;
import game.items.ItemType;
import game.items.RawResource;
import game.objects.facilities.StorageSilo;
import game.objects.facilities.generators.NuclearReactor;
import game.objects.facilities.generators.SolarPanel;
import game.objects.facilities.producers.*;
import game.objects.spaceBodies.Hub;
import game.objects.spaceBodies.Planet;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UIBuildTab {
	private static void addBasicRow(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Vector4f buildBtnBg,
			Vector4f textCol,
			List<UIElement> elements) {
		List<RawResource> harvestableResources = planet.getType().getHarvestableResources();
		int basicRowButtonsCount = harvestableResources.size() + 2; // Extractors + Hub + Silo
		float basicBtnWidth =
				( width - ( 10f * ( basicRowButtonsCount - 1 ) ) ) / basicRowButtonsCount;

		UIRow row1 = new UIRow(10);
		for (RawResource resource : harvestableResources) {
			String label = resource.name() + " Extractor\nCost: " + ResourceExtractor.COST;
			UIButton btn = new UIButton(basicBtnWidth, 80, buildBtnBg, textCol, label, () -> {
				if (canAfford(playerStorage, ResourceExtractor.COST)) {
					deductCost(playerStorage, ResourceExtractor.COST);
					new ResourceExtractor(resource, planet);
					onRebuild.run();
				}
			}, font);
			btn.setEnabled(canAfford(playerStorage, ResourceExtractor.COST));
			row1.addElement(btn);
		}

		boolean hasHub = planet.getHub() != null;
		String hubLabel = hasHub ? "Hub Built" : "Build Hub\nCost: " + Hub.COST;
		UIButton hubBtn = new UIButton(basicBtnWidth, 80, buildBtnBg, textCol, hubLabel, () -> {
			if (canAfford(playerStorage, Hub.COST) && planet.getHub() == null) {
				deductCost(playerStorage, Hub.COST);
				new Hub(planet);
				onRebuild.run();
			}
		}, font);
		hubBtn.setEnabled(!hasHub && canAfford(playerStorage, Hub.COST));
		row1.addElement(hubBtn);

		String siloLabel = "Build Silo\nCost: " + StorageSilo.COST;
		UIButton siloBtn = new UIButton(basicBtnWidth, 80, buildBtnBg, textCol, siloLabel, () -> {
			if (canAfford(playerStorage, StorageSilo.COST)) {
				deductCost(playerStorage, StorageSilo.COST);
				new StorageSilo(planet);
				onRebuild.run();
			}
		}, font);
		siloBtn.setEnabled(canAfford(playerStorage, StorageSilo.COST));
		row1.addElement(siloBtn);

		elements.add(row1);
	}

	private static void addBuildingButtons(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Vector4f buildBtnBg,
			Vector4f textCol,
			List<UIElement> elements) {

		addBasicRow(planet, playerStorage, font, width, onRebuild, buildBtnBg, textCol, elements);

		addEnergyGenerationRow(planet,
							   playerStorage,
							   font,
							   width,
							   onRebuild,
							   buildBtnBg,
							   textCol,
							   elements);

		addResourceProcessingRow(planet,
								 playerStorage,
								 font,
								 width,
								 onRebuild,
								 buildBtnBg,
								 textCol,
								 elements);

		addResearchRow(planet,
					   playerStorage,
					   font,
					   width,
					   onRebuild,
					   buildBtnBg,
					   textCol,
					   elements);
	}

	private static void addEnergyGenerationRow(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Vector4f buildBtnBg,
			Vector4f textCol,
			List<UIElement> elements) {
		elements.add(new UIText("Energy Generation:",
								UIText.Alignment.LEFT,
								textCol,
								15,
								10,
								5,
								font,
								width));
		UIRow row2 = new UIRow(10);
		float powerBtnWidth = ( width - 10f ) / 2f;

		String solarLabel = "Solar Array\nCost: " + SolarPanel.COST;
		UIButton solarBtn = new UIButton(powerBtnWidth, 80, buildBtnBg, textCol, solarLabel,
										 () -> {
			if (canAfford(playerStorage, SolarPanel.COST)) {
				deductCost(playerStorage, SolarPanel.COST);
				new SolarPanel(planet);
				onRebuild.run();
			}
		}, font);
		solarBtn.setEnabled(canAfford(playerStorage, SolarPanel.COST));
		row2.addElement(solarBtn);

		String reactorLabel = "Nuclear Reactor\nCost: " + NuclearReactor.COST;
		UIButton reactorBtn = new UIButton(powerBtnWidth,
										   80,
										   buildBtnBg,
										   textCol,
										   reactorLabel,
										   () -> {
											   if (canAfford(playerStorage, NuclearReactor.COST)) {
												   deductCost(playerStorage, NuclearReactor.COST);
												   new NuclearReactor(planet);
												   onRebuild.run();
											   }
										   },
										   font);
		reactorBtn.setEnabled(canAfford(playerStorage, NuclearReactor.COST));
		row2.addElement(reactorBtn);

		elements.add(row2);
	}

	private static void addMiningButtons(Planet planet,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Vector4f textCol,
			List<UIElement> elements) {
		List<RawResource> harvestable = planet.getType().getHarvestableResources();
		Vector4f btnBg = new Vector4f(0.2f, 0.4f, 0.8f, 1.0f);

		elements.add(new UIText("Manual Harvesting:",
								UIText.Alignment.LEFT,
								textCol,
								15,
								10,
								5,
								font,
								width));

		if (harvestable.size() > 1) {
			UIRow mineRow = new UIRow(10);
			float btnWidth = ( width - ( 10f * ( harvestable.size() - 1 ) ) ) / harvestable.size();
			for (RawResource resource : harvestable) {
				UIButton harvestButton = new UIButton(btnWidth,
													  35,
													  btnBg,
													  textCol,
													  "Mine " + resource.name(),
													  () -> {
														  planet.deposit(resource, 10);
														  onRebuild.run();
													  },
													  font);
				mineRow.addElement(harvestButton);
			}
			elements.add(mineRow);
		}
		else if (harvestable.size() == 1) {
			RawResource resource = harvestable.getFirst();
			elements.add(new UIButton(width, 35, btnBg, textCol, "Mine " + resource.name(), () -> {
				planet.deposit(resource, 10);
				onRebuild.run();
			}, font));
		}
	}

	private static void addResearchRow(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Vector4f buildBtnBg,
			Vector4f textCol,
			List<UIElement> elements) {
		elements.add(new UIText("Scientific Research:",
								UIText.Alignment.LEFT,
								textCol,
								15,
								10,
								5,
								font,
								width));
		UIRow row4 = new UIRow(10);

		String labType = "Geological";
		switch (planet.getType()) {
			case ORGANIC -> labType = "Biological";
			case GAS_GIANT -> labType = "Gas";
			case ICE_GIANT -> labType = "Cryo-Physics";
		}
		String labLabel = "Build " + labType + " Lab\nCost: " + ResearchLab.COST;
		UIButton labBtn = new UIButton(width, 80, buildBtnBg, textCol, labLabel, () -> {
			if (canAfford(playerStorage, ResearchLab.COST)) {
				deductCost(playerStorage, ResearchLab.COST);
				new ResearchLab(planet);
				onRebuild.run();
			}
		}, font);
		labBtn.setEnabled(canAfford(playerStorage, ResearchLab.COST));
		row4.addElement(labBtn);

		elements.add(row4);
	}

	private static void addResourceProcessingRow(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Vector4f buildBtnBg,
			Vector4f textCol,
			List<UIElement> elements) {
		elements.add(new UIText("Resource Processing:",
								UIText.Alignment.LEFT,
								textCol,
								15,
								10,
								5,
								font,
								width));
		UIRow row3 = new UIRow(10);
		float prodBtnWidth = ( width - 20f ) / 3f;

		String smelterLabel = "Alloy Smelter\nCost: " + AlloySmelter.COST;
		UIButton smelterBtn = new UIButton(prodBtnWidth,
										   80,
										   buildBtnBg,
										   textCol,
										   smelterLabel,
										   () -> {
											   if (canAfford(playerStorage, AlloySmelter.COST)) {
												   deductCost(playerStorage, AlloySmelter.COST);
												   new AlloySmelter(planet);
												   onRebuild.run();
											   }
										   },
										   font);
		smelterBtn.setEnabled(canAfford(playerStorage, AlloySmelter.COST));
		row3.addElement(smelterBtn);

		String chemLabel = "Chem Plant\nCost: " + ChemicalPlant.COST;
		UIButton chemBtn = new UIButton(prodBtnWidth, 80, buildBtnBg, textCol, chemLabel, () -> {
			if (canAfford(playerStorage, ChemicalPlant.COST)) {
				deductCost(playerStorage, ChemicalPlant.COST);
				new ChemicalPlant(planet);
				onRebuild.run();
			}
		}, font);
		chemBtn.setEnabled(canAfford(playerStorage, ChemicalPlant.COST));
		row3.addElement(chemBtn);

		String engineLabel = "Engine Fab\nCost: " + EngineFabricator.COST;
		UIButton engineBtn = new UIButton(prodBtnWidth,
										  80,
										  buildBtnBg,
										  textCol,
										  engineLabel,
										  () -> {
											  if (canAfford(playerStorage,
															EngineFabricator.COST)) {
												  deductCost(playerStorage, EngineFabricator.COST);
												  new EngineFabricator(planet);
												  onRebuild.run();
											  }
										  },
										  font);
		engineBtn.setEnabled(canAfford(playerStorage, EngineFabricator.COST));
		row3.addElement(engineBtn);

		elements.add(row3);
	}

	public static List<UIElement> build(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild) {
		List<UIElement> elements = new ArrayList<>();
		Vector4f textCol = new Vector4f(1, 1, 1, 1);
		Vector4f buildBtnBg = new Vector4f(0.8f, 0.5f, 0.2f, 1.0f);

		elements.add(new UIText("Construct Planetary Infrastructure:",
								UIText.Alignment.LEFT,
								textCol,
								20,
								15,
								10,
								font,
								width));

		addBuildingButtons(planet,
						   playerStorage,
						   font,
						   width,
						   onRebuild,
						   buildBtnBg,
						   textCol,
						   elements);

		addMiningButtons(planet, font, width, onRebuild, textCol, elements);

		return elements;
	}

	private static boolean canAfford(StorageComponent playerStorage, Map<ItemType, Integer> cost) {
		if (cost == null) return false;
		for (Map.Entry<ItemType, Integer> entry : cost.entrySet()) {
			if (!playerStorage.canWithdraw(entry.getKey(), entry.getValue())) {
				return false;
			}
		}
		return true;
	}

	private static void deductCost(StorageComponent playerStorage, Map<ItemType, Integer> cost) {
		if (cost == null) return;
		for (Map.Entry<ItemType, Integer> entry : cost.entrySet()) {
			playerStorage.attemptWithdraw(entry.getKey(), entry.getValue());
		}
	}
}
