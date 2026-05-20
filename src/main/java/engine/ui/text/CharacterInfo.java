package engine.ui.text;

public record CharacterInfo(
        int id,
        int x,
        int y,
        int width,
        int height,
        int xOffset,
        int yOffset,
        int xAdvance
) {
}
