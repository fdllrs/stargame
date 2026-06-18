package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public abstract class UIElement {

	protected float x, y;
	protected float width, height;
	protected float hPadding, vPadding;
	protected Vector4f color;
	protected Matrix4f modelMatrix;
	protected float scale;
	protected boolean visible = false;

	public abstract float getBoundingHeight();

	public abstract void handleClick(float mouseX, float mouseY);

	public abstract void render(ShaderProgram shader, Mesh uiQuad);

	public abstract void update(float mouseX, float mouseY, float deltaTime);

	public UIElement(float x, float y, float width, float height, Vector4f color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color != null ? new Vector4f(color) : new Vector4f(1, 1, 1, 1);
		this.modelMatrix = new Matrix4f();
		updateMatrix();
	}

	public void cleanup() {
	}

	public boolean contains(float mouseX, float mouseY) {
		boolean conditionX = mouseX >= this.x && mouseX <= this.x + width;
		boolean conditionY = mouseY >= this.y && mouseY <= this.y + height;
		return conditionX && conditionY;
	}

	public Vector4f getColor() {
		return color;
	}

	public LayoutAlignment getLayoutAlignment() {
		return LayoutAlignment.FILL;
	}

	public Vector2f getPosition() {
		return new Vector2f(x, y);
	}

	public Vector2f getSize() {
		return new Vector2f(width, height);
	}

	public void handleScroll(float mouseX, float mouseY, double yOffset, boolean shiftPressed) {
	}

	public boolean isVisible() { return visible; }

	public void setVisible(boolean visible) { this.visible = visible; }

	public void onResize(int screenWidth, int screenHeight) {
	}

	public void setOpacity(float opacity) {
		this.color.w = opacity;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		updateMatrix();
	}

	public void setScale(float scale) {
		float centerX = x + width / 2.0f;
		float centerY = y + height / 2.0f;
		float scaledWidth = width * scale;
		float scaledHeight = height * scale;
		float scaledX = centerX - scaledWidth / 2.0f;
		float scaledY = centerY - scaledHeight / 2.0f;

		modelMatrix.identity();
		modelMatrix.translate(scaledX, scaledY, 0);
		modelMatrix.scale(scaledWidth, scaledHeight, 1);
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		updateMatrix();
	}

	protected void updateMatrix() {
		modelMatrix.identity();
		modelMatrix.translate(x, y, 0);
		modelMatrix.scale(width, height, 1);
	}

	public enum LayoutAlignment {
		FILL, CENTER, LEFT
	}
}
