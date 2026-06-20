package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import game.components.StorageComponent;
import game.objects.items.ItemIconRegistry;
import game.objects.items.ItemType;
import org.joml.Vector4f;

public class UIResourceSlot extends UIElement {
	private final ItemType itemType;
	private final StorageComponent planetStorage;
	private final StorageComponent playerStorage;
	private final UIText labelName;
	private final UIText labelAmounts;
	private final Runnable onTransfer;

	public UIResourceSlot(float width,
			float height,
			ItemType itemType,
			StorageComponent planetStorage,
			StorageComponent playerStorage,
			FontAtlas font,
			Runnable onTransfer,
			Vector4f color) {
		super(0, 0, width, height, color);

		this.vPadding = 10;
		this.itemType = itemType;
		this.planetStorage = planetStorage;
		this.playerStorage = playerStorage;
		this.onTransfer = onTransfer;

		this.labelName = new UIText(itemType.name(),
									UIText.Alignment.LEFT,
									new Vector4f(1.0f, 1.0f, 1.0f, 1.0f),
									20,
									52,
									5,
									font,
									width);
		this.labelAmounts = new UIText(getAmountsText(),
									   UIText.Alignment.RIGHT,
									   new Vector4f(1f, 1f, 1f, 1.0f),
									   22,
									   10,
									   5,
									   font,
									   width);
	}

	private String getAmountsText() {
		int planetAmt = planetStorage.getAmount(itemType);
		int shipAmt = playerStorage.getAmount(itemType);
		return planetAmt + "   |   " + shipAmt;
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
		shader.setUniform("useTexture", 0);
		shader.setUniform("uiColor", this.color);
		shader.setUniform("model", this.modelMatrix);
		uiQuad.render();

		engine.graphics.Texture iconTex = ItemIconRegistry.getIcon(itemType);
		if (iconTex != null) {
			shader.setUniform("useTexture", 1);
			shader.setUniform("uiTexture", 0);
			shader.setUniform("uvScale", new org.joml.Vector2f(1, 1));
			shader.setUniform("uvOffset", new org.joml.Vector2f(0, 0));
			shader.setUniform("uiColor", new Vector4f(1, 1, 1, 1));

			org.joml.Matrix4f iconModel = new org.joml.Matrix4f();
			iconModel.translate(this.x + 10, this.y + ( this.height - 32 ) / 2, 0);
			iconModel.scale(32, 32, 1);
			shader.setUniform("model", iconModel);

			iconTex.bind();
			uiQuad.render();
			iconTex.unbind();
		}

		labelName.render(shader, uiQuad);
		labelAmounts.render(shader, uiQuad);
	}

	@Override
	public void update(float mouseX, float mouseY, float deltaTime) {
	}

	@Override
	public void handleScroll(float mouseX, float mouseY, double yOffset, boolean shiftPressed) {
		if (!shiftPressed) return;
		int transferAmount = 10;
		if (yOffset < 0) {
			transferItemsFromTo(planetStorage, playerStorage, transferAmount);
		}
		else if (yOffset > 0) {
			transferItemsFromTo(playerStorage, planetStorage, transferAmount);
		}
		labelAmounts.setText(getAmountsText());
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		labelName.setPosition(x, y + ( this.height - labelName.getBoundingHeight() ) / 2);
		labelAmounts.setPosition(x, y + ( this.height - labelAmounts.getBoundingHeight() ) / 2);
	}

	private void transferItemsFromTo(StorageComponent sourceStorage,
			StorageComponent targetStorage,
			int transferAmount) {
		if (sourceStorage.attemptMoveItemsTo(targetStorage, itemType, transferAmount)) {
			onTransfer.run();
		}
		else if (sourceStorage.attemptMoveItemsTo(targetStorage, itemType, 1)) {
			onTransfer.run();
		}
	}
}