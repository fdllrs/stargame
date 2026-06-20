package game.ui.tabs.infotabs;

import engine.ui.UIElement;
import engine.ui.UIResourceSlot;
import engine.ui.UIScrollArea;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.components.StorageComponent;
import game.objects.items.ItemType;
import game.objects.items.ProcessedItem;
import game.objects.items.RawResource;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class UIStorageTab {
	public static List<UIElement> build(StorageComponent planetStorage,
			StorageComponent playerStorage,
			FontAtlas font,
			float width,
			Runnable onRebuild,
			float availableHeight) {
		List<UIElement> elements = new ArrayList<>();
		Vector4f textCol = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

		elements.add(new UIText("Storage capacity: " + planetStorage.getFillForDisplay(),
								UIText.Alignment.LEFT,
								textCol,
								20,
								5,
								5,
								font,
								width));

		elements.add(new UIText("Hover line & Shift + Scroll to transfer:",
								UIText.Alignment.LEFT,
								new Vector4f(0.7f, 0.7f, 0.7f, 1.0f),
								14,
								5,
								10,
								font,
								width));

		elements.add(new UIText("Resource              Planet   |   Ship",
								UIText.Alignment.LEFT,
								new Vector4f(0.5f, 0.5f, 0.5f, 1.0f),
								13,
								10,
								5,
								font,
								width));

		List<ItemType> allItems = new ArrayList<>();
		allItems.addAll(List.of(RawResource.values()));
		allItems.addAll(List.of(ProcessedItem.values()));
		Vector4f bg = new Vector4f(0.1f, 0.5f, 0.1f, 0.5f);

		float scrollAreaHeight = availableHeight -
								 170; // Pin title, tab bar, and capacity headers, leaving content
		// scrollable
		UIScrollArea scrollArea = new UIScrollArea(width, scrollAreaHeight, 0);

		for (ItemType item : allItems) {
			if (planetStorage.getAmount(item) > 0 || playerStorage.getAmount(item) > 0) {
				bg.y += 0.1f;
				bg.z += 0.1f;
				scrollArea.addElement(new UIResourceSlot(width,
														 40,
														 item,
														 planetStorage,
														 playerStorage,
														 font,
														 onRebuild,
														 new Vector4f(bg)));
			}
		}

		elements.add(scrollArea);
		return elements;
	}
}
