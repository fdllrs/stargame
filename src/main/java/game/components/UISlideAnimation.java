package game.components;

import engine.ui.UIElement;

public class UISlideAnimation {

	private final UIElement target;
	private final float animationSpeed;
	private final AxisAnimation xAxis = new AxisAnimation();
	private final AxisAnimation yAxis = new AxisAnimation();
	private boolean animationEnabled = true;

	public UISlideAnimation(UIElement target, float animationSpeed) {
		this.target = target;
		this.animationSpeed = animationSpeed;
	}

	public void configSlideX(float anchorX, float hiddenX) {
		xAxis.config(anchorX, hiddenX);
	}

	public void configSlideY(float anchorY, float hiddenY) {
		yAxis.config(anchorY, hiddenY);
	}

	public boolean isAnimatingX() {
		return xAxis.isAnimating();
	}

	public boolean isAnimatingY() {
		return yAxis.isAnimating();
	}

	public void setAnimationEnabled(boolean enabled) {
		this.animationEnabled = enabled;
	}

	public void setTargetX(float targetX) {
		xAxis.setTarget(targetX, target.getPosition().x);
	}

	public void slideIn(boolean snapToHiddenFirst) {
		slideAxisToTarget(xAxis, snapToHiddenFirst, xAxis.anchorVal, xAxis.hiddenVal);
		slideAxisToTarget(yAxis, snapToHiddenFirst, yAxis.anchorVal, yAxis.hiddenVal);
	}

	private void slideAxisToTarget(AxisAnimation axis,
			boolean snapFirst,
			Float targetVal,
			Float snapVal) {
		if (targetVal != null && snapVal != null) {
			if (snapFirst) {
				axis.force(snapVal);
				if (axis == xAxis) {
					target.setPosition(snapVal, target.getPosition().y);
				}
				else {
					target.setPosition(target.getPosition().x, snapVal);
				}
			}
			axis.targetVal = targetVal;
		}
	}

	public void slideOut(boolean snapToAnchorFirst) {
		slideAxisToTarget(xAxis, snapToAnchorFirst, xAxis.hiddenVal, xAxis.anchorVal);
		slideAxisToTarget(yAxis, snapToAnchorFirst, yAxis.hiddenVal, yAxis.anchorVal);
	}

	public void snapToHidden() {
		if (xAxis.hiddenVal != null) {
			forceX(xAxis.hiddenVal);
		}
		if (yAxis.hiddenVal != null) {
			forceY(yAxis.hiddenVal);
		}
	}

	public void forceX(float x) {
		xAxis.force(x);
		target.setPosition(x, target.getPosition().y);
	}

	public void forceY(float y) {
		yAxis.force(y);
		target.setPosition(target.getPosition().x, y);
	}

	public void update(float deltaTime) {
		if (!animationEnabled) return;

		float currentX = target.getPosition().x;
		float currentY = target.getPosition().y;

		float newX = xAxis.update(currentX, animationSpeed, deltaTime);
		float newY = yAxis.update(currentY, animationSpeed, deltaTime);

		if (newX != currentX || newY != currentY) {
			target.setPosition(newX, newY);
		}
	}

	private static class AxisAnimation {
		private Float targetVal = null;
		private float currentVal = Float.NaN;
		private Float anchorVal = null;
		private Float hiddenVal = null;

		public void config(float anchorVal, float hiddenVal) {
			boolean wasAtAnchor = ( targetVal != null && targetVal.equals(this.anchorVal) );
			boolean wasAtHidden = ( targetVal != null && targetVal.equals(this.hiddenVal) );

			this.anchorVal = anchorVal;
			this.hiddenVal = hiddenVal;

			if (wasAtAnchor) {
				this.targetVal = anchorVal;
			}
			else if (wasAtHidden) {
				this.targetVal = hiddenVal;
			}
		}

		public void force(float val) {
			this.targetVal = val;
			this.currentVal = val;
		}

		public boolean isAnimating() {
			return targetVal != null && currentVal != targetVal;
		}

		public void setTarget(float targetVal, float initialVal) {
			this.targetVal = targetVal;
			if (Float.isNaN(this.currentVal)) {
				this.currentVal = initialVal;
			}
		}

		public float update(float currentPos, float animationSpeed, float deltaTime) {
			if (targetVal == null) return currentPos;
			if (Float.isNaN(currentVal)) {
				currentVal = currentPos;
			}
			if (currentVal != targetVal) {
				currentVal += ( targetVal - currentVal ) * animationSpeed * deltaTime;
				if (Math.abs(targetVal - currentVal) < 1.0f) {
					currentVal = targetVal;
				}
			}
			return currentVal;
		}
	}
}
