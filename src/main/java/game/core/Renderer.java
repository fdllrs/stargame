package game.core;

import engine.graphics.Camera;
import engine.graphics.Framebuffer;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import game.info.PlanetType;
import game.objects.spaceBodies.Planet;
import game.objects.spaceBodies.SpaceBody;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static game.geometry.ScreenQuadGeometry.generateScreenQuad;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL30C.*;

/**
 * Owns all rendering resources and executes the two-pass render pipeline:
 * 1. Scene rendered into a low-resolution FBO (pixel-art effect).
 * 2. FBO texture upscaled onto the full-resolution screen quad.
 * <p>
 * Call {@link #onResize(int, int)} when the framebuffer size changes.
 */
public class Renderer {
	private static final int PIXEL_ART_DOWNSCALE = 3;
	private final ShaderProgram shader3D;
	private final ShaderProgram shaderStar;
	private final ShaderProgram shaderPixelArt;
	private final ShaderProgram shaderOutline;
	private final ShaderProgram shaderStarfield;
	private final ShaderProgram shaderShadow;
	private final ShaderProgram shaderRocky;
	private final ShaderProgram shaderOrganic;
	private final ShaderProgram shaderGasGiant;
	private final ShaderProgram shaderIceGiant;
	private final ShaderProgram shaderRing;
	private final ShaderProgram[] planetShaders;
	private final Mesh screenQuad;
	private final int shadowFbo;
	private final int shadowDepthTex;
	private final Matrix4f currentLightSpaceMatrix = new Matrix4f();
	private final Matrix4f outlineShellMatrix = new Matrix4f();
	private static final Vector3f OUTLINE_COLOR = new Vector3f(0.0f, 1.0f, 1.0f);
	private static final Vector3f UP_VECTOR = new Vector3f(0.0f, 1.0f, 0.0f);
	private final Vector3f shadowLightDir = new Vector3f();
	private final Vector3f shadowLightPos = new Vector3f();
	private final Vector3f tempLightDirMul = new Vector3f();
	private final Matrix4f shadowLightView = new Matrix4f();
	private final Matrix4f shadowLightProjection = new Matrix4f();
	private Framebuffer fbo;

	public Renderer(long windowHandle) {
		shader3D = ShaderProgram.initShader("/game/basic.vert", "/game/basic.frag");
		shaderStar = ShaderProgram.initShader("/game/star.vert", "/game/star.frag");
		shaderPixelArt = ShaderProgram.initShader("/game/screen.vert", "/game/screen.frag");
		shaderOutline = ShaderProgram.initShader("/game/outline.vert", "/game/outline.frag");
		shaderStarfield = ShaderProgram.initShader("/game/starfield.vert", "/game/starfield.frag");
		shaderShadow = ShaderProgram.initShader("/game/shadow.vert", "/game/shadow.frag");

		shaderRocky = ShaderProgram.initShader("/game/basic.vert", "/game/rocky.frag");
		shaderOrganic = ShaderProgram.initShader("/game/basic.vert", "/game/organic.frag");
		shaderGasGiant = ShaderProgram.initShader("/game/basic.vert", "/game/gasgiant.frag");
		shaderIceGiant = ShaderProgram.initShader("/game/basic.vert", "/game/icegiant.frag");
		shaderRing = ShaderProgram.initShader("/game/basic.vert", "/game/ring.frag");

		planetShaders = new ShaderProgram[ PlanetType.values().length ];
		planetShaders[ PlanetType.ROCKY.ordinal() ] = shaderRocky;
		planetShaders[ PlanetType.GAS_GIANT.ordinal() ] = shaderGasGiant;
		planetShaders[ PlanetType.ICE_GIANT.ordinal() ] = shaderIceGiant;
		planetShaders[ PlanetType.ORGANIC.ordinal() ] = shaderOrganic;

		Vector2i size = Window.getWindowSize(windowHandle);
		fbo = new Framebuffer(size.x / PIXEL_ART_DOWNSCALE, size.y / PIXEL_ART_DOWNSCALE);
		screenQuad = generateScreenQuad();

		shadowFbo = glGenFramebuffers();
		shadowDepthTex = glGenTextures();
		setupShadowFramebuffer();
	}

	public void cleanup() {
		shader3D.cleanup();
		shaderStar.cleanup();
		shaderPixelArt.cleanup();
		shaderOutline.cleanup();
		shaderStarfield.cleanup();
		shaderShadow.cleanup();
		shaderRocky.cleanup();
		shaderOrganic.cleanup();
		shaderGasGiant.cleanup();
		shaderIceGiant.cleanup();
		shaderRing.cleanup();
		glDeleteFramebuffers(shadowFbo);
		glDeleteTextures(shadowDepthTex);
		fbo.cleanup();
		screenQuad.cleanup();
	}

	public Matrix4f getCurrentLightSpaceMatrix() {
		return currentLightSpaceMatrix;
	}

	public ShaderProgram getDefaultShader() {
		return shader3D;
	}

	public ShaderProgram getShaderForType(PlanetType type) {
		return planetShaders[ type.ordinal() ];
	}

	public ShaderProgram getShaderRing() {
		return shaderRing;
	}

	public int getShadowDepthTex() {
		return shadowDepthTex;
	}

	/**
	 * Re-create the FBO at the new (downscaled) size after a window resize.
	 */
	public void onResize(int width, int height) {
		fbo.cleanup();
		fbo = new Framebuffer(width / PIXEL_ART_DOWNSCALE, height / PIXEL_ART_DOWNSCALE);
	}

	/**
	 * Executes render passes for a single frame, including shadow rendering.
	 */
	public void render(Scene scene, Camera camera, long windowHandle) {
		renderShadowPass(scene, camera);
		renderScenePass(scene, camera, windowHandle);
		renderUpscalePass();
	}

	private void renderObjects(Scene scene, Camera camera) {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_STENCIL_TEST);

		glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
		glStencilFunc(GL_ALWAYS, 1, 0xFF);
		glStencilMask(0xFF);

		scene.render(this, shaderStar, camera);
	}

	private void renderOutline(Scene scene, Camera camera) {
		SpaceBody selected = scene.getSelectedObject();
		if (selected != null) {
			shaderOutline.bind();
			shaderOutline.setUniform("view", camera.getViewMatrix());
			shaderOutline.setUniform("projection", camera.getProjectionMatrix());

			glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
			glStencilMask(0x00);
			glDisable(GL_DEPTH_TEST);

			outlineShellMatrix.set(selected.getModelMatrix());
			outlineShellMatrix.scale(1.02f);

			shaderOutline.setUniform("model", outlineShellMatrix);
			shaderOutline.setUniform("outlineColor", OUTLINE_COLOR);

			selected.getMesh().render();

			// Cleanup Outline State
			glEnable(GL_DEPTH_TEST);
			shaderOutline.unbind();
		}
	}

	private void renderScenePass(Scene scene, Camera camera, long windowHandle) {
		fbo.bind();

		renderStarfield(scene, camera);

		renderOutline(scene, camera);

		renderObjects(scene, camera);
		
		glStencilFunc(GL_ALWAYS, 0, 0xFF);
		glDisable(GL_STENCIL_TEST);

		Vector2i screenSize = Window.getWindowSize(windowHandle);
		fbo.unbind(screenSize.x, screenSize.y);
	}

	private void renderShadowPass(Scene scene, Camera camera) {
		glBindFramebuffer(GL_FRAMEBUFFER, shadowFbo);
		glViewport(0, 0, 2048, 2048);
		glClear(GL_DEPTH_BUFFER_BIT);

		glEnable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		shaderShadow.bind();

		// 1. Calculate direction vector from Star (0,0,0) to player/camera
		Vector3f cameraPos = camera.getPosition();
		shadowLightDir.set(cameraPos).normalize();

		// 2. Position light camera at a distance behind player (closer to the star)
		tempLightDirMul.set(shadowLightDir).mul(350.0f);
		shadowLightPos.set(cameraPos).sub(tempLightDirMul);

		shadowLightView.identity().lookAt(shadowLightPos, cameraPos, UP_VECTOR);
		shadowLightProjection.identity().ortho(-120.0f,
											   120.0f,
											   -120.0f,
											   120.0f,
											   200.0f,
											   500.0f);

		currentLightSpaceMatrix.set(shadowLightProjection).mul(shadowLightView);
		shaderShadow.setUniform("lightSpaceMatrix", currentLightSpaceMatrix);

		for (Planet planet : scene.getPlanets()) {
			planet.render(shaderShadow);
			planet.renderFacilities(shaderShadow);
			for (SpaceBody orbiter : planet.satellites) {
				orbiter.render(shaderShadow);
			}
		}
		scene.getPlayer().render(shaderShadow);

		shaderShadow.unbind();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	private void renderStarfield(Scene scene, Camera camera) {
		glClearColor(0.01f, 0.01f, 0.01f, 1f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

		glDepthMask(false);
		scene.getStarfield().render(shaderStarfield,
									camera.getViewMatrix(),
									camera.getProjectionMatrix());
		glDepthMask(true);
	}

	private void renderUpscalePass() {
		glDisable(GL_DEPTH_TEST);

		shaderPixelArt.bind();
		glBindTexture(GL_TEXTURE_2D, fbo.textureId);
		screenQuad.render();
		shaderPixelArt.unbind();
	}

	private void setupShadowFramebuffer() {
		glBindTexture(GL_TEXTURE_2D, shadowDepthTex);
		glTexImage2D(GL_TEXTURE_2D,
					 0,
					 GL_DEPTH_COMPONENT,
					 2048,
					 2048,
					 0,
					 GL_DEPTH_COMPONENT,
					 GL_FLOAT,
					 (java.nio.ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		float[] borderColor = { 1.0f, 1.0f, 1.0f, 1.0f };
		glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

		glBindFramebuffer(GL_FRAMEBUFFER, shadowFbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER,
							   GL_DEPTH_ATTACHMENT,
							   GL_TEXTURE_2D,
							   shadowDepthTex,
							   0);
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Shadow Framebuffer is not complete!");
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
}
