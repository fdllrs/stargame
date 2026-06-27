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
	private boolean hasVertexColors = false;
	private Texture diffuseTexture;
	private Texture emissiveTexture;

	private Mesh(float[] positions,
			int[] indices,
			float[] normals,
			float[] uvs,
			float[] colors,
			float[] emissive,
			int dim) {

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
			this.hasVertexColors = true;
		}

		if (emissive != null && emissive.length > 0) {
			createFloatVbo(4, 3, emissive);
		}

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
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

	public static Mesh create2DUI(float[] positions, int[] indices, float[] uvs) {
		// Pass null for normals, colors, and emissive, and 2 for dimensions
		return new Mesh(positions, indices, null, uvs, null, null, 2);
	}

	public static Mesh create3D(float[] positions,
			int[] indices,
			float[] normals,
			float[] uvs,
			float[] colors,
			float[] emissive) {
		return new Mesh(positions, indices, normals, uvs, colors, emissive, 3);
	}

	public static Mesh create3D(float[] positions, int[] indices, float[] normals, float[] uvs) {
		return new Mesh(positions, indices, normals, uvs, null, null, 3);
	}

	public static Mesh create3D(float[] positions,
			int[] indices,
			float[] normals,
			float[] uvs,
			float[] colors) {
		return new Mesh(positions, indices, normals, uvs, colors, null, 3);
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

		if (diffuseTexture != null) {
			diffuseTexture.cleanup();
		}
		if (emissiveTexture != null) {
			emissiveTexture.cleanup();
		}
	}

	public void setDiffuseTexture(Texture texture) {
		this.diffuseTexture = texture;
	}

	public void setEmissiveTexture(Texture texture) {
		this.emissiveTexture = texture;
	}

	public boolean hasVertexColors() {
		return hasVertexColors;
	}

	public void render() {
		glBindVertexArray(vaoId);
		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	public void render(ShaderProgram shader) {
		boolean hasDiffuse = ( diffuseTexture != null );
		boolean hasEmissive = ( emissiveTexture != null );

		if (hasDiffuse) {
			diffuseTexture.bind(org.lwjgl.opengl.GL13C.GL_TEXTURE0);
			shader.setUniform("useTexture", 1);
			shader.setUniform("diffuseMap", 0);
		}
		else {
			shader.setUniform("useTexture", 0);
		}

		if (hasEmissive) {
			emissiveTexture.bind(org.lwjgl.opengl.GL13C.GL_TEXTURE2);
			shader.setUniform("useEmissiveMap", 1);
			shader.setUniform("emissiveMap", 2);
		}
		else {
			shader.setUniform("useEmissiveMap", 0);
		}

		glBindVertexArray(vaoId);
		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);

		if (hasDiffuse) {
			diffuseTexture.unbind();
		}
		if (hasEmissive) {
			emissiveTexture.unbind();
		}
	}
}