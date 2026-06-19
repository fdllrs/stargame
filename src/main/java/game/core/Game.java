package game.core;

import engine.graphics.Camera;
import engine.ui.UIManager;
import engine.ui.text.FontAtlas;
import engine.window.Window;
import game.objects.Player;
import game.objects.spaceBodies.SpaceBody;
import game.ui.panel.InfoPanel;
import game.ui.panel.PlayerResourcesPanel;
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
	private PlayerResourcesPanel playerResourcesPanel;
	private UIMapPanel uiMapPanel;
	private boolean wasCursorEnabledBeforeMap = false;

	private void cleanup() {
		scene.cleanup();
		renderer.cleanup();
		uiManager.cleanup();
		window.cleanup();
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

	private void handleCameraRotation() {
		if (!input.isCursorEnabled()) {
			camera.addRotation(input.getMouseDx(), input.getMouseDy());
		}
	}

	private void handleEscapeKey() {
		if (uiMapPanel.isVisible()) {
			toggleMap();
		}
		else if (infoPanel.shouldRender()) {
			scene.updateSelectedObject(null);
			infoPanel.setTarget(null);
		}
		else if (playerResourcesPanel.isExpanded()) {
			playerResourcesPanel.setExpanded(false);
		}
		else if (input.isCursorEnabled()) {
			input.toggleCursor();
		}
	}

	private void handleLeftClick() {
		float mouseX = input.getMouseX();
		float mouseY = input.getMouseY();

		if (uiManager.objectClicked(mouseX, mouseY)) {
			playerResourcesPanel.refreshAmounts();
		}
		else if (!uiMapPanel.isVisible()) {
			SpaceBody clicked = scene.objectClicked(mouseX, mouseY, windowHandle, camera);
			scene.updateSelectedObject(clicked);
			infoPanel.setTarget(clicked);
		}
		else if (!uiMapPanel.contains(mouseX, mouseY)) {
			uiMapPanel.setVisible(false);
			input.toggleCursor();
		}
	}

	private void init() {
		window = new Window();
		window.init(INITIAL_WIDTH, INITIAL_HEIGHT);
		windowHandle = window.windowHandle;

		camera = new Camera((float) INITIAL_WIDTH / INITIAL_HEIGHT);
		scene = new Scene();
		renderer = new Renderer(windowHandle);
		player = scene.getPlayer();
		FontAtlas fontAtlas = new FontAtlas(FONT_FILE, FONT_TEXTURE);
		uiManager = new UIManager(windowHandle);
		infoPanel = new InfoPanel(10,
								  25,
								  INITIAL_WIDTH * 0.4f,
								  INITIAL_HEIGHT - 100,
								  new Vector4f(0.2f, 0.2f, 0.2f, 0.8f),
								  fontAtlas,
								  scene.getPlayer().getStorage());

		playerResourcesPanel = new PlayerResourcesPanel(INITIAL_WIDTH - 400,
														INITIAL_HEIGHT - 220,
														380,
														200,
														fontAtlas,
														scene.getPlayer().getStorage(),
														new Vector4f(0.2f, 0.2f, 0.2f, 0.8f));

		input = new Input(windowHandle);
		infoPanel.setOnSelectTarget(body -> {
			scene.updateSelectedObject(body);
			infoPanel.setTarget(body);
		});
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
									input,
									windowHandle);
		uiManager.addElement(uiMapPanel);

		registerResizeCallback();
		placePlayerAtPlanetNumber(2);
	}

	private void placePlayerAtPlanetNumber(int planetNumber) {
		scene.update(camera, false, 0f);
		camera.moveTo(scene.getPlanets()
						   .get(planetNumber)
						   .getPosition()
						   .add(0, 20, 0, new Vector3f()));
	}

	private void registerResizeCallback() {
		glfwSetFramebufferSizeCallback(windowHandle, (_, width, height) -> {
			camera.onResize(width, height);
			uiManager.onResize(width, height);
			renderer.onResize(width, height);
		});
	}

	public void run() {
		init();
		gameLoop();
		cleanup();
	}

	private void toggleMap() {
		boolean nextState = !uiMapPanel.isVisible();
		uiMapPanel.setVisible(nextState);

		if (nextState) {
			wasCursorEnabledBeforeMap = input.isCursorEnabled();
			if (!wasCursorEnabledBeforeMap) {
				input.toggleCursor();
			}
		}
		else {
			if (input.isCursorEnabled() != wasCursorEnabledBeforeMap) {
				input.toggleCursor();
			}
		}
	}

	private void update(float deltaTime) {
		float mouseX = input.getMouseX();
		float mouseY = input.getMouseY();
		input.update();
		if (input.isCursorEnabled()) {
			uiManager.update(mouseX, mouseY, deltaTime);
		}
		if (input.isKeyJustPressed(GLFW_KEY_M)) {
			toggleMap();
		}

		if (input.isKeyJustPressed(GLFW_KEY_TAB)) {
			input.toggleCursor();
		}

		if (input.consumeMouseButtonJustPressed(GLFW_MOUSE_BUTTON_LEFT)) {
			handleLeftClick();
		}

		if (input.isKeyJustPressed(GLFW_KEY_ESCAPE)) {
			handleEscapeKey();
		}

		double scrollY = input.getScrollDeltaY();
		if (scrollY != 0 && input.isCursorEnabled()) {
			boolean shiftPressed = input.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || input.isKeyPressed(
					GLFW_KEY_RIGHT_SHIFT);
			if (uiManager.handleScroll(mouseX, mouseY, scrollY, shiftPressed)) {
				playerResourcesPanel.refreshAmounts();
			}
		}

		if (!uiMapPanel.isVisible()) {
			updateCameraFromInput(deltaTime);
		}

		if (input.isKeyJustPressed(GLFW_KEY_O)) {
			infoPanel.setTarget(null);
			scene.recreateStarSystem();
			placePlayerAtPlanetNumber(2);
		}

		camera.applyMovement(deltaTime, player.getMaxSpeed());
		scene.update(camera, input.isForwardMovementPressed(), deltaTime);
		camera.updateViewMatrix();
	}

	private void updateCameraFromInput(float deltaTime) {
		handleCameraRotation();
		handleCameraMovement(deltaTime);
	}
}