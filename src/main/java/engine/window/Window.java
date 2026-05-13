package engine.window;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.glEnable;

public class Window {

    public long windowHandle;

    public void init(int width, int height) {
        try {
            glfwInit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        windowHandle = glfwCreateWindow(width,height, "stargame", 0L, 0);
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert videoMode != null;
        glfwSetWindowPos(windowHandle, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);

        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();
        glfwShowWindow(windowHandle);

        glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glEnable(GL_DEPTH_TEST);
    }

}
