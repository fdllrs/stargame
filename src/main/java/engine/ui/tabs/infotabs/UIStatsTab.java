package engine.ui.tabs.infotabs;

import engine.ui.Describable;
import engine.ui.UIElement;
import engine.ui.buttons.UIButton;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.objects.celestialBodies.Planet;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UIStatsTab {
    public static List<UIElement> build(Describable target,
                                        FontAtlas font,
                                        float width,
                                        Runnable onSelectHub) {
        List<UIElement> elements = new ArrayList<>();
        Vector4f textCol = new Vector4f(1, 1, 1, 1);

        for (Map.Entry<String, String> entry : target.getDisplayProperties()) {
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

        if (onSelectHub != null) {
            Vector4f btnBg = new Vector4f(0.15f, 0.25f, 0.45f, 0.85f);
            UIButton selectHubButton = new UIButton(width * 0.8f,
                                                    80,
                                                    btnBg,
                                                    textCol,
                                                    "Go to Hub",
                                                    onSelectHub,
                                                    font);
            selectHubButton.setEnabled(
                    target instanceof Planet planet && planet.hasHub());
            elements.add(selectHubButton);
        }

        return elements;
    }
}

