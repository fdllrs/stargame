#version 330 core

out vec4 outColor;

uniform vec3 colorA;
uniform vec3 colorB;
uniform float noiseScale;

in vec3 localPosition;

float hash(vec3 p) {
    return fract(sin(dot(p, vec3(12.9898, 78.233, 45.164))) * 43758.5453);
}

float noise(vec3 x) {
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);

    return mix(
    mix(mix(hash(p + vec3(0, 0, 0)), hash(p + vec3(1, 0, 0)), f.x),
    mix(hash(p + vec3(0, 1, 0)), hash(p + vec3(1, 1, 0)), f.x), f.y),
    mix(mix(hash(p + vec3(0, 0, 1)), hash(p + vec3(1, 0, 1)), f.x),
    mix(hash(p + vec3(0, 1, 1)), hash(p + vec3(1, 1, 1)), f.x), f.y), f.z);
}

float fbm(vec3 p) {
    float v = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        v += amplitude * noise(p);
        p *= 2.0;
        amplitude *= 0.5;
    }
    return v;
}

void main() {
    vec3 sphereCoord = normalize(localPosition);
    float terrainHeight = fbm(sphereCoord * noiseScale);
    vec3 objectColor;
    if (terrainHeight < 0.45) {
        objectColor = colorA;
    } else {
        objectColor = colorB;
    }
    outColor = vec4(objectColor, 1.0);
}
