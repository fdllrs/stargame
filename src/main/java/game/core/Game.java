package game.core;

import engine.graphics.*;
import engine.ui.InfoPanel;
import engine.ui.UIManager;
import engine.ui.text.FontAtlas;
import engine.window.Window;
import game.objects.GameObject;
import game.objects.Planet;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static game.geometry.ScreenQuadGeometry.generateScreenQuad;
import static game.geometry.ScreenQuadGeometry.generateUIRect;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class Game {
    private static final String DEFAULT_FONT_FILE = "src/main/resources/fonts/fontfile.fnt";
    private static final String DEFAULT_FONT_TEXTURE = "src/main/resources/fonts/fontfile.png";

    private long windowHandle;

    private ShaderProgram shader3D;

    private ShaderProgram shaderPixelArt;
    Framebuffer fbo;
    Mesh screenQuad;

    private ShaderProgram shaderUi;
    Mesh uiRect;
    private UIManager uiManager;

    private Camera camera;
    private Input input;
    private Scene scene;
    private Window window;

    private InfoPanel infoPanel;
    private FontAtlas fontAtlas;

    public void run() throws Exception {
        init();
        gameLoop();
        cleanup();
    }

    private void gameLoop() {
        double lastTime = glfwGetTime();
        float deltaTime;

        while (!glfwWindowShouldClose(windowHandle)) {
            double currentTime = glfwGetTime();
            deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;
            glfwPollEvents();

            update(deltaTime);
            render();
            glfwSwapBuffers(windowHandle);

        }
    }

    private void init() throws Exception {
        createComponents();
        initShaders();
        placePlayerInRandomPlanet();
    }

    private void initShaders() {
        shader3D = ShaderProgram.initShader("/game/basic.vert", "/game/basic.frag");

        shaderPixelArt = ShaderProgram.initShader("/game/screen.vert", "/game/screen.frag");

        int PIXEL_ART_DOWNSCALE_FACTOR = 3;
        Vector2i screenSize = Window.getWindowSize(windowHandle);

        fbo = new Framebuffer(screenSize.x/PIXEL_ART_DOWNSCALE_FACTOR, screenSize.y/PIXEL_ART_DOWNSCALE_FACTOR);
        screenQuad = generateScreenQuad();

        shaderUi = uiManager.getUiShader();
        uiRect = generateUIRect();
    }
    private void createComponents() {
        int INITIAL_WINDOW_WIDTH = 1280;
        int INITIAL_WINDOW_HEIGHT = 720;

        window = new Window();
        window.init(INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);
        windowHandle = window.windowHandle;
        camera = new Camera();
        scene = new Scene();
        input = new Input(windowHandle, camera, scene);
        uiManager = new UIManager(windowHandle);
        fontAtlas = new FontAtlas(DEFAULT_FONT_FILE, DEFAULT_FONT_TEXTURE);
        infoPanel = new InfoPanel(
                20,
                20,
                400,
                500,
                new Vector4f(0.2f,0.2f,0.2f,0.5f),
                fontAtlas);
        uiManager.addElement(infoPanel);

    }
    private void placePlayerInRandomPlanet() {
        scene.update(camera, false);
        camera.moveTo(scene.getPlanets().getFirst().getPosition().add(35, 0, 0));
    }


    private void update(float deltaTime) {
        Boolean isMoving = input.isForwardMovementPressed();

        input.handleCameraInput(deltaTime);

        if(input.consumeLeftClick()){
            float mouseX = input.getMouseX();
            float mouseY = input.getMouseY();
            GameObject objectClicked = scene.objectClicked(mouseX, mouseY, windowHandle, camera);
            infoPanel.setTarget(objectClicked);

        }

        if(glfwGetKey(windowHandle, GLFW_KEY_L) == GLFW_PRESS) {
            Vector2f panelSize = infoPanel.getSize();
            infoPanel.setSize(panelSize.x + 5, panelSize.y);
        }
        if(glfwGetKey(windowHandle, GLFW_KEY_K) == GLFW_PRESS) {
            Vector2f panelSize = infoPanel.getSize();
            infoPanel.setSize(panelSize.x - 5, panelSize.y);
        }

        camera.applyMovement(deltaTime);
        camera.updateViewMatrix();
        scene.update(camera, isMoving);

    }

    private void render() {
        performFirstPassRendering();
        performSecondPassRendering();
        performUIRendering();
    }
    private void performFirstPassRendering() {
        fbo.bind();
        shader3D.bind();
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.05f, 0.05f, 0.1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader3D.setUniform("view", camera.getViewMatrix());
        shader3D.setUniform("projection", camera.getProjectionMatrix());

        scene.render(shader3D);

        shader3D.unbind();
        Vector2i screenSize = Window.getWindowSize(windowHandle);
        fbo.unbind(screenSize.x, screenSize.y);
    }
    private void performSecondPassRendering() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        shaderPixelArt.bind();

        glBindTexture(GL_TEXTURE_2D, fbo.textureId);
        screenQuad.render();

        shaderPixelArt.unbind();
    }
    private void performUIRendering() {
        uiManager.renderAll();
    }

    private void cleanup() {
        scene.cleanup();

        shader3D.cleanup();
        shaderPixelArt.cleanup();
        shaderUi.cleanup();

        uiRect.cleanup();
        fbo.cleanup();
        window.cleanup();

    }


}