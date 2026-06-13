package engine.ui.panels;

import engine.ui.UIElement;
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

public class PlanetPanelController extends TabBarPanelController {

	public PlanetPanelController(Planet planet,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			Consumer<SpaceBody> onSelectTarget) {
		super(new UITabBar(5, font, onRebuild));
		Supplier<List<UIElement>> statsSupplier = () -> {
			Runnable selectHubAction = () -> {
				if (planet.hasHub() && onSelectTarget != null) {
					onSelectTarget.accept(planet.getHub());
				}
			};
			return UIStatsTab.build(planet, font, width, selectHubAction);
		};

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
