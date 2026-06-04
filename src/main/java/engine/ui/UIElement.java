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

    public UIElement(float x, float y, float width, float height, Vector4f color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.modelMatrix = new Matrix4f();
        updateMatrix();
    }

    protected void updateMatrix() {
        modelMatrix.identity();
        modelMatrix.translate(x, y, 0);
        modelMatrix.scale(width, height, 1);
    }

    public abstract float getBoundingHeight();
    public abstract void handleClick(float mouseX, float mouseY);
    public abstract void render(ShaderProgram shader, Mesh uiQuad);

    public void handleScroll(float mouseX, float mouseY, double yOffset) {
    }

    public Vector2f getSize() {
        return new Vector2f(width, height);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateMatrix();
    }

    public boolean contains(float mouseX, float mouseY) {
        boolean conditionX = mouseX >= this.x && mouseX <= this.x + width;
        boolean conditionY = mouseY >= this.y && mouseY <= this.y + height;
        return conditionX && conditionY;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        updateMatrix();
    }

    public void onResize(int screenWidth, int screenHeight) {
    }

}
