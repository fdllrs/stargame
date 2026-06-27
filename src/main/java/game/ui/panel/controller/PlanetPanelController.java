package game.ui.panel.controller;

import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import engine.ui.text.UIText.Alignment;
import game.objects.spaceBodies.Planet;
import game.objects.spaceBodies.SpaceBody;
import game.ui.Describable;
import game.ui.tabs.infotabs.UIStatsTab;
import org.joml.Vector4f;

import java.util.List;
import java.util.function.Consumer;

public class PlanetPanelController implements InfoPanelController {

	private static final Vector4f SECTION_HEADER_COLOR = new Vector4f(0.6f, 0.8f, 1.0f, 1.0f);

	private final Planet planet;
	private final Consumer<SpaceBody> onSelectTarget;

	public PlanetPanelController(Planet planet, Consumer<SpaceBody> onSelectTarget) {
		this.planet = planet;
		this.onSelectTarget = onSelectTarget;
	}

	private Runnable buildSelectHubAction() {
		return () -> {
			if (planet.hasHub() && onSelectTarget != null) {
				onSelectTarget.accept(planet.getHub());
			}
		};
	}

	@Override
	public void populate(List<UIElement> children,
			Describable target,
			FontAtlas font,
			float width) {

		children.add(sectionHeader("INFO", SECTION_HEADER_COLOR, font, width));
		for (UIElement element : UIStatsTab.build(planet, font, width, buildSelectHubAction())) {
			children.add(element);
		}
	}

	private UIText sectionHeader(String label, Vector4f color, FontAtlas font, float width) {
		return new UIText("- " + label + " -", Alignment.CENTER, color, 24, 4, 6, font, width);
	}
}
