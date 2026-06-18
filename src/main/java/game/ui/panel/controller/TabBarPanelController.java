package game.ui.panel.controller;

import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import game.components.StorageComponent;
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
