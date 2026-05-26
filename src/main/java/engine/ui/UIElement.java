package engine.ui;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;

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

    public Vector2f getSize() {
        return new Vector2f(width, height);
    }

    public Vector2f getPosition() {
        return new Vector2f(x, y);
    }


    // The method every specific UI element must implement
    public abstract void render(ShaderProgram shader, Mesh uiQuad);

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        updateMatrix();
    }

    public abstract float getBoundingHeight();


    protected void setYPos(float newY) {
        this.setPosition(this.x, newY);
    }

    public boolean contains(float mouseX, float mouseY) {
        System.out.println("x: " + mouseX + " y: " + mouseY);
        boolean conditionX = mouseX >= this.x && mouseX <= this.x + width;
        boolean conditionY = mouseY >= this.y && mouseY <= this.y + height;
        System.out.println(conditionX);
        System.out.println(conditionY);
        return conditionX && conditionY;
    }

    public abstract void handleClick(float mouseX, float mouseY);


}