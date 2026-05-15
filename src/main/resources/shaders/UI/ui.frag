#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D uiTexture;
uniform vec4 uiColor; // A solid color for things like health bars
uniform int useTexture; // 1 if drawing an image, 0 if drawing a solid block

void main() {
    if (useTexture == 1) {
        FragColor = texture(uiTexture, TexCoords) * uiColor;
    } else {
        FragColor = uiColor;
    }

    // Discard completely transparent pixels so they don't block the 3D game
    if (FragColor.a < 0.1) {
        discard;
    }
}