package game.core;

import engine.graphics.Camera;
import engine.window.Window;
import game.objects.Planet;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private final long windowHandle;
    private final Camera camera;

    private boolean cursorEnabled;

    private final double[] mouseX = new double[1];
    private final double[] mouseY = new double[1];

    private double lastX;
    private double lastY;

    private boolean leftClickPressed = false;


    public Input(long windowHandle, Camera camera, Scene scene) {
        this.windowHandle = windowHandle;
        cursorEnabled = false;
        lastX = mouseX[0];
        lastY = mouseY[0];
        this.camera = camera;

        setupTabToggleCallback(windowHandle);
        setupClickDetectionCallback(windowHandle, camera);
    }

    private void setupTabToggleCallback(long windowHandle) {
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_TAB && action == GLFW_PRESS) {
                toggleCursor();
            }
        });
    }
    private void setupClickDetectionCallback(long windowHandle, Camera camera) {
        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> {
            if (button != GLFW_MOUSE_BUTTON_LEFT || action != GLFW_PRESS) {
                return;
            }

            if (glfwGetInputMode(windowHandle, GLFW_CURSOR) == GLFW_CURSOR_DISABLED) {
                return;
            }
            glfwGetCursorPos(windowHandle, mouseX, mouseY);
            leftClickPressed = true;
        });
    }

    public boolean isForwardMovementPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_W) == GLFW_PRESS
                || glfwGetKey(windowHandle, GLFW_KEY_S) == GLFW_PRESS;
    }

    public void handleCameraInput(float deltaTime) {
        if (!cursorEnabled){
            glfwGetCursorPos(windowHandle, mouseX, mouseY);
            handleCameraRotation();
        };
        handleCameraMovement(deltaTime);

    }
    private void handleCameraRotation() {
        float deltaX = (float) (mouseX[0] - lastX);
        float deltaY = (float) (mouseY[0] - lastY);

        lastX = mouseX[0];
        lastY = mouseY[0];

        camera.addRotation(deltaX, deltaY);
    }
    public void handleCameraMovement(float deltaTime) {

        if (isKeyPressed(GLFW_KEY_W)) camera.accelerateForwards(deltaTime);
        if (isKeyPressed(GLFW_KEY_W) && isKeyPressed(GLFW_KEY_LEFT_SHIFT)) camera.accelerateWithTurbo(deltaTime);

        if (isKeyPressed(GLFW_KEY_A)) camera.accelerateLeft(deltaTime);
        if (isKeyPressed(GLFW_KEY_S)) camera.accelerateBackwards(deltaTime);
        if (isKeyPressed(GLFW_KEY_D)) camera.accelerateRight(deltaTime);

        if (isKeyPressed(GLFW_KEY_SPACE)) camera.zeroAcceleration(false);
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(windowHandle, key) == GLFW_PRESS;
    }
    public boolean consumeLeftClick() {
        if (!leftClickPressed) {
            return false;
        }

        leftClickPressed = false;
        return true;
    }
    public void toggleCursor() {
        if (cursorEnabled) {
            cursorEnabled = false;
            glfwSetCursorPos(windowHandle, mouseX[0], mouseY [0]);
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        } else {
            cursorEnabled = true;
            glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            Vector2i screenSize = Window.getWindowSize(windowHandle);
            glfwSetCursorPos(windowHandle, screenSize.x / 2.0, screenSize.y / 2.0);
        }
    }


    public float getMouseX() {
        return (float) mouseX[0];
    }
    public float getMouseY() {
        return (float) mouseY[0];
    }
}