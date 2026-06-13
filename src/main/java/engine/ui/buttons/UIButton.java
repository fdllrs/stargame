package engine.ui.buttons;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import org.joml.Vector4f;

public class UIButton extends UIElement {
    private final Runnable onClick;
    private final UIText textLabel;
    private boolean enabled = true;

    public UIButton(float width,
                    float height,
                    Vector4f backgroundColor,
                    Vector4f textColor,
                    String textLabel,
                    Runnable onClick,
                    FontAtlas fontAtlas) {
        super(0, 0, width, height, backgroundColor);

        this.onClick = onClick;
        this.vPadding = 15;
        this.hPadding = 10;
        this.textLabel = new UIText(textLabel,
                                    UIText.Alignment.CENTER,
                                    textColor,
                                    15,
                                    10,
                                    5,
                                    fontAtlas,
                                    width);
        alignText();
    }

    private void alignText() {
        if (textLabel != null) {
            textLabel.setPosition(x, y + (height - textLabel.getBoundingHeight()) / 2);
        }
    }

    @Override
    public LayoutAlignment getLayoutAlignment() {
        return LayoutAlignment.CENTER;
    }

    @Override public float getBoundingHeight() {
        return height + vPadding;
    }

    @Override public void handleClick(float mouseX, float mouseY) {
        if (enabled && onClick != null) {
            onClick.run();
        }
    }

    @Override public void render(ShaderProgram shader, Mesh uiQuad) {
        shader.setUniform("useTexture", 0);
        shader.setUniform("uiColor", this.color);
        shader.setUniform("model", this.modelMatrix);
        uiQuad.render();

        textLabel.render(shader, uiQuad);
    }

    @Override public void setPosition(float x, float y) {
        super.setPosition(x, y);
        alignText();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setOpacity(enabled ? 1.0f : 0.3f);
        if (this.textLabel != null) {
            this.textLabel.setOpacity(enabled ? 1.0f : 0.3f);
        }
    }
}
