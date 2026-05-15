package game.core;

import engine.graphics.Camera;
import engine.window.Window;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private final long windowHandle;

    public boolean isCursorEnabled() {
        return cursorEnabled;
    }

    private boolean cursorEnabled;

    private final double[] mouseX = new double[1];
    private final double[] mouseY = new double[1];

    private double lastX;
    private double lastY;

    public Input(long windowHandle) {
        this.windowHandle = windowHandle;
        cursorEnabled = false;
        lastX = mouseX[0];
        lastY = mouseY[0];

        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_TAB && action == GLFW_PRESS) {
                toggleCursor();
            }
        });
    }

    public void handleCameraInput(Camera camera, float deltaTime) {
        if (!cursorEnabled){
            glfwGetCursorPos(windowHandle, mouseX, mouseY);
            handleCameraRotation(camera);
        };
        handleCameraMovement(camera, deltaTime);

    }

    private void handleCameraRotation(Camera camera) {
        float deltaX = (float) (mouseX[0] - lastX);
        float deltaY = (float) (mouseY[0] - lastY);

        lastX = mouseX[0];
        lastY = mouseY[0];

        camera.addRotation(deltaX, deltaY);
    }

    public boolean isForwardMovementPressed() {
        return glfwGetKey(windowHandle, GLFW_KEY_W) == GLFW_PRESS
                || glfwGetKey(windowHandle, GLFW_KEY_S) == GLFW_PRESS;
    }
    public void handleCameraMovement(Camera camera, float deltaTime) {

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

    public boolean isKeyReleased(int key) {
        return glfwGetKey(windowHandle, key) == GLFW_RELEASE;
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
}