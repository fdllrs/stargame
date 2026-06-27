package game.ui.panel;

import engine.ui.UIElement;
import engine.ui.UIScrollArea;
import engine.ui.text.FontAtlas;
import game.components.StorageComponent;
import game.components.UISlideAnimation;
import game.objects.spaceBodies.Planet;
import game.ui.tabs.infotabs.UIBuildTab;
import org.joml.Vector4f;

public class PlanetBuildPanel extends UIPanel {

	private final StorageComponent playerStorage;
	private final Runnable onRebuild;
	private final UISlideAnimation animation;
	private Planet currentPlanet;
	private UIScrollArea scrollArea;

	public PlanetBuildPanel(float x,
			float y,
			float width,
			float height,
			Vector4f color,
			FontAtlas font,
			StorageComponent playerStorage,
			Runnable onRebuild) {
		super(x, y, width, height, color, font);
		this.playerStorage = playerStorage;
		this.onRebuild = onRebuild;

		this.animation = new UISlideAnimation(this, 10.0f);
	}

	public void configSlideX(float anchorX, float hiddenX) {
		animation.configSlideX(anchorX, hiddenX);
	}

	public void forceX(float x) {
		animation.forceX(x);
	}

	@Override
	public float getBoundingHeight() {
		return this.height + vPadding;
	}

	@Override
	public void update(float mouseX, float mouseY, float deltaTime) {
		animation.update(deltaTime);
		if (shouldRender()) {
			super.update(mouseX, mouseY, deltaTime);
		}
	}

	@Override
	public void onResize(int screenWidth, int screenHeight) {
		if (scrollArea != null) {
			scrollArea.setSize(this.width, this.height - 60);
		}
		super.onResize(screenWidth, screenHeight);
	}

	@Override
	public void rebuildElements() {
		children.clear();

		if (currentPlanet == null) return;

		setPanelTitle(currentPlanet.getName() + " - Build");

		if (scrollArea == null) {
			scrollArea = new UIScrollArea(width, height - 60, 0);
		}
		else {
			scrollArea.setSize(width, height - 60);
			scrollArea.clearElements();
		}

		for (UIElement element : UIBuildTab.build(currentPlanet,
												  playerStorage,
												  font,
												  width,
												  onRebuild)) {
			scrollArea.addElement(element);
		}

		children.add(scrollArea);

		super.rebuildElements();
		layout();
	}

	@Override
	protected boolean shouldRender() {
		return currentPlanet != null;
	}

	public boolean isAnimating() {
		return animation.isAnimatingX();
	}

	public void setCurrentPlanet(Planet planet) {
		this.currentPlanet = planet;
	}

	public void slideIn(boolean snapToHiddenFirst) {
		animation.slideIn(snapToHiddenFirst);
	}

	public void slideOut(boolean snapToAnchorFirst) {
		animation.slideOut(snapToAnchorFirst);
	}
}
