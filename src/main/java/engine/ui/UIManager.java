package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import game.geometry.ScreenQuadGeometry;
import game.ui.panel.UIPanel;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11C.*;

public class UIManager {
	private final Matrix4f uiProjection;
	private final ArrayList<UIPanel> uiPanels;
	private final ShaderProgram uiShader;
	private final Mesh uiQuad;

	public UIManager(long windowHandle) {
		uiPanels = new ArrayList<>();
		this.uiShader = ShaderProgram.initShader("/UI/ui.vert", "/UI/ui.frag");
		uiQuad = ScreenQuadGeometry.generateUIRect();
		Vector2i windowSize = Window.getWindowSize(windowHandle);
		uiProjection = new Matrix4f();
		rebuildProjection(windowSize.x, windowSize.y);
	}

	public void addElement(UIPanel element) {
		uiPanels.add(element);
	}

	public void cleanup() {
		uiShader.cleanup();
		uiQuad.cleanup();
		for (UIElement element : uiPanels) {
			element.cleanup();
		}
	}

	public boolean handleScroll(float mouseX, float mouseY, double yOffset) {
		for (UIElement element : uiPanels) {
			if (element.contains(mouseX, mouseY)) {
				element.handleScroll(mouseX, mouseY, yOffset);
				return true;
			}
		}
		return false;
	}

	public Boolean objectClicked(float mouseX, float mouseY) {
		for (UIElement element : uiPanels) {
			if (element.contains(mouseX, mouseY)) {
				element.handleClick(mouseX, mouseY);
				return true;
			}
		}

		return false;
	}

	public void onResize(int width, int height) {
		rebuildProjection(width, height);
		for (UIElement element : uiPanels) {
			element.onResize(width, height);
		}
	}

	private void rebuildProjection(int width, int height) {
		uiProjection.setOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
	}

	public void renderAll() {
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		uiShader.bind();
		uiShader.setUniform("projection", uiProjection);

		// Tell every element to draw itself!
		for (UIElement element : uiPanels) {
			element.render(uiShader, uiQuad);
		}

		uiShader.unbind();
		glDisable(GL_BLEND);
	}

	public void update(float mouseX, float mouseY, float deltaTime) {
		for (UIElement element : uiPanels) {
			element.update(mouseX, mouseY, deltaTime);
		}
	}
}
