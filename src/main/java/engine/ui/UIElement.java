package engine.ui;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;

public abstract class UIElement {
    protected float x, y;
    protected float width, height;
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

    // Whenever an element moves or changes size, recalculate its matrix
    protected void updateMatrix() {
        modelMatrix.identity();
        modelMatrix.translate(x, y, 0);
        modelMatrix.scale(width, height, 1);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateMatrix();
    }

    // The method every specific UI element must implement
    public abstract void render(ShaderProgram shader, Mesh uiQuad);
}