package game.objects;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11C;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_FILL;
import static org.lwjgl.opengl.GL11C.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11C.glLineWidth;
import static org.lwjgl.opengl.GL11C.glPolygonMode;

public class CelestialBody extends GameObject{

    protected boolean isSelected = false;



    public CelestialBody(Mesh mesh, Vector3f color, Vector3f position){
        super(mesh, color, position);

    }

    public void render(ShaderProgram shader){

        if (isSelected) {
            GL11C.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glLineWidth(2.0f);
            Matrix4f shellMatrix = new Matrix4f(modelMatrix);
            shellMatrix.scale(1.05f); // 5% larger than the planet

            // 4. Force the shader to draw it as a pure, bright color (e.g., Cyan)
            shader.setUniform("model", shellMatrix);
            shader.setUniform("colorA", new Vector3f(1.0f, 1.0f, 1.0f));
            shader.setUniform("colorB", new Vector3f(0.0f, 1.0f, 1.0f));

            mesh.render();

            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glLineWidth(1.0f);
        }



    }


    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
