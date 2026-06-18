package game.objects.entities;

import engine.graphics.ShaderProgram;
import org.joml.Vector3f;

public abstract class GameEntity {
	protected Vector3f position;
	protected Vector3f color;

	public abstract void cleanup();

	public abstract void render(ShaderProgram shader);

	public GameEntity(Vector3f position, Vector3f color) {
		this.position = new Vector3f(position);
		this.color = color;
	}
}
