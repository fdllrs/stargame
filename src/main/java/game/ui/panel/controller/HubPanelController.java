package game.ui.panel.controller;

import engine.ui.text.FontAtlas;
import game.objects.spaceBodies.Hub;
import game.ui.tabs.UITabBar;
import game.ui.tabs.infotabs.UIStatsTab;

public class HubPanelController extends TabBarPanelController {

	public HubPanelController(Hub hub, FontAtlas font, float width) {
		super(new UITabBar(5, font, () -> { }));
		this.tabBar.addTab("Stats", () -> UIStatsTab.build(hub, font, width, null));
	}
}
