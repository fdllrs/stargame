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
#include "noise.glsl"
#include "dither.glsl"

void main() {
    vec3 sphereCoord = normalize(localPosition);

    // Smooth bands
    float wobble = fbm(sphereCoord * 3.0) * 0.03;
    float bandVal = sin((sphereCoord.y + wobble) * 6.0) * 0.5 + 0.5;

    vec3 col1 = colorA;
    vec3 col2 = colorB;
    vec3 colCloud = mix(colorA, vec3(0.95, 0.98, 1.0), 0.5);

    vec3 objectColor = mix(mix(col1, col2, bandVal), colCloud, fbm(sphereCoord * 8.0) * 0.3);
    float specular = 0.8; // Ice exhibits a subtle shininess
    float shininess = 32.0;

    vec3 normal = normalize(surfaceNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    float brightness = max(dot(normal, lightDir), 0.0);
    vec3 ambient = AMBIENT_STRENGTH * objectColor;

    float shadow = calculateShadow(fragPosLightSpace, 0.005, 0.0005);
    vec3 diffuse = (1.0 - shadow) * lightColor * brightness * objectColor;

    // Specular Highlight
    vec3 specHighlight = vec3(0.0);
    if (shadow < 0.5) {
        vec3 viewDir = normalize(viewPos - worldPosition);
        vec3 reflectDir = reflect(-lightDir, normal);
        float specFactor = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
        specHighlight = specular * specFactor * lightColor;
    }

    // Atmospheric outer glow (Ice Giant soft blue)
    vec3 viewDir = normalize(viewPos - worldPosition);
    float rim = 1.0 - max(dot(viewDir, normal), 0.0);
    rim = pow(rim, 4.0); // sharp edge glow

    vec3 glowColor = vec3(0.4, 0.6, 0.9) * 0.12;
    float dayFactor = max(dot(normal, lightDir), 0.0);
    float distToCenter = length(viewPos - (worldPosition - localPosition));
    float glowFade = smoothstep(radius * 1.05, radius * 1.8, distToCenter);
    vec3 glow = glowColor * rim * (0.2 + 0.8 * dayFactor) * lightColor * glowFade;

    vec3 lit = ambient + diffuse + specHighlight + glow + VertexEmissive;

    outColor = vec4(ditheredBand(lit.r), ditheredBand(lit.g), ditheredBand(lit.b), 1.0);
}
