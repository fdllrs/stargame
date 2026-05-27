package engine.ui.panels;

import engine.ui.Describable;
import engine.ui.UIButton;
import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.managers.ResourceManager;
import org.joml.Vector4f;

import java.util.Map;

public class InfoPanel extends UIPanel {

    private final ResourceManager resourceManager;
    private Describable currentTarget;

    public InfoPanel(float x,
                     float y,
                     float width,
                     float height,
                     Vector4f color,
                     FontAtlas font,
                     ResourceManager resourceManager) {
        super(x, y, width, height, color, font);

        this.resourceManager = resourceManager;

    }

    @Override
    public float getBoundingHeight() {
        return this.height + vPadding;
    }

    public void handleClick(float mouseX, float mouseY) {
        for (UIElement element : children) {
            if (element.contains(mouseX, mouseY)) {
                element.handleClick(mouseX, mouseY);
                return;
            }
        }

    }

    public void setTarget(Describable target) {
        this.currentTarget = target;
        rebuildElements();
    }

    private void rebuildElements() {
        children.clear();

        if (currentTarget == null)
            return;

        setPanelTitle(currentTarget.getDisplayName());
        addProperties();
        addButtons();

        layout();
    }

    @Override
    protected void layout() {
        float currentY = this.y + vPadding;
        for (UIElement element : children) {
            float elementX = this.x;
            // Center buttons horizontally; keep other components left-aligned
            if (element instanceof UIButton) {
                elementX = this.x + (this.width - element.getSize().x) / 2.0f;
            }
            element.setPosition(elementX, currentY);
            currentY += element.getBoundingHeight();
        }
    }

    @Override
    public boolean shouldRender() {
        return currentTarget != null;
    }

    private void addButtons() {
        UIButton button = new UIButton(width / 2,
                                       70,
                                       new Vector4f(0, 0, 1, 1),
                                       new Vector4f(1, 1, 1, 1),
                                       "gather " + "iron",
                                       () -> resourceManager.gatherResource(ResourceManager.ResourceType.IRON, 10),
                                       font);
        UIButton button2 = new UIButton(width / 2,
                                        70,
                                        new Vector4f(1, 0, 1, 1),
                                        new Vector4f(1, 1, 1, 1),
                                        "gather " + "gold",
                                        () -> resourceManager.gatherResource(ResourceManager.ResourceType.GOLD, 10),
                                        font);
        UIButton button3 = new UIButton(width / 2,
                                        70,
                                        new Vector4f(1, 0, 1, 1),
                                        new Vector4f(1, 1, 1, 1),
                                        "gather " + "water",
                                        () -> resourceManager.gatherResource(ResourceManager.ResourceType.WATER, 10),
                                        font);
        children.add(button);
        children.add(button2);
        children.add(button3);
    }

    private void addProperties() {
        for (Map.Entry<String, String> entry : currentTarget.getDisplayProperties()) {
            String line = entry.getKey() + ": " + entry.getValue();
            children.add(new UIText(line, UIText.Alignment.LEFT, new Vector4f(1, 1, 1, 1), 20, 15, 10, font, width));
        }
    }

    @Override
    public void onResize(int screenWidth, int screenHeight) {
        // Keep width at 400, adjust height to fill screen height minus padding
        setSize(400, screenHeight - 100);
    }

}
