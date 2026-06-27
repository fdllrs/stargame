package engine.state;

public interface GameState <Game> {
	void enter(Game gameContext);
	void exit(Game gameContext);
	void handleInput(Game gameContext);
	void update(Game gameContext, float deltaTime);
}
