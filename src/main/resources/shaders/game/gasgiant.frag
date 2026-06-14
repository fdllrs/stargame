#version 330 core

out vec4 outColor;

uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 colorA;
uniform vec3 colorB;

in vec3 surfaceNormal;
in vec3 worldPosition;
in vec3 localPosition;
in vec3 VertexColor;
in vec3 VertexEmissive;
in vec4 fragPosLightSpace;

uniform sampler2D shadowMap;

#include "shadows.glsl"
#include "noise.glsl"
#include "dither.glsl"

void main() {
    vec3 sphereCoord = normalize(localPosition);

    // Horizontal cloud bands wobbled by noise
    float wobble = fbm(sphereCoord * 5.0) * 0.05;
    float bandVal = sin((sphereCoord.y + wobble) * 12.0) * 0.5 + 0.5;

    vec3 col1 = colorA;
    vec3 col2 = colorB;
    vec3 col3 = mix(colorA, colorB, 0.5);
    vec3 col4 = mix(colorB, vec3(0.95, 0.85, 0.75), 0.3);

    vec3 objectColor;
    if (bandVal < 0.25) {
        objectColor = mix(col1, col2, smoothstep(0.0, 0.25, bandVal));
    } else if (bandVal < 0.5) {
        objectColor = mix(col2, col3, smoothstep(0.25, 0.5, bandVal));
    } else if (bandVal < 0.75) {
        objectColor = mix(col3, col4, smoothstep(0.5, 0.75, bandVal));
    } else {
        objectColor = mix(col4, col1, smoothstep(0.75, 1.0, bandVal));
    }

    vec3 normal = normalize(surfaceNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    float brightness = max(dot(normal, lightDir), 0.0);
    vec3 ambient = AMBIENT_STRENGTH * objectColor;

    float shadow = calculateShadow(fragPosLightSpace, 0.0015, 0.00015);
    vec3 diffuse = (1.0 - shadow) * lightColor * brightness * objectColor;

    vec3 lit = ambient + diffuse + VertexEmissive;

    outColor = vec4(ditheredBand(lit.r), ditheredBand(lit.g), ditheredBand(lit.b), 1.0);
}
