package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.components.UIHoverAnimation;
import org.joml.Vector4f;

public class UIButton extends UIElement {

	private final ButtonCallback onClick;
	private final UIHoverAnimation animationComponent;
	private UIText textLabel;
	private UIElement content;
	private boolean isEnabled = true;

	public UIButton(float width,
			float height,
			Vector4f backgroundColor,
			Vector4f textColor,
			String textLabel,
			Runnable onClick,
			FontAtlas fontAtlas) {
		this(width,
			 height,
			 backgroundColor,
			 textColor,
			 textLabel,
			 (_, _) -> { if (onClick != null) onClick.run(); },
			 fontAtlas);
	}

	public UIButton(float width,
			float height,
			Vector4f backgroundColor,
			Vector4f textColor,
			String textLabel,
			ButtonCallback onClick,
			FontAtlas fontAtlas) {
		super(0, 0, width, height, backgroundColor);

		this.onClick = onClick;
		this.vPadding = 15;
		this.hPadding = 10;
		if (textLabel != null) {
			this.textLabel = new UIText(textLabel,
										UIText.Alignment.CENTER,
										textColor,
										15,
										10,
										5,
										fontAtlas,
										width);
		}
		animationComponent = new UIHoverAnimation(this, 15f);
		alignContent();
	}

	private void alignContent() {
		if (textLabel != null) {
			textLabel.setPosition(x, y + ( height - textLabel.getBoundingHeight() ) / 2);
		}
		if (content != null) {
			content.setPosition(x + ( width - content.getSize().x ) / 2,
								y + ( height - content.getBoundingHeight() ) / 2);
		}
	}

	public UIButton(float width,
			float height,
			Vector4f backgroundColor,
			UIElement content,
			Runnable onClick) {
		super(0, 0, width, height, backgroundColor);
		this.onClick = (_, _) -> { if (onClick != null) onClick.run(); };
		this.vPadding = 15;
		this.hPadding = 10;
		this.content = content;
		animationComponent = new UIHoverAnimation(this, 15f);
		alignContent();
	}

	@Override
	public float getBoundingHeight() {
		return height + vPadding;
	}

	@Override
	public void handleClick(float mouseX, float mouseY) {
		if (isEnabled && onClick != null) {
			onClick.onClick(mouseX, mouseY);
		}
	}

	@Override
	public void render(ShaderProgram shader, Mesh uiQuad) {
		shader.setUniform("useTexture", 0);

		UIBackgroundRenderer.renderFuturisticBackground(this, shader, uiQuad, 3.0f);

		if (textLabel != null) {
			textLabel.render(shader, uiQuad);
		}
		if (content != null) {
			content.render(shader, uiQuad);
		}
	}

	@Override
	public void update(float mouseX, float mouseY, float deltaTime) {
		boolean isHovered = contains(mouseX, mouseY);
		animationComponent.update(deltaTime, isHovered, isEnabled);
		if (content != null) {
			content.update(mouseX, mouseY, deltaTime);
		}
	}

	@Override
	public LayoutAlignment getLayoutAlignment() {
		return LayoutAlignment.CENTER;
	}

	@Override
	public void rebuildElements() {
		if (content != null) content.rebuildElements();
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		alignContent();
	}

	public void setAnimationEnabled(boolean enabled) {
		animationComponent.setAnimationEnabled(enabled);
	}

	public void setEnabled(boolean enabled) {
		this.isEnabled = enabled;
		setOpacity(enabled ? 1.0f : 0.3f);
		if (this.textLabel != null) {
			this.textLabel.setOpacity(enabled ? 1.0f : 0.3f);
		}
		if (this.content != null) {
			this.content.setOpacity(enabled ? 1.0f : 0.3f);
		}
	}

	public void setHoverScaleEnabled(boolean enabled) {
		animationComponent.setScaleEnabled(enabled);
	}

	@FunctionalInterface
	public interface ButtonCallback {
		void onClick(float mouseX, float mouseY);
	}
}
