#version 330 core

out vec4 outColor;

uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 colorA;
uniform vec3 colorB;
uniform float noiseScale;
uniform int useVertexColor;

in vec3 surfaceNormal;
in vec3 worldPosition;
in vec3 localPosition;
in vec3 VertexColor;
in vec3 VertexEmissive;
in vec4 fragPosLightSpace;

uniform sampler2D shadowMap;

float calculateShadow(vec4 fragPosLS) {
    // perform perspective divide
    vec3 projCoords = fragPosLS.xyz / fragPosLS.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;
    
    // If outside shadow map frustum, no shadow
    if (projCoords.z > 1.0) {
        return 0.0;
    }

    // get closest depth value from light's perspective
    float closestDepth = texture(shadowMap, projCoords.xy).r; 
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;

    // calculate bias (based on depth map resolution and slope)
    vec3 normal = normalize(surfaceNormal);
    vec3 lightDir = normalize(lightPosition - worldPosition);
    float biasScale = (useVertexColor != 0) ? 0.0005 : 0.005;
    float biasMin   = (useVertexColor != 0) ? 0.00005 : 0.0005;
    float bias = max(biasScale * (1.0 - dot(normal, lightDir)), biasMin);

    // PCF (Percentage-Closer Filtering) for smoother edges:
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for(int x = -1; x <= 1; ++x) {
        for(int y = -1; y <= 1; ++y) {
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
     0.0 / 16.0,  8.0 / 16.0,  2.0 / 16.0, 10.0 / 16.0,
    12.0 / 16.0,  4.0 / 16.0, 14.0 / 16.0,  6.0 / 16.0,
     3.0 / 16.0, 11.0 / 16.0,  1.0 / 16.0,  9.0 / 16.0,
    15.0 / 16.0,  7.0 / 16.0, 13.0 / 16.0,  5.0 / 16.0
);

// --- Noise helpers for planet terrain (fbm) ---
float hash(vec3 p) {
    return fract(sin(dot(p, vec3(12.9898, 78.233, 45.164))) * 43758.5453);
}

float noise(vec3 x) {
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(mix(hash(p), hash(p + vec3(1,0,0)), f.x), mix(hash(p + vec3(0,1,0)), hash(p + vec3(1,1,0)), f.x), f.y),
        mix(mix(hash(p + vec3(0,0,1)), hash(p + vec3(1,0,1)), f.x), mix(hash(p + vec3(0,1,1)), hash(p + vec3(1,1,1)), f.x), f.y),
        f.z);
}

float fbm(vec3 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 4; i++) { v += a * noise(p); p *= 2.0; a *= 0.5; }
    return v;
}

// --- Dithered color quantization ---
float ditheredBand(float value) {
    int x = int(gl_FragCoord.x) % 4;
    int y = int(gl_FragCoord.y) % 4;
    float dither = BAYER_4X4[y * 4 + x] - 0.5;
    return clamp(floor((value + dither * 0.5 / BANDS) * BANDS) / BANDS, 0.0, 1.0);
}

void main() {
    // Resolve base object color
    vec3 objectColor;
    float extraAmbient = 0.0;
    if (useVertexColor != 0) {
        objectColor  = VertexColor;
        extraAmbient = 0.05; // Reduced from 0.4 to allow realistic shadows and dark night-sides
    } else {
        vec3 sphereCoord  = normalize(localPosition);
        float terrainHeight = fbm(sphereCoord * noiseScale);
        objectColor = terrainHeight < 0.45 ? colorA : colorB;
    }

    // Diffuse lighting
    vec3  lightDir  = normalize(lightPosition - worldPosition);
    float brightness = max(dot(normalize(surfaceNormal), lightDir), 0.0);

    vec3 ambient = (AMBIENT_STRENGTH + extraAmbient) * objectColor;
    
    // Calculate shadow factor
    float shadow = calculateShadow(fragPosLightSpace);
    vec3 diffuse = (1.0 - shadow) * lightColor * brightness * objectColor;

    // Apply dithered banding and emissive
    vec3 lit = ambient + diffuse + VertexEmissive;
    outColor = vec4(ditheredBand(lit.r), ditheredBand(lit.g), ditheredBand(lit.b), 1.0);
}