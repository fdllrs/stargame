package game.ui.tabs.infotabs;

import engine.ui.*;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import engine.ui.text.UIText.Alignment;
import game.components.StorageComponent;
import game.objects.facilities.StorageSilo;
import game.objects.facilities.generators.NuclearReactor;
import game.objects.facilities.generators.SolarPanel;
import game.objects.facilities.producers.*;
import game.objects.items.ItemIconRegistry;
import game.objects.items.ItemType;
import game.objects.items.RawResource;
import game.objects.spaceBodies.Hub;
import game.objects.spaceBodies.Planet;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UIBuildTab {

	private static final float ROW_WIDTH = 350.0f;
	private static final float BUTTON_GAP = 10.0f;
	private static final float MIN_BUTTON_HEIGHT = 50.0f;
	private static final int MANUAL_MINING_AMOUNT = 10;

	public static List<UIElement> build(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild) {

		List<UIElement> elements = new ArrayList<>();
		Vector4f textCol = new Vector4f(1, 1, 1, 1);
		Vector4f buildBtnBg = new Vector4f(0.8f, 0.5f, 0.2f, 1.0f);
		addMiningCategory(planet, font, width, onRebuild, textCol, elements);

		addBuildingButtons(planet,
						   playerStorage,
						   font,
						   width,
						   onRebuild,
						   buildBtnBg,
						   textCol,
						   elements);

		return elements;
	}

	private static void addMiningCategory(Planet planet,
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

		UIRow row = new UIRow(BUTTON_GAP);

		int numResources = harvestable.size();
		float btnWidth = numResources > 0
						 ? ( ROW_WIDTH - BUTTON_GAP * ( numResources - 1 ) ) / numResources
						 : ROW_WIDTH;

		for (RawResource resource : harvestable) {
			row.addElement(new UIButton(btnWidth,
										MIN_BUTTON_HEIGHT,
										btnBg,
										textCol,
										"Mine " + resource.name(),
										(mouseX, mouseY) -> {
											planet.deposit(resource, MANUAL_MINING_AMOUNT);
											engine.events.EventBus.publish(new game.events.SpawnFloatingTextEvent("+" + MANUAL_MINING_AMOUNT, mouseX, mouseY, new org.joml.Vector4f(0.2f, 1.0f, 0.2f, 1.0f)));
											onRebuild.run();
										},
										font));
		}

		elements.add(row);
	}

	private static void addBuildingButtons(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Vector4f buildBtnBg,
			Vector4f textCol,
			List<UIElement> elements) {

		addBasicCategory(planet,
						 playerStorage,
						 font,
						 width,
						 onRebuild,
						 buildBtnBg,
						 textCol,
						 elements);

		addEnergyGenerationCategory(planet,
									playerStorage,
									font,
									width,
									onRebuild,
									buildBtnBg,
									textCol,
									elements);

		addResourceProcessingCategory(planet,
									  playerStorage,
									  font,
									  width,
									  onRebuild,
									  buildBtnBg,
									  textCol,
									  elements);

		addResearchCategory(planet,
							playerStorage,
							font,
							width,
							onRebuild,
							buildBtnBg,
							textCol,
							elements);
	}

	private static void addBasicCategory(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Vector4f buildBtnBg,
			Vector4f textCol,
			List<UIElement> elements) {
		elements.add(new UIText("Basic Infrastructure:",
								UIText.Alignment.LEFT,
								textCol,
								15,
								10,
								5,
								font,
								width));

		List<RawResource> harvestableResources = planet.getType().getHarvestableResources();
		for (RawResource resource : harvestableResources) {
			String label = resource.name() + " Extractor";
			boolean canAfford = canAfford(playerStorage, ResourceExtractor.COST);
			Runnable action = () -> {
				if (canAfford(playerStorage, ResourceExtractor.COST)) {
					deductCost(playerStorage, ResourceExtractor.COST);
					new ResourceExtractor(resource, planet);
					onRebuild.run();
				}
			};
			elements.add(createBuildRow(label,
										ResourceExtractor.COST,
										action,
										canAfford,
										font,
										buildBtnBg,
										textCol));
		}

		boolean hasHub = planet.getHub() != null;
		String hubLabel = hasHub ? "Hub Built" : "Planetary Hub";
		boolean canAffordHub = !hasHub && canAfford(playerStorage, Hub.COST);
		Runnable hubAction = () -> {
			if (canAfford(playerStorage, Hub.COST) && planet.getHub() == null) {
				deductCost(playerStorage, Hub.COST);
				new Hub(planet);
				onRebuild.run();
			}
		};
		Map<ItemType, Integer> hubCost = hasHub ? null : Hub.COST;
		elements.add(createBuildRow(hubLabel,
									hubCost,
									hubAction,
									canAffordHub,
									font,
									buildBtnBg,
									textCol));

		String siloLabel = "Storage Silo";
		boolean canAffordSilo = canAfford(playerStorage, StorageSilo.COST);
		Runnable siloAction = () -> {
			if (canAfford(playerStorage, StorageSilo.COST)) {
				deductCost(playerStorage, StorageSilo.COST);
				new StorageSilo(planet);
				onRebuild.run();
			}
		};
		elements.add(createBuildRow(siloLabel,
									StorageSilo.COST,
									siloAction,
									canAffordSilo,
									font,
									buildBtnBg,
									textCol));
	}

	private static void addEnergyGenerationCategory(Planet planet,
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

		boolean canAffordSolar = canAfford(playerStorage, SolarPanel.COST);
		elements.add(createBuildRow("Solar Array", SolarPanel.COST, () -> {
			if (canAfford(playerStorage, SolarPanel.COST)) {
				deductCost(playerStorage, SolarPanel.COST);
				new SolarPanel(planet);
				onRebuild.run();
			}
		}, canAffordSolar, font, buildBtnBg, textCol));

		boolean canAffordReactor = canAfford(playerStorage, NuclearReactor.COST);
		elements.add(createBuildRow("Nuclear Reactor", NuclearReactor.COST, () -> {
			if (canAfford(playerStorage, NuclearReactor.COST)) {
				deductCost(playerStorage, NuclearReactor.COST);
				new NuclearReactor(planet);
				onRebuild.run();
			}
		}, canAffordReactor, font, buildBtnBg, textCol));
	}

	private static void addResourceProcessingCategory(Planet planet,
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

		boolean canAffordSmelter = canAfford(playerStorage, AlloySmelter.COST);
		elements.add(createBuildRow("Alloy Smelter", AlloySmelter.COST, () -> {
			if (canAfford(playerStorage, AlloySmelter.COST)) {
				deductCost(playerStorage, AlloySmelter.COST);
				new AlloySmelter(planet);
				onRebuild.run();
			}
		}, canAffordSmelter, font, buildBtnBg, textCol));

		boolean canAffordChem = canAfford(playerStorage, ChemicalPlant.COST);
		elements.add(createBuildRow("Chemical Plant", ChemicalPlant.COST, () -> {
			if (canAfford(playerStorage, ChemicalPlant.COST)) {
				deductCost(playerStorage, ChemicalPlant.COST);
				new ChemicalPlant(planet);
				onRebuild.run();
			}
		}, canAffordChem, font, buildBtnBg, textCol));

		boolean canAffordEngine = canAfford(playerStorage, EngineFabricator.COST);
		elements.add(createBuildRow("Engine Fabricator", EngineFabricator.COST, () -> {
			if (canAfford(playerStorage, EngineFabricator.COST)) {
				deductCost(playerStorage, EngineFabricator.COST);
				new EngineFabricator(planet);
				onRebuild.run();
			}
		}, canAffordEngine, font, buildBtnBg, textCol));
	}

	private static void addResearchCategory(Planet planet,
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

		String labType = "Geological";
		switch (planet.getType()) {
			case ORGANIC -> labType = "Biological";
			case GAS_GIANT -> labType = "Gas";
			case ICE_GIANT -> labType = "Cryo-Physics";
		}

		boolean canAffordLab = canAfford(playerStorage, ResearchLab.COST);
		elements.add(createBuildRow(labType + " Lab", ResearchLab.COST, () -> {
			if (canAfford(playerStorage, ResearchLab.COST)) {
				deductCost(playerStorage, ResearchLab.COST);
				new ResearchLab(planet);
				onRebuild.run();
			}
		}, canAffordLab, font, buildBtnBg, textCol));
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

	private static UIElement createBuildRow(String name,
			Map<ItemType, Integer> cost,
			Runnable onBuild,
			boolean canAfford,
			FontAtlas font,
			Vector4f buildBtnBg,
			Vector4f textCol) {

		UIRow btnContent = new UIRow(BUTTON_GAP);
		btnContent.addElement(new UIText(name, Alignment.LEFT, textCol, 15, 0, 5, font, 200));

		UIColumn costsColumn = new UIColumn(5);
		if (cost != null && !cost.isEmpty()) {
			for (Map.Entry<ItemType, Integer> entry : cost.entrySet()) {
				UIRow costRow = new UIRow(5);
				costRow.addElement(new UIImage(24, 24, ItemIconRegistry.getIcon(entry.getKey())));
				costRow.addElement(new UIText(String.valueOf(entry.getValue()),
											  Alignment.LEFT,
											  textCol,
											  15,
											  0,
											  5,
											  font,
											  40));
				costsColumn.addElement(costRow);
			}
			btnContent.addElement(costsColumn);
		}

		float buttonHeight = Math.max(MIN_BUTTON_HEIGHT, btnContent.getBoundingHeight() + 20);
		UIButton buildBtn = new UIButton(ROW_WIDTH, buttonHeight, buildBtnBg, btnContent, onBuild);
		buildBtn.setEnabled(canAfford);
		buildBtn.setHoverScaleEnabled(false);

		return buildBtn;
	}
}
