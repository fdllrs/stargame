package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class UIRow extends UIElement {
	private final List<UIElement> elements = new ArrayList<>();
	private final float gap;

	public UIRow(float gap) {
		super(0, 0, 0, 0, new Vector4f(0, 0, 0, 0));
		this.gap = gap;
	}

	public void addElement(UIElement element) {
		elements.add(element);
	}

	@Override
	public float getBoundingHeight() {
		float maxHeight = 0;
		for (UIElement element : elements) {
			maxHeight = Math.max(maxHeight, element.getBoundingHeight());
		}
		return maxHeight;
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
			size.x += element.getSize().x + gap;
		}
		return size;
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		float currentX = x;
		float maxHeight = 0;
		for (UIElement element : elements) {
			element.setPosition(currentX, y);
			currentX += element.getSize().x + gap;
			maxHeight = Math.max(maxHeight, element.getBoundingHeight());
		}
		this.setSize(currentX - x - gap, maxHeight);
	}
}