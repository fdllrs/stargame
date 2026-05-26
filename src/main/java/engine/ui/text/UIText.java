package engine.ui.text;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.UIElement;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class UIText extends UIElement {

    public static final float REAL_FONT_SIZE = 88f;
    public static final int MAX_CHAR_HEIGHT = 60;
    public static final int LINE_SPACING = 10;
    private final FontAtlas font;
    private final UIElement container;
    private final Alignment alignment;

    // Instantiate these ONCE to prevent Garbage Collection stutters
    private final Matrix4f transformMatrix = new Matrix4f();
    private final Vector2f uvScaleVec = new Vector2f();
    private final Vector2f uvOffsetVec = new Vector2f();

    public float fontSizeMultiplier;
    private String rawText;
    private String renderText;
    private float currentMaxWidth = -1f;

    public UIText(String text,
                  UIElement container,
                  Alignment alignment,
                  Vector4f color,
                  int fontSize,
                  int horizontalPadding,
                  int verticalPadding,
                  FontAtlas font) {
        super(0, 0, 0, 0, color);
        this.font = font;
        this.alignment = alignment;
        this.container = container;
        this.fontSizeMultiplier = (float) fontSize / REAL_FONT_SIZE;
        this.hPadding = horizontalPadding;
        this.vPadding = verticalPadding;

        // Ensure maxWidth is initialized based on the container before wrapping
        this.currentMaxWidth = container.getSize().x - (2 * horizontalPadding);

        setText(text);
    }

    public void setText(String text) {
        this.rawText = text;
        this.renderText = wrapText(this.currentMaxWidth);

        this.y = container.getPosition().y + container.getSize().y - vPadding;
    }

    public void setMaxWidth(float maxPixelWidth) {
        if (Math.abs(this.currentMaxWidth - maxPixelWidth) < 0.1f) return;
        this.currentMaxWidth = maxPixelWidth;
        this.renderText = wrapText(this.currentMaxWidth);
    }

    private String wrapText(float maxPixelWidth) {
        String[] words = rawText.split(" ");
        StringBuilder wrappedText = new StringBuilder();
        float currentLineWidth = 0;

        // Pre-calculate space width to avoid doing it every loop
        CharacterInfo spaceInfo = font.getCharacter(' ');
        float spaceWidth = (spaceInfo != null) ? (spaceInfo.xAdvance() * fontSizeMultiplier) :
                (10 * fontSizeMultiplier);

        for (String word : words) {
            float wordWidth = calculateTextWidth(word); // Reuse our helper method!

            if (currentLineWidth + wordWidth > maxPixelWidth && currentLineWidth > 0) {
                wrappedText.append("\n").append(word).append(" ");
                currentLineWidth = wordWidth + spaceWidth;
            } else {
                wrappedText.append(word).append(" ");
                currentLineWidth += wordWidth + spaceWidth;
            }
        }
        return wrappedText.toString().trim(); // Remove the trailing space
    }

    @Override
    public void render(ShaderProgram shader, Mesh uiQuad) {
        shader.setUniform("useTexture", 1);
        shader.setUniform("uiTexture", 0);
        shader.setUniform("uiColor", this.color);
        font.getTexture().bind();

        float scaleW = font.getScaleW();
        float scaleH = font.getScaleH();
        float lineHeight = (MAX_CHAR_HEIGHT + LINE_SPACING) * fontSizeMultiplier;

        float cursorY = this.y;

        // Split the text by lines so we can align each line individually
        String[] lines = renderText.split("\n");

        for (String line : lines) {
            // 1. Calculate the starting X for THIS specific line
            float cursorX = getAlignedStartX(line);

            // 2. Draw the line
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                CharacterInfo charInfo = font.getCharacter(c);
                if (charInfo == null) continue;

                // Update vectors in-place (No 'new' keyword)
                uvScaleVec.set(charInfo.width() / scaleW, charInfo.height() / scaleH);
                uvOffsetVec.set(charInfo.x() / scaleW, (scaleH - charInfo.y() - charInfo.height()) / scaleH);

                shader.setUniform("uvScale", uvScaleVec);
                shader.setUniform("uvOffset", uvOffsetVec);

                // Update matrix in-place
                transformMatrix.identity();
                float targetX = cursorX + (charInfo.xOffset() * fontSizeMultiplier);
                float targetY = cursorY - ((charInfo.yOffset() + charInfo.height()) * fontSizeMultiplier);
                transformMatrix.translate(targetX, targetY, 0.0f);
                transformMatrix.scale(charInfo.width() * fontSizeMultiplier,
                                      charInfo.height() * fontSizeMultiplier,
                                      1.0f);

                shader.setUniform("model", transformMatrix);
                uiQuad.render();

                cursorX += (charInfo.xAdvance() * fontSizeMultiplier);
            }

            // 3. Move down for the next line
            cursorY -= lineHeight;
        }
    }

    private float getAlignedStartX(String line) {
        Vector2f containerPos = container.getPosition();
        Vector2f containerSize = container.getSize();

        if (alignment == Alignment.LEFT) {
            return containerPos.x + hPadding;
        }

        float lineWidth = calculateTextWidth(line);

        if (alignment == Alignment.CENTER) {
            return containerPos.x + ((containerSize.x - lineWidth) / 2.0f);
        } else { // RIGHT
            return containerPos.x + containerSize.x - lineWidth - hPadding;
        }
    }

    // --- ALIGNMENT MATH ---

    private float calculateTextWidth(String text) {
        float totalWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            CharacterInfo info = font.getCharacter(text.charAt(i));
            if (info != null) {
                totalWidth += (info.xAdvance() * fontSizeMultiplier);
            }
        }
        return totalWidth;
    }

    @Override
    public float getBoundingHeight() {
        int numLines = this.renderText.split("\n").length;
        float exactLineHeight = (MAX_CHAR_HEIGHT + LINE_SPACING) * fontSizeMultiplier;
        return numLines * exactLineHeight + vPadding;
    }

    @Override
    public void handleClick(float mouseX, float mouseY) {
        
    }

    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }
}