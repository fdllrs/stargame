package engine.ui.tabs.infotabs;

import engine.ui.UIElement;
import engine.ui.buttons.UIButton;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.components.StorageComponent;
import game.items.RawResource;
import game.objects.celestialBodies.Hub;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class UIUpgradeTab {
	public static List<UIElement> build(Hub hub,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild) {
		List<UIElement> elements = new ArrayList<>();
		Vector4f textCol = new Vector4f(1, 1, 1, 1);
		Vector4f buildBtnBg = new Vector4f(0.2f, 0.2f, 0.2f, 0.8f);

		int cost = hub.level * 200;
		elements.add(new UIText("Current Level: " + hub.level,
								UIText.Alignment.LEFT,
								textCol,
								20,
								15,
								10,
								font,
								width));

		elements.add(new UIText("Upgrade Cost: " + cost + " Metal",
								UIText.Alignment.LEFT,
								textCol,
								20,
								15,
								10,
								font,
								width));

		boolean canAfford = playerStorage.canWithdraw(RawResource.METAL, cost);

		UIButton upgradeButton = new UIButton(width * 0.8f,
											  80,
											  buildBtnBg,
											  textCol,
											  "Upgrade Hub",
											  () -> {
												  if (playerStorage.canWithdraw(RawResource.METAL,
																				cost)) {
													  playerStorage.attemptWithdraw(RawResource.METAL,
																					cost);
													  hub.level++;
													  onRebuild.run();
												  }
											  },
											  font);
		upgradeButton.setEnabled(canAfford);
		elements.add(upgradeButton);

		return elements;
	}
}
