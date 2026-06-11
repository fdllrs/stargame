package engine.ui.panels;

import engine.ui.Describable;
import engine.ui.UIElement;
import engine.ui.tabs.UITab;
import engine.ui.tabs.UITabBar;
import engine.ui.tabs.infotabs.UIBuildTab;
import engine.ui.tabs.infotabs.UIStatsTab;
import engine.ui.tabs.infotabs.UIStorageTab;
import engine.ui.text.FontAtlas;
import game.components.StorageComponent;
import game.objects.celestialBodies.Planet;
import game.objects.celestialBodies.SpaceBody;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlanetPanelController implements InfoPanelController {
    private final UITabBar tabBar;

    public PlanetPanelController(Planet planet,
                                 StorageComponent playerStorage,
                                 FontAtlas font,
                                 float width,
                                 Runnable onRebuild,
                                 Consumer<SpaceBody> onSelectTarget) {
        this.tabBar = new UITabBar(5, font, onRebuild);
        Supplier<List<UIElement>> statsSupplier = () -> {
            Runnable selectHubAction = () -> {
                if (planet.hasHub() && onSelectTarget != null) {
                    onSelectTarget.accept(planet.getHub());
                }
            };
            return UIStatsTab.build(planet, font, width, selectHubAction);
        };

        List<UIElement> storageSupplier = UIStorageTab.build(planet.getStorage(),
                                                             playerStorage,
                                                             font,
                                                             width,
                                                             onRebuild);
        List<UIElement> buildSupplier = UIBuildTab.build(planet,
                                                         playerStorage,
                                                         font,
                                                         width,
                                                         onRebuild);
        this.tabBar.addTab("Stats", statsSupplier);
        this.tabBar.addTab("Storage", () -> storageSupplier);
        this.tabBar.addTab("Build", () -> buildSupplier);
    }

    @Override
    public void populate(List<UIElement> children,
                         Describable target,
                         StorageComponent playerStorage,
                         FontAtlas font,
                         float width) {
        children.add(tabBar);
        UITab activeTab = tabBar.getActiveTab();
        if (activeTab != null) {
            children.addAll(activeTab.getContent());
        }
    }
}
