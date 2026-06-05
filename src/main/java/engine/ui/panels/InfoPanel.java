package engine.ui.panels;

import engine.ui.Describable;
import engine.ui.UIElement;
import engine.ui.UIRow;
import engine.ui.buttons.UIButton;
import engine.ui.tabs.UITab;
import engine.ui.tabs.UITabBar;
import engine.ui.tabs.infotabs.UIBuildTab;
import engine.ui.tabs.infotabs.UIStatsTab;
import engine.ui.tabs.infotabs.UIStorageTab;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.components.StorageComponent;
import game.items.RawResource;
import game.objects.celestialBodies.Planet;
import org.joml.Vector4f;

import java.util.Map;

public class InfoPanel extends UIPanel {
    private final StorageComponent playerStorage;
    private Describable currentTarget;
    private UITabBar tabBar;

    public InfoPanel(float x,
                     float y,
                     float width,
                     float height,
                     Vector4f color,
                     FontAtlas font,
                     StorageComponent playerStorage) {
        super(x, y, width, height, color, font);
        this.playerStorage = playerStorage;

        playerStorage.deposit(RawResource.METAL, 10000);
    }

    public void tick() {
        rebuildElements();
    }

    @Override protected void rebuildElements() {
        children.clear();

        if (currentTarget == null)
            return;

        setPanelTitle(currentTarget.getDisplayName());

        if (tabBar != null) {
            children.add(tabBar);
            UITab activeTab = tabBar.getActiveTab();
            if (activeTab != null) {
                children.addAll(activeTab.getContent());
            }
        } else {
            buildDisplayProperties();
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

    private void buildDisplayProperties() {
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

    public void setTarget(Describable target) {
        this.currentTarget = target;
        initializeTabBar(target);
        rebuildElements();
    }

    private void initializeTabBar(Describable target) {
        if (target instanceof Planet planet) {
            tabBar = new UITabBar(5, font, this::rebuildElements);
            tabBar.addTab("Stats", () -> UIStatsTab.build(planet, font, width));
            tabBar.addTab("Storage",
                          () -> UIStorageTab.build(planet.getStorage(),
                                                   playerStorage,
                                                   font,
                                                   width,
                                                   this::rebuildElements));
            tabBar.addTab("Build",
                          () -> UIBuildTab.build(planet,
                                                 playerStorage,
                                                 font,
                                                 width,
                                                 this::rebuildElements));
        } else {
            tabBar = null;
        }
    }

    @Override public void onResize(int screenWidth, int screenHeight) {
        setSize(400, screenHeight - 100);
    }
}
