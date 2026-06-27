package game.ui.panel;

import engine.ui.UIElement;
import engine.ui.UIImage;
import engine.ui.UIRow;
import engine.ui.UIScrollArea;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import engine.ui.text.UIText.Alignment;
import game.components.StorageComponent;
import game.objects.items.ItemIconRegistry;
import game.objects.items.ItemType;
import game.objects.items.ProcessedItem;
import game.objects.items.RawResource;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryPanel extends UIPanel {
	private final StorageComponent storageComponent;
	private final Map<ItemType, UIText> resourceLabels;
	private final float expandedHeight;
	private final game.components.UISlideAnimation slideAnimation;
	private float expandedY;
	private UIText storageFillText;
	private boolean expanded;
	private UIScrollArea scrollArea;
	private boolean isDocked = false;

	public InventoryPanel(float x,
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
		this.slideAnimation = new game.components.UISlideAnimation(this, 10.0f);

		engine.events.EventBus.subscribe(game.events.PlayerDockedEvent.class, event -> {
			isDocked = true;
			slideAnimation.slideOut(false);
		});

		engine.events.EventBus.subscribe(game.events.PlayerUndockedEvent.class, event -> {
			isDocked = false;
			slideAnimation.slideIn(false);
		});

		testGiveAllResources();
		setExpanded(false);
	}

	private void addResourceAmountsText() {
		List<ItemType> allItems = getAllItems();
		float scrollAreaHeight = this.height - 75;

		if (scrollArea == null) {
			scrollArea = new UIScrollArea(width, scrollAreaHeight, 0);
		}
		else {
			scrollArea.clearElements();
		}

		for (ItemType type : allItems) {
			String text = getNameAndAmountText(type);
			UIText label = new UIText(text,
									  Alignment.LEFT,
									  new Vector4f(1, 1, 1, 1),
									  20,
									  0,
									  5,
									  font,
									  width - hPadding * 2 - 42);

			resourceLabels.put(type, label);

			UIImage icon = new UIImage(32, 32, ItemIconRegistry.getIcon(type));
			UIRow row = new UIRow(10);
			row.addElement(icon);
			row.addElement(label);

			scrollArea.addElement(row);
		}
		children.add(scrollArea);
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
	public void update(float mouseX, float mouseY, float deltaTime) {
		slideAnimation.update(deltaTime);
		if (shouldRender()) {
			super.update(mouseX, mouseY, deltaTime);
		}
	}

	@Override
	public void rebuildElements() {
		children.clear();
		resourceLabels.clear();

		setPanelTitle("Inventory");
		addStorageCapacityText();

		if (expanded) {
			addResourceAmountsText();
		}

		super.rebuildElements();
		layout();
	}

	@Override
	public void onResize(int screenWidth, int screenHeight) {
		this.expandedY = screenHeight - this.expandedHeight - 20;
		float newX = screenWidth - this.width - 20;
		this.x = newX;

		float currentAnchorY = expanded ? expandedY : expandedY + ( expandedHeight - 80 );
		float currentHiddenY = screenHeight + 50;
		slideAnimation.configSlideY(currentAnchorY, currentHiddenY);

		if (isDocked) {
			slideAnimation.forceY(currentHiddenY);
		}
		else {
			slideAnimation.forceY(currentAnchorY);
		}
		setPosition(newX, this.y);

		if (scrollArea != null) {
			scrollArea.setSize(this.width, this.height - 75);
		}
		super.onResize(screenWidth, screenHeight);
	}

	@Override
	public boolean shouldRender() {
		return !isDocked || slideAnimation.isAnimatingY();
	}

	@NotNull
	private String getNameAndAmountText(ItemType type) {
		return type.name() + ": " + storageComponent.getAmount(type);
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
		float currentAnchorY = expanded ? expandedY : expandedY + ( expandedHeight - 80 );
		float currentHiddenY = expandedY + expandedHeight + 50;
		slideAnimation.configSlideY(currentAnchorY, currentHiddenY);

		if (isDocked) {
			slideAnimation.slideOut(true);
		}
		else {
			slideAnimation.slideIn(true);
		}

		setSize(width, expanded ? expandedHeight : 80);
		rebuildElements();
	}

	public void refreshAmounts() {
		storageFillText.setText(storageComponent.getFillForDisplay() + " " + "items");

		resourceLabels.forEach((type, label) -> label.setText(getNameAndAmountText(type)));
	}

	public void testGiveAllResources() {
		storageComponent.addCapacity(10000000);
		List<ItemType> allItems = getAllItems();
		for (ItemType type : allItems) {
			storageComponent.deposit(type, 10000);
		}
	}
}
