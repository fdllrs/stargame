package engine.ui.tabs.infotabs;

import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.objects.celestialBodies.Planet;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UIStatsTab {
    public static List<UIElement> build(Planet planet, FontAtlas font, float width) {
        List<UIElement> elements = new ArrayList<>();
        Vector4f textCol = new Vector4f(1, 1, 1, 1);

        for (Map.Entry<String, String> entry : planet.getDisplayProperties()) {
            String line = entry.getKey() + ": " + entry.getValue();
            elements.add(new UIText(line,
                                    UIText.Alignment.LEFT,
                                    textCol,
                                    20,
                                    15,
                                    10,
                                    font,
                                    width));
        }

        return elements;
    }
}

