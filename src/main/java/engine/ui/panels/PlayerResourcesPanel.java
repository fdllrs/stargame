package engine.ui.panels;

import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import engine.ui.text.UIText.Alignment;
import game.components.StorageComponent;
import game.items.ItemType;
import game.items.ProcessedItem;
import game.items.RawResource;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerResourcesPanel extends UIPanel {
	private final StorageComponent storageComponent;
	private final Map<ItemType, UIText> resourceLabels;
	private final float expandedHeight;
	private float expandedY;
	private UIText storageFillText;
	private boolean expanded;

	public PlayerResourcesPanel(float x,
			float y,
			float width,
			float height,
			FontAtlas font,
			StorageComponent storageComponent,
			Vector4f color) {
		super(x, y, width, height, color, font);
		this.resourceLabels = new HashMap<>();
		this.storageComponent = storageComponent;
		this.expandedHeight = height;
		this.expandedY = y;

		testGiveAllResources();
		setExpanded(false);
	}

	private void addResourceAmountsText() {
		List<ItemType> allItems = getAllItems();
		for (ItemType type : allItems) {
			String text = getNameAndAmountText(type);
			UIText label = new UIText(text,
									  Alignment.LEFT,
									  new Vector4f(1, 1, 1, 1),
									  16,
									  10,
									  15,
									  font,
									  width);

			resourceLabels.put(type, label);
			children.add(label);
		}
	}

	private void addStorageCapacityText() {
		UIText storageFillText = new UIText(storageComponent.getFillForDisplay() + " " + "items",
											Alignment.CENTER,
											new Vector4f(1, 1, 1, 1),
											16,
											10,
											5,
											font,
											width);
		this.storageFillText = storageFillText;
		children.add(storageFillText);
	}

	private List<ItemType> getAllItems() {
		List<ItemType> rawResources = new ArrayList<>(List.of(RawResource.values()));
		List<ItemType> processedItems = new ArrayList<>(List.of(ProcessedItem.values()));
		List<ItemType> allItems = new ArrayList<>(rawResources);
		allItems.addAll(processedItems);
		return allItems;
	}

	@NotNull
	private String getNameAndAmountText(ItemType type) {
		return type.name() + ": " + storageComponent.getAmount(type);
	}

	@Override
	protected void layout() {
		float currentY = this.y + vPadding;
		for (UIElement element : children) {
			element.setPosition(this.x, currentY);
			currentY += element.getBoundingHeight();
		}
	}

	@Override
	protected void rebuildElements() {
		children.clear();
		resourceLabels.clear();

		setPanelTitle("Inventory");
		addStorageCapacityText();

		if (expanded) {
			addResourceAmountsText();
		}
		layout();
	}

	@Override
	public float getBoundingHeight() {
		float currentY = this.y + vPadding;
		for (UIElement element : children) {
			currentY += element.getBoundingHeight();
		}
		return currentY;
	}

	public void handleClick(float mouseX, float mouseY) {

		setExpanded(!expanded);

		super.handleClick(mouseX, mouseY);
	}

	@Override
	public void onResize(int screenWidth, int screenHeight) {
		this.expandedY = screenHeight - this.expandedHeight - 20;
		if (expanded) {
			setPosition(screenWidth - this.width - 20, expandedY);
		}
		else {
			setPosition(screenWidth - this.width - 20, expandedY + ( expandedHeight - 80 ));
		}
	}

	public void refreshAmounts() {
		storageFillText.setText(storageComponent.getFillForDisplay() + " " + "items");

		resourceLabels.forEach((type, label) -> label.setText(getNameAndAmountText(type)));
	}

	private void setExpanded(boolean expanded) {
		this.expanded = expanded;
		if (expanded) {
			setSize(width, expandedHeight);
			setPosition(x, expandedY);
			rebuildElements();
		}
		else {
			setSize(width, 80);
			setPosition(x, expandedY + ( expandedHeight - 80 ));
			rebuildElements();
		}
	}

	public void testGiveAllResources() {
		storageComponent.addCapacity(10000000);
		List<ItemType> allItems = getAllItems();
		for (ItemType type : allItems) {
			storageComponent.deposit(type, 10000);
		}
	}
}
