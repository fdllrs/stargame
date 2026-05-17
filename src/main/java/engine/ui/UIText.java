package engine.ui;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;

public class UIText extends UIElement {
    private static final String DEFAULT_FONT_FILE = "src/main/resources/fonts/fontfile.fnt";
    private static final String DEFAULT_FONT_TEXTURE = "src/main/resources/fonts/fontfile.png";

    private String text;
    private FontAtlas font;
    private float fontSizeMultiplier;


    public UIText(String text, float x, float y, float fontSizeMultiplier, Vector4f color) {
        this(text, x, y, fontSizeMultiplier, color, new FontAtlas(DEFAULT_FONT_FILE, DEFAULT_FONT_TEXTURE));
    }

    public UIText(String text, float x, float y, float fontSizeMultiplier, Vector4f color, FontAtlas font) {
        super(x, y, 0, 0, color); // width/height don't matter here, they are dynamic
        this.text = text;
        this.font = font;
        this.fontSizeMultiplier = fontSizeMultiplier;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        shader.setUniform("useTexture", 1);
        shader.setUniform("uiTexture", 0);
        shader.setUniform("uiColor", this.color);
        font.getTexture().bind();

        float scaleW = font.getScaleW();
        float scaleH = font.getScaleH();

        // The cursor starts at the element's base X and Y position
        float cursorX = this.x;
        float cursorY = this.y;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            CharacterInfo charInfo = font.getCharacter((int) c);

            // If we try to draw a character that doesn't exist in the file, skip it
            if (charInfo == null) continue;

            // --- 1. CALCULATE UVS (Shader Math) ---
            float uScale = charInfo.width() / scaleW;
            float vScale = charInfo.height() / scaleH;

            float uOffset = charInfo.x() / scaleW;
            float vOffset = (scaleH - charInfo.y() - charInfo.height()) / scaleH;

            shader.setUniform("uvScale", new org.joml.Vector2f(uScale, vScale));
            shader.setUniform("uvOffset", new org.joml.Vector2f(uOffset, vOffset));

            Matrix4f letterMatrix = getLetterMatrix(cursorX, charInfo, cursorY);

            shader.setUniform("model", letterMatrix);

            uiQuad.render();

            cursorX += (charInfo.xAdvance() * fontSizeMultiplier);
        }
    }

    @NotNull
    private Matrix4f getLetterMatrix(float cursorX, CharacterInfo charInfo, float cursorY) {
        Matrix4f letterMatrix = new Matrix4f();

        float targetX = cursorX + (charInfo.xOffset() * fontSizeMultiplier);
        float targetY = cursorY - ((charInfo.yOffset() + charInfo.height()) * fontSizeMultiplier);

        letterMatrix.translate(targetX, targetY, 0.0f);

        letterMatrix.scale(charInfo.width() * fontSizeMultiplier, charInfo.height() * fontSizeMultiplier, 1.0f);
        return letterMatrix;
    }
}