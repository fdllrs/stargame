package engine.ui.text;

import engine.ui.UIElement;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;

public class UIText extends UIElement {

    public static final float REAL_FONT_SIZE = 88f;
    public static final int MAX_CHAR_HEIGHT = 60;
    public static final int LINE_SPACING = 10;

    private String rawText;       // The original string
    private String renderText;    // The wrapped string with \n
    private float currentMaxWidth = -1f; // Tracks the last known width

    private FontAtlas font;
    public float fontSizeMultiplier;
    private UIElement container;
    private float containerPadding;
    private Alignment alignment;

    public enum Alignment {
        LEFT, CENTER, RIGHT;
    }

    public UIText(String text,
                  UIElement container,
                  Alignment alignment,
                  Vector4f color,
                  int fontSize,
                  int containerPadding,
                  FontAtlas font
                  ) {

        super(0, 0, 0, 0, color);

        this.font = font;
        this.alignment = alignment;
        this.container = container;
        this.fontSizeMultiplier = (float) fontSize / REAL_FONT_SIZE;
        this.containerPadding = containerPadding;
        this.rawText = text;
        this.renderText = wrapText(container.getSize().x - (2 * containerPadding));
        calculateTextPosition();
    }

    public void setMaxWidth(float maxPixelWidth) {
        if (Math.abs(this.currentMaxWidth - maxPixelWidth) < 0.1f) return;

        this.currentMaxWidth = maxPixelWidth;

        this.renderText = wrapText(maxPixelWidth - (2 * containerPadding));
    }

    public String wrapText(float maxPixelWidth) {
        String[] words = rawText.split(" ");
        StringBuilder wrappedText = new StringBuilder();
        float currentLineWidth = 0;

        for (String word : words) {
            float wordWidth = 0;
            for (int i = 0; i < word.length(); i++) {
                CharacterInfo info = font.getCharacter(word.charAt(i));
                if (info != null) {
                    wordWidth += (info.xAdvance() * fontSizeMultiplier);
                }
            }

            CharacterInfo spaceInfo = font.getCharacter(' ');
            float spaceWidth = (spaceInfo != null ? spaceInfo.xAdvance() * fontSizeMultiplier : 10 * fontSizeMultiplier);

            if (currentLineWidth + wordWidth > maxPixelWidth) {
                wrappedText.append("\n").append(word).append(" ");
                currentLineWidth = wordWidth + spaceWidth;
            } else {
                wrappedText.append(word).append(" ");
                currentLineWidth += wordWidth + spaceWidth;
            }
        }

        return wrappedText.toString();

    }
    private void calculateTextPosition() {
        Vector2f containerSize = container.getSize();
        Vector2f containerPosition = container.getPosition();

        this.x = calculatePositionX( containerPosition, containerSize);
        this.y = calculatePositionY(containerPosition, containerSize);
    }
    private float calculatePositionY(Vector2f containerPosition, Vector2f containerSize) {
        return containerPosition.y + containerSize.y - containerPadding;
    }
    private float calculatePositionX(Vector2f containerPosition, Vector2f containerSize) {
        float exactTextWidth = calculateTextWidth(renderText, font, fontSizeMultiplier);

        if (alignment == Alignment.LEFT) {
            return containerPosition.x + containerPadding;
        }
        else if (alignment == Alignment.CENTER) {
            return containerPosition.x + ((containerSize.x - exactTextWidth) / 2.0f);
        }

        return containerPosition.x + containerSize.x - exactTextWidth - containerPadding;
    }
    public void setText(String text) {
        this.rawText = text;
        this.renderText = wrapText(currentMaxWidth - (2 * containerPadding));
        calculateTextPosition();
    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        shader.setUniform("useTexture", 1);
        shader.setUniform("uiTexture", 0);
        shader.setUniform("uiColor", this.color);
        font.getTexture().bind();

        float scaleW = font.getScaleW();
        float scaleH = font.getScaleH();

        float cursorX = this.x;
        float cursorY = this.y;
        float lineHeight = MAX_CHAR_HEIGHT * fontSizeMultiplier;

        Vector2f containerSize = container.getSize();
        for (int i = 0; i < renderText.length(); i++) {
            char c = renderText.charAt(i);
            CharacterInfo charInfo = font.getCharacter((int) c);

            if (charInfo == null) continue;

            if (c == '\n') {
                cursorX = this.x;
                cursorY -= lineHeight;
                continue;
            }

            float uScale = charInfo.width() / scaleW;
            float vScale = charInfo.height() / scaleH;

            float uOffset = charInfo.x() / scaleW;
            float vOffset = (scaleH - charInfo.y() - charInfo.height()) / scaleH;

            shader.setUniform("uvScale", new org.joml.Vector2f(uScale, vScale));
            shader.setUniform("uvOffset", new org.joml.Vector2f(uOffset, vOffset));

            Matrix4f letterMatrix = getLetterMatrix(cursorX, cursorY, charInfo);

            shader.setUniform("model", letterMatrix);

            uiQuad.render();

            cursorX += (charInfo.xAdvance() * fontSizeMultiplier);

        }
    }
    @Override
    public float getBoundingHeight() {
        int numLines = this.renderText.split("\n").length;

        float exactLineHeight = MAX_CHAR_HEIGHT * fontSizeMultiplier;

        return numLines * exactLineHeight;
    }

    @NotNull
    private Matrix4f getLetterMatrix(float cursorX, float cursorY, CharacterInfo charInfo) {
        Matrix4f letterMatrix = new Matrix4f();

        float targetX = cursorX + (charInfo.xOffset() * fontSizeMultiplier);
        float targetY = cursorY - ((charInfo.yOffset() + charInfo.height()) * fontSizeMultiplier);

        letterMatrix.translate(targetX, targetY, 0.0f);

        letterMatrix.scale(charInfo.width() * fontSizeMultiplier, charInfo.height() * fontSizeMultiplier, 1.0f);
        return letterMatrix;
    }

    private float calculateTextWidth(String text, FontAtlas font, float scale) {
        float totalWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            CharacterInfo info = font.getCharacter(c);
            if (info != null) {
                totalWidth += (info.xAdvance() * scale);
            }
        }
        return totalWidth;
    }
}