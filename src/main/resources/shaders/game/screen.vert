#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 2) in vec2 aTexCoords; // Location 2 matches our Java code

out vec2 TexCoords; // Send to fragment shader

void main() {
    TexCoords = aTexCoords;
    // We completely bypass matrices. The quad is already perfectly sized from -1 to 1!
    gl_Position = vec4(aPos.x, aPos.y, 0.0, 1.0);
}