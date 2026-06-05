package engine.ui.tabs.infotabs;

import engine.ui.UIElement;
import engine.ui.UIRow;
import engine.ui.buttons.UIButton;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.components.StorageComponent;
import game.items.ItemType;
import game.items.RawResource;
import game.objects.celestialBodies.Planet;
import game.objects.facilities.FacilityConfig;
import game.objects.facilities.ResourceExtractor;
import game.objects.facilities.StorageSilo;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UIBuildTab {
    public static List<UIElement> build(Planet planet,
                                        StorageComponent playerStorage,
                                        FontAtlas font,
                                        float width,
                                        Runnable onRebuild) {
        List<UIElement> elements = new ArrayList<>();
        Vector4f textCol = new Vector4f(1, 1, 1, 1);
        Vector4f buildBtnBg = new Vector4f(0.8f, 0.5f, 0.2f, 1.0f);

        elements.add(new UIText("Construct Orbital Infrastructure:",
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
                           onRebuild,
                           buildBtnBg,
                           textCol,
                           elements);

        addMiningButtons(planet, font, onRebuild, textCol, elements);

        return elements;
    }

    private static void addMiningButtons(Planet planet,
                                         FontAtlas font,
                                         Runnable onRebuild,
                                         Vector4f textCol,
                                         List<UIElement> elements) {
        List<RawResource> harvestable = planet.getType().getHarvestableResources();
        Vector4f btnBg = new Vector4f(0.2f, 0.4f, 0.8f, 1.0f);

        if (harvestable.size() > 1) {
            UIRow mineRow = new UIRow(10);
            float btnWidth = (350f - (10f * (harvestable.size() - 1))) /
                             harvestable.size();
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
        } else if (harvestable.size() == 1) {
            RawResource resource = harvestable.getFirst();
            elements.add(new UIButton(220,
                                      35,
                                      btnBg,
                                      textCol,
                                      "Mine " + resource.name(),
                                      () -> {
                                          planet.deposit(resource, 10);
                                          onRebuild.run();
                                      },
                                      font));
        }
    }

    private static void addBuildingButtons(Planet planet,
                                           StorageComponent playerStorage,
                                           FontAtlas font,
                                           Runnable onRebuild,
                                           Vector4f buildBtnBg,
                                           Vector4f textCol,
                                           List<UIElement> elements) {
        List<RawResource> harvestableResources = planet.getType()
                                                       .getHarvestableResources();
        int totalBuildActions = harvestableResources.size() + 1;
        float buildBtnWidth = (350f - (10f * (totalBuildActions - 1))) /
                              totalBuildActions;

        UIRow buildRow = new UIRow(10);

        for (RawResource resource : harvestableResources) {
            String facilityButtonLabel = resource.name() + " Extractor" + " cost:" +
                                         FacilityConfig.COSTS.get("Extractor").toString();

            UIButton buildExtractorButton = new UIButton(buildBtnWidth,
                                                         80,
                                                         buildBtnBg,
                                                         textCol,
                                                         facilityButtonLabel,
                                                         () -> buildExtractor(planet,
                                                                              playerStorage,
                                                                              onRebuild,
                                                                              resource),
                                                         font);
            buildExtractorButton.setEnabled(canAfford(playerStorage, "Extractor"));
            buildRow.addElement(buildExtractorButton);
        }

        String siloButtonLabel = "Storage Silo " + " cost:" +
                                 FacilityConfig.COSTS.get("Storage Silo").toString() +
                                 " Capacity: " + StorageSilo.initialCapacity;
        UIButton buildSiloButton = new UIButton(buildBtnWidth,
                                                80,
                                                buildBtnBg,
                                                textCol,
                                                siloButtonLabel,
                                                () -> BuildSilo(planet,
                                                                playerStorage,
                                                                onRebuild),
                                                font);
        buildSiloButton.setEnabled(canAfford(playerStorage, "Storage Silo"));
        buildRow.addElement(buildSiloButton);

        elements.add(buildRow);
    }

    private static void BuildSilo(Planet planet,
                                  StorageComponent playerStorage,
                                  Runnable onRebuild) {
        if (canAfford(playerStorage, "Storage Silo")) {
            deductCost(playerStorage, "Storage Silo");
            new StorageSilo(planet);
            onRebuild.run();
        }
    }

    private static void buildExtractor(Planet planet,
                                       StorageComponent playerStorage,
                                       Runnable onRebuild,
                                       RawResource resource) {
        if (canAfford(playerStorage, "Extractor")) {
            deductCost(playerStorage, "Extractor");

            new ResourceExtractor(resource, planet);

            onRebuild.run();
        }
    }

    private static boolean canAfford(StorageComponent playerStorage,
                                     String facilityName) {
        Map<ItemType, Integer> cost = FacilityConfig.COSTS.get(facilityName);
        if (cost == null)
            return false;
        for (Map.Entry<ItemType, Integer> entry : cost.entrySet()) {
            if (!playerStorage.canWithdraw(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private static void deductCost(StorageComponent playerStorage, String facilityName) {
        Map<ItemType, Integer> cost = FacilityConfig.COSTS.get(facilityName);
        if (cost == null)
            return;
        for (Map.Entry<ItemType, Integer> entry : cost.entrySet()) {
            playerStorage.withdraw(entry.getKey(), entry.getValue());
        }
    }
}
