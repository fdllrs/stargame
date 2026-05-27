package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.managers.ResourceManager;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcesPanel extends UIElement {

    private final ResourceManager resourceManager;
    private final List<UIElement> elements = new java.util.ArrayList<>();
    private final Map<ResourceManager.RESOURCE_TYPE, UIText> resources = new HashMap<>();
    private final FontAtlas font;

    public ResourcesPanel(float x,
                          float y,
                          float width,
                          float height,
                          FontAtlas font,
                          ResourceManager resourceManager,
                          Vector4f color) {
        super(x, y, width, height, color);
        this.resourceManager = resourceManager;
        this.font = font;
        addTitle();
        elements.addAll(resources.values());
        refreshAmounts();
        calculateElementsYPos();
    }

    public void refreshAmounts() {

        resources.forEach((type, text) -> {
            text.setText(type.toString() + ": " + resourceManager.amountOf(type));
        });

        elements.clear();
        addTitle();
        displayResources();
        elements.addAll(resources.values());
        calculateElementsYPos();
    }

    private void calculateElementsYPos() {
        float currentY = this.y + this.height;
        for (UIElement element : elements) {
            element.setYPos(currentY);
            currentY -= element.getBoundingHeight();
        }
    }

    public void displayResources() {
        for (ResourceManager.RESOURCE_TYPE type : ResourceManager.RESOURCE_TYPE.values()) {
            resources.put(type,
                          new UIText(type.toString() + ": " + resourceManager.amountOf(type),
                                     this,
                                     UIText.Alignment.LEFT,
                                     new Vector4f(1, 1, 1, 1),
                                     16,
                                     10,
                                     15,
                                     font));
        }

    }

    private void addTitle() {
        elements.add(new UIText("RESOURCES", this, UIText.Alignment.CENTER, new Vector4f(1, 1, 1, 1), 24, 1, 15, font));
    }

    @Override
    public float getBoundingHeight() {
        return height;
    }

    @Override
    public void handleClick(float mouseX, float mouseY) {

    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        shader.setUniform("useTexture", 0);
        shader.setUniform("uiColor", this.color);
        shader.setUniform("model", this.modelMatrix);
        uiQuad.render();

        for (UIElement element : elements) {
            element.render(shader, uiQuad);
        }
    }
}
