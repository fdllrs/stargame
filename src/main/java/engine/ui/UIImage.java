package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.graphics.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class UIImage extends UIElement {
	private final Texture texture;
	private final Vector2f uvScale = new Vector2f(1, 1);
	private final Vector2f uvOffset = new Vector2f(0, 0);

	public UIImage(float width, float height, Texture texture) {
		super(0, 0, width, height, new Vector4f(1, 1, 1, 1));
		this.texture = texture;
	}

	@Override
	public float getBoundingHeight() {
		return height + vPadding;
	}

	@Override
	public void handleClick(float mouseX, float mouseY) {
	}

	@Override
	public void render(ShaderProgram shader, Mesh uiQuad) {
		if (texture == null) return;

		shader.setUniform("useTexture", 1);
		shader.setUniform("uiTexture", 0);
		shader.setUniform("uvScale", uvScale);
		shader.setUniform("uvOffset", uvOffset);
		shader.setUniform("uiColor", this.color);
		shader.setUniform("model", this.modelMatrix);

		texture.bind();
		uiQuad.render();
		texture.unbind();
	}

	@Override
	public void update(float mouseX, float mouseY, float deltaTime) {
	}
}
