package engine.ui;

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

    private void parseFontFile(String path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));

            for (String line : lines) {
                if (line.startsWith("common ")) {
                    // Extract the total image size (e.g., scaleW=512)
                    scaleW = extractValue(line, "scaleW=") ;
                    scaleH = extractValue(line, "scaleH=");
                }
                else if (line.startsWith("char ")) {
                    // Extract all the data for a specific letter
                    int id = extractValue(line, "id=");
                    int x = extractValue(line, "x=");
                    int y = extractValue(line, "y=");
                    int width = extractValue(line, "width=");
                    int height = extractValue(line, "height=");
                    int xOffset = extractValue(line, "xoffset=");
                    int yOffset = extractValue(line, "yoffset=");
                    int xAdvance = extractValue(line, "xadvance=");

                    characterMap.put(id, new CharacterInfo(id, x, y, width, height, xOffset, yOffset, xAdvance));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load font file: " + path, e);
        }
    }

    // Helper method to parse "key=value" from a string line
    private int extractValue(String line, String key) {
        int startIndex = line.indexOf(key) + key.length();
        int endIndex = line.indexOf(" ", startIndex);
        if (endIndex == -1) endIndex = line.length();
        return Integer.parseInt(line.substring(startIndex, endIndex).trim());
    }

    public CharacterInfo getCharacter(int ascii) {
        return characterMap.get(ascii);
    }

    public Texture getTexture() { return texture; }
    public float getScaleW() { return scaleW; }
    public float getScaleH() { return scaleH; }
}