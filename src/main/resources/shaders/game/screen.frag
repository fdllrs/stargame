#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

// sampler2D is a special GLSL type for textures
uniform sampler2D screenTexture;

void main() {
    // Look up the exact color of the texture at this U,V coordinate
    FragColor = texture(screenTexture, TexCoords);
}