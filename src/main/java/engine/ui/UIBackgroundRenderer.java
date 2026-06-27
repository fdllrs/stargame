package engine.ui;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class UIBackgroundRenderer {

	private static final Matrix4f tempTopEdge = new Matrix4f();
	private static final Matrix4f tempBottomEdge = new Matrix4f();
	private static final Matrix4f tempLeftEdge = new Matrix4f();
	private static final Matrix4f tempRightEdge = new Matrix4f();
	private static final Matrix4f tempInnerMatrix = new Matrix4f();
	private static final Vector4f tempBorderColor = new Vector4f();
	private static final Vector4f tempInnerColor = new Vector4f();

	public static void renderFuturisticBackground(UIElement element,
			ShaderProgram shader,
			Mesh uiQuad,
			float outlineGap) {
		Vector4f baseColor = element.getColor();

		tempBorderColor.set(Math.min(1.0f, baseColor.x * 1.5f),
							Math.min(1.0f, baseColor.y * 1.5f),
							Math.min(1.0f, baseColor.z * 1.5f),
							baseColor.w);
		shader.setUniform("uiColor", tempBorderColor);

		Matrix4f modelMatrix = element.modelMatrix;
		float currentScaledWidth = modelMatrix.m00();
		float currentScaledHeight = modelMatrix.m11();
		float borderWidth = 2.0f;

		float bx = borderWidth / currentScaledWidth;
		float by = borderWidth / currentScaledHeight;

		tempTopEdge.set(modelMatrix);
		tempTopEdge.translate(0, 0, 0);
		tempTopEdge.scale(1.0f, by, 1.0f);
		shader.setUniform("model", tempTopEdge);
		uiQuad.render();

		tempBottomEdge.set(modelMatrix);
		tempBottomEdge.translate(0, 1.0f - by, 0);
		tempBottomEdge.scale(1.0f, by, 1.0f);
		shader.setUniform("model", tempBottomEdge);
		uiQuad.render();

		tempLeftEdge.set(modelMatrix);
		tempLeftEdge.translate(0, by, 0);
		tempLeftEdge.scale(bx, 1.0f - 2 * by, 1.0f);
		shader.setUniform("model", tempLeftEdge);
		uiQuad.render();

		tempRightEdge.set(modelMatrix);
		tempRightEdge.translate(1.0f - bx, by, 0);
		tempRightEdge.scale(bx, 1.0f - 2 * by, 1.0f);
		shader.setUniform("model", tempRightEdge);
		uiQuad.render();

		tempInnerColor.set(baseColor.x * 0.2f,
						   baseColor.y * 0.2f,
						   baseColor.z * 0.2f,
						   baseColor.w * 0.85f);
		shader.setUniform("uiColor", tempInnerColor);

		float offset = borderWidth + outlineGap;
		float dx = offset / currentScaledWidth;
		float dy = offset / currentScaledHeight;
		float sx = ( currentScaledWidth - 2 * offset ) / currentScaledWidth;
		float sy = ( currentScaledHeight - 2 * offset ) / currentScaledHeight;

		tempInnerMatrix.set(modelMatrix);
		tempInnerMatrix.translate(dx, dy, 0);
		tempInnerMatrix.scale(sx, sy, 1.0f);

		shader.setUniform("model", tempInnerMatrix);
		uiQuad.render();
	}
}
