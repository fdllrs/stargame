package game.core;

import engine.graphics.Camera;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    private final long windowHandle;

    private final double[] mouseX = new double[1];
    private final double[] mouseY = new double[1];

    private double lastX;
    private double lastY;

    public Input(long windowHandle) {
        this.windowHandle = windowHandle;

        glfwGetCursorPos(windowHandle, mouseX, mouseY);
        lastX = mouseX[0];
        lastY = mouseY[0];
    }

    public void updateCamera(Camera camera) {
        glfwGetCursorPos(windowHandle, mouseX, mouseY);

        updateCameraRotation(camera);

        updateCameraMovement(camera);

    }

    private void updateCameraRotation(Camera camera) {
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
    public void updateCameraMovement(Camera camera) {
        if (glfwGetKey(windowHandle, GLFW_KEY_W) == GLFW_PRESS) camera.moveForwards();
        if (glfwGetKey(windowHandle, GLFW_KEY_A) == GLFW_PRESS) camera.moveLeft();
        if (glfwGetKey(windowHandle, GLFW_KEY_S) == GLFW_PRESS) camera.moveBackwards();
        if (glfwGetKey(windowHandle, GLFW_KEY_D) == GLFW_PRESS) camera.moveRight();
        if (glfwGetKey(windowHandle, GLFW_KEY_SPACE) == GLFW_PRESS) camera.moveUp();
        if (glfwGetKey(windowHandle, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) camera.moveDown();

    }
}