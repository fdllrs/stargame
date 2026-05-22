package game.core;

import engine.graphics.Camera;
import engine.ui.Describable;
import engine.ui.InfoPanel;
import engine.ui.UIManager;
import engine.ui.text.FontAtlas;
import engine.window.Window;
import game.objects.CelestialBody;
import game.objects.GameObject;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The top-level game orchestrator: owns the game loop and wires all subsystems together.
 * Rendering internals live in {@link Renderer}; scene logic lives in {@link Scene}.
 */
public class Game {

    private static final String FONT_FILE = "src/main/resources/fonts/fontfile.fnt";
    private static final String FONT_TEXTURE = "src/main/resources/fonts/fontfile.png";

    private static final int INITIAL_WIDTH = 1280;
    private static final int INITIAL_HEIGHT = 720;

    private long windowHandle;

    private Window window;
    private Camera camera;
    private Input input;
    private Scene scene;
    private Renderer renderer;
    private UIManager uiManager;
    private InfoPanel infoPanel;

    public void run() throws Exception {
        init();
        gameLoop();
        cleanup();
    }

    private void init() {
        window = new Window();
        window.init(INITIAL_WIDTH, INITIAL_HEIGHT);
        windowHandle = window.windowHandle;

        camera = new Camera((float) INITIAL_WIDTH / INITIAL_HEIGHT);
        scene = new Scene();
        renderer = new Renderer(windowHandle);
        input = new Input(windowHandle, camera, scene);

        FontAtlas fontAtlas = new FontAtlas(FONT_FILE, FONT_TEXTURE);
        uiManager = new UIManager(windowHandle);
        infoPanel = new InfoPanel(20, 20, 400, 500, new Vector4f(0.2f, 0.2f, 0.2f, 0.5f), fontAtlas);
        uiManager.addElement(infoPanel);

        registerResizeCallback();
        placePlayerAtFirstPlanet();
    }

    private void registerResizeCallback() {
        glfwSetFramebufferSizeCallback(windowHandle, (win, width, height) -> {
            camera.onResize(width, height);
            uiManager.onResize(width, height);
            renderer.onResize(width, height);
        });
    }

    private void placePlayerAtFirstPlanet() {
        scene.update(camera, false, 0f);
        camera.moveTo(scene.getPlanets().getFirst().getPosition().add(35, 0, 0));
    }

    private void gameLoop() {
        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(windowHandle)) {
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            glfwPollEvents();
            update(deltaTime);
            renderer.render(scene, camera, windowHandle);
            uiManager.renderAll();
            glfwSwapBuffers(windowHandle);
        }
    }

    private void update(float deltaTime) {
        boolean isMoving = input.isForwardMovementPressed();

        input.handleCameraInput(deltaTime);

        if (input.consumeLeftClick()) {
            CelestialBody clicked = scene.objectClicked(input.getMouseX(), input.getMouseY(), windowHandle, camera);
            scene.updateSelectedObject(clicked);
            infoPanel.setTarget(clicked instanceof Describable d ? d : null);


        }

        if (glfwGetKey(windowHandle, GLFW_KEY_L) == GLFW_PRESS) {
            Vector2f size = infoPanel.getSize();
            infoPanel.setSize(size.x + 5, size.y);
        }
        if (glfwGetKey(windowHandle, GLFW_KEY_K) == GLFW_PRESS) {
            Vector2f size = infoPanel.getSize();
            infoPanel.setSize(size.x - 5, size.y);
        }
        if (glfwGetKey(windowHandle, GLFW_KEY_O) == GLFW_PRESS) {
            scene.recreateStarSystem();
        }

        camera.applyMovement(deltaTime);
        camera.updateViewMatrix();
        scene.update(camera, isMoving, deltaTime);
    }

    private void cleanup() {
        scene.cleanup();
        renderer.cleanup();
        uiManager.cleanup();
        window.cleanup();
    }
}