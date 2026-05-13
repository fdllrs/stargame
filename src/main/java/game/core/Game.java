package game.core;

import engine.graphics.Camera;
import engine.graphics.Framebuffer;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import org.joml.Matrix4f;
import org.lwjgl.glfw.Callbacks;

import static game.geometry.ScreenQuadGeometry.generateScreenQuad;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class Game {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    private final int TARGET_TPS = 60;
    private final double TIME_PER_TICK = 1.0 / TARGET_TPS;

    private Window window;
    private long windowHandle;

    private ShaderProgram shader;
    private ShaderProgram screenShader;
    private Camera camera;
    private Input input;
    private Scene scene;
    Framebuffer fbo;
    Mesh screenQuad;

    private Matrix4f projection;

    public void run() throws Exception {
        init();



        double lastTime = glfwGetTime();
        double accumulator = 0.0;

        while (!glfwWindowShouldClose(windowHandle)) {
            double currentTime = glfwGetTime();
            double frameTime = currentTime - lastTime;
            lastTime = currentTime;

            accumulator += frameTime;

            glfwPollEvents();

            while (accumulator >= TIME_PER_TICK) {
                update();
                accumulator -= TIME_PER_TICK;
            }
            render();

            glfwSwapBuffers(windowHandle);
            glfwPollEvents();
        }

        cleanup();
    }

    private void init() throws Exception {
        window = new Window();
        window.init(WINDOW_WIDTH, WINDOW_HEIGHT);

        windowHandle = window.windowHandle;

        shader = initShader();
        camera = new Camera();
        input = new Input(windowHandle);
        scene = new Scene();

        projection = new Matrix4f().perspective(
                (float) Math.toRadians(45.0f),
                1280f / 720f,
                10f,
                100_000.0f
        );

        // Create a 320x180 virtual screen (exactly 1/4th of 1280x720)
        fbo = new Framebuffer(320, 180);
        screenQuad = generateScreenQuad();
        screenShader = new ShaderProgram("/shaders/screen.vert", "/shaders/screen.frag");
    }

    private void update() {

        input.updateCamera(camera);
        camera.updateViewMatrix();

        Boolean isMoving = input.isForwardMovementPressed();
        scene.update(camera, isMoving);
    }

    private void render() {
        performFirstPassRendering();
//        performSecondPassRendering();
    }

    private void performSecondPassRendering() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST); // The screen is flat, no depth needed

        screenShader.bind();

        // Bind the texture we just drew our 3D game onto
        glBindTexture(GL_TEXTURE_2D, fbo.textureId);
        screenQuad.render();

        screenShader.unbind();
    }

    private void performFirstPassRendering() {
//        fbo.bind();
        shader.bind();
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.05f, 0.05f, 0.1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.setUniform("view", camera.getViewMatrix());
        shader.setUniform("projection", projection);

        scene.render(shader);

        shader.unbind();
//        fbo.unbind(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void cleanup() {
        scene.cleanup();

        shader.cleanup();
        fbo.cleanup();
        screenShader.cleanup();
        Callbacks.glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    private static ShaderProgram initShader() {
        try {
            return new ShaderProgram("/shaders/basic.vert", "/shaders/basic.frag");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize shader", e);
        }
    }
}