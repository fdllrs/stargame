package engine.graphics;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.*;

public class Mesh {

    private final float[] vertices;
    private final int[] indices;
    private final float[] normals;
    private final int vaoId;
    private final int vboId;
    private final int eboId;
    private final int normalVboId;



    public Mesh(float[] vertexArray, int[] indicesArray, float[] normalsArray, float[] uvsArray) {
        vertices = vertexArray;
        indices = indicesArray;
        normals = normalsArray;

        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        FloatBuffer normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
        FloatBuffer uvsBuffer = MemoryUtil.memAllocFloat(uvsArray.length);
        try {
            verticesBuffer.put(vertices).flip();
            indicesBuffer.put(indices).flip();
            normalsBuffer.put(normals).flip();
            uvsBuffer.put(uvsArray).flip();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(0);

            eboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            normalVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(1);

            int uvVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, uvVboId);
            glBufferData(GL_ARRAY_BUFFER, uvsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(2);

            glBindVertexArray(0);
        } catch (Exception e) {
            cleanup();
            throw e;
        } finally {
            MemoryUtil.memFree(verticesBuffer);
            MemoryUtil.memFree(indicesBuffer);
            MemoryUtil.memFree(normalsBuffer);
            MemoryUtil.memFree(uvsBuffer);
        }
    }

    public int vertexCount() {
        return indices.length;
    }

    public void render() {

        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount(), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteBuffers(eboId);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(normalVboId);
    }

}
