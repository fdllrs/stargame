package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class UIColumn extends UIElement {
	private final List<UIElement> elements = new ArrayList<>();
	private final float gap;

	public UIColumn(float gap) {
		super(0, 0, 0, 0, new Vector4f(0, 0, 0, 0));
		this.gap = gap;
	}

	public void addElement(UIElement element) {
		elements.add(element);
	}

	@Override
	public float getBoundingHeight() {
		float totalHeight = 0;
		for (UIElement element : elements) {
			totalHeight += element.getBoundingHeight() + gap;
		}
		if (totalHeight > 0) totalHeight -= gap;
		return totalHeight;
	}

	@Override
	public void handleClick(float mouseX, float mouseY) {
		for (UIElement element : elements) {
			if (element.contains(mouseX, mouseY)) {
				element.handleClick(mouseX, mouseY);
				return;
			}
		}
	}

	@Override
	public void rebuildElements() {
		for (UIElement element : elements) {
			element.rebuildElements();
		}
	}

	@Override
	public void render(ShaderProgram shader, Mesh uiQuad) {
		for (UIElement element : elements) {
			element.render(shader, uiQuad);
		}
	}

	@Override
	public void update(float mouseX, float mouseY, float deltaTime) {
		for (UIElement element : elements) {
			element.update(mouseX, mouseY, deltaTime);
		}
	}

	@Override
	public boolean contains(float mouseX, float mouseY) {
		for (UIElement element : elements) {
			if (element.contains(mouseX, mouseY)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public LayoutAlignment getLayoutAlignment() {
		return LayoutAlignment.CENTER;
	}

	@Override
	public Vector2f getSize() {
		Vector2f size = new Vector2f(0, getBoundingHeight());
		for (UIElement element : elements) {
			size.x = Math.max(size.x, element.getSize().x);
		}
		return size;
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		float currentY = y;
		float maxWidth = 0;
		for (UIElement element : elements) {
			element.setPosition(x, currentY);
			currentY += element.getBoundingHeight() + gap;
			maxWidth = Math.max(maxWidth, element.getSize().x);
		}
		this.setSize(maxWidth, currentY - y - gap);
	}
}
