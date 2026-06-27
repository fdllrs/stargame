package game.states;

import engine.events.EventBus;
import engine.state.GameState;
import game.core.Game;
import game.core.Input;
import game.events.MapToggledEvent;

import static org.lwjgl.glfw.GLFW.*;

public class MapState implements GameState<Game> {
	private final GameState<Game> previousState;
	private boolean wasCursorEnabled;

	public MapState(GameState<Game> previousState) {
		this.previousState = previousState;
	}

	@Override
	public void enter(Game gameContext) {
		wasCursorEnabled = gameContext.getInput().isCursorEnabled();
		if (!wasCursorEnabled) {
			gameContext.getInput().toggleCursor();
		}
		EventBus.publish(new MapToggledEvent(true));
	}

	@Override
	public void exit(Game gameContext) {
		if (gameContext.getInput().isCursorEnabled() != wasCursorEnabled) {
			gameContext.getInput().toggleCursor();
		}
		EventBus.publish(new MapToggledEvent(false));
	}

	@Override
	public void handleInput(Game gameContext) {
		Input input = gameContext.getInput();

		if (input.isKeyJustPressed(GLFW_KEY_M) || input.isKeyJustPressed(GLFW_KEY_ESCAPE)) {
			gameContext.getStateMachine().changeState(previousState);
		}

		if (input.consumeMouseButtonJustPressed(GLFW_MOUSE_BUTTON_LEFT)) {
			if (gameContext.getUIManager().objectClicked(input.getMouseX(), input.getMouseY())) {
				gameContext.getPlayerResourcesPanel().refreshAmounts();
			}
			else if (!gameContext.getUiMapPanel().contains(input.getMouseX(), input.getMouseY())) {
				gameContext.getStateMachine().changeState(previousState);
			}
		}

		if (input.isKeyJustPressed(GLFW_KEY_TAB)) {
			input.toggleCursor();
		}
	}

	@Override
	public void update(Game gameContext, float deltaTime) {
		// Camera does not update from input while in map
	}
}
