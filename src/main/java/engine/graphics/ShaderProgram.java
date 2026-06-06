package engine.graphics;

import engine.utils.FileUtils;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20C.*;

public class ShaderProgram {
    private final int programId;

    public ShaderProgram(String vertexPath, String fragmentPath) {
        programId = glCreateProgram();

        int vertexId = compileShader(vertexPath, GL_VERTEX_SHADER);
        int fragmentId = compileShader(fragmentPath, GL_FRAGMENT_SHADER);

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
            return new ShaderProgram("/shaders/" + vertexPath,
                                     "/shaders/" + fragmentPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize shader", e);
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void cleanup() {
        unbind();
        glDeleteProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    private int compileShader(String shaderPath, int shaderType) {
        String vertexShader = loadShader(shaderPath);
        int shaderId = glCreateShader(shaderType);
        glShaderSource(shaderId, vertexShader);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String info = glGetShaderInfoLog(shaderId,
                                             glGetShaderi(shaderId, GL_INFO_LOG_LENGTH));
            throw new RuntimeException("shader compilation failed: " + info);
        }
        return shaderId;
    }

    private String loadShader(String shaderPath) {
        FileUtils utils = new FileUtils();
        String content = utils.readFile(shaderPath);
        return resolveIncludes(content, utils);
    }

    private String resolveIncludes(String content, FileUtils utils) {
        String[] lines = content.split("\r?\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("#include")) {
                int firstQuote = trimmed.indexOf('"');
                int lastQuote = trimmed.lastIndexOf('"');
                if (firstQuote != -1 && lastQuote != -1 && firstQuote < lastQuote) {
                    String includeFile = trimmed.substring(firstQuote + 1, lastQuote);
                    String fullPath;
                    if (includeFile.startsWith("/")) {
                        fullPath = "/shaders" + includeFile;
                    } else {
                        fullPath = "/shaders/game/" + includeFile;
                    }
                    String includeContent = utils.readFile(fullPath);
                    sb.append(resolveIncludes(includeContent, utils)).append("\n");
                }
            } else {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public void setUniform(String uniformName, Matrix3f value) {
        int location = glGetUniformLocation(programId, uniformName);
        if (location == -1)
            return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(9);
            value.get(buffer);
            glUniformMatrix3fv(location, false, buffer);
        }
    }

    public void setUniform(String uniformName, Matrix4f value) {
        int location = glGetUniformLocation(programId, uniformName);
        if (location == -1)
            return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void setUniform(String uniformName, int value) {
        int location = glGetUniformLocation(programId, uniformName);
        if (location == -1)
            return;
        glUniform1i(location, value);
    }

    public void setUniform(String uniformName, Float value) {
        int location = glGetUniformLocation(programId, uniformName);
        if (location == -1)
            return;
        glUniform1f(location, value);
    }

    public void setUniform(String uniformName, Vector2f value) {
        int location = glGetUniformLocation(programId, uniformName);
        if (location == -1)
            return;
        glUniform2f(location, value.x, value.y);
    }

    public void setUniform(String uniformName, Vector3f value) {
        int location = glGetUniformLocation(programId, uniformName);
        if (location == -1)
            return;
        glUniform3f(location, value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector4f value) {
        int location = glGetUniformLocation(programId, uniformName);
        if (location == -1)
            return;
        glUniform4f(location, value.x, value.y, value.z, value.w);
    }

}
