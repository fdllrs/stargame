package engine.ui;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.text.FontAtlas;
import engine.ui.text.UIText;
import engine.window.Window;
import game.geometry.ScreenQuadGeometry;
import game.ui.panel.UIPanel;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11C.*;

public class UIManager {
	private final Matrix4f uiProjection;
	private final ArrayList<UIPanel> uiPanels;
	private final ShaderProgram uiShader;
	private final Mesh uiQuad;
	private final ArrayList<FloatingText> floatingTexts = new ArrayList<>();
	private UIText topText;
	private int screenWidth;
	private int screenHeight;

	public UIManager(long windowHandle, FontAtlas fontAtlas) {
		uiPanels = new ArrayList<>();
		this.uiShader = ShaderProgram.initShader("/UI/ui.vert", "/UI/ui.frag");
		uiQuad = ScreenQuadGeometry.generateUIRect();
		Vector2i windowSize = Window.getWindowSize(windowHandle);
		uiProjection = new Matrix4f();
		rebuildProjection(windowSize.x, windowSize.y);

		engine.events.EventBus.subscribe(game.events.PlayerDockedEvent.class,
										 _ -> this.updateDockingLabel(true));
		engine.events.EventBus.subscribe(game.events.PlayerUndockedEvent.class,
										 _ -> this.updateDockingLabel(false));
		engine.events.EventBus.subscribe(game.events.SpawnFloatingTextEvent.class, event -> {
			if (event.worldPos() != null) {
				floatingTexts.add(new FloatingText(event.text(),
												   event.worldPos(),
												   event.planetCenter(),
												   event.color(),
												   fontAtlas));
			}
			else if (event.x() != null && event.y() != null) {
				floatingTexts.add(new FloatingText(event.text(),
												   event.x(),
												   event.y(),
												   event.color(),
												   fontAtlas));
			}
		});
	}

	private void rebuildProjection(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
		uiProjection.setOrtho(0.0f, width, height, 0.0f, -1.0f, 1.0f);
	}

	public void updateDockingLabel(boolean playerDocked) {
		if (topText != null) {
			topText.setText(playerDocked ? "Player Docked" : "Player Undocked");
		}
	}

	public void addElement(UIPanel element) {
		uiPanels.add(element);
	}

	public void addTopText(UIText text) {
		topText = text;
	}

	public void cleanup() {
		uiShader.cleanup();
		uiQuad.cleanup();
		for (UIElement element : uiPanels) {
			element.cleanup();
		}
	}

	public boolean handleScroll(float mouseX, float mouseY, double yOffset, boolean shiftPressed) {
		for (UIElement element : uiPanels) {
			if (element.contains(mouseX, mouseY)) {
				element.handleScroll(mouseX, mouseY, yOffset, shiftPressed);
				return true;
			}
		}
		return false;
	}

	public Boolean objectClicked(float mouseX, float mouseY) {
		for (UIElement element : uiPanels) {
			if (element.contains(mouseX, mouseY)) {
				element.handleClick(mouseX, mouseY);
				return true;
			}
		}
		return false;
	}

	public void onResize(int width, int height) {
		rebuildProjection(width, height);
		for (UIElement element : uiPanels) {
			element.onResize(width, height);
		}
		if (topText != null) {
			topText.setMaxWidth(width);
		}
	}

	public void renderAll() {
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		uiShader.bind();
		uiShader.setUniform("projection", uiProjection);

		for (UIElement element : uiPanels) {
			element.render(uiShader, uiQuad);
		}
		if (topText != null) {
			topText.render(uiShader, uiQuad);
		}

		for (FloatingText ft : floatingTexts) {
			ft.render(uiShader, uiQuad);
		}

		uiShader.unbind();
		glDisable(GL_BLEND);
	}

	public void update(float mouseX, float mouseY, float deltaTime, Camera camera) {
		for (UIElement element : uiPanels) {
			element.update(mouseX, mouseY, deltaTime);
		}
		for (int i = floatingTexts.size() - 1; i >= 0; i--) {
			if (!floatingTexts.get(i).update(deltaTime, camera, screenWidth, screenHeight)) {
				floatingTexts.remove(i);
			}
		}
	}

	private static class FloatingText {
		private final UIText text;
		private final float ySpeed = 80.0f; // Pixels per second upwards
		private final float lifetime = 1.0f; // 1 second total
		// 3D world space fields
		private final Vector3f worldPos;
		private final Vector3f planetCenter;
		private float age = 0f;
		private float worldYOffset = 0.0f;

		public FloatingText(String textStr, float x, float y, Vector4f color, FontAtlas font) {
			this.text = new UIText(textStr, UIText.Alignment.CENTER, color, 24, 0, 0, font, 100);
			this.text.setPosition(x - 50,
								  y - 15); // Offset to center and spawn slightly above cursor
			this.worldPos = null;
			this.planetCenter = null;
		}

		public FloatingText(String textStr,
				Vector3f worldPos,
				Vector3f planetCenter,
				Vector4f color,
				FontAtlas font) {
			this.text = new UIText(textStr, UIText.Alignment.CENTER, color, 24, 0, 0, font, 100);
			this.worldPos = new Vector3f(worldPos);
			this.planetCenter = planetCenter != null ? new Vector3f(planetCenter) : null;
			this.text.setPosition(-1000, -1000); // Offscreen until projected
		}

		public boolean update(float deltaTime, Camera camera, int screenWidth, int screenHeight) {
			age += deltaTime;
			if (age >= lifetime) {
				return false;
			}

			if (worldPos != null) {
				if (planetCenter != null) {
					Vector3f normal = new Vector3f(worldPos).sub(planetCenter);
					Vector3f toCamera = new Vector3f(camera.getPosition()).sub(worldPos);
					if (normal.dot(toCamera) <= 0.0f) {
						// Behind the horizon of the planet, hide it
						text.setPosition(-1000, -1000);
						return true;
					}
				}

				worldYOffset += 2.0f * deltaTime; // Rise in 3D world space
				Vector3f currentWorldPos = new Vector3f(worldPos).add(0, worldYOffset, 0);

				Vector4f clipPos = new Vector4f(currentWorldPos, 1.0f);
				new Matrix4f(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).transform(
						clipPos);

				if (clipPos.w <= 0) {
					text.setPosition(-1000, -1000);
				}
				else {
					float ndcX = clipPos.x / clipPos.w;
					float ndcY = clipPos.y / clipPos.w;
					float screenX = ( ( ndcX + 1.0f ) / 2.0f ) * screenWidth;
					float screenY = ( ( 1.0f - ndcY ) / 2.0f ) * screenHeight;
					text.setPosition(screenX - 50, screenY - 15);
				}
			}
			else {
				float currentY = text.getPosition().y;
				text.setPosition(text.getPosition().x, currentY - ySpeed * deltaTime);
			}

			float opacity = 1.0f - ( age / lifetime );
			text.setOpacity(opacity);
			return true;
		}

		public void render(ShaderProgram shader, Mesh uiQuad) {
			if (text.getPosition().x > -500.0f) {
				text.render(shader, uiQuad);
			}
		}
	}
}
