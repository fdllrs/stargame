package engine.ui.panels;

import engine.ui.Describable;
import engine.ui.UIElement;
import engine.ui.tabs.UITab;
import engine.ui.tabs.UITabBar;
import engine.ui.tabs.infotabs.UIStatsTab;
import engine.ui.tabs.infotabs.UIUpgradeTab;
import engine.ui.text.FontAtlas;
import game.components.StorageComponent;
import game.objects.celestialBodies.Hub;

import java.util.List;

public class HubPanelController implements InfoPanelController {
    private final UITabBar tabBar;

    public HubPanelController(Hub hub,
                              StorageComponent playerStorage,
                              FontAtlas font,
                              float width,
                              Runnable onRebuild) {
        this.tabBar = new UITabBar(5, font, onRebuild);
        this.tabBar.addTab("Stats", () -> UIStatsTab.build(hub, font, width, null));
        this.tabBar.addTab("Upgrade",
                           () -> UIUpgradeTab.build(hub,
                                                    playerStorage,
                                                    font,
                                                    width,
                                                    onRebuild));
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
