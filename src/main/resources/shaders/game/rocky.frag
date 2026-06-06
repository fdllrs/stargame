#version 330 core

out vec4 outColor;

uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 colorA;
uniform vec3 colorB;
uniform float radius;
uniform vec3 viewPos;

in vec3 surfaceNormal;
in vec3 worldPosition;
in vec3 localPosition;
in vec3 VertexColor;
in vec3 VertexEmissive;
in vec4 fragPosLightSpace;

uniform sampler2D shadowMap;

#include "shadows.glsl"
#include "dither.glsl"

void main() {
    float terrainHeight = VertexColor.r;

    vec3 colDeep = colorA * 0.4; // Dark valleys
    vec3 colMid = colorA;       // Flatlands
    vec3 colHigh = colorB;      // Highlands
    vec3 colPeak = mix(colorB, vec3(0.9, 0.85, 0.8), 0.4); // Rocky peaks

    vec3 objectColor;
    if (terrainHeight < 0.4) {
        objectColor = mix(colDeep, colMid, smoothstep(0.2, 0.4, terrainHeight));
    } else if (terrainHeight < 0.7) {
        objectColor = mix(colMid, colHigh, smoothstep(0.4, 0.7, terrainHeight));
    } else {
        objectColor = mix(colHigh, colPeak, smoothstep(0.7, 0.9, terrainHeight));
    }

    vec3 normal = normalize(surfaceNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    float brightness = max(dot(normal, lightDir), 0.0);
    vec3 ambient = AMBIENT_STRENGTH * objectColor;

    float shadow = calculateShadow(fragPosLightSpace, 0.005, 0.0005);
    vec3 diffuse = (1.0 - shadow) * lightColor * brightness * objectColor;

    // Atmospheric outer glow (Rocky dusty orange)
    vec3 viewDir = normalize(viewPos - worldPosition);
    float rim = 1.0 - max(dot(viewDir, normal), 0.0);
    rim = pow(rim, 4.0); // sharp edge glow

    vec3 glowColor = vec3(0.5, 0.3, 0.2) * 0.12;
    float dayFactor = max(dot(normal, lightDir), 0.0);
    float distToCenter = length(viewPos - (worldPosition - localPosition));
    float glowFade = smoothstep(radius * 1.05, radius * 1.8, distToCenter);
    vec3 glow = glowColor * rim * (0.2 + 0.8 * dayFactor) * lightColor * glowFade;

    vec3 lit = ambient + diffuse + glow + VertexEmissive;

    outColor = vec4(ditheredBand(lit.r), ditheredBand(lit.g), ditheredBand(lit.b), 1.0);
}
