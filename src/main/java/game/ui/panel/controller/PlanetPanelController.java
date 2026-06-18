package game.ui.panel.controller;

import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import game.components.StorageComponent;
import game.objects.spaceBodies.Planet;
import game.objects.spaceBodies.SpaceBody;
import game.ui.tabs.UITabBar;
import game.ui.tabs.infotabs.UIBuildTab;
import game.ui.tabs.infotabs.UIStatsTab;
import game.ui.tabs.infotabs.UIStorageTab;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PlanetPanelController extends TabBarPanelController {

	public PlanetPanelController(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Consumer<SpaceBody> onSelectTarget) {
		super(new UITabBar(5, font, onRebuild));

		Runnable selectHubAction = () -> {
			if (planet.hasHub() && onSelectTarget != null) {
				onSelectTarget.accept(planet.getHub());
			}
		};

		Supplier<List<UIElement>> statsSupplier = () -> UIStatsTab.build(planet,
																		 font,
																		 width,
																		 selectHubAction);

		this.tabBar.addTab("Stats", statsSupplier);
		this.tabBar.addTab("Storage",
						   () -> UIStorageTab.build(planet.getStorage(),
													playerStorage,
													font,
													width,
													onRebuild));
		this.tabBar.addTab("Build",
						   () -> UIBuildTab.build(planet, playerStorage, font, width, onRebuild));
	}
}
