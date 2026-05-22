#version 330 core

// We ONLY need the position (Location 0).
// We completely ignore Normals (Location 1) and UVs (Location 2).
layout (location = 0) in vec3 aPos;

// The standard 3D camera matrices
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    // Multiply the matrices in reverse order: Projection * View * Model
    gl_Position = projection * view * model * vec4(aPos, 1.0);
}