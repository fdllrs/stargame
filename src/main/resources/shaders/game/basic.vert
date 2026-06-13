#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoord;
layout (location = 3) in vec3 color;
layout (location = 4) in vec3 aEmissive;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat3 normalMatrix;
uniform mat4 lightSpaceMatrix;

out vec3 surfaceNormal;
out vec3 worldPosition;
out vec3 localPosition;
out vec3 VertexColor;
out vec3 VertexEmissive;
out vec2 TexCoords;
out vec4 fragPosLightSpace;

void main() {
    surfaceNormal = normalMatrix * normal;
    localPosition = position;
    worldPosition = vec3(model * vec4(position, 1.0));
    VertexColor = color;
    VertexEmissive = aEmissive;
    TexCoords = texCoord;
    fragPosLightSpace = lightSpaceMatrix * vec4(worldPosition, 1.0);
    gl_Position = projection * view * vec4(worldPosition, 1.0);
}