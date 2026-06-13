package engine.ui.panels;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.UIElement;
import engine.ui.text.FontAtlas;
import game.components.StorageComponent;
import game.objects.celestialBodies.SpaceBody;
import game.ui.Describable;
import org.joml.Vector4f;

import java.util.function.Consumer;

public class InfoPanel extends UIPanel {
	private final StorageComponent playerStorage;
	private Describable currentTarget;
	private InfoPanelController currentController;
	private Consumer<SpaceBody> onSelectTarget;

	private boolean dirty = true;

	public InfoPanel(float x,
			float y,
			float width,
			float height,
			Vector4f color,
			FontAtlas font,
			StorageComponent playerStorage) {
		super(x, y, width, height, color, font);
		this.playerStorage = playerStorage;
	}

	@Override
	public void layout() {
		float currentY = this.y + vPadding;
		for (UIElement element : children) {
			float elementX = this.x;
			if (element.getLayoutAlignment() == UIElement.LayoutAlignment.CENTER) {
				elementX = this.x + ( this.width - element.getSize().x ) / 2.0f;
			}
			element.setPosition(elementX, currentY);
			currentY += element.getBoundingHeight();
		}
	}

	@Override
	protected void rebuildElements() {
		children.clear();

		if (currentTarget == null || currentController == null) return;

		setPanelTitle(currentTarget.getDisplayName());

		currentController.populate(children, currentTarget, playerStorage, font, width);

		layout();
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
	public boolean shouldRender() {
		return currentTarget != null;
	}

	public void markDirty() {
		this.dirty = true;
	}

	@Override
	public void onResize(int screenWidth, int screenHeight) {
		setSize(screenWidth * 0.4f, screenHeight - 50);
	}

	public void setOnSelectTarget(java.util.function.Consumer<SpaceBody> callback) {
		this.onSelectTarget = callback;
	}

	public void setTarget(Describable target) {
		this.currentTarget = target;
		if (target != null) {
			this.currentController = target.getPanelController(playerStorage,
															   font,
															   width,
															   this::markDirty,
															   onSelectTarget);
		}
		else {
			this.currentController = null;
		}
		markDirty();
	}

	public void tick() {
		markDirty();
	}
}
