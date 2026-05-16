package game.core;

import engine.graphics.*;
import engine.ui.UIManager;
import engine.ui.UIText;
import engine.window.Window;
import org.joml.Vector2i;
import org.joml.Vector4f;

import static game.geometry.ScreenQuadGeometry.generateScreenQuad;
import static game.geometry.ScreenQuadGeometry.generateUIRect;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class Game {

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

    public void run() throws Exception {
        init();

        gameLoop();

        cleanup();
    }

    private void gameLoop() {
        double lastTime = glfwGetTime();
        float deltaTime;
        Texture fontAtlas = new Texture("src/main/resources/fonts/charmap-oldschool.png");

        UIText ammoText = new UIText("speed: " + camera.getVelocity(), 20, 650, 20, 30, new Vector4f(1, 1, 1,
                1),
                fontAtlas);
        uiManager.addElement(ammoText);
        while (!glfwWindowShouldClose(windowHandle)) {
            double currentTime = glfwGetTime();
            deltaTime = (float) (currentTime - lastTime);
            lastTime = currentTime;
            glfwPollEvents();

            update(deltaTime);

            ammoText.setText("speed: " + camera.getVelocity());
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
        fbo = new Framebuffer(320, 180);
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
        input = new Input(windowHandle);
        scene = new Scene();
        uiManager = new UIManager(windowHandle);

    }

    private void placePlayerInRandomPlanet() {
        scene.update(camera, false);
        camera.moveTo(scene.getPlanets().getFirst().getPosition().add(35, 0, 0));
    }


    private void update(float deltaTime) {
        Boolean isMoving = input.isForwardMovementPressed();

        input.handleCameraInput(camera, deltaTime);
        camera.applyMovement(deltaTime);
        camera.updateViewMatrix();
        scene.update(camera, isMoving);
    }

    private void render() {
        performFirstPassRendering();
        performSecondPassRendering();
        performUIRendering();
    }
    private void performUIRendering() {
        uiManager.renderAll();
    }
    private void performSecondPassRendering() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST); // The screen is flat, no depth needed

        shaderPixelArt.bind();

        // Bind the texture we just drew our 3D game onto
        glBindTexture(GL_TEXTURE_2D, fbo.textureId);
        screenQuad.render();

        shaderPixelArt.unbind();
    }

    private void performFirstPassRendering() {
        fbo.bind();
        shader3D.bind();
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.05f, 0.05f, 0.1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader3D.setUniform("view", camera.getViewMatrix());
        shader3D.setUniform("projection", camera.getCameraProjection());

        scene.render(shader3D);

        shader3D.unbind();
        Vector2i screenSize = Window.getWindowSize(windowHandle);
        fbo.unbind(screenSize.x, screenSize.y);
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