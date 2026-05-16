#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 2) in vec2 aTexCoords;

uniform mat4 model;
uniform mat4 projection;

// NEW: Variables to slice the sprite sheet
uniform vec2 uvOffset;
uniform vec2 uvScale;

out vec2 TexCoords;

void main() {
    // Zoom in on the texture, then slide to the correct letter
    TexCoords = (aTexCoords * uvScale) + uvOffset;

    gl_Position = projection * model * vec4(aPos, 0.0, 1.0);
}