package game.states;

import engine.state.GameState;
import game.core.Game;
import game.core.Input;
import game.objects.spaceBodies.SpaceBody;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class FlightState implements GameState<Game> {

	@Override
	public void enter(Game gameContext) {
	}

	@Override
	public void exit(Game gameContext) {
	}

	@Override
	public void handleInput(Game gameContext) {
		Input input = gameContext.getInput();

		if (input.isKeyJustPressed(GLFW_KEY_M)) {
			gameContext.getStateMachine().changeState(new MapState(this));
		}

		if (input.consumeMouseButtonJustPressed(GLFW_MOUSE_BUTTON_LEFT)) {
			if (gameContext.getUIManager().objectClicked(input.getMouseX(), input.getMouseY())) {
				gameContext.getPlayerResourcesPanel().refreshAmounts();
			}
			else {
				SpaceBody clicked = gameContext.getScene().objectClicked(input.getMouseX(),
																		 input.getMouseY(),
																		 gameContext.getWindowHandle(),
																		 gameContext.getCamera());
				gameContext.getScene().updateSelectedObject(clicked);
				gameContext.getInfoPanel().setTarget(clicked);
			}
		}

		DockedState.checkEscapeKey(gameContext, input);
		DockedState.checkTabKey(input);
	}

	@Override
	public void update(Game gameContext, float deltaTime) {
		gameContext.updateCameraFromInput(deltaTime);

		if (gameContext.getScene().isPlayerDocked()) {
			gameContext.getStateMachine().changeState(new DockedState());
		}
	}
}
