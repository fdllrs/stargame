package engine.graphics;

import engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector2i;


public class UI {

    private final Matrix4f uiProjection;
    private final long windowHandle;

    public UI(int WINDOW_WIDTH, int WINDOW_HEIGHT, long windowHandle) {
        this.windowHandle = windowHandle;
        uiProjection = new Matrix4f().ortho(
                0.0f,
                WINDOW_WIDTH,
                0.0f,
                WINDOW_HEIGHT,
                -1.0f,
                1.0f);

    };


    public Matrix4f crosshair(){
        Matrix4f crosshairModel = new Matrix4f();

        crosshairModel.translate(50f, 100f, 0.0f);
        Vector2i screenSize = Window.getWindowSize(windowHandle);
        crosshairModel.scale(screenSize.x, 50f, 1.0f);
        return crosshairModel;
    }

    public Matrix4f getProjection() {
        return uiProjection;
    }
}
