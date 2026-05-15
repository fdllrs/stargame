package engine.graphics;

import engine.utils.FileUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20C.*;

public class ShaderProgram {

    private final int programId;
    private final int vertexId;
    private final int fragmentId;
    public ShaderProgram(String vertexPath, String fragmentPath) throws Exception {
        programId = glCreateProgram();

        vertexId = compileShader(vertexPath, GL_VERTEX_SHADER);
        fragmentId = compileShader(fragmentPath, GL_FRAGMENT_SHADER);

        glAttachShader(programId, vertexId);
        glAttachShader(programId, fragmentId);
        glLinkProgram(programId);
        glValidateProgram(programId);
        glDetachShader(programId, vertexId);
        glDetachShader(programId, fragmentId);
        glDeleteShader(vertexId);
        glDeleteShader(fragmentId);

    }

    public static ShaderProgram initShader(String vertexPath, String fragmentPath) {
        try {
            return new ShaderProgram("/shaders/" + vertexPath, "/shaders/" + fragmentPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize shader", e);
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        glDeleteProgram(programId);
    }

    private int compileShader(String shaderPath, int shaderType) throws Exception {
        String vertexShader = loadShader(shaderPath);
        int shaderId = glCreateShader(shaderType);
        glShaderSource(shaderId, vertexShader);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String info = glGetShaderInfoLog(shaderId, glGetShaderi(shaderId, GL_INFO_LOG_LENGTH));
            throw new RuntimeException("shader compilation failed: " + info);
        }
        return shaderId;
    }

    private String loadShader(String shaderPath) throws IOException {

        FileUtils utils = new FileUtils();
        return utils.readFile(shaderPath);
    }

    private static void assertValidLocation(String uniformName, int location) {
        if (location == -1) {
            System.err.println("Uniform not found: " + uniformName);
        }
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(glGetUniformLocation(programId, uniformName), value);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        int location = glGetUniformLocation(programId, uniformName);
        assertValidLocation(uniformName, location);


        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);

            value.get(buffer);

            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void setUniform(String uniformName, Vector3f value) {
        int location = glGetUniformLocation(programId, uniformName);
        assertValidLocation(uniformName, location);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);

            value.get(buffer);
            glUniform3f(location, value.x, value.y, value.z);

        }
    }

    public void setUniform(String uniformName, Float value) {
        int location = glGetUniformLocation(programId, uniformName);
        assertValidLocation(uniformName, location);

        glUniform1f(location, value);

    }

    public void setUniform(String uniformName, Vector4f value) {
        int location = glGetUniformLocation(programId, uniformName);
        assertValidLocation(uniformName, location);

        glUniform4f(location, value.x, value.y, value.z, value.w);
    }
}
