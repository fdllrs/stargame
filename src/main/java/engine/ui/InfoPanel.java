package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

/**
 * A UI panel that displays the name and properties of any {@link Describable} object.
 * The engine layer has no dependency on game-specific types.
 */
public class InfoPanel extends UIElement {

    private Describable currentTarget;
    private List<UIElement> elements = new java.util.ArrayList<>();
    private final FontAtlas font;

    public InfoPanel(float x, float y, float width, float height, Vector4f color, FontAtlas font) {
        super(x, y, width, height, color);
        this.font = font;
    }

    private void rebuildElements() {
        elements.clear();

        if (currentTarget == null) return;

        // Title
        elements.add(new UIText(
                currentTarget.getDisplayName(), this,
                UIText.Alignment.CENTER, new Vector4f(1, 1, 1, 1), 24, 1, 15, font));

        // Properties
        for (Map.Entry<String, String> entry : currentTarget.getDisplayProperties()) {
            String line = entry.getKey() + ": " + entry.getValue();
            elements.add(new UIText(
                    line, this,
                    UIText.Alignment.LEFT, new Vector4f(1, 1, 1, 1), 20, 15, 10, font));
        }

        calculateElementsSpacing();
    }

    private void calculateElementsSpacing() {
        float spacing  = 10;
        float currentY = this.y + this.height;
        for (UIElement element : elements) {
            element.y = currentY;
            currentY -= element.getBoundingHeight() + spacing;
        }
    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        if (currentTarget == null) return;

        shader.setUniform("useTexture", 0);
        shader.setUniform("uiColor",    this.color);
        shader.setUniform("model",      this.modelMatrix);
        uiQuad.render();

        for (UIElement element : elements) {
            element.render(shader, uiQuad);
        }
    }

    @Override
    public float getBoundingHeight() {
        return this.height;
    }

    public void setTarget(Describable target) {
        this.currentTarget = target;
        rebuildElements();
    }

    @Override
    public void setSize(float newWidth, float newHeight) {
        if (this.width == newWidth && this.height == newHeight) return;

        super.setSize(newWidth, newHeight);

        for (UIElement child : elements) {
            if (child instanceof UIText textChild) {
                textChild.setMaxWidth(this.width);
            }
        }

        calculateElementsSpacing();
    }
}
