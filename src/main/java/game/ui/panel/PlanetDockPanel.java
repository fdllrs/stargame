package game.ui.panel;

import engine.events.EventBus;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.text.FontAtlas;
import game.components.StorageComponent;
import game.events.PlayerDockedEvent;
import game.events.PlayerUndockedEvent;
import game.objects.spaceBodies.Planet;
import org.joml.Vector4f;

public class PlanetDockPanel extends UIPanel {

	private final PlanetBuildPanel buildPanel;
	private final PlanetStoragePanel storagePanel;
	private Planet currentPlanet;
	private boolean isDocked;
	private boolean dirty = false;

	public PlanetDockPanel(float x,
			float y,
			float width,
			float height,
			Vector4f color,
			FontAtlas font,
			StorageComponent playerStorage,
			Runnable onRebuild) {
		super(x, y, width, height, color, font);

		this.buildPanel = new PlanetBuildPanel(x,
											   y,
											   width,
											   height,
											   color,
											   font,
											   playerStorage,
											   onRebuild);
		this.storagePanel = new PlanetStoragePanel(x,
												   y,
												   width,
												   height,
												   color,
												   font,
												   playerStorage,
												   onRebuild);
		this.isDocked = false;

		setupLayout(height);

		EventBus.subscribe(PlayerDockedEvent.class, event -> {
			this.currentPlanet = event.planet();
			this.isDocked = true;
			this.buildPanel.setCurrentPlanet(currentPlanet);
			this.storagePanel.setCurrentPlanet(currentPlanet);

			this.rebuildElements();

			// Slide in!
			this.buildPanel.slideIn(true);
			this.storagePanel.slideIn(true);
		});

		EventBus.subscribe(PlayerUndockedEvent.class, _ -> {
			this.isDocked = false;
			this.buildPanel.slideOut(false);
			this.storagePanel.slideOut(false);
		});
	}

	private void setupLayout(float totalHeight) {
		float spacing = 15.0f;
		float storageHeight = totalHeight * 0.35f;
		float buildHeight = totalHeight * 0.65f - spacing;

		buildPanel.setSize(this.width, buildHeight);
		storagePanel.setSize(this.width, storageHeight);

		// Setup animation ranges for children based on current x position
		float currentAnchorX = this.x;
		float currentHiddenX = currentAnchorX + this.width + 50;

		buildPanel.configSlideX(currentAnchorX, currentHiddenX);
		storagePanel.configSlideX(currentAnchorX, currentHiddenX);

		if (isDocked) {
			buildPanel.forceX(currentAnchorX);
			storagePanel.forceX(currentAnchorX);
		}
		else {
			buildPanel.forceX(currentHiddenX);
			storagePanel.forceX(currentHiddenX);
		}

		buildPanel.setPosition(buildPanel.getPosition().x, this.y);
		storagePanel.setPosition(storagePanel.getPosition().x, this.y + buildHeight + spacing);
	}

	@Override
	public boolean contains(float mouseX, float mouseY) {
		return shouldRender() && ( buildPanel.contains(mouseX, mouseY) || storagePanel.contains(
				mouseX,
				mouseY) );
	}

	@Override
	public float getBoundingHeight() {
		return this.height + vPadding;
	}

	@Override
	public void handleClick(float mouseX, float mouseY) {
		if (!shouldRender()) return;
		if (buildPanel.contains(mouseX, mouseY)) {
			buildPanel.handleClick(mouseX, mouseY);
		}
		else if (storagePanel.contains(mouseX, mouseY)) {
			storagePanel.handleClick(mouseX, mouseY);
		}
	}

	@Override
	public void render(ShaderProgram shader, Mesh uiQuad) {
		if (dirty) {
			rebuildElements();
			dirty = false;
		}
		if (shouldRender()) {
			buildPanel.render(shader, uiQuad);
			storagePanel.render(shader, uiQuad);
		}
	}

	@Override
	public void update(float mouseX, float mouseY, float deltaTime) {
		buildPanel.update(mouseX, mouseY, deltaTime);
		storagePanel.update(mouseX, mouseY, deltaTime);

		if (!isDocked && !buildPanel.isAnimating() && !storagePanel.isAnimating()) {
			currentPlanet = null;
			buildPanel.setCurrentPlanet(null);
			storagePanel.setCurrentPlanet(null);
		}
	}

	@Override
	public void handleScroll(float mouseX, float mouseY, double yOffset, boolean shiftPressed) {
		if (!shouldRender()) return;
		if (buildPanel.contains(mouseX, mouseY)) {
			buildPanel.handleScroll(mouseX, mouseY, yOffset, shiftPressed);
		}
		else if (storagePanel.contains(mouseX, mouseY)) {
			storagePanel.handleScroll(mouseX, mouseY, yOffset, shiftPressed);
		}
	}

	@Override
	public void onResize(int screenWidth, int screenHeight) {
		this.x = screenWidth - this.width - 20;
		setSize(this.width, screenHeight - 50);

		setupLayout(this.height);

		buildPanel.onResize(screenWidth, screenHeight);
		storagePanel.onResize(screenWidth, screenHeight);
	}

	@Override
	public void rebuildElements() {
		children.clear();

		if (currentPlanet == null) return;

		buildPanel.setCurrentPlanet(currentPlanet);
		storagePanel.setCurrentPlanet(currentPlanet);

		buildPanel.rebuildElements();
		storagePanel.rebuildElements();

		children.add(buildPanel);
		children.add(storagePanel);

		layout();
	}

	@Override
	public boolean shouldRender() {
		return currentPlanet != null;
	}

	public void markDirty() {
		this.dirty = true;
	}
}
