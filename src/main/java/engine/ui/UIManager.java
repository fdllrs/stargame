package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import game.geometry.ScreenQuadGeometry;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11C.*;

public class UIManager {

    private final Matrix4f uiProjection;
    private final long windowHandle;

    private final ArrayList<UIElement> elements;
    private final ShaderProgram uiShader;
    private final Mesh uiQuad;

    public UIManager(long windowHandle) {
        this.windowHandle = windowHandle;
        elements = new ArrayList<>();
        this.uiShader = ShaderProgram.initShader("/UI/ui.vert", "/UI/ui.frag");
        uiQuad = ScreenQuadGeometry.generateUIRect();
        Vector2i windowSize = Window.getWindowSize(windowHandle);
        uiProjection = new Matrix4f();
        rebuildProjection(windowSize.x, windowSize.y);
    }

    private void rebuildProjection(int width, int height) {
        uiProjection.setOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
    }

    public void onResize(int width, int height) {
        rebuildProjection(width, height);
        for (UIElement element : elements) {
            element.onResize(width, height);
        }
    }

    public void addElement(UIElement element) {
        elements.add(element);
    }

    public ShaderProgram getUiShader() {
        return uiShader;
    }

    public void renderAll() {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        uiShader.bind();
        uiShader.setUniform("projection", uiProjection);

        // Tell every element to draw itself!
        for (UIElement element : elements) {
            element.render(uiShader, uiQuad);
        }

        uiShader.unbind();
        glDisable(GL_BLEND);
    }

    public void cleanup() {
        uiShader.cleanup();
        uiQuad.cleanup();
    }

    public Boolean objectClicked(float mouseX, float mouseY, long windowHandle) {
        for (UIElement element : elements) {
            if (element.contains(mouseX, mouseY)) {
                element.handleClick(mouseX, mouseY);
                return true;
            }
        }

        return false;
    }
}
