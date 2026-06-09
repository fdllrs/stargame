#version 330 core
layout (location = 0) in vec3 position;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform float time;

out vec3 localPosition;
out vec3 viewNormal;
out vec3 viewDir;

float getDisplacement(vec3 p) {
    // Multi-frequency wave formula to simulate a bubbling, dynamic solar surface
    float d = sin(p.x * 5.0 + time * 1.2) * cos(p.y * 5.0 + time * 0.8);
    d += sin(p.z * 8.0 - time * 1.5) * cos(p.x * 7.0 + time * 1.3) * 0.5;
    d += sin(p.y * 12.0 + time * 2.0) * cos(p.z * 10.0 - time * 1.0) * 0.25;
    return d * 0.015;
}

void main() {
    vec3 normal = normalize(position);
    float r = length(position);

    // Displace vertex position for organic bubbling solar flares/corona base
    vec3 displaced = position + normal * getDisplacement(normal) * r;

    localPosition = displaced;
    vec4 viewPos4 = view * model * vec4(displaced, 1.0);
    viewDir = normalize(-viewPos4.xyz);

    // Normal in view space
    mat3 normalMatrix = transpose(inverse(mat3(view * model)));
    viewNormal = normalize(normalMatrix * normal);

    gl_Position = projection * viewPos4;
}

