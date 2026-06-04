package engine.ui.panels;

import engine.ui.Describable;
import engine.ui.UIElement;
import engine.ui.UIRow;
import engine.ui.buttons.UIButton;
import engine.ui.panels.infotabs.UIBuildTab;
import engine.ui.panels.infotabs.UIStatsTab;
import engine.ui.panels.infotabs.UIStorageTab;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.components.StorageComponent;
import game.objects.celestialBodies.Planet;
import org.joml.Vector4f;

import java.util.Map;

public class InfoPanel extends UIPanel {
    private final StorageComponent playerStorage;
    private Describable currentTarget;
    private Tab currentTab = Tab.STATS;

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

    public void tick() {
        rebuildElements();
    }

    public void setTarget(Describable target) {
        this.currentTarget = target;
        this.currentTab = Tab.STATS; // Reset to stats tab on target switch
        rebuildElements();
    }

    @Override protected void rebuildElements() {
        children.clear();

        if (currentTarget == null)
            return;

        setPanelTitle(currentTarget.getDisplayName());

        addTabsBar();

        if (currentTarget instanceof Planet planet) {
            switch (currentTab) {
                case STATS -> children.addAll(UIStatsTab.build(planet, font, width));
                case STORAGE -> children.addAll(UIStorageTab.build(planet.getStorage(),
                                                                   playerStorage,
                                                                   font,
                                                                   width,
                                                                   this::rebuildElements));
                case CONSTRUCTION -> children.addAll(UIBuildTab.build(planet,
                                                                      font,
                                                                      width,
                                                                      this::rebuildElements));
            }
        } else {
            addProperties();
        }

        layout();
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

    @Override public void layout() {
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

    private void addTabsBar() {
        UIRow tabsRow = new UIRow(5);
        Vector4f activeColor = new Vector4f(0.2f, 0.5f, 0.9f, 1.0f);    // Slate Blue
        Vector4f inactiveColor = new Vector4f(0.12f, 0.12f, 0.12f, 1.0f); // Dark Charcoal
        Vector4f textCol = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

        tabsRow.addElement(new UIButton(110,
                                        30,
                                        (currentTab == Tab.STATS)
                                        ? activeColor
                                        : inactiveColor,
                                        textCol,
                                        "Stats",
                                        () -> {
                                            currentTab = Tab.STATS;
                                            rebuildElements();
                                        },
                                        font));

        tabsRow.addElement(new UIButton(110,
                                        30,
                                        (currentTab == Tab.STORAGE)
                                        ? activeColor
                                        : inactiveColor,
                                        textCol,
                                        "Storage",
                                        () -> {
                                            currentTab = Tab.STORAGE;
                                            rebuildElements();
                                        },
                                        font));

        tabsRow.addElement(new UIButton(110,
                                        30,
                                        (currentTab == Tab.CONSTRUCTION)
                                        ? activeColor
                                        : inactiveColor,
                                        textCol,
                                        "Build",
                                        () -> {
                                            currentTab = Tab.CONSTRUCTION;
                                            rebuildElements();
                                        },
                                        font));

        children.add(tabsRow);
    }

    private void addProperties() {
        for (Map.Entry<String, String> entry : currentTarget.getDisplayProperties()) {
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
    }

    @Override public void onResize(int screenWidth, int screenHeight) {
        setSize(400, screenHeight - 100);
    }

    private enum Tab {STATS, STORAGE, CONSTRUCTION}
}
