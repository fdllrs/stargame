package game.core;

import engine.window.Window;
import org.joml.Vector2i;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
	private final long windowHandle;
	private final boolean[] keyPressAccumulator = new boolean[ GLFW_KEY_LAST + 1 ];
	private final boolean[] mouseButtonPressAccumulator =
			new boolean[ GLFW_MOUSE_BUTTON_LAST + 1 ];
	private final boolean[] keysJustPressed = new boolean[ GLFW_KEY_LAST + 1 ];
	private final boolean[] mouseButtonsJustPressed = new boolean[ GLFW_MOUSE_BUTTON_LAST + 1 ];
	private float mouseX = 0;
	private float mouseY = 0;
	private float mouseDx = 0;
	private float mouseDy = 0;
	private float lastMouseX = 0;
	private float lastMouseY = 0;
	private double scrollDeltaX = 0;
	private double scrollDeltaY = 0;
	private double scrollAccumulatorX = 0;
	private double scrollAccumulatorY = 0;
	private boolean firstMouse = true;
	private boolean cursorEnabled = false;

	public Input(long windowHandle) {
		this.windowHandle = windowHandle;
		registerCallbacks();
	}

	private void registerCallbacks() {
		registerKeyboardCallback();
		registerMouseButtonCallback();
		registerScrollCallback();
	}

	private void registerKeyboardCallback() {
		glfwSetKeyCallback(windowHandle, (_, key, _, action, _) -> {
			if (key >= 0 && key <= GLFW_KEY_LAST) {
				if (action == GLFW_PRESS) {
					keyPressAccumulator[ key ] = true;
				}
			}
		});
	}

	private void registerMouseButtonCallback() {
		glfwSetMouseButtonCallback(windowHandle, (_, button, action, _) -> {
			if (button >= 0 && button <= GLFW_MOUSE_BUTTON_LAST) {
				if (action == GLFW_PRESS) {
					mouseButtonPressAccumulator[ button ] = true;
				}
			}
		});
	}

	private void registerScrollCallback() {
		glfwSetScrollCallback(windowHandle, (_, xOffset, yOffset) -> {
			scrollAccumulatorX += xOffset;
			scrollAccumulatorY += yOffset;
		});
	}

	public boolean consumeMouseButtonJustPressed(int button) {
		if (!cursorEnabled) { return false; }
		if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) return false;
		boolean pressed = mouseButtonsJustPressed[ button ];
		mouseButtonsJustPressed[ button ] = false;
		return pressed;
	}

	public float getMouseDx() {
		return mouseDx;
	}

	public float getMouseDy() {
		return mouseDy;
	}

	public float getMouseX() {
		return mouseX;
	}

	public float getMouseY() {
		return mouseY;
	}

	public double getScrollDeltaY() {
		return scrollDeltaY;
	}

	public boolean isCursorEnabled() {
		return cursorEnabled;
	}

	public boolean isForwardMovementPressed() {
		return isKeyPressed(GLFW_KEY_W) || isKeyPressed(GLFW_KEY_S);
	}

	public boolean isKeyPressed(int key) {
		if (key < 0 || key > GLFW_KEY_LAST) return false;
		return glfwGetKey(windowHandle, key) == GLFW_PRESS;
	}

	public boolean isKeyJustPressed(int key) {
		if (key < 0 || key > GLFW_KEY_LAST) return false;
		return keysJustPressed[ key ];
	}

	public boolean isMouseButtonJustPressed(int button) {
		if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) return false;
		return mouseButtonsJustPressed[ button ];
	}

	public boolean isMouseButtonPressed(int button) {
		if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) return false;
		return glfwGetMouseButton(windowHandle, button) == GLFW_PRESS;
	}

	public void toggleCursor() {
		if (cursorEnabled) {
			cursorEnabled = false;
			glfwSetCursorPos(windowHandle, mouseX, mouseY);
			glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
			firstMouse = true;
		}
		else {
			cursorEnabled = true;
			glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			Vector2i screenSize = Window.getWindowSize(windowHandle);
			glfwSetCursorPos(windowHandle, screenSize.x / 2.0, screenSize.y / 2.0);
		}
	}

	public void update() {
		registerInputAndResetArray(keyPressAccumulator, keysJustPressed);
		registerInputAndResetArray(mouseButtonPressAccumulator, mouseButtonsJustPressed);
		processScroll();
		processMousePosition();
	}

	private void registerInputAndResetArray(boolean[] inputAccumulator,
			boolean[] justPressedArray) {
		System.arraycopy(inputAccumulator, 0, justPressedArray, 0, justPressedArray.length);
		Arrays.fill(inputAccumulator, false);
	}

	private void processScroll() {
		scrollDeltaX = scrollAccumulatorX;
		scrollDeltaY = scrollAccumulatorY;
		scrollAccumulatorX = 0;
		scrollAccumulatorY = 0;
	}

	private void processMousePosition() {
		double[] mx = new double[ 1 ];
		double[] my = new double[ 1 ];
		glfwGetCursorPos(windowHandle, mx, my);
		float currentX = (float) mx[ 0 ];
		float currentY = (float) my[ 0 ];
		if (firstMouse) {
			lastMouseX = currentX;
			lastMouseY = currentY;
			firstMouse = false;
		}
		mouseDx = currentX - lastMouseX;
		mouseDy = currentY - lastMouseY;

		lastMouseX = currentX;
		lastMouseY = currentY;
		mouseX = currentX;
		mouseY = currentY;
	}
}