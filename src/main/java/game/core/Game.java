package game.core;

import engine.graphics.Camera;
import engine.ui.Describable;
import engine.ui.UIManager;
import engine.ui.panels.InfoPanel;
import engine.ui.panels.ResourcesPanel;
import engine.ui.text.FontAtlas;
import engine.window.Window;
import game.objects.celestialBodies.CelestialBody;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

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
    private ResourcesPanel resourcesPanel;

    public void run()
            throws Exception {
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
        infoPanel = new InfoPanel(20,
                                  50,
                                  400,
                                  INITIAL_HEIGHT - 100,
                                  new Vector4f(0.2f, 0.2f, 0.2f, 0.8f),
                                  fontAtlas,
                                  scene.getPlayer().getStorage());

        resourcesPanel = new ResourcesPanel(INITIAL_WIDTH - 320,
                                            INITIAL_HEIGHT - 220,
                                            300,
                                            200,
                                            fontAtlas,
                                            scene.getPlayer().getStorage(),
                                            new Vector4f(0.2f, 0.2f, 0.2f, 0.8f));

        uiManager.addElement(infoPanel);
        uiManager.addElement(resourcesPanel);
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
        camera.moveTo(scene.getPlanets()
                           .getFirst()
                           .getPosition()
                           .add(35, 0, 0, new Vector3f()));
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
            handleLeftClick();
        }
        if (glfwGetKey(windowHandle, GLFW_KEY_O) == GLFW_PRESS) {
            scene.recreateStarSystem();
        }

        camera.applyMovement(deltaTime);

        scene.update(camera, isMoving, deltaTime);
        camera.updateViewMatrix();
    }

    private void handleLeftClick() {
        float mouseX = input.getMouseX();
        float mouseY = input.getMouseY();

        if (uiManager.objectClicked(mouseX, mouseY, windowHandle)) {
            resourcesPanel.refreshAmounts();

        } else {
            CelestialBody clicked = scene.objectClicked(mouseX,
                                                        mouseY,
                                                        windowHandle,
                                                        camera);
            scene.updateSelectedObject(clicked);
            infoPanel.setTarget(clicked instanceof Describable d ? d : null);
        }

    }

    private void cleanup() {
        scene.cleanup();
        renderer.cleanup();
        uiManager.cleanup();
        window.cleanup();
    }
}