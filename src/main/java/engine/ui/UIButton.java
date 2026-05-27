package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import org.joml.Vector4f;

public class UIButton extends UIElement {

    private final Runnable onClick;
    private final UIText textLabel;
    private final UIElement container;

    public UIButton(UIElement container, float width, float height, Vector4f backgroundColor, Vector4f textColor, String textLabel, Runnable onClick, FontAtlas fontAtlas) {
        super(0, 0, width, height, backgroundColor);

        this.x = (container.width - this.width) / 2;
        this.textLabel = new UIText(textLabel, this, UIText.Alignment.CENTER, textColor, 15, 10, 5, fontAtlas);
        this.onClick = onClick;
        this.vPadding = 15;
        this.hPadding = 10;
        this.container = container;

    }

    @Override
    protected void updateMatrix() {
        modelMatrix.identity();
        // Translate the rendering start point down by height so 'y' is the top
        modelMatrix.translate(x, y - height, 0);
        modelMatrix.scale(width, height, 1);
    }

    @Override
    public float getBoundingHeight() {
        return height + vPadding;
    }

    @Override
    public void handleClick(float mouseX, float mouseY) {
        if (onClick != null) {
            onClick.run();
        }
    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        shader.setUniform("useTexture", 0);
        shader.setUniform("uiColor", this.color);
        shader.setUniform("model", this.modelMatrix);
        uiQuad.render();

        textLabel.render(shader, uiQuad);

    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (textLabel != null) {
            textLabel.setPosition(x, y - height / 2 + textLabel.getBoundingHeight() / 2);
        }
    }

    @Override
    public boolean contains(float mouseX, float mouseY) {
        return mouseX >= this.x && mouseX <= this.x + width && mouseY >= this.y - height && mouseY <= this.y;
    }

}
