package engine.state;

public class GameStateMachine <Game> {
	private final Game gameContext;
	private GameState<Game> currentState;

	public GameStateMachine(Game context) {
		this.gameContext = context;
	}

	public void changeState(GameState<Game> newState) {
		if (currentState != null) {
			currentState.exit(gameContext);
		}
		currentState = newState;
		if (currentState != null) {
			currentState.enter(gameContext);
		}
	}

	public GameState<Game> getCurrentState() {
		return currentState;
	}

	public void handleInput() {
		if (currentState != null) {
			currentState.handleInput(gameContext);
		}
	}

	public void update(float deltaTime) {
		if (currentState != null) {
			currentState.update(gameContext, deltaTime);
		}
	}
}
