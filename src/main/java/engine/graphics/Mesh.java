package engine.graphics;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30C.*;

public class Mesh {

    private final int vaoId;
    private final int vertexCount;
    private final List<Integer> vboIdList;

    private Mesh(float[] positions, int[] indices, float[] normals, float[] uvs, float[] colors, float[] emissive, int dim) {
        this.vertexCount = indices.length;
        this.vboIdList = new ArrayList<>();

        this.vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        createIntBuffer(indices);
        createFloatVbo(0, dim, positions);

        if (normals != null && normals.length > 0) {
            createFloatVbo(1, 3, normals);
        }

        if (uvs != null && uvs.length > 0) {
            createFloatVbo(2, 2, uvs);
        }

        if (colors != null && colors.length > 0) {
            createFloatVbo(3, 3, colors);
        }

        if (emissive != null && emissive.length > 0) {
            createFloatVbo(4, 3, emissive);
        }

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }


    /**
     * Builds a standard 3D model (requires Normals for lighting).
     */
    public static Mesh create3D(float[] positions, int[] indices, float[] normals, float[] uvs) {
        return new Mesh(positions, indices, normals, uvs, null, null, 3);
    }

    /**
     * Builds a standard 3D model with custom vertex colors.
     */
    public static Mesh create3D(float[] positions, int[] indices, float[] normals, float[] uvs, float[] colors) {
        return new Mesh(positions, indices, normals, uvs, colors, null, 3);
    }

    /**
     * Builds a standard 3D model with custom vertex colors and emissive properties.
     */
    public static Mesh create3D(float[] positions, int[] indices, float[] normals, float[] uvs, float[] colors, float[] emissive) {
        return new Mesh(positions, indices, normals, uvs, colors, emissive, 3);
    }

    /**
     * Builds a flat 2D UI element (no Normals required, Z-axis ignored).
     */
    public static Mesh create2DUI(float[] positions, int[] indices, float[] uvs) {
        // Pass null for normals, colors, and emissive, and 2 for dimensions
        return new Mesh(positions, indices, null, uvs, null, null, 2);
    }

    private void createFloatVbo(int attribute, int size, float[] data) {
        int vboId = glGenBuffers();
        vboIdList.add(vboId);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        try {
            buffer.put(data).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            glVertexAttribPointer(attribute, size, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(attribute);
        } finally {
            MemoryUtil.memFree(buffer);
        }
    }

    private void createIntBuffer(int[] data) {
        int eboId = glGenBuffers();
        vboIdList.add(eboId);

        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        try {
            buffer.put(data).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        } finally {
            MemoryUtil.memFree(buffer);
        }
    }

    // --- 4. ENGINE METHODS ---

    public void render() {
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);
        glBindVertexArray(0);

        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }
        glDeleteVertexArrays(vaoId);
    }
}