package engine.ui.panels;

import engine.ui.Describable;
import engine.ui.UIButton;
import engine.ui.UIElement;
import engine.ui.UIRow;
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
import java.util.Map.Entry;

public class InfoPanel extends UIPanel {
    private final StorageComponent playerStorage;
    private Tab currentTab = Tab.STATS;
    private Describable currentTarget;

    public InfoPanel(float x,
                     float y,
                     float width,
                     float height,
                     Vector4f color,
                     FontAtlas font,
                     StorageComponent playerStorage) {
        super(x, y, width, height, color, font);
        this.playerStorage = playerStorage;

    }

    @Override public float getBoundingHeight() {
        return this.height + vPadding;
    }

    public void handleClick(float mouseX, float mouseY) {
        for (UIElement element : children) {
            if (element.contains(mouseX, mouseY)) {
                element.handleClick(mouseX, mouseY);
                return;
            }
        }

    }

    @Override protected void layout() {
        float currentY = this.y + vPadding;
        for (UIElement element : children) {
            float elementX = this.x;
            if (element instanceof UIButton || element instanceof UIRow) {
                elementX = this.x + (this.width - element.getSize().x) / 2.0f;
            }
            element.setPosition(elementX, currentY);
            currentY += element.getBoundingHeight();
        }
    }

    @Override public boolean shouldRender() {
        return currentTarget != null;
    }

    public void setTarget(Describable target) {
        this.currentTarget = target;
        this.currentTab = Tab.STATS;
        rebuildElements();
    }

    public void tick() {
        rebuildElements();
    }

    private void rebuildElements() {
        children.clear();

        if (currentTarget == null)
            return;

        setTabs();
        setPanelTitle(currentTarget.getDisplayName());

        if (currentTarget instanceof Planet planet) {
            addEntryText(planet.getDisplayStorage());
        }
        if (currentTab.equals(Tab.STATS)) {
            showStats();
        }
        if (currentTarget instanceof Planet planet && currentTab.equals(Tab.GATHER)) {
            addGatherButtons(planet);
        }
        if (currentTarget instanceof Planet planet && currentTab.equals(Tab.BUILD)) {
            addBuildButtons(planet);
        }

        layout();
    }

    private void setTabs() {
        UIRow tabsRow = new UIRow(0);
        Vector4f textCol = new Vector4f(1.0f, 1.0f, 1.0f, 1f);
        float btnWidth = (350f - (10f * (Tab.values().length - 1))) / Tab.values().length;
        for (Tab tab : Tab.values()) {
            Vector4f btnBg = new Vector4f(0.2f, 0.2f, 0.5f, 0.5f);
            btnBg.y += 0.1f * tab.ordinal();
            if (currentTab.equals(tab)) {
                btnBg.w = 1f;
                btnBg.add(0.35f, 0.35f, 0.35f, 0f);
            }

            UIButton tabButton = new UIButton(btnWidth,
                                              40,
                                              btnBg,
                                              textCol,
                                              tab.name(),
                                              () -> {
                                                  currentTab = tab;
                                                  rebuildElements();
                                              },
                                              font);

            tabsRow.addElement(tabButton);
        }
        children.add(tabsRow);

    }

    private void showStats() {
        for (Map.Entry<String, String> entry : currentTarget.getDisplayProperties()) {
            addEntryText(entry);
        }
    }

    private void addEntryText(Entry<String, String> entry) {
        String line = entry.getKey() + ": " + entry.getValue();
        children.add(new UIText(line,
                                UIText.Alignment.LEFT,
                                new Vector4f(1, 1, 1, 1),
                                20,
                                15,
                                10,
                                font,
                                width));
    }

    private void addGatherButtons(Planet planet) {
        Vector4f btnBg = new Vector4f(0.2f, 0.4f, 0.8f, 1.0f);
        Vector4f textCol = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

        addMiningButtonRow(planet, btnBg, textCol);
        addActionButtonRow(planet, btnBg, textCol);
    }

    private void addBuildButtons(Planet planet) {
        Vector4f buildBtnBg = new Vector4f(0.8f, 0.5f, 0.2f, 1.0f);
        Vector4f textCol = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

        addBuildButtonRow(planet, buildBtnBg, textCol);

    }

    private void addBuildButtonRow(Planet planet, Vector4f buildBtnBg, Vector4f textCol) {
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
                                                                 rebuildElements();
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
                                                        rebuildElements();
                                                    }
                                                },
                                                font);
        buildSiloButton.setEnabled(canAfford(planet, "Storage Silo"));
        buildRow.addElement(buildSiloButton);

        children.add(buildRow);
    }

    private void addActionButtonRow(Planet planet, Vector4f btnBg, Vector4f textCol) {
        List<ItemType> allItems = new ArrayList<>(List.of(RawResource.values()));

        for (ItemType item : allItems) {

            UIRow transferRow = new UIRow(10);
            UIButton takeButton = new UIButton(170,
                                               35,
                                               btnBg,
                                               textCol,
                                               "Take 10 " + item.name(),
                                               () -> {
                                                   if (planet.getStorage()
                                                             .canWithdraw(item, 10) &&
                                                       playerStorage.canDeposit(10)) {
                                                       planet.withdraw(item, 10);
                                                       playerStorage.deposit(item, 10);
                                                       rebuildElements();
                                                   }
                                               },
                                               font);
            UIButton depositButton = new UIButton(170,
                                                  35,
                                                  btnBg,
                                                  textCol,
                                                  "Deposit 10 " + item.name(),
                                                  () -> {
                                                      if (planet.getStorage()
                                                                .canDeposit(10) &&
                                                          playerStorage.canWithdraw(item,
                                                                                    10)) {
                                                          playerStorage.withdraw(item,
                                                                                 10);
                                                          planet.deposit(item, 10);
                                                          rebuildElements();
                                                      }
                                                  },
                                                  font);

            takeButton.setEnabled(planet.getStorage().canWithdraw(item, 10) &&
                                  playerStorage.canDeposit(10));
            depositButton.setEnabled(planet.getStorage().canDeposit(10) &&
                                     playerStorage.canWithdraw(item, 10));

            transferRow.addElement(takeButton);
            transferRow.addElement(depositButton);

            children.add(transferRow);

        }
    }

    private void addMiningButtonRow(Planet planet, Vector4f btnBg, Vector4f textCol) {

        List<RawResource> harvestable = planet.getType().getHarvestableResources();
        UIRow mineRow = new UIRow(10);
        float btnWidth = (350f - (10f * (harvestable.size() - 1))) / harvestable.size();
        for (RawResource resource : harvestable) {
            mineRow.addElement(new UIButton(btnWidth,
                                            35,
                                            btnBg,
                                            textCol,
                                            "gather " + resource.name(),
                                            () -> {
                                                planet.deposit(resource, 10);
                                                rebuildElements();
                                            },
                                            font));
        }
        children.add(mineRow);
    }

    private boolean canAfford(Planet planet, String facilityName) {
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

    private void deductCost(Planet planet, String facilityName) {
        Map<ItemType, Integer> cost = FacilityConfig.COSTS.get(facilityName);
        if (cost == null)
            return;
        for (Map.Entry<ItemType, Integer> entry : cost.entrySet()) {
            planet.getStorage().withdraw(entry.getKey(), entry.getValue());
        }
    }

    @Override public void onResize(int screenWidth, int screenHeight) {
        setSize(400, screenHeight - 100);
    }

    private enum Tab {STATS, GATHER, BUILD}

}
