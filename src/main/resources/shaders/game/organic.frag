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

    float oceanLevel = 0.45;
    float beachLevel = 0.48;
    float mountainLevel = 0.75;

    float specular = 0.0;
    float shininess = 16.0;
    vec3 objectColor;

    // 1. Water/Oceans
    if (terrainHeight < oceanLevel) {
        vec3 deepWater = colorA * 0.4;
        vec3 shallowWater = mix(colorA, vec3(0.0, 0.8, 0.7), 0.25);
        objectColor = mix(deepWater, shallowWater, smoothstep(0.2, oceanLevel, terrainHeight));
        specular = 0.4;
        shininess = 16.0;
    }
    // 2. Sandy Beaches
    else if (terrainHeight < beachLevel) {
        vec3 sand = vec3(0.85, 0.8, 0.65);
        specular = 0.02;
        objectColor = mix(sand, colorB, smoothstep(oceanLevel, beachLevel, terrainHeight));
    }
    // 3. Forests & Grassland
    else {
        vec3 forest = colorB;
        vec3 mountainBase = mix(colorB, vec3(0.35, 0.28, 0.22), 0.6);
        objectColor = mix(forest, mountainBase, smoothstep(beachLevel, mountainLevel, terrainHeight));
    }

    vec3 normal = normalize(surfaceNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    float brightness = max(dot(normal, lightDir), 0.0);
    vec3 ambient = AMBIENT_STRENGTH * objectColor;

    float shadow = calculateShadow(fragPosLightSpace, 0.005, 0.0005);
    vec3 diffuse = (1.0 - shadow) * lightColor * brightness * objectColor;

    // Specular Highlight
    vec3 specHighlight = vec3(0.0);
    if (specular > 0.0 && shadow < 0.5) {
        vec3 viewDir = normalize(viewPos - worldPosition);
        vec3 reflectDir = reflect(-lightDir, normal);
        float specFactor = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
        specHighlight = specular * specFactor * lightColor;
    }

    // Atmospheric outer glow (Organic lush cyan/blue)
    vec3 viewDir = normalize(viewPos - worldPosition);
    float rim = 1.0 - max(dot(viewDir, normal), 0.0);
    rim = pow(rim, 4.0); // sharp edge glow

    vec3 glowColor = vec3(0.4, 0.7, 1.0) * 0.15;
    float dayFactor = max(dot(normal, lightDir), 0.0);
    float distToCenter = length(viewPos - (worldPosition - localPosition));
    float glowFade = smoothstep(radius * 1.05, radius * 1.8, distToCenter);
    vec3 glow = glowColor * rim * (0.2 + 0.8 * dayFactor) * lightColor * glowFade;

    vec3 lit = ambient + diffuse + specHighlight + glow + VertexEmissive;

    outColor = vec4(ditheredBand(lit.r), ditheredBand(lit.g), ditheredBand(lit.b), 1.0);
}
