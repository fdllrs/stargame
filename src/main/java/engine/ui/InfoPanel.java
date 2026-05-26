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

    private final FontAtlas font;
    private final List<UIElement> elements = new java.util.ArrayList<>();
    private Describable currentTarget;

    public InfoPanel(float x, float y, float width, float height, Vector4f color, FontAtlas font) {
        super(x, y, width, height, color);
        this.font = font;
        this.vPadding = 10;

    }

    private void rebuildElements() {
        elements.clear();

        if (currentTarget == null) return;

        addTitle();
        addProperties();
        addButtons();

        calculateElementsYPos();
    }

    private void addButtons() {
        UIButton button = new UIButton(this,
                                       width / 3,
                                       20,
                                       new Vector4f(0, 0, 1, 1),
                                       new Vector4f(1, 1, 1, 1),
                                       "gather " + "iron",
                                       () -> System.out.println("click iron"),
                                       font);
        UIButton button2 = new UIButton(this,
                                        width / 2,
                                        70,
                                        new Vector4f(1, 0, 1, 1),
                                        new Vector4f(1, 1, 1, 1),
                                        "gather " + "gold",
                                        () -> System.out.println("click gold"),
                                        font);
        UIButton button3 = new UIButton(this,
                                        width / 2,
                                        70,
                                        new Vector4f(1, 0, 1, 1),
                                        new Vector4f(1, 1, 1, 1),
                                        "gather " + "water",
                                        () -> System.out.println("click water"),
                                        font);
        elements.add(button2);
        elements.add(button);
        elements.add(button3);
    }

    private void addProperties() {
        for (Map.Entry<String, String> entry : currentTarget.getDisplayProperties()) {
            String line = entry.getKey() + ": " + entry.getValue();
            elements.add(new UIText(line, this, UIText.Alignment.LEFT, new Vector4f(1, 1, 1, 1), 20, 15, 10, font));
        }
    }

    private void addTitle() {
        elements.add(new UIText(currentTarget.getDisplayName(),
                                this,
                                UIText.Alignment.CENTER,
                                new Vector4f(1, 1, 1, 1),
                                24,
                                1,
                                15,
                                font));
    }


    private void calculateElementsYPos() {
        float currentY = this.y + this.height;
        for (UIElement element : elements) {

            element.setYPos(currentY);
            currentY -= element.getBoundingHeight();
        }
    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        if (currentTarget == null) return;

        shader.setUniform("useTexture", 0);
        shader.setUniform("uiColor", this.color);
        shader.setUniform("model", this.modelMatrix);
        uiQuad.render();

        for (UIElement element : elements) {
            element.render(shader, uiQuad);
        }
    }

    @Override
    public float getBoundingHeight() {
        return this.height + vPadding;
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

        rebuildElements();
    }

    public void handleClick(float mouseX, float mouseY) {
        for (UIElement element : elements) {
            if (element.contains(mouseX, mouseY)) {
                element.handleClick(mouseX, mouseY);
                return;
            }
        }

    }
}
