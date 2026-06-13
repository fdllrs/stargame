package engine.ui.panels;

import game.ui.Describable;
import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.components.StorageComponent;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

public class DefaultPanelController implements InfoPanelController {
    @Override
    public void populate(List<UIElement> children,
                         Describable target,
                         StorageComponent playerStorage,
                         FontAtlas font,
                         float width) {
        Vector4f textCol = new Vector4f(1, 1, 1, 1);
        for (Map.Entry<String, String> entry : target.getDisplayProperties()) {
            String line = entry.getKey() + ": " + entry.getValue();
            children.add(new UIText(line,
                                    UIText.Alignment.LEFT,
                                    textCol,
                                    20,
                                    15,
                                    10,
                                    font,
                                    width));
        }
    }
}
