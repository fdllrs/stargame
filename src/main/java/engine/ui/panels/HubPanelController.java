package engine.ui.panels;

import engine.ui.tabs.UITabBar;
import engine.ui.tabs.infotabs.UIStatsTab;
import engine.ui.tabs.infotabs.UIUpgradeTab;
import engine.ui.text.FontAtlas;
import game.components.StorageComponent;
import game.objects.spaceBodies.Hub;

public class HubPanelController extends TabBarPanelController {

	public HubPanelController(Hub hub,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild) {
		super(new UITabBar(5, font, onRebuild));
		this.tabBar.addTab("Stats", () -> UIStatsTab.build(hub, font, width, null));
		this.tabBar.addTab("Upgrade",
						   () -> UIUpgradeTab.build(hub, playerStorage, font, width, onRebuild));
	}
}
