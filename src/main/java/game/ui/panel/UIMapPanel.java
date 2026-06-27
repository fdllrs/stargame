package game.ui.panel;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.ui.text.FontAtlas;
import game.core.Input;
import game.core.Scene;
import game.objects.StarSystem;
import game.objects.spaceBodies.Moon;
import game.objects.spaceBodies.Planet;
import game.objects.spaceBodies.SpaceBody;
import game.objects.spaceBodies.Star;
import game.ui.Describable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class UIMapPanel extends UIPanel {
	// -- Map element types sent to the fragment shader --
	private static final int ELEMENT_CIRCLE_TYPE = 0;
	private static final int ELEMENT_ORBIT_TYPE = 1;
	private static final int ELEMENT_PLAYER_TYPE = 2;
	private static final int ELEMENT_TARGET_TYPE = 3;
	private static final int ELEMENT_PANEL_TYPE = 4;
	private static final int ELEMENT_STAR_TYPE = 5;
	// -- Sizing constants --
	private static final float MAP_RADIUS_FACTOR = 0.85f;
	private static final float ZOOM_FACTOR = 1.15f;
	private static final float ZOOM_MIN = 0.8f;
	private static final float ZOOM_MAX = 8.0f;
	private static final float MOON_TEXT_ZOOM_THRESHOLD = 1.5f;
	public static final float PLANET_TEXT_ZOOM_THRESHOLD = MOON_TEXT_ZOOM_THRESHOLD * 2f;
	private static final Vector4f COLOR_WHITE_TRANSPARENT = new Vector4f(1.0f, 1.0f, 1.0f, 0.9f);
	private static final Vector4f COLOR_WHITE_MUTED = new Vector4f(0.9f, 0.9f, 0.9f, 0.85f);
	private static final Vector4f COLOR_WHITE_DARK = new Vector4f(0.7f, 0.75f, 0.8f, 0.7f);
	private static final Vector4f COLOR_ORBIT = new Vector4f(0.2f, 0.25f, 0.35f, 0.4f);
	private static final Vector4f COLOR_MOON_ORBIT = new Vector4f(0.15f, 0.2f, 0.25f, 0.25f);
	private static final Vector4f COLOR_CYAN = new Vector4f(0.0f, 0.8f, 1.0f, 0.9f);
	private static final Vector4f COLOR_CYAN_OPAQUE = new Vector4f(0.0f, 0.8f, 1.0f, 1.0f);
	private static final Vector4f COLOR_MUTED_TEXT = new Vector4f(0.6f, 0.7f, 0.8f, 0.8f);
	private static final Vector4f COLOR_PLAYER = new Vector4f(0.1f, 1.0f, 0.4f, 1.0f);
	private final Scene scene;
	private final InfoPanel infoPanel;
	private final long windowHandle;
	private final ShaderProgram mapShader;
	// Pre-allocated to avoid GC allocation stutter each frame
	private final Matrix4f elementMatrix = new Matrix4f();
	private final Vector2f panOffset = new Vector2f();
	private final int[] fw = new int[ 1 ];
	private final int[] fh = new int[ 1 ];
	private final Matrix4f projMatrix = new Matrix4f();
	private final Vector2f tempPos1 = new Vector2f();
	private final Vector2f tempPos2 = new Vector2f();
	private final Vector4f tempColor1 = new Vector4f();
	private final Input input;
	private float zoom = 1.0f;
	private boolean isDragging = false;
	private double lastMouseX = 0.0;
	private double lastMouseY = 0.0;

	public UIMapPanel(float x,
			float y,
			float width,
			float height,
			Vector4f color,
			FontAtlas font,
			Scene scene,
			InfoPanel infoPanel,
			Input input,
			long windowHandle) {
		super(x, y, width, height, color, font);
		this.scene = scene;
		this.infoPanel = infoPanel;
		this.windowHandle = windowHandle;
		this.input = input;
		this.mapShader = ShaderProgram.initShader("/UI/map_element.vert", "/UI/map_element.frag");

		engine.events.EventBus.subscribe(game.events.MapToggledEvent.class,
										 event -> this.setVisible(event.open()));
	}

	@Override
	public void cleanup() {
		super.cleanup();
		mapShader.cleanup();
	}

	@Override
	public boolean contains(float mouseX, float mouseY) {
		return visible && super.contains(mouseX, mouseY);
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
		if (!visible) isDragging = false;
	}

	/**
	 * Draws a centered quad at (centerX, centerY) of the given size using the map shader.
	 */
	private void drawElement(Mesh quad,
			float centerX,
			float centerY,
			float w,
			float h,
			Vector4f color,
			int type,
			float rotation) {
		elementMatrix.identity().translate(centerX, centerY, 0.0f).rotateZ(rotation).translate(
				-w / 2.0f, -h / 2.0f, 0.0f).scale(w, h, 1.0f);

		mapShader.setUniform("model", elementMatrix);
		mapShader.setUniform("uiColor", color);
		mapShader.setUniform("elementType", type);
		mapShader.setUniform("elementSize", new Vector2f(w, h));
		mapShader.setUniform("time", (float) glfwGetTime());
		quad.render();
	}

	@Override
	public float getBoundingHeight() { return height; }

	@Override
	public void handleClick(float mouseX, float mouseY) {
		if (!visible) return;

		Star star = scene.closestStarToPlayer();
		if (star == null) return;

		MapViewport vp = buildViewport(star);
		if (!trySelectBodyAt(mouseX, mouseY, star, vp)) {
			isDragging = true;
			lastMouseX = mouseX;
			lastMouseY = mouseY;
		}
	}

	private MapViewport buildViewport(Star star) {
		StarSystem system = scene.getStarSystem();
		float mapRadius = Math.min(width, height) / 2.0f * MAP_RADIUS_FACTOR;
		float maxOrbit = system.maxOrbitDistance(star);
		float scale = ( mapRadius / maxOrbit ) * zoom;
		return new MapViewport(x + width / 2.0f + panOffset.x,
							   y + height / 2.0f + panOffset.y,
							   scale,
							   this);
	}

	/**
	 * Returns true if a map body was found near (mouseX, mouseY) and selected.
	 * Priority: Moons > Planets > Star (closest first).
	 */
	private boolean trySelectBodyAt(float mouseX, float mouseY, Star star, MapViewport vp) {
		List<Planet> planets = scene.getStarSystem().getPlanetsOrbitingStar(star);

		for (Planet planet : planets) {
			for (Moon moon : planet.getMoons()) {
				float size = moonDisplaySize(moon, vp.scale());
				float radius = Math.max(size / 2.0f + 6.0f, 10.0f);
				vp.toScreen(moon, star, tempPos1);
				if (hitTest(mouseX, mouseY, tempPos1.x, tempPos1.y, radius)) {
					return selectAndReturn(moon);
				}
			}
		}
		for (Planet planet : planets) {
			float size = planetDisplaySize(planet, vp.scale());
			float radius = Math.max(size / 2.0f + 6.0f, 12.0f);
			vp.toScreen(planet, star, tempPos1);
			if (hitTest(mouseX, mouseY, tempPos1.x, tempPos1.y, radius)) {
				return selectAndReturn(planet);
			}
		}
		float starSize = starDisplaySize(star, vp.scale());
		float starRadius = Math.max(starSize / 2.0f + 6.0f, 16.0f);
		if (hitTest(mouseX, mouseY, vp.centerX(), vp.centerY(), starRadius)) {
			return selectAndReturn(star);
		}
		scene.updateSelectedObject(null);
		infoPanel.setTarget(null);
		return false;
	}

	private float moonDisplaySize(Moon moon, float scale) {
		return Math.clamp(moon.getRadius() * scale, 8.0f, 16.0f);
	}

	private boolean hitTest(float mouseX, float mouseY, float posX, float posY, float radius) {
		float dx = mouseX - posX;
		float dy = mouseY - posY;
		return dx * dx + dy * dy <= radius * radius;
	}

	private boolean selectAndReturn(SpaceBody body) {
		scene.updateSelectedObject(body);
		infoPanel.setTarget(body instanceof Describable d ? d : null);
		return true;
	}

	private float planetDisplaySize(Planet planet, float scale) {
		return Math.clamp(planet.getRadius() * scale, 12.0f, 32.0f);
	}

	private float starDisplaySize(Star star, float scale) {
		return Math.clamp(star.getRadius() * scale, 30.0f, 75.0f);
	}

	@Override
	public void render(ShaderProgram uiShader, Mesh uiQuad) {
		if (!visible) return;

		updatePanDrag();

		glfwGetFramebufferSize(windowHandle, fw, fh);
		projMatrix.identity().setOrtho(0, fw[ 0 ], fh[ 0 ], 0, -1, 1);

		renderMapBackground(uiQuad, projMatrix);

		Star star = scene.closestStarToPlayer();
		if (star != null) {
			withScissor(fh[ 0 ], () -> renderEntities(uiQuad, star));
		}

		renderOverlayText(uiShader, uiQuad, projMatrix, star, fh[ 0 ]);
	}

	@Override
	public void handleScroll(float mouseX, float mouseY, double yOffset, boolean shiftPressed) {
		if (!visible) return;
		float oldZoom = zoom;
		zoom = Math.clamp(zoom * ( yOffset > 0 ? ZOOM_FACTOR : 1.0f / ZOOM_FACTOR ),
						  ZOOM_MIN,
						  ZOOM_MAX);
		float ratio = zoom / oldZoom;
		float dx = mouseX - ( x + width / 2.0f + panOffset.x );
		float dy = mouseY - ( y + height / 2.0f + panOffset.y );
		panOffset.x += dx * ( 1.0f - ratio );
		panOffset.y += dy * ( 1.0f - ratio );

		Star star = scene.closestStarToPlayer();
		clampPanOffset(star);
	}

	private void clampPanOffset(Star star) {
		if (star == null) return;
		StarSystem system = scene.getStarSystem();
		float maxOrbit = system.maxOrbitDistance(star);
		float mapRadius = Math.min(width, height) / 2.0f * MAP_RADIUS_FACTOR;
		float scale = ( mapRadius / maxOrbit ) * zoom;
		float systemRadius = getAdjustedDistance(maxOrbit, star, scale);

		float maxPanX = Math.max(systemRadius, width / 2.0f);
		float maxPanY = Math.max(systemRadius, height / 2.0f);

		panOffset.x = Math.clamp(panOffset.x, -maxPanX, maxPanX);
		panOffset.y = Math.clamp(panOffset.y, -maxPanY, maxPanY);
	}

	/**
	 * Maps a physical distance to an adjusted screen radius in pixels.
	 * The spacing safeguard is calculated entirely in world units so that it scales
	 * smoothly and proportionally when zooming, avoiding any jitter/freezing bugs.
	 */
	private float getAdjustedDistance(float physDist, Star star, float scale) {
		if (star == null || physDist <= 0f) return 0f;
		List<Planet> planets = scene.getStarSystem().getPlanetsOrbitingStar(star);
		if (planets.isEmpty()) return physDist * scale;

		// Sort planets by physical orbit distance
		List<Planet> sortedPlanets = planets.stream()
											.sorted((p1, p2) -> Float.compare(p1.getPlanetInfo()
																				.orbitDistance(),
																			  p2.getPlanetInfo()
																				.orbitDistance()))
											.toList();

		float maxOrbit = sortedPlanets.getLast().getPlanetInfo().orbitDistance();

		// Enforce spacing in world units (proportional to total system size)
		float minSpacingWorld = maxOrbit * 0.12f;
		float minFirstWorld = maxOrbit * 0.15f;

		float[] adjustedWorldRadii = new float[ sortedPlanets.size() ];
		float currentWorldRadius = 0f;

		for (int i = 0; i < sortedPlanets.size(); i++) {
			Planet p = sortedPlanets.get(i);
			float physWorldRadius = p.getPlanetInfo().orbitDistance();
			if (i == 0) {
				currentWorldRadius = Math.max(physWorldRadius, minFirstWorld);
			}
			else {
				currentWorldRadius = Math.max(physWorldRadius,
											  currentWorldRadius + minSpacingWorld);
			}
			adjustedWorldRadii[ i ] = currentWorldRadius;
		}

		float adjustedWorldDist = calculateAdjustedDistance(physDist,
															sortedPlanets,
															adjustedWorldRadii);

		return adjustedWorldDist * scale;
	}

	private static float calculateAdjustedDistance(float physDist,
			List<Planet> sortedPlanets,
			float[] adjustedWorldRadii) {
		float d0 = sortedPlanets.getFirst().getPlanetInfo().orbitDistance();
		float adjustedWorldDist;
		if (physDist < d0) {
			adjustedWorldDist = ( physDist / d0 ) * adjustedWorldRadii[ 0 ];
		}
		else {
			int matchIdx = -1;
			for (int i = 0; i < sortedPlanets.size() - 1; i++) {
				float di = sortedPlanets.get(i).getPlanetInfo().orbitDistance();
				float dip1 = sortedPlanets.get(i + 1).getPlanetInfo().orbitDistance();
				if (physDist >= di && physDist < dip1) {
					matchIdx = i;
					break;
				}
			}
			if (matchIdx != -1) {
				float di = sortedPlanets.get(matchIdx).getPlanetInfo().orbitDistance();
				float dip1 = sortedPlanets.get(matchIdx + 1).getPlanetInfo().orbitDistance();
				float t = ( physDist - di ) / ( dip1 - di );
				adjustedWorldDist = adjustedWorldRadii[ matchIdx ] + t * ( adjustedWorldRadii[
																				   matchIdx + 1 ] -
																		   adjustedWorldRadii[ matchIdx ] );
			}
			else {
				// Extrapolate beyond the last planet
				int lastIdx = sortedPlanets.size() - 1;
				float dLast = sortedPlanets.get(lastIdx).getPlanetInfo().orbitDistance();
				float extra = physDist - dLast;
				adjustedWorldDist = adjustedWorldRadii[ lastIdx ] + extra;
			}
		}
		return adjustedWorldDist;
	}

	@Override
	public void onResize(int screenWidth, int screenHeight) {
		setSize(screenWidth * 0.8f, screenHeight * 0.8f);
		setPosition(screenWidth * 0.1f, screenHeight * 0.1f);
		super.onResize(screenWidth, screenHeight);
	}

	@Override
	public void rebuildElements() { /* procedural */ }

	@Override
	protected void layout() { /* procedural */ }

	@Override
	public boolean shouldRender() { return visible; }

	private float getMoonOrbitRadius(Moon moon, Planet planet, float scale, float planetSize) {
		int index = planet.getMoons().indexOf(moon);
		if (index < 0) index = 0;
		// Base orbit radius starts at planetSize / 2.0f + 14.0f, and each later
		// moon is spaced by 12.0f pixels
		float minRadius = planetSize / 2.0f + 14.0f + index * 12.0f;
		// Apply a multiplier to make physical moon orbit distance scale up nicely when
		// zooming in
		float baseRadius = moon.getPlanetInfo().orbitDistance() * scale * 4.0f;
		return Math.max(baseRadius, minRadius);
	}

	private void renderBodies(Mesh uiQuad, Star star, List<Planet> planets, MapViewport vp) {
		for (Planet planet : planets) {
			renderPlanetDot(uiQuad, planet, star, vp);
			for (Moon moon : planet.getMoons()) {
				renderMoonDot(uiQuad, moon, star, vp);
			}
		}
	}

	private void renderBodyLabels(ShaderProgram uiShader, Mesh uiQuad, Star star) {
		MapViewport vp = buildViewport(star);
		List<Planet> planets = scene.getStarSystem().getPlanetsOrbitingStar(star);

		float fontSize = Math.clamp(20.0f / zoom, 15f, 22f);
		float starSize = starDisplaySize(star, vp.scale());
		font.renderText(uiShader,
						uiQuad,
						star.getName(),
						vp.centerX() + starSize / 2.0f + 8.0f,
						vp.centerY() - 8.0f,
						fontSize,
						COLOR_WHITE_TRANSPARENT);

		for (Planet planet : planets) {
			if (zoom < PLANET_TEXT_ZOOM_THRESHOLD) { continue; }

			Vector2f pPos = vp.toScreen(planet, star, tempPos1);
			float planetSz = planetDisplaySize(planet, vp.scale());
			font.renderText(uiShader,
							uiQuad,
							planet.getName(),
							pPos.x + planetSz / 2.0f + 6.0f,
							pPos.y - 6.0f,
							fontSize,
							COLOR_WHITE_MUTED);

			if (zoom < MOON_TEXT_ZOOM_THRESHOLD) { continue; }
			for (Moon moon : planet.getMoons()) {
				Vector2f mPos = vp.toScreen(moon, star, tempPos2);
				float moonSz = moonDisplaySize(moon, vp.scale());
				font.renderText(uiShader,
								uiQuad,
								moon.getName(),
								mPos.x + moonSz / 2.0f + 5.0f,
								mPos.y - 5.0f,
								fontSize,
								COLOR_WHITE_DARK);
			}
		}
	}

	private void renderEntities(Mesh uiQuad, Star star) {
		mapShader.bind();

		MapViewport vp = buildViewport(star);
		List<Planet> planets = scene.getStarSystem().getPlanetsOrbitingStar(star);

		renderOrbits(uiQuad, star, planets, vp);
		renderStar(uiQuad, star, vp);
		renderBodies(uiQuad, star, planets, vp);
		renderSelection(uiQuad, star, planets, vp);
		renderPlayer(uiQuad, star, vp);

		mapShader.unbind();
	}

	private void renderMapBackground(Mesh uiQuad, Matrix4f proj) {
		mapShader.bind();
		mapShader.setUniform("projection", proj);
		drawElement(uiQuad,
					x + width / 2.0f,
					y + height / 2.0f,
					width,
					height,
					color,
					ELEMENT_PANEL_TYPE,
					0.0f);
		mapShader.unbind();
	}

	private void renderMoonDot(Mesh uiQuad, Moon moon, Star star, MapViewport vp) {
		Vector2f pos = vp.toScreen(moon, star, tempPos1);
		float size = moonDisplaySize(moon, vp.scale());
		tempColor1.set(moon.getPlanetInfo().colorA(), 1.0f);
		drawElement(uiQuad, pos.x, pos.y, size, size, tempColor1, ELEMENT_CIRCLE_TYPE, 0.0f);
	}

	private void renderOrbits(Mesh uiQuad, Star star, List<Planet> planets, MapViewport vp) {
		for (Planet planet : planets) {
			float orbitRadius = getAdjustedDistance(planet.getPlanetInfo().orbitDistance(),
													star,
													vp.scale());
			float orbitDiam = orbitRadius * 2.0f;
			drawElement(uiQuad,
						vp.centerX(),
						vp.centerY(),
						orbitDiam,
						orbitDiam,
						COLOR_ORBIT,
						ELEMENT_ORBIT_TYPE,
						0.0f);

			Vector2f planetPos = vp.toScreen(planet, star, tempPos1);
			float planetSz = planetDisplaySize(planet, vp.scale());
			for (Moon moon : planet.getMoons()) {
				float moonOrbitDiam =
						getMoonOrbitRadius(moon, planet, vp.scale(), planetSz) * 2.0f;
				drawElement(uiQuad,
							planetPos.x,
							planetPos.y,
							moonOrbitDiam,
							moonOrbitDiam,
							COLOR_MOON_ORBIT,
							ELEMENT_ORBIT_TYPE,
							0.0f);
			}
		}
	}

	// -- Selection highlight --

	private void renderOverlayText(ShaderProgram uiShader,
			Mesh uiQuad,
			Matrix4f proj,
			Star star,
			int fbHeight) {
		uiShader.bind();
		uiShader.setUniform("projection", proj);

		if (star != null) {
			font.renderText(uiShader,
							uiQuad,
							star.getName().toUpperCase(),
							x + 25.0f,
							y + 25.0f,
							28.0f,
							COLOR_CYAN_OPAQUE);
		}
		font.renderText(uiShader,
						uiQuad,
						"ZOOM: [SCROLL] | PAN: [DRAG] | TARGET: [CLICK] | CLOSE: [M]",
						x + 25.0f,
						y + height - 35.0f,
						18.0f,
						COLOR_MUTED_TEXT);

		if (star != null) {
			withScissor(fbHeight, () -> renderBodyLabels(uiShader, uiQuad, star));
		}
	}

	// -- Text overlay --

	private void renderPlanetDot(Mesh uiQuad, Planet planet, Star star, MapViewport vp) {
		Vector2f pos = vp.toScreen(planet, star, tempPos1);
		float size = planetDisplaySize(planet, vp.scale());
		tempColor1.set(planet.getPlanetInfo().colorA(), 1.0f);
		drawElement(uiQuad, pos.x, pos.y, size, size, tempColor1, ELEMENT_CIRCLE_TYPE, 0.0f);
	}

	private void renderPlayer(Mesh uiQuad, Star star, MapViewport vp) {
		org.joml.Vector3f pPos = scene.getPlayer().getPosition();
		org.joml.Vector3f sPos = star.getPosition();
		float dx = pPos.x - sPos.x;
		float dz = pPos.z - sPos.z;
		float dist = (float) Math.sqrt(dx * dx + dz * dz);
		Vector2f pos;
		if (dist == 0f) {
			pos = tempPos1.set(vp.centerX(), vp.centerY());
		}
		else {
			float adjustedDist = getAdjustedDistance(dist, star, vp.scale());
			pos = tempPos1.set(vp.centerX() + ( dx / dist ) * adjustedDist,
							   vp.centerY() + ( dz / dist ) * adjustedDist);
		}
		float yawRad = (float) Math.toRadians(scene.getPlayer().getRotation().y);
		drawElement(uiQuad, pos.x, pos.y, 24.0f, 24.0f, COLOR_PLAYER, ELEMENT_PLAYER_TYPE,
					-yawRad);
	}

	// -- Click targeting --

	private void renderSelection(Mesh uiQuad, Star star, List<Planet> planets, MapViewport vp) {
		SpaceBody selected = scene.getSelectedObject();
		if (selected == null) return;

		if (selected == star) {
			float size = starDisplaySize(star, vp.scale()) * 1.8f;
			drawElement(uiQuad,
						vp.centerX(),
						vp.centerY(),
						size,
						size,
						COLOR_CYAN,
						ELEMENT_TARGET_TYPE,
						0.0f);
			return;
		}

		for (Planet planet : planets) {
			if (selected == planet) {
				Vector2f pos = vp.toScreen(planet, star, tempPos1);
				float size = planetDisplaySize(planet, vp.scale()) * 1.8f;
				drawElement(uiQuad,
							pos.x,
							pos.y,
							size,
							size,
							COLOR_CYAN,
							ELEMENT_TARGET_TYPE,
							0.0f);
				return;
			}
			for (Moon moon : planet.getMoons()) {
				if (selected == moon) {
					Vector2f pos = vp.toScreen(moon, star, tempPos1);
					float size = moonDisplaySize(moon, vp.scale()) * 1.8f;
					drawElement(uiQuad,
								pos.x,
								pos.y,
								size,
								size,
								COLOR_CYAN,
								ELEMENT_TARGET_TYPE,
								0.0f);
					return;
				}
			}
		}
	}

	private void renderStar(Mesh uiQuad, Star star, MapViewport vp) {
		float starSize = starDisplaySize(star, vp.scale());
		tempColor1.set(star.getLight().getColor(), 1.0f);
		drawElement(uiQuad,
					vp.centerX(),
					vp.centerY(),
					starSize * 4f,
					starSize * 4f,
					tempColor1,
					ELEMENT_STAR_TYPE,
					0.0f);
	}

	private void updatePanDrag() {
		if (!input.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
			isDragging = false;
			return;
		}
		float mx = input.getMouseX();
		float my = input.getMouseY();

		if (isDragging) {
			panOffset.add(mx - (float) lastMouseX, my - (float) lastMouseY);
			Star star = scene.closestStarToPlayer();
			clampPanOffset(star);
		}
		lastMouseX = mx;
		lastMouseY = my;
	}

	// -- Primitive rendering --

	private void withScissor(int fbHeight, Runnable body) {
		glEnable(GL_SCISSOR_TEST);
		glScissor((int) x, (int) ( fbHeight - y - height ), (int) width, (int) height);
		body.run();
		glDisable(GL_SCISSOR_TEST);
	}

	// -- Lifecycle --

	/**
	 * Captures the three values that define the current map view.
	 */
	private record MapViewport(
			float centerX, float centerY, float scale, UIMapPanel panel) {
		Vector2f toScreen(SpaceBody body, Star star, Vector2f dest) {
			if (body == star) {
				return dest.set(centerX, centerY);
			}
			if (body instanceof Planet planet) {
				if (planet instanceof Moon moon) {
					float parentSz = panel.planetDisplaySize(moon.getParentPlanet(), scale);
					toScreen(moon.getParentPlanet(), star, dest);
					float parentX = dest.x;
					float parentY = dest.y;
					float dx = moon.getPosition().x - moon.getParentPlanet().getPosition().x;
					float dz = moon.getPosition().z - moon.getParentPlanet().getPosition().z;
					float dist = (float) Math.sqrt(dx * dx + dz * dz);
					if (dist == 0f) return dest.set(parentX, parentY);
					float moonOrbitRadius = panel.getMoonOrbitRadius(moon,
																	 moon.getParentPlanet(),
																	 scale,
																	 parentSz);
					return dest.set(parentX + ( dx / dist ) * moonOrbitRadius,
									parentY + ( dz / dist ) * moonOrbitRadius);
				}

				// For planets
				float dx = planet.getPosition().x - star.getPosition().x;
				float dz = planet.getPosition().z - star.getPosition().z;
				float dist = (float) Math.sqrt(dx * dx + dz * dz);
				if (dist == 0f) return dest.set(centerX, centerY);
				float adjustedDist = panel.getAdjustedDistance(dist, star, scale);
				return dest.set(centerX + ( dx / dist ) * adjustedDist,
								centerY + ( dz / dist ) * adjustedDist);
			}
			return dest.set(centerX, centerY);
		}
	}
}
