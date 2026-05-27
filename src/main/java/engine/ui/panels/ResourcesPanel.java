package engine.ui.panels;

import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.managers.ResourceManager;
import game.managers.ResourceManager.ResourceType;
import org.joml.Vector4f;

import java.util.EnumMap;
import java.util.Map;

public class ResourcesPanel extends UIPanel {

    private final ResourceManager resourceManager;
    private final Map<ResourceType, UIText> resourceLabels;

    public ResourcesPanel(float x,
                          float y,
                          float width,
                          float height,
                          FontAtlas font,
                          ResourceManager resourceManager,
                          Vector4f color) {
        super(x, y, width, height, color, font);
        this.resourceLabels = new EnumMap<>(ResourceType.class);
        this.resourceManager = resourceManager;

        setPanelTitle("Resources");
        initializeResourceLabels();
        layout();
    }

    @Override
    protected void layout() {
        float currentY = this.y + vPadding;
        for (UIElement element : children) {
            element.setPosition(this.x, currentY);
            currentY += element.getBoundingHeight();
        }
    }

    private void initializeResourceLabels() {
        for (ResourceType type : ResourceType.values()) {
            UIText label = new UIText(type.name() + ": " + resourceManager.amountOf(type),
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

    @Override
    public void onResize(int screenWidth, int screenHeight) {
        // Snap to the new right edge of the screen
        setPosition(screenWidth - this.width - 20, screenHeight - this.height - 20);
    }

    public void refreshAmounts() {
        resourceLabels.forEach((type, label) -> {
            label.setText(type.name() + ": " + resourceManager.amountOf(type));
        });
    }

    @Override
    public float getBoundingHeight() {
        return height;
    }

}
