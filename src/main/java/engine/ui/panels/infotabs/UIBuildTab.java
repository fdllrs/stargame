package engine.ui.panels.infotabs;

import engine.ui.UIElement;
import engine.ui.UIRow;
import engine.ui.buttons.UIButton;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
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

        addBuildingButtons(planet, font, onRebuild, buildBtnBg, textCol, elements);

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
                mineRow.addElement(new UIButton(btnWidth,
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
                                                         () -> {
                                                             if (canAfford(planet,
                                                                           "Extractor")) {
                                                                 deductCost(planet,
                                                                            "Extractor");
                                                                 planet.getFacilities()
                                                                       .add(new ResourceExtractor(
                                                                               resource));
                                                                 onRebuild.run();
                                                             }
                                                         },
                                                         font);
            buildExtractorButton.setEnabled(canAfford(planet, "Extractor"));
            buildRow.addElement(buildExtractorButton);
        }

        String siloButtonLabel = "Storage Silo " + " cost:" +
                                 FacilityConfig.COSTS.get("Extractor").toString() +
                                 "Capacity: " + StorageSilo.initialCapacity;
        UIButton buildSiloButton = new UIButton(buildBtnWidth,
                                                80,
                                                buildBtnBg,
                                                textCol,
                                                siloButtonLabel,
                                                () -> {
                                                    if (canAfford(planet,
                                                                  "Storage Silo")) {
                                                        deductCost(planet,
                                                                   "Storage Silo");
                                                        new StorageSilo(planet);
                                                        onRebuild.run();
                                                    }
                                                },
                                                font);
        buildSiloButton.setEnabled(canAfford(planet, "Storage Silo"));
        buildRow.addElement(buildSiloButton);

        elements.add(buildRow);
    }

    private static boolean canAfford(Planet planet, String facilityName) {
        Map<ItemType, Integer> cost = FacilityConfig.COSTS.get(facilityName);
        if (cost == null)
            return false;
        for (Map.Entry<ItemType, Integer> entry : cost.entrySet()) {
            if (planet.getStorage().getAmount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    private static void deductCost(Planet planet, String facilityName) {
        Map<ItemType, Integer> cost = FacilityConfig.COSTS.get(facilityName);
        if (cost == null)
            return;
        for (Map.Entry<ItemType, Integer> entry : cost.entrySet()) {
            planet.getStorage().withdraw(entry.getKey(), entry.getValue());
        }
    }
}
