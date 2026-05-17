package engine.ui;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.graphics.Texture;

public class UIText extends UIElement {
    public static final String DEFAULT_FONT_PATH = "src/main/resources/fonts/charmap-oldschool.png";
    private String text;
    private Texture fontTexture;
    private int columns = 16;
    private int rows = 8;

    public UIText(String text, float x, float y, float letterWidth, float letterHeight, Vector4f color) {
        this(text, x, y, letterWidth, letterHeight, color, new Texture(DEFAULT_FONT_PATH));
    }

    public UIText(String text, float x, float y, float letterWidth, float letterHeight, Vector4f color, Texture fontTexture) {
        super(x, y, letterWidth, letterHeight, color);
        this.text = text;
        this.fontTexture = fontTexture;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        shader.setUniform("useTexture", 1);
        shader.setUniform("uiColor", this.color);
        shader.setUniform("uiTexture", 0);
        fontTexture.bind();

        float uScale = 1.0f / columns;
        float vScale = 1.0f / rows;
        shader.setUniform("uvScale", new org.joml.Vector2f(uScale, vScale));

        Matrix4f cursorMatrix = new Matrix4f(this.modelMatrix);

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            int ascii = (int) c;
            int col = ascii % columns;
            int row = ascii / columns;

            float uOffset = col * uScale;
            float vOffset = (rows - 1 - row) * vScale;

            shader.setUniform("uvOffset", new org.joml.Vector2f(uOffset, vOffset));
            shader.setUniform("model", cursorMatrix);

            uiQuad.render();

            cursorMatrix.translate(1.0f, 0.0f, 0.0f);
        }
    }
}