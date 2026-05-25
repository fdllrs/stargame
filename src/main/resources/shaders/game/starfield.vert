#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in float aSize;
layout (location = 2) in float aIntensity;

uniform mat4 projection;
uniform mat4 view;

out float vIntensity;

void main() {
    vIntensity = aIntensity;

    vec4 pos = projection * view * vec4(aPos, 1.0);
    gl_Position = pos;

    gl_PointSize = aSize;
}