package engine.ui.panels;

import game.ui.Describable;
import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import game.components.StorageComponent;

import java.util.List;

public interface InfoPanelController {
    void populate(List<UIElement> children,
                  Describable target,
                  StorageComponent playerStorage,
                  FontAtlas font,
                  float width);
}
