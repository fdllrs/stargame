package engine.graphics;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class Texture {
	int width, height;
	int textureID;

	public Texture(String path) {
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			stbi_set_flip_vertically_on_load(true);
			ByteBuffer image = STBImage.stbi_load(Path.of(path).toString(), w, h, comp, 4);
			if (image == null) {
				throw new RuntimeException(
						"Failed to load texture: " + STBImage.stbi_failure_reason());
			}
			width = w.get();
			height = h.get();
			glTexImage2D(GL_TEXTURE_2D,
						 0,
						 GL_RGBA,
						 width,
						 height,
						 0,
						 GL_RGBA,
						 GL_UNSIGNED_BYTE,
						 image);

			stbi_image_free(image);
		}

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void bind() {
		bind(GL_TEXTURE0);
	}

	public void bind(int textureUnit) {
		glActiveTexture(textureUnit);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	}

	public void cleanup() {
		glDeleteTextures(textureID);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
}
