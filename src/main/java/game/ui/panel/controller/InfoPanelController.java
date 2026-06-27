package game.ui.panel.controller;

import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import game.ui.Describable;

import java.util.List;

public interface InfoPanelController {
	void populate(List<UIElement> children, Describable target, FontAtlas font, float width);
}
