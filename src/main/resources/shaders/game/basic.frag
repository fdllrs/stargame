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

float calculateShadow(vec4 fragPosLS) {
    vec3 projCoords = fragPosLS.xyz / fragPosLS.w;
    projCoords = projCoords * 0.5 + 0.5;

    if (projCoords.z > 1.0) {
        return 0.0;
    }

    float closestDepth = texture(shadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;

    vec3 normal = normalize(surfaceNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    float biasScale = (useVertexColor != 0) ? 0.0005 : 0.005;
    float biasMin   = (useVertexColor != 0) ? 0.00005 : 0.0005;
    float bias = max(biasScale * (1.0 - dot(normal, lightDir)), biasMin);

    // PCF (Percentage-Closer Filtering) for smoother edges:
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for (int x = -1; x <= 1; ++x) {
        for (int y = -1; y <= 1; ++y) {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
        }
    }
    shadow /= 9.0;

    return shadow;
}

const float AMBIENT_STRENGTH = 0.18;
const float BANDS = 5.0;

const float BAYER_4X4[16] = float[](
    0.0 / 16.0, 8.0 / 16.0, 2.0 / 16.0, 10.0 / 16.0,
    12.0 / 16.0, 4.0 / 16.0, 14.0 / 16.0, 6.0 / 16.0,
    3.0 / 16.0, 11.0 / 16.0, 1.0 / 16.0, 9.0 / 16.0,
    15.0 / 16.0, 7.0 / 16.0, 13.0 / 16.0, 5.0 / 16.0
);

// --- Dithered color quantization ---
float ditheredBand(float value) {
    int x = int(gl_FragCoord.x) % 4;
    int y = int(gl_FragCoord.y) % 4;
    float dither = BAYER_4X4[y * 4 + x] - 0.5;
    return clamp(floor((value + dither * 0.5 / BANDS) * BANDS) / BANDS, 0.0, 1.0);
}

void main() {
    vec3 objectColor;
    float extraAmbient = 0.0;

    if (useVertexColor != 0) {
        objectColor  = VertexColor;
        extraAmbient = 0.05;
    } else {
        objectColor = mix(colorA, colorB, 0.5);
    }

    vec3 normal = normalize(surfaceNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    float brightness = max(dot(normal, lightDir), 0.0);
    vec3 ambient = (AMBIENT_STRENGTH + extraAmbient) * objectColor;

    float shadow = calculateShadow(fragPosLightSpace);
    vec3 diffuse = (1.0 - shadow) * lightColor * brightness * objectColor;

    vec3 lit = ambient + diffuse + VertexEmissive;

    outColor = vec4(ditheredBand(lit.r), ditheredBand(lit.g), ditheredBand(lit.b), 1.0);
}