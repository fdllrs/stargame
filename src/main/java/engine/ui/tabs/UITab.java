package engine.ui.tabs;

import engine.ui.UIElement;
import engine.ui.buttons.UIButton;
import engine.ui.text.FontAtlas;
import org.joml.Vector4f;

import java.util.List;
import java.util.function.Supplier;

public class UITab extends UIButton {
    private boolean selected;
    private final Vector4f activeColor;
    private final Vector4f inactiveColor;
    private final Supplier<List<UIElement>> contentSupplier;

    public UITab(float width,
                 float height,
                 Vector4f activeColor,
                 Vector4f inactiveColor,
                 Vector4f textColor,
                 String label,
                 Supplier<List<UIElement>> contentSupplier,
                 FontAtlas fontAtlas,
                 Runnable onClick) {
        super(width, height, inactiveColor, textColor, label, onClick, fontAtlas);
        this.activeColor = activeColor != null ? new Vector4f(activeColor) : new Vector4f(0.2f, 0.5f, 0.9f, 1.0f);
        this.inactiveColor = inactiveColor != null ? new Vector4f(inactiveColor) : new Vector4f(0.12f, 0.12f, 0.12f, 1.0f);
        this.contentSupplier = contentSupplier;
        this.selected = false;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.color.set(selected ? activeColor : inactiveColor);
    }

    public boolean isSelected() {
        return selected;
    }

    public List<UIElement> getContent() {
        return contentSupplier.get();
    }
}
