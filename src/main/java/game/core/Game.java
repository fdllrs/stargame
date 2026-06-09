package game.core;

import engine.graphics.Camera;
import engine.ui.Describable;
import engine.ui.UIManager;
import engine.ui.panels.InfoPanel;
import engine.ui.panels.PlayerResourcesPanel;
import engine.ui.panels.UIMapPanel;
import engine.ui.text.FontAtlas;
import engine.window.Window;
import game.objects.celestialBodies.CelestialBody;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    public static final int TICK_TIME = 5;
    private static final String FONT_FILE = "src/main/resources/fonts/fontfile.fnt";
    private static final String FONT_TEXTURE = "src/main/resources/fonts/fontfile.png";
    private static final int INITIAL_WIDTH = 1280;
    private static final int INITIAL_HEIGHT = 720;
    private final boolean wasTPressed = false;
    private long windowHandle;
    private Window window;
    private Camera camera;
    private Input input;
    private Scene scene;
    private Renderer renderer;
    private UIManager uiManager;
    private InfoPanel infoPanel;
    private PlayerResourcesPanel playerResourcesPanel;
    private UIMapPanel uiMapPanel;
    private boolean wasOPressed = false;
    private boolean wasMPressed = false;
    private boolean wasCursorEnabledBeforeMap = false;

    public void run() {
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

        FontAtlas fontAtlas = new FontAtlas(FONT_FILE, FONT_TEXTURE);
        uiManager = new UIManager(windowHandle);
        infoPanel = new InfoPanel(20,
                                  50,
                                  400,
                                  INITIAL_HEIGHT - 100,
                                  new Vector4f(0.2f, 0.2f, 0.2f, 0.8f),
                                  fontAtlas,
                                  scene.getPlayer().getStorage());

        playerResourcesPanel = new PlayerResourcesPanel(INITIAL_WIDTH - 320,
                                                        INITIAL_HEIGHT - 220,
                                                        300,
                                                        200,
                                                        fontAtlas,
                                                        scene.getPlayer().getStorage(),
                                                        new Vector4f(0.2f,
                                                                     0.2f,
                                                                     0.2f,
                                                                     0.8f));

        input = new Input(windowHandle, camera);
        uiManager.addElement(infoPanel);
        uiManager.addElement(playerResourcesPanel);

        uiMapPanel = new UIMapPanel(INITIAL_WIDTH * 0.1f,
                                    INITIAL_HEIGHT * 0.1f,
                                    INITIAL_WIDTH * 0.8f,
                                    INITIAL_HEIGHT * 0.8f,
                                    new Vector4f(0.05f, 0.06f, 0.08f, 0.85f),
                                    fontAtlas,
                                    scene,
                                    infoPanel,
                                    windowHandle);
        uiManager.addElement(uiMapPanel);

        registerResizeCallback();
        registerScrollCallback();
        placePlayerAtFirstPlanet();
    }

    private void placePlayerAtFirstPlanet() {
        scene.update(camera, false, 0f);
        camera.moveTo(scene.getPlanets()
                           .getFirst()
                           .getPosition()
                           .add(35, 0, 0, new Vector3f()));
    }

    private void registerResizeCallback() {
        glfwSetFramebufferSizeCallback(windowHandle, (_, width, height) -> {
            camera.onResize(width, height);
            uiManager.onResize(width, height);
            renderer.onResize(width, height);
        });
    }

    private void registerScrollCallback() {
        glfwSetScrollCallback(windowHandle, (win, _, yOffset) -> {
            if (!input.isCursorEnabled()) {
                return;
            }

            double[] xpos = new double[1];
            double[] ypos = new double[1];
            glfwGetCursorPos(win, xpos, ypos);
            float mouseX = (float) xpos[0];
            float mouseY = (float) ypos[0];

            if (uiManager.handleScroll(mouseX, mouseY, yOffset)) {
                playerResourcesPanel.refreshAmounts();
            }
        });
    }

    private void gameLoop() {
        double lastTime = glfwGetTime();
        double tickAccumulator = 0.0;
        while (!glfwWindowShouldClose(windowHandle)) {
            double currentTime = glfwGetTime();
            float deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;

            glfwPollEvents();
            update(deltaTime);

            tickAccumulator += deltaTime;
            if (tickAccumulator >= TICK_TIME) {
                scene.tick();
                infoPanel.tick();
                tickAccumulator -= TICK_TIME;
            }

            renderer.render(scene, camera, windowHandle);
            uiManager.renderAll();
            glfwSwapBuffers(windowHandle);
        }
    }

    private void update(float deltaTime) {
        boolean isMPressed = glfwGetKey(windowHandle, GLFW_KEY_M) == GLFW_PRESS;
        if (isMPressed && !wasMPressed) {
            toggleMap();
        }
        wasMPressed = isMPressed;

        if (uiMapPanel.isVisible()) {
            camera.applyMovement(deltaTime);
            scene.update(camera, false, deltaTime);
            camera.updateViewMatrix();

            if (input.consumeLeftClick()) {
                handleLeftClick();
            }
            return;
        }

        boolean isMoving = input.isForwardMovementPressed();

        input.handleCameraInput(deltaTime);

        if (input.consumeLeftClick()) {
            handleLeftClick();
        }
        boolean isOPressed = glfwGetKey(windowHandle, GLFW_KEY_O) == GLFW_PRESS;
        if (isOPressed && !wasOPressed) {
            scene.recreateStarSystem();
            placePlayerAtFirstPlanet();
        }
        wasOPressed = isOPressed;

        camera.applyMovement(deltaTime);

        scene.update(camera, isMoving, deltaTime);
        camera.updateViewMatrix();
    }

    private void handleLeftClick() {
        float mouseX = input.getMouseX();
        float mouseY = input.getMouseY();

        if (uiManager.objectClicked(mouseX, mouseY)) {
            playerResourcesPanel.refreshAmounts();

        } else if (!uiMapPanel.isVisible()) {
            CelestialBody clicked = scene.objectClicked(mouseX,
                                                        mouseY,
                                                        windowHandle,
                                                        camera);
            scene.updateSelectedObject(clicked);
            infoPanel.setTarget(clicked instanceof Describable d ? d : null);
        }

    }

    private void toggleMap() {
        boolean nextState = !uiMapPanel.isVisible();
        uiMapPanel.setVisible(nextState);

        if (nextState) {
            wasCursorEnabledBeforeMap = input.isCursorEnabled();
            if (!wasCursorEnabledBeforeMap) {
                input.toggleCursor();
            }
        } else {
            if (input.isCursorEnabled() != wasCursorEnabledBeforeMap) {
                input.toggleCursor();
            }
        }
    }

    private void cleanup() {
        scene.cleanup();
        renderer.cleanup();
        uiManager.cleanup();
        window.cleanup();
    }

}