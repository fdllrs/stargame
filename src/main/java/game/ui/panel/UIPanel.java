package game.ui.panel;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.UIBackgroundRenderer;
import engine.ui.UIElement;
import engine.ui.UIRow;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import org.joml.Vector4f;

import java.util.List;

public abstract class UIPanel extends UIElement {
	protected final List<UIElement> children = new java.util.ArrayList<>();
	protected final FontAtlas font;

	public UIPanel(float x, float y, float width, float height, Vector4f color, FontAtlas font) {
		super(x, y, width, height, color);
		this.font = font;
		this.vPadding = 10;
		this.hPadding = 10;
	}

	@Override
	public float getBoundingHeight() {
		return 0;
	}

	@Override
	public void handleClick(float mouseX, float mouseY) {
		if (!shouldRender()) return;
		for (UIElement child : children) {
			if (child.contains(mouseX, mouseY)) {
				child.handleClick(mouseX, mouseY);
				return;
			}
		}
	}

	@Override
	public void render(ShaderProgram shader, Mesh uiQuad) {
		if (!shouldRender()) return;

		shader.setUniform("useTexture", 0);
		UIBackgroundRenderer.renderFuturisticBackground(this, shader, uiQuad, 3.0f);

		for (UIElement child : children) {
			child.render(shader, uiQuad);
		}
	}

	@Override
	public void update(float mouseX, float mouseY, float deltaTime) {
		for (UIElement element : children) {
			element.update(mouseX, mouseY, deltaTime);
		}
	}

	@Override
	public void handleScroll(float mouseX, float mouseY, double yOffset, boolean shiftPressed) {
		if (!shouldRender()) return;

		for (UIElement child : children) {
			if (child.contains(mouseX, mouseY)) {
				child.handleScroll(mouseX, mouseY, yOffset, shiftPressed);
				return;
			}
		}
	}

	@Override
	public void onResize(int screenWidth, int screenHeight) {
		for (UIElement child : children) {
			child.onResize(screenWidth, screenHeight);
		}
	}

	@Override
	public void rebuildElements() {
		for (UIElement child : children) {
			child.rebuildElements();
		}
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		layout();
	}

	@Override
	public void setSize(float newWidth, float newHeight) {
		if (this.width == newWidth && this.height == newHeight) {
			return;
		}
		super.setSize(newWidth, newHeight);
		for (UIElement child : children) {
			if (child instanceof UIText textChild) {
				textChild.setMaxWidth(this.width);
			}
		}
		layout();
	}

	protected void layout() {
		float currentY = this.y + vPadding;
		for (UIElement element : children) {
			float elementX = this.x;
			if (element.getLayoutAlignment() == LayoutAlignment.CENTER) {
				elementX = this.x + ( this.width - element.getSize().x ) / 2.0f;
			}
			else if (element instanceof UIRow) {
				elementX = this.x + hPadding;
			}
			element.setPosition(elementX, currentY);
			currentY += element.getBoundingHeight();
		}
	}

	protected boolean shouldRender() {
		return true;
	}

	protected void setPanelTitle(String title) {
		children.addFirst(new UIText(title,
									 UIText.Alignment.CENTER,
									 new Vector4f(1, 1, 1, 1),
									 24,
									 1,
									 10,
									 font,
									 width));
	}
}
