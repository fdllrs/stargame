package game.core;

import engine.graphics.Camera;
import engine.state.GameStateMachine;
import engine.ui.UIManager;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import engine.ui.text.UIText.Alignment;
import engine.window.Window;
import game.objects.Player;
import game.states.FlightState;
import game.ui.panel.InfoPanel;
import game.ui.panel.InventoryPanel;
import game.ui.panel.PlanetDockPanel;
import game.ui.panel.UIMapPanel;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
	public static final int TICK_TIME = 5;
	private static final String FONT_FILE = "src/main/resources/fonts/fontfile.fnt";
	private static final String FONT_TEXTURE = "src/main/resources/fonts/fontfile.png";
	private static final int INITIAL_WIDTH = 1280;
	private static final int INITIAL_HEIGHT = 720;
	private long windowHandle;
	private Window window;
	private Camera camera;
	private Input input;
	private Scene scene;
	private Player player;
	private Renderer renderer;
	private UIManager uiManager;
	private InfoPanel infoPanel;
	private PlanetDockPanel planetDockPanel;
	private InventoryPanel playerResourcesPanel;
	private UIMapPanel uiMapPanel;
	private GameStateMachine<Game> stateMachine;

	public Camera getCamera() { return camera; }

	public InfoPanel getInfoPanel() { return infoPanel; }

	public Input getInput() { return input; }

	public InventoryPanel getPlayerResourcesPanel() { return playerResourcesPanel; }

	public Scene getScene() { return scene; }

	public GameStateMachine<Game> getStateMachine() { return stateMachine; }

	public UIManager getUIManager() { return uiManager; }

	public UIMapPanel getUiMapPanel() { return uiMapPanel; }

	public long getWindowHandle() { return windowHandle; }

	public void run() {
		init();
		gameLoop();
		cleanup();
	}

	private void init() {
		initWindow();

		camera = new Camera((float) INITIAL_WIDTH / INITIAL_HEIGHT);
		scene = new Scene();
		renderer = new Renderer(windowHandle);
		player = scene.getPlayer();
		FontAtlas fontAtlas = new FontAtlas(FONT_FILE, FONT_TEXTURE);

		input = new Input(windowHandle);

		initUIPanels(fontAtlas);
		initUIManager(fontAtlas);

		setupStateMachine();

		registerResizeCallback();
		placePlayerAtSecondPlanet();
	}

	private void gameLoop() {
		double lastTime = glfwGetTime();
		double tickAccumulator = 0.0;
		while (!glfwWindowShouldClose(windowHandle)) {
			double currentTime = glfwGetTime();
			float deltaTime = (float) ( currentTime - lastTime );
			lastTime = currentTime;

			glfwPollEvents();
			update(deltaTime);

			tickAccumulator += deltaTime;
			if (tickAccumulator >= TICK_TIME) {
				scene.tick();
				tickAccumulator -= TICK_TIME;
			}

			renderer.render(scene, camera, windowHandle);
			uiManager.renderAll();
			glfwSwapBuffers(windowHandle);
		}
	}

	private void cleanup() {
		scene.cleanup();
		renderer.cleanup();
		uiManager.cleanup();
		window.cleanup();
	}

	private void initWindow() {
		window = new Window();
		window.init(INITIAL_WIDTH, INITIAL_HEIGHT);
		windowHandle = window.windowHandle;
	}

	private void initUIPanels(FontAtlas fontAtlas) {
		infoPanel = new InfoPanel(10,
								  25,
								  380,
								  INITIAL_HEIGHT - 100,
								  new Vector4f(0.2f, 0.2f, 0.2f, 0.95f),
								  fontAtlas);

		planetDockPanel = new PlanetDockPanel(INITIAL_WIDTH - 400,
											  25,
											  380,
											  INITIAL_HEIGHT - 100,
											  new Vector4f(0.15f, 0.2f, 0.25f, 0.95f),
											  fontAtlas,
											  scene.getPlayer().getStorage(),
											  () -> planetDockPanel.markDirty());

		playerResourcesPanel = new InventoryPanel(INITIAL_WIDTH - 400,
												  INITIAL_HEIGHT - 220,
												  380,
												  200,
												  fontAtlas,
												  scene.getPlayer().getStorage(),
												  new Vector4f(0.2f, 0.2f, 0.2f, 0.95f));

		infoPanel.setOnSelectTarget(body -> {
			scene.updateSelectedObject(body);
			infoPanel.setTarget(body);
		});

		uiMapPanel = new UIMapPanel(INITIAL_WIDTH * 0.1f,
									INITIAL_HEIGHT * 0.1f,
									INITIAL_WIDTH * 0.8f,
									INITIAL_HEIGHT * 0.8f,
									new Vector4f(0.05f, 0.06f, 0.08f, 0.95f),
									fontAtlas,
									scene,
									infoPanel,
									input,
									windowHandle);
	}

	private void initUIManager(FontAtlas fontAtlas) {
		uiManager = new UIManager(windowHandle);
		uiManager.addElement(infoPanel);
		uiManager.addElement(planetDockPanel);
		uiManager.addElement(playerResourcesPanel);
		uiManager.addElement(uiMapPanel);
		UIText dockedLabel = new UIText("",
										Alignment.CENTER,
										new Vector4f(1f, 1f, 1f, 1f),
										32,
										10,
										15,
										fontAtlas,
										INITIAL_WIDTH);
		uiManager.addTopText(dockedLabel);
	}

	private void setupStateMachine() {
		stateMachine = new GameStateMachine<>(this);
		stateMachine.changeState(new FlightState());
	}

	private void registerResizeCallback() {
		glfwSetFramebufferSizeCallback(windowHandle, (_, width, height) -> {
			camera.onResize(width, height);
			uiManager.onResize(width, height);
			renderer.onResize(width, height);
		});
	}

	private void placePlayerAtSecondPlanet() {
		scene.update(camera, false, 0f);
		camera.moveTo(scene.getPlanets().get(2).getPosition().add(0, 100, 0, new Vector3f()));
	}

	private void update(float deltaTime) {
		float mouseX = input.getMouseX();
		float mouseY = input.getMouseY();
		input.update();
		if (input.isCursorEnabled()) {
			uiManager.update(mouseX, mouseY, deltaTime);
		}
		else {
			uiManager.update(-1f, -1f, deltaTime);
		}

		double scrollY = input.getScrollDeltaY();
		if (scrollY != 0 && input.isCursorEnabled()) {
			boolean shiftPressed = input.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || input.isKeyPressed(
					GLFW_KEY_RIGHT_SHIFT);
			if (uiManager.handleScroll(mouseX, mouseY, scrollY, shiftPressed)) {
				playerResourcesPanel.refreshAmounts();
			}
		}

		stateMachine.handleInput();
		stateMachine.update(deltaTime);

		if (input.isKeyJustPressed(GLFW_KEY_O)) {
			infoPanel.setTarget(null);
			scene.recreateStarSystem();
			placePlayerAtSecondPlanet();
		}

		camera.applyMovement(deltaTime, player.getMaxSpeed());
		scene.update(camera, input.isForwardMovementPressed(), deltaTime);

		camera.updateViewMatrix();
	}

	public void updateCameraFromInput(float deltaTime) {
		handleCameraRotation();
		handleCameraMovement(deltaTime);
	}

	private void handleCameraRotation() {
		if (!input.isCursorEnabled()) {
			camera.addRotation(input.getMouseDx(), input.getMouseDy());
		}
	}

	public void handleCameraMovement(float deltaTime) {

		float playerAcceleration = player.accelerate(deltaTime);
		if (input.isKeyPressed(GLFW_KEY_W)) {
			if (input.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
				playerAcceleration = player.accelerateWithTurbo(deltaTime);
			}
			camera.updateVelocityForwards(playerAcceleration);
		}
		if (input.isKeyPressed(GLFW_KEY_A)) camera.updateVelocityLeft(playerAcceleration);
		if (input.isKeyPressed(GLFW_KEY_S)) camera.updateVelocityBack(playerAcceleration);
		if (input.isKeyPressed(GLFW_KEY_D)) camera.updateVelocityRight(playerAcceleration);

		if (input.isKeyPressed(GLFW_KEY_SPACE)) camera.zeroAcceleration(player.getBrakeStrength());
	}
}