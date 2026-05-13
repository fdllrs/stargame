#version 330 core
out vec4 FragColor;

uniform vec3 objectColor;
uniform vec3 lightPos;
uniform bool isLightSource;

in vec3 Normal;
in vec3 FragPos;

void main() {
    if (isLightSource) {
        // The sun doesn't have shadows, it just glows purely
        FragColor = vec4(objectColor, 0.1);
    } else {
        // 1. Point Light Direction: Vector pointing from the pixel TO the light
        vec3 lightDir = normalize(lightPos - FragPos);

        // 2. Diffuse Math
        vec3 norm = normalize(Normal);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = diff * objectColor;

        FragColor = vec4(diffuse, 1.0);
    }
}