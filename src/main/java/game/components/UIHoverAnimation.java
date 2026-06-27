package game.components;

import engine.ui.UIElement;
import org.joml.Vector4f;

public class UIHoverAnimation {

	private final UIElement target;
	private final float animationSpeed;
	private final Vector4f baseColor;
	private final Vector4f hoverColor;
	private boolean animationEnabled = true;
	private boolean scaleEnabled = true;
	private float targetScale = 1.1f;
	private float currentScale = 1.0f;

	public UIHoverAnimation(UIElement target, float animationSpeed) {
		this.target = target;
		this.animationSpeed = animationSpeed;
		this.baseColor = new Vector4f(target.getColor());
		this.hoverColor = new Vector4f(Math.min(1.0f, baseColor.x + 0.15f),
									   Math.min(1.0f, baseColor.y + 0.15f),
									   Math.min(1.0f, baseColor.z + 0.15f),
									   baseColor.w);
	}

	private void animateColor(float deltaTime, boolean isHovered) {
		Vector4f color = target.getColor();
		float t = 1.0f - (float) Math.exp(-animationSpeed * deltaTime);
		if (isHovered) {
			color.lerp(hoverColor, t);
		}
		else {
			color.lerp(baseColor, t);
		}
	}

	private void animateScale(float deltaTime, boolean isHovered) {
		if (!scaleEnabled) return;
		if (isHovered) {
			targetScale = 1.03f;
		}
		else {
			targetScale = 1f;
		}
		currentScale = calculateScale(deltaTime);
		target.setScale(currentScale);
	}

	private float calculateScale(float deltaTime) {
		return currentScale + ( targetScale - currentScale ) * ( 1.0f - (float) Math.exp(
				-animationSpeed * deltaTime) );
	}

	public void setAnimationEnabled(boolean enabled) {
		animationEnabled = enabled;
	}

	public void setScaleEnabled(boolean enabled) {
		scaleEnabled = enabled;
	}

	public void update(float deltaTime, boolean isHovered, boolean isEnabled) {
		if (!animationEnabled) return;
		if (!isEnabled) return;
		
		animateScale(deltaTime, isHovered);
		animateColor(deltaTime, isHovered);
	}
}
