package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import game.geometry.ScreenQuadGeometry;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_BLEND;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glDisable;


public class UIManager {

    private final Matrix4f uiProjection;
    private final long windowHandle;

    private ArrayList<UIElement> elements;
    private ShaderProgram uiShader;
    private Mesh uiQuad;


    public UIManager(long windowHandle) {
        this.windowHandle = windowHandle;
        elements = new ArrayList<>();
        this.uiShader = ShaderProgram.initShader("/UI/ui.vert", "/UI/ui.frag");
        uiQuad = ScreenQuadGeometry.generateUIRect();
        Vector2i windowSize = Window.getWindowSize(windowHandle);
        uiProjection = new Matrix4f().ortho(
                0.0f,
                windowSize.x,
                0.0f,
                windowSize.y,
                -1.0f,
                1.0f);

    };

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

    public Matrix4f getProjection() {
        return uiProjection;
    }
}
