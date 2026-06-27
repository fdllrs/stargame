package game.ui.panel;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import game.components.UISlideAnimation;
import game.objects.spaceBodies.SpaceBody;
import game.ui.Describable;
import game.ui.panel.controller.InfoPanelController;
import org.joml.Vector4f;

import java.util.function.Consumer;

public class InfoPanel extends UIPanel {
	private final UISlideAnimation animation;
	private Describable currentTarget;
	private InfoPanelController currentController;
	private Consumer<SpaceBody> onSelectTarget;

	private final float layoutX;
	private boolean dirty = true;

	public InfoPanel(float x, float y, float width, float height, Vector4f color, FontAtlas font) {
		super(x, y, width, height, color, font);
		this.layoutX = x;

		this.animation = new UISlideAnimation(this, 10f);
		this.animation.configSlideX(x, x - width - 50);
		this.animation.snapToHidden();

		engine.events.EventBus.subscribe(game.events.PlayerDockedEvent.class, event -> {
			this.setTarget(event.planet());
			this.animation.slideIn(true);
			this.rebuildElements();
		});

		engine.events.EventBus.subscribe(game.events.PlayerUndockedEvent.class, _ -> {
			this.setTarget(null);
			this.animation.slideOut(false);
		});
	}

	@Override
	public float getBoundingHeight() {
		return this.height + vPadding;
	}

	@Override
	public void render(ShaderProgram shader, Mesh uiQuad) {

		if (dirty) {
			rebuildElements();
			dirty = false;
		}
		super.render(shader, uiQuad);
	}

	@Override
	public void update(float mouseX, float mouseY, float deltaTime) {
		animation.update(deltaTime);

		if (shouldRender()) {
			super.update(mouseX, mouseY, deltaTime);
		}
	}

	@Override
	public void rebuildElements() {
		children.clear();

		if (currentTarget == null || currentController == null) {
			return;
		}

		setPanelTitle(currentTarget.getDisplayName());
		currentController.populate(children, currentTarget, font, width);
		super.rebuildElements();
		layout();
	}

	@Override
	public boolean shouldRender() {
		return currentTarget != null || animation.isAnimatingX();
	}

	public void markDirty() {
		this.dirty = true;
	}

	@Override
	public void onResize(int screenWidth, int screenHeight) {
		setSize(this.width, screenHeight - 100);
		float anchorX = layoutX;
		float hiddenX = layoutX - this.width - 50;
		this.animation.configSlideX(anchorX, hiddenX);

		if (currentTarget != null) {
			animation.forceX(anchorX);
		} else {
			animation.forceX(hiddenX);
		}

		super.onResize(screenWidth, screenHeight);
	}

	public void setOnSelectTarget(Consumer<SpaceBody> callback) {
		this.onSelectTarget = callback;
	}

	public void setTarget(Describable target) {
		this.currentTarget = target;
		if (target != null) {
			this.currentController = target.getPanelController(font, width, onSelectTarget);
		}
		else {
			this.currentController = null;
		}
		markDirty();
	}
}
