package game.core;

import engine.graphics.Camera;
import engine.graphics.Framebuffer;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import org.joml.Vector2i;

import static game.geometry.ScreenQuadGeometry.generateScreenQuad;
import static org.lwjgl.opengl.GL11C.*;

/**
 * Owns all rendering resources and executes the two-pass render pipeline:
 * 1. Scene rendered into a low-resolution FBO (pixel-art effect).
 * 2. FBO texture upscaled onto the full-resolution screen quad.
 *
 * Call {@link #onResize(int, int)} when the framebuffer size changes.
 */
public class Renderer {

    private static final int PIXEL_ART_DOWNSCALE = 3;

    private final ShaderProgram shader3D;
    private final ShaderProgram shaderPixelArt;

    private Framebuffer fbo;
    private final Mesh  screenQuad;

    public Renderer(long windowHandle) {
        shader3D      = ShaderProgram.initShader("/game/basic.vert",  "/game/basic.frag");
        shaderPixelArt = ShaderProgram.initShader("/game/screen.vert", "/game/screen.frag");

        Vector2i size = Window.getWindowSize(windowHandle);
        fbo        = new Framebuffer(size.x / PIXEL_ART_DOWNSCALE, size.y / PIXEL_ART_DOWNSCALE);
        screenQuad = generateScreenQuad();
    }

    /** Re-create the FBO at the new (downscaled) size after a window resize. */
    public void onResize(int width, int height) {
        fbo.cleanup();
        fbo = new Framebuffer(width / PIXEL_ART_DOWNSCALE, height / PIXEL_ART_DOWNSCALE);
    }

    /** Executes both render passes for a single frame. */
    public void render(Scene scene, Camera camera, long windowHandle) {
        renderScenePass(scene, camera, windowHandle);
        renderUpscalePass(windowHandle);
    }

    private void renderScenePass(Scene scene, Camera camera, long windowHandle) {
        fbo.bind();
        shader3D.bind();

        glEnable(GL_DEPTH_TEST);
        glClearColor(0.05f, 0.05f, 0.1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader3D.setUniform("view",       camera.getViewMatrix());
        shader3D.setUniform("projection", camera.getProjectionMatrix());
        scene.render(shader3D);

        shader3D.unbind();

        Vector2i screenSize = Window.getWindowSize(windowHandle);
        fbo.unbind(screenSize.x, screenSize.y);
    }

    private void renderUpscalePass(long windowHandle) {
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        shaderPixelArt.bind();
        glBindTexture(GL_TEXTURE_2D, fbo.textureId);
        screenQuad.render();
        shaderPixelArt.unbind();
    }

    public void cleanup() {
        shader3D.cleanup();
        shaderPixelArt.cleanup();
        fbo.cleanup();
        screenQuad.cleanup();
    }
}
