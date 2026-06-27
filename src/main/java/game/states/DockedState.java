package game.states;

import engine.events.EventBus;
import engine.state.GameState;
import game.core.Game;
import game.core.Input;
import game.events.PlayerDockedEvent;
import game.events.PlayerUndockedEvent;
import game.objects.spaceBodies.Planet;
import game.objects.spaceBodies.SpaceBody;

import static org.lwjgl.glfw.GLFW.*;

public class DockedState implements GameState<Game> {

	@Override
	public void enter(Game gameContext) {
		Planet dockedPlanet = gameContext.getScene().getDockedPlanet();
		EventBus.publish(new PlayerDockedEvent(dockedPlanet));
	}

	@Override
	public void exit(Game gameContext) {
		EventBus.publish(new PlayerUndockedEvent());
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
				if (clicked != gameContext.getScene().getDockedPlanet()) {
					SpaceBody newTarget =
							clicked == null ? gameContext.getScene().getDockedPlanet() : clicked;
					gameContext.getScene().updateSelectedObject(newTarget);
					gameContext.getInfoPanel().setTarget(newTarget);
				}
			}
		}

		checkEscapeKey(gameContext, input);
		checkTabKey(input);
	}

	static void checkEscapeKey(Game gameContext, Input input) {
		if (input.isKeyJustPressed(GLFW_KEY_ESCAPE)) {
			if (gameContext.getPlayerResourcesPanel().isExpanded()) {
				gameContext.getPlayerResourcesPanel().setExpanded(false);
			}
			else if (input.isCursorEnabled()) {
				input.toggleCursor();
			}
		}
	}

	static void checkTabKey(Input input) {
		if (input.isKeyJustPressed(GLFW_KEY_TAB)) {
			input.toggleCursor();
		}
	}

	@Override
	public void update(Game gameContext, float deltaTime) {
		gameContext.updateCameraFromInput(deltaTime);

		if (!gameContext.getScene().isPlayerDocked()) {
			gameContext.getStateMachine().changeState(new FlightState());
		}
	}
}
