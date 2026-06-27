package game.objects;

import engine.graphics.ShaderProgram;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.opengl.GL32C.GL_PROGRAM_POINT_SIZE;

public class Starfield {
    private final int vaoId;
    private final int starCount;
    private final Matrix4f skyboxView = new Matrix4f();

    public Starfield(int count, float radius) {
        this.starCount = count;
        Random random = new Random();

        float[] starData = new float[count * 5];
        int pointer = 0;

        for (int i = 0; i < count; i++) {
            float u = random.nextFloat();
            float v = random.nextFloat();
            float theta = u * 2.0f * (float) Math.PI;
            float phi = (float) Math.acos(2.0f * v - 1.0f);

            float x = (float) (radius * Math.sin(phi) * Math.cos(theta));
            float y = (float) (radius * Math.sin(phi) * Math.sin(theta));
            float z = (float) (radius * Math.cos(phi));

            float size = 1.0f + random.nextFloat() * 1.5f;
            float intensity = 0.2f + random.nextFloat() * 0.8f;

            starData[pointer++] = x;
            starData[pointer++] = y;
            starData[pointer++] = z;
            starData[pointer++] = size;
            starData[pointer++] = intensity;
        }

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        int vboId = glGenBuffers();
        FloatBuffer buffer = MemoryUtil.memAllocFloat(starData.length);
        buffer.put(starData).flip();

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        int stride = 5 * Float.BYTES;

        // Location 0: Position (3 floats)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        // Location 1: Size (1 float)
        glVertexAttribPointer(1, 1, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Location 2: Intensity (1 float)
        glVertexAttribPointer(2, 1, GL_FLOAT, false, stride, 4 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        MemoryUtil.memFree(buffer);
    }

    public void render(ShaderProgram shader,
                       Matrix4f viewMatrix,
                       Matrix4f projectionMatrix) {
        shader.bind();

        skyboxView.set(viewMatrix);
        skyboxView.m30(0);
        skyboxView.m31(0);
        skyboxView.m32(0);

        shader.setUniform("view", skyboxView);
        shader.setUniform("projection", projectionMatrix);

        // Turn on the ability for the vertex shader to change point sizes
        glEnable(GL_PROGRAM_POINT_SIZE);

        // Enable blending for the soft glowing edges
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Draw
        glBindVertexArray(vaoId);
        glDrawArrays(GL_POINTS, 0, starCount);
        glBindVertexArray(0);

        // Cleanup state
        glDisable(GL_PROGRAM_POINT_SIZE);
        glDisable(GL_BLEND);
        shader.unbind();
    }
}