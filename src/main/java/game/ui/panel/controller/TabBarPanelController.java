package game.ui.panel.controller;

import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import game.ui.Describable;
import game.ui.tabs.UITab;
import game.ui.tabs.UITabBar;

import java.util.List;

public abstract class TabBarPanelController implements InfoPanelController {
	protected final UITabBar tabBar;

	protected TabBarPanelController(UITabBar tabBar) {
		this.tabBar = tabBar;
	}

	@Override
	public final void populate(List<UIElement> children,
			Describable target,
			FontAtlas font,
			float width) {
		children.add(tabBar);
		UITab activeTab = tabBar.getActiveTab();
		if (activeTab != null) {
			children.addAll(activeTab.getContent());
		}
	}
}
