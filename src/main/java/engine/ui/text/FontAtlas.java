package engine.ui.text;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.graphics.Texture;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontAtlas {
    private final Texture texture;
    private final Map<Integer, CharacterInfo> characterMap = new HashMap<>();
    private float scaleW, scaleH;

    public FontAtlas(String fontFilePath, String textureFilePath) {
        this.texture = new Texture(textureFilePath);
        parseFontFile(fontFilePath);
    }

    // Helper method to parse "key=value" from a string line
    private int extractValue(String line, String key) {
        int startIndex = line.indexOf(key) + key.length();
        int endIndex = line.indexOf(" ", startIndex);
        if (endIndex == -1)
            endIndex = line.length();
        return Integer.parseInt(line.substring(startIndex, endIndex).trim());
    }

    public CharacterInfo getCharacter(int ascii) {
        return characterMap.get(ascii);
    }

    public float getScaleH() {return scaleH;}

    public float getScaleW() {return scaleW;}

    public Texture getTexture() {return texture;}

    private void parseFontFile(String path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));

            for (String line : lines) {
                if (line.startsWith("common ")) {
                    // Extract the total image size (e.g., scaleW=512)
                    scaleW = extractValue(line, "scaleW=");
                    scaleH = extractValue(line, "scaleH=");
                } else if (line.startsWith("char ")) {
                    // Extract all the data for a specific letter
                    int id = extractValue(line, "id=");
                    int x = extractValue(line, "x=");
                    int y = extractValue(line, "y=");
                    int width = extractValue(line, "width=");
                    int height = extractValue(line, "height=");
                    int xOffset = extractValue(line, "xoffset=");
                    int yOffset = extractValue(line, "yoffset=");
                    int xAdvance = extractValue(line, "xadvance=");

                    characterMap.put(id,
                                     new CharacterInfo(id,
                                                       x,
                                                       y,
                                                       width,
                                                       height,
                                                       xOffset,
                                                       yOffset,
                                                       xAdvance));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load font file: " + path, e);
        }
    }

    /**
     * Renders a string at (startX, startY) using the given UI shader and quad.
     * Caller is responsible for binding the shader and setting the projection uniform.
     */
    public void renderText(ShaderProgram shader,
                           Mesh quad,
                           String text,
                           float startX,
                           float startY,
                           float fontSize,
                           org.joml.Vector4f color) {
        shader.setUniform("useTexture", 1);
        shader.setUniform("uiTexture", 0);
        shader.setUniform("uiColor", color);
        texture.bind();

        float fontSizeMultiplier = fontSize / UIText.REAL_FONT_SIZE;
        float cursorX = startX;

        org.joml.Matrix4f transform = new org.joml.Matrix4f();
        org.joml.Vector2f uvScale = new org.joml.Vector2f();
        org.joml.Vector2f uvOffset = new org.joml.Vector2f();

        for (int i = 0; i < text.length(); i++) {
            CharacterInfo info = characterMap.get((int) text.charAt(i));
            if (info == null)
                continue;

            uvScale.set(info.width() / scaleW, info.height() / scaleH);
            uvOffset.set(info.x() / scaleW, (scaleH - info.y() - info.height()) / scaleH);

            shader.setUniform("uvScale", uvScale);
            shader.setUniform("uvOffset", uvOffset);

            transform.identity()
                     .translate(cursorX + info.xOffset() * fontSizeMultiplier,
                                startY + info.yOffset() * fontSizeMultiplier,
                                0.0f)
                     .scale(info.width() * fontSizeMultiplier,
                            info.height() * fontSizeMultiplier,
                            1.0f);

            shader.setUniform("model", transform);
            quad.render();

            cursorX += info.xAdvance() * fontSizeMultiplier;
        }
    }

}