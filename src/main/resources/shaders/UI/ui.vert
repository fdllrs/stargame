#version 330 core
layout (location = 0) in vec2 aPos; // We only need X and Y for 2D!
layout (location = 1) in vec2 aTexCoords;

uniform mat4 model;      // Where is the UI element on screen?
uniform mat4 projection; // Our new Orthographic matrix

out vec2 TexCoords;

void main() {
    TexCoords = aTexCoords;
    // Z is forced to 0.0, W is 1.0
    gl_Position = projection * model * vec4(aPos, 0.0, 1.0);
}