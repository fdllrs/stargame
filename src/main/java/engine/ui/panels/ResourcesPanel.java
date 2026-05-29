package engine.ui.panels;

import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import engine.ui.text.UIText.Alignment;
import game.components.StorageComponent;
import game.items.ItemType;
import game.items.RawResource;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcesPanel extends UIPanel {
    private final StorageComponent storageComponent;
    private final Map<ItemType, UIText> resourceLabels;

    public ResourcesPanel(float x,
                          float y,
                          float width,
                          float height,
                          FontAtlas font,
                          StorageComponent storageComponent,
                          Vector4f color) {
        super(x, y, width, height, color, font);
        this.resourceLabels = new HashMap<>();
        this.storageComponent = storageComponent;

        setPanelTitle("Ship Cargo");
        initializeResourceLabels();
        layout();
    }

    @Override protected void layout() {
        float currentY = this.y + vPadding;
        for (UIElement element : children) {
            element.setPosition(this.x, currentY);
            currentY += element.getBoundingHeight();
        }
    }

    private void initializeResourceLabels() {
        List<ItemType> allItems = new ArrayList<>(List.of(RawResource.values()));

        children.add(new UIText(storageComponent.getFillForDisplay(),
                                Alignment.CENTER,
                                new Vector4f(1, 1, 1, 1),
                                16,
                                10,
                                5,
                                font,
                                width));

        for (ItemType type : allItems) {
            String text = getNameAndAmountText(type);
            UIText label = new UIText(text,
                                      UIText.Alignment.LEFT,
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

    @NotNull private String getNameAndAmountText(ItemType type) {
        return type.name() + ": " + storageComponent.getAmount(type);
    }

    @Override public void onResize(int screenWidth, int screenHeight) {
        // Snap to the new right edge of the screen
        setPosition(screenWidth - this.width - 20, screenHeight - this.height - 20);
    }

    public void refreshAmounts() {
        resourceLabels.forEach((type, label) -> {
            label.setText(getNameAndAmountText(type));
        });
    }

    @NotNull private String getNameAndAmountAndCapacityText(ItemType type) {
        return getNameAndAmountText(type) + "/" + storageComponent.getCapacity();
    }

    @Override public float getBoundingHeight() {
        return height;
    }

}
