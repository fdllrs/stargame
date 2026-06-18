#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D uiTexture;
uniform vec4 uiColor;
uniform int useTexture;

void main() {

    if (useTexture == 1) {
        FragColor = texture(uiTexture, TexCoords) * uiColor;
    } else {
        FragColor = uiColor;
    }

    if (FragColor.a < 0.1) {
        discard;
    }
}