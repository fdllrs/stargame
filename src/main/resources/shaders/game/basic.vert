#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat3 normalMatrix; // inverse-transpose of model's 3x3, correct under non-uniform scale

out vec3 Normal;
out vec3 FragPos;
out vec3 LocalPos;

void main() {
    Normal   = normalMatrix * aNormal;
    LocalPos = aPos;
    FragPos  = vec3(model * vec4(aPos, 1.0));
    gl_Position = projection * view * model * vec4(aPos, 1.0);
}