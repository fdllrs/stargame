package game.core;

import engine.graphics.Camera;
import engine.graphics.Framebuffer;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.window.Window;
import game.objects.CelestialBody;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import static game.geometry.ScreenQuadGeometry.generateScreenQuad;
import static org.lwjgl.opengl.GL11C.*;

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
    private final ShaderProgram shaderPixelArt;
    private final ShaderProgram shaderOutline;
    private final ShaderProgram shaderStarfield;
    private final Mesh screenQuad;
    private Framebuffer fbo;

    public Renderer(long windowHandle) {
        shader3D = ShaderProgram.initShader("/game/basic.vert", "/game/basic.frag");
        shaderPixelArt = ShaderProgram.initShader("/game/screen.vert", "/game/screen.frag");
        shaderOutline = ShaderProgram.initShader("/game/outline.vert", "/game/outline.frag");
        shaderStarfield = ShaderProgram.initShader("/game/starfield.vert", "/game/starfield.frag");

        Vector2i size = Window.getWindowSize(windowHandle);
        fbo = new Framebuffer(size.x / PIXEL_ART_DOWNSCALE, size.y / PIXEL_ART_DOWNSCALE);
        screenQuad = generateScreenQuad();
    }

    /**
     * Re-create the FBO at the new (downscaled) size after a window resize.
     */
    public void onResize(int width, int height) {
        fbo.cleanup();
        fbo = new Framebuffer(width / PIXEL_ART_DOWNSCALE, height / PIXEL_ART_DOWNSCALE);
    }

    /**
     * Executes both render passes for a single frame.
     */
    public void render(Scene scene, Camera camera, long windowHandle) {
        renderScenePass(scene, camera, windowHandle);
        renderUpscalePass(windowHandle);
    }

    private void renderScenePass(Scene scene, Camera camera, long windowHandle) {
        fbo.bind(); // 1. Open the Pixel-Art Canvas

        // 2. Clear the FBO with your deep space background color
        glClearColor(0.05f, 0.05f, 0.05f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        // ==========================================
        // LAYER 1: THE BACKGROUND (Stars)
        // ==========================================
        glDepthMask(false);
        scene.getStarfield().render(shaderStarfield, camera.getViewMatrix(), camera.getProjectionMatrix());
        glDepthMask(true);

        // ==========================================
        // LAYER 2: THE MIDGROUND (Planets & Masking)
        // ==========================================
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);

        // Tell the stencil buffer: "Write a 1 to the mask for EVERY planet pixel we draw"
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilMask(0xFF);

        shader3D.bind();
        shader3D.setUniform("view", camera.getViewMatrix());
        shader3D.setUniform("projection", camera.getProjectionMatrix());
        shader3D.setUniform("playerPos", camera.getPosition());

        scene.render(shader3D); // Draws all planets
        shader3D.unbind();

        // ==========================================
        // LAYER 3: THE FOREGROUND (Outlines)
        // ==========================================
        CelestialBody selected = scene.getSelectedObject();
        if (selected != null) {
            shaderOutline.bind();
            shaderOutline.setUniform("view", camera.getViewMatrix());
            shaderOutline.setUniform("projection", camera.getProjectionMatrix());

            // Tell the stencil buffer: "Only draw the cyan pixel if the mask is NOT 1"
            glStencilFunc(GL_NOTEQUAL, 1, 0xFF);
            glStencilMask(0x00); // Lock the mask from being overwritten
            glDisable(GL_DEPTH_TEST); // Ensure outline draws over everything

            Matrix4f shellMatrix = new Matrix4f(selected.getModelMatrix());
            shellMatrix.scale(1.05f); // Outline thickness

            shaderOutline.setUniform("model", shellMatrix);
            shaderOutline.setUniform("outlineColor", new Vector3f(0.0f, 1.0f, 1.0f));

            selected.getMesh().render();

            // Cleanup Outline State
            glEnable(GL_DEPTH_TEST);
            shaderOutline.unbind();
        }

        // Cleanup Global State
        glStencilMask(0xFF);
        glStencilFunc(GL_ALWAYS, 0, 0xFF);
        glDisable(GL_STENCIL_TEST);

        Vector2i screenSize = Window.getWindowSize(windowHandle);
        fbo.unbind(screenSize.x, screenSize.y); // Close the Canvas
    }

    private void renderUpscalePass(long windowHandle) {
        glDisable(GL_DEPTH_TEST);

        shaderPixelArt.bind();
        glBindTexture(GL_TEXTURE_2D, fbo.textureId);
        screenQuad.render();
        shaderPixelArt.unbind();
    }

    public void cleanup() {
        shader3D.cleanup();
        shaderPixelArt.cleanup();
        shaderOutline.cleanup();
        shaderStarfield.cleanup();
        fbo.cleanup();
        screenQuad.cleanup();
    }
}
