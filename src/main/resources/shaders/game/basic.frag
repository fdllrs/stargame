#version 330 core

out vec4 outColor;

uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 colorA;
uniform vec3 colorB;
uniform int useVertexColor;
uniform int useTexture;
uniform int useEmissiveMap;

in vec3 surfaceNormal;
in vec3 worldPosition;
in vec3 VertexColor;
in vec3 VertexEmissive;
in vec2 TexCoords;
in vec4 fragPosLightSpace;

uniform sampler2D shadowMap;
uniform sampler2D diffuseMap;
uniform sampler2D emissiveMap;

#include "shadows.glsl"
#include "dither.glsl"

void main() {
    vec3 objectColor;
    float extraAmbient = 0.0;

    if (useTexture != 0) {
        objectColor = texture(diffuseMap, TexCoords).rgb;
        extraAmbient = 0.5;
    } else if (useVertexColor != 0) {
        objectColor = VertexColor;
        extraAmbient = 0.3;
    } else {
        objectColor = mix(colorA, colorB, 0.5);
    }

    vec3 normal = normalize(surfaceNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    float brightness = max(dot(normal, lightDir), 0.0);

    float ambientStrength = (useTexture != 0) ? 0.15 : (AMBIENT_STRENGTH + extraAmbient);
    vec3 ambient = ambientStrength * objectColor;

    float biasScale = (useVertexColor != 0 || useTexture != 0) ? 0.0015 : 0.0025;
    float biasMin = (useVertexColor != 0 || useTexture != 0) ? 0.00015 : 0.00025;
    float shadow = calculateShadow(fragPosLightSpace, biasScale, biasMin);

    float diffuseCoeff = (useTexture != 0) ? 0.75 : 1.0;
    vec3 diffuse = diffuseCoeff * (1.0 - shadow) * lightColor * brightness * objectColor;

    vec3 emissiveColor = VertexEmissive;
    if (useEmissiveMap != 0) {
        emissiveColor = texture(emissiveMap, TexCoords).rgb;
    }

    vec3 lit = ambient + diffuse + emissiveColor;

    outColor = vec4(ditheredBand(lit.r), ditheredBand(lit.g), ditheredBand(lit.b), 1.0);
}