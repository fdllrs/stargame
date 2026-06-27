package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class UIScrollArea extends UIElement {
	private final List<UIElement> elements = new ArrayList<>();
	private final float gap;
	private final int[] viewport = new int[ 4 ];
	private float scrollY = 0;
	private float maxScrollY = 0;

	public UIScrollArea(float width, float height, float gap) {
		super(0, 0, width, height, new Vector4f(0, 0, 0, 0)); // transparent background
		this.gap = gap;
		this.visible = true;
	}

	public void addElement(UIElement element) {
		elements.add(element);
	}

	private void clampScrollY() {
		if (scrollY < 0) scrollY = 0;
		if (scrollY > maxScrollY) scrollY = maxScrollY;
	}

	public void clearElements() {
		elements.clear();
	}

	@Override
	public float getBoundingHeight() {
		return height + vPadding;
	}

	@Override
	public void handleClick(float mouseX, float mouseY) {
		if (!visible) return;
		for (UIElement element : elements) {
			if (element.contains(mouseX, mouseY)) {
				element.handleClick(mouseX, mouseY);
				return;
			}
		}
	}

	@Override
	public void render(ShaderProgram shader, Mesh uiQuad) {
		if (!visible) return;

		// 1. Get current screen viewport to map scissor coordinates
		org.lwjgl.opengl.GL11C.glGetIntegerv(org.lwjgl.opengl.GL11C.GL_VIEWPORT, viewport);
		int screenHeight = viewport[ 3 ];

		// 2. Enable scissor box for this scroll area's bounds
		int scissorX = (int) this.x;
		int scissorY = (int) ( screenHeight - ( this.y + this.height ) );
		int scissorW = (int) this.width;
		int scissorH = (int) this.height;

		if (scissorH > 0) {
			org.lwjgl.opengl.GL11C.glEnable(org.lwjgl.opengl.GL11C.GL_SCISSOR_TEST);
			org.lwjgl.opengl.GL11C.glScissor(scissorX, scissorY, scissorW, scissorH);

			// Render children
			for (UIElement element : elements) {
				element.render(shader, uiQuad);
			}

			org.lwjgl.opengl.GL11C.glDisable(org.lwjgl.opengl.GL11C.GL_SCISSOR_TEST);
		}
	}

	@Override
	public void update(float mouseX, float mouseY, float deltaTime) {
		for (UIElement element : elements) {
			element.update(mouseX, mouseY, deltaTime);
		}
	}

	@Override
	public void handleScroll(float mouseX, float mouseY, double yOffset, boolean shiftPressed) {
		// Pass to children (e.g. UIResourceSlot) first
		boolean childHandled = false;
		for (UIElement child : elements) {
			if (child.contains(mouseX, mouseY)) {
				child.handleScroll(mouseX, mouseY, yOffset, shiftPressed);
				if (shiftPressed && child instanceof UIResourceSlot) {
					childHandled = true;
				}
				break;
			}
		}

		// Scroll internal content if not handled by a child
		if (!childHandled && maxScrollY > 0) {
			this.scrollY -= (float) ( yOffset * 40.0f );
			clampScrollY();
			layout();
		}
	}

	@Override
	public void onResize(int screenWidth, int screenHeight) {
		layout();
		for (UIElement element : elements) {
			element.onResize(screenWidth, screenHeight);
		}
	}

	@Override
	public void rebuildElements() {
		for (UIElement element : elements) {
			element.rebuildElements();
		}
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		layout();
	}

	private void layout() {
		float currentY = this.y - scrollY;
		float totalContentHeight = 0;
		for (UIElement element : elements) {
			float elementX = this.x;
			if (element.getLayoutAlignment() == LayoutAlignment.CENTER) {
				elementX = this.x + ( this.width - element.getSize().x ) / 2.0f;
			}
			else if (element instanceof UIRow) {
				elementX = this.x;
			}
			element.setPosition(elementX, currentY);
			currentY += element.getBoundingHeight() + gap;
			totalContentHeight += element.getBoundingHeight() + gap;
		}
		this.maxScrollY = Math.max(0, totalContentHeight - gap - this.height);
		if (scrollY > maxScrollY) {
			scrollY = maxScrollY;
			layout();
		}
	}
}
