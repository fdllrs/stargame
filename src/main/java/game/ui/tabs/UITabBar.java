package game.ui.tabs;

import engine.ui.UIElement;
import engine.ui.UIRow;
import engine.ui.text.FontAtlas;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UITabBar extends UIRow {
	private final List<UITab> tabs = new ArrayList<>();
	private final FontAtlas font;
	private final Runnable onTabChanged;
	private final float tabWidth;
	private final float tabHeight;
	private final Vector4f activeColor;
	private final Vector4f inactiveColor;
	private final Vector4f textColor;

	public UITabBar(float gap, FontAtlas font, Runnable onTabChanged) {
		this(gap,
			 font,
			 onTabChanged,
			 110,
			 30,
			 new Vector4f(0.2f, 0.5f, 0.9f, 1.0f),
			 new Vector4f(0.12f, 0.12f, 0.12f, 1.0f),
			 new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
	}

	public UITabBar(float gap,
			FontAtlas font,
			Runnable onTabChanged,
			float tabWidth,
			float tabHeight,
			Vector4f activeColor,
			Vector4f inactiveColor,
			Vector4f textColor) {
		super(gap);
		this.font = font;
		this.onTabChanged = onTabChanged;
		this.tabWidth = tabWidth;
		this.tabHeight = tabHeight;
		this.activeColor = activeColor != null ? new Vector4f(activeColor) : new Vector4f(0.2f,
																						  0.5f,
																						  0.9f,
																						  1.0f);
		this.inactiveColor = inactiveColor != null ? new Vector4f(inactiveColor) : new Vector4f(
				0.12f,
				0.12f,
				0.12f,
				1.0f);
		this.textColor = textColor != null ? new Vector4f(textColor) : new Vector4f(1.0f,
																					1.0f,
																					1.0f,
																					1.0f);
	}

	public void addTab(String label, Supplier<List<UIElement>> contentSupplier) {
		final UITab[] tabRef = new UITab[ 1 ];
		UITab tab = new UITab(tabWidth,
							  tabHeight,
							  activeColor,
							  inactiveColor,
							  textColor,
							  label,
							  contentSupplier,
							  font,
							  () -> selectTab(tabRef[ 0 ]));
		tabRef[ 0 ] = tab;

		tabs.add(tab);
		addElement(tab);

		// Select the first tab by default
		if (tabs.size() == 1) {
			selectTab(tab);
		}
	}

	public UITab getActiveTab() {
		for (UITab tab : tabs) {
			if (tab.isSelected()) {
				return tab;
			}
		}
		return null;
	}

	public void selectTab(UITab tabToSelect) {
		for (UITab tab : tabs) {
			tab.setSelected(tab == tabToSelect);
		}
		if (onTabChanged != null) {
			onTabChanged.run();
		}
	}
}
