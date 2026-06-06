#version 330 core

out vec4 outColor;

uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 colorA;
uniform vec3 colorB;
uniform int useVertexColor;

in vec3 surfaceNormal;
in vec3 worldPosition;
in vec3 VertexColor;
in vec3 VertexEmissive;
in vec4 fragPosLightSpace;

uniform sampler2D shadowMap;

#include "shadows.glsl"
#include "dither.glsl"

void main() {
    vec3 objectColor;
    float extraAmbient = 0.0;

    if (useVertexColor != 0) {
        objectColor  = VertexColor;
        extraAmbient = 0.3;
    } else {
        objectColor = mix(colorA, colorB, 0.5);
    }

    vec3 normal = normalize(surfaceNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    float brightness = max(dot(normal, lightDir), 0.0);
    vec3 ambient = (AMBIENT_STRENGTH + extraAmbient) * objectColor;

    float biasScale = (useVertexColor != 0) ? 0.0005 : 0.005;
    float biasMin   = (useVertexColor != 0) ? 0.00005 : 0.0005;
    float shadow = calculateShadow(fragPosLightSpace, biasScale, biasMin);

    vec3 diffuse = (1.0 - shadow) * lightColor * brightness * objectColor;

    vec3 lit = ambient + diffuse + VertexEmissive;

    outColor = vec4(ditheredBand(lit.r), ditheredBand(lit.g), ditheredBand(lit.b), 1.0);
}