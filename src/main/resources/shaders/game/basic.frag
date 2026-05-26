#version 330 core

out vec4 FragColor;

uniform vec3 objectColor;
uniform vec3 lightPos;
uniform bool isLightSource;
uniform vec3 colorA;      // e.g., Water / Dark Plasma
uniform vec3 colorB;      // e.g., Land / Bright Plasma
uniform float noiseScale; // How "zoomed in" the continents are
uniform vec3 playerPos;

in vec3 Normal;
in vec3 FragPos;
in vec3 LocalPos;

const float AMBIENT_STRENGTH = 0.05;
const float LIGHT_SOURCE_ALPHA = 0.1;
const float BANDS = 4.0;

const float BAYER_4X4[16] = float[](
0.0 / 16.0,  8.0 / 16.0,  2.0 / 16.0, 10.0 / 16.0,
12.0 / 16.0,  4.0 / 16.0, 14.0 / 16.0,  6.0 / 16.0,
3.0 / 16.0, 11.0 / 16.0,  1.0 / 16.0,  9.0 / 16.0,
15.0 / 16.0,  7.0 / 16.0, 13.0 / 16.0,  5.0 / 16.0
);

float hash(vec3 p) {
    return fract(sin(dot(p, vec3(12.9898, 78.233, 45.164))) * 43758.5453);
}

float noise(vec3 x) {
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f); // Smooth interpolation

    return mix(
    mix(mix(hash(p + vec3(0,0,0)), hash(p + vec3(1,0,0)), f.x),
    mix(hash(p + vec3(0,1,0)), hash(p + vec3(1,1,0)), f.x), f.y),
    mix(mix(hash(p + vec3(0,0,1)), hash(p + vec3(1,0,1)), f.x),
    mix(hash(p + vec3(0,1,1)), hash(p + vec3(1,1,1)), f.x), f.y), f.z);
}

float fbm(vec3 p) {
    float v = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) { // 4 layers of detail
        v += amplitude * noise(p);
        p *= 2.0; // Double the frequency
        amplitude *= 0.5; // Halve the impact
    }
    return v;
}

float getDiffuseStrength() {
    vec3 lightDir = normalize(lightPos - FragPos);
    vec3 normal = normalize(Normal);

    return max(dot(normal, lightDir), 0.0);
}

float getDitherValue() {
    int x = int(gl_FragCoord.x) % 4;
    int y = int(gl_FragCoord.y) % 4;

    return BAYER_4X4[y * 4 + x] - 0.5;
}

float applyDitheredBands(float value) {
    float dither = getDitherValue();

    value += (dither * 0.5) / BANDS;
    value = floor(value * BANDS) / BANDS;

    return clamp(value, 0.0, 1.0);
}

void main() {
    vec3 sphereCoord = normalize(LocalPos);
    float terrainHeight = fbm(sphereCoord * noiseScale);
    vec3 objectColor;
    if (terrainHeight < 0.45) {
        objectColor = colorA;
    } else {
        objectColor = colorB;
    }
    if (isLightSource) {
        FragColor = vec4(objectColor, LIGHT_SOURCE_ALPHA);
        return;
    }

    float sunDiffuse = getDiffuseStrength();

    // Player light calculation (Point light attached to player/camera)
    vec3 normal = normalize(Normal);
    vec3 playerLightDir = normalize(playerPos - FragPos);
    float playerDiffuse = max(dot(normal, playerLightDir), 0.3);

    float playerDist = length(playerPos - FragPos);
    float playerLightRange = 200.0;
    float attenuation = clamp(1.0 - (playerDist / playerLightRange), 0.0, 0.6);
    attenuation = attenuation * attenuation; // quadratic falloff

    float playerLightIntensity = 0.7;
    float playerLightContribution = playerDiffuse * attenuation * playerLightIntensity;

    float totalDiffuse = sunDiffuse + playerLightContribution;
    totalDiffuse = applyDitheredBands(totalDiffuse);

    vec3 ambient = AMBIENT_STRENGTH * objectColor;
    vec3 diffuse = totalDiffuse * objectColor;

    FragColor = vec4(ambient + diffuse, 1.0);
}