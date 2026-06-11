#version 330 core

out vec4 outColor;

uniform vec3 colorA;
uniform vec3 colorB;
uniform float noiseScale;
uniform float time;
uniform int isGlowShell;

in vec3 localPosition;
in vec3 viewNormal;
in vec3 viewDir;

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

    float NdotV = max(0.0, dot(normalize(viewNormal), normalize(viewDir)));
    float fresnel = 1.0 - NdotV;

    if (isGlowShell == 1) {
        // CORONA / GLOW PASS
        // 3D noise coords animate to simulate solar wind / plasma flares
        vec3 noiseCoord = sphereCoord * (noiseScale * 0.8) + vec3(0.0, 0.0, time * 0.4);
        float coronaNoise = fbm(noiseCoord) * 2;

        // Edge falloff: brighter towards center, fades out at the edges
        float falloff = pow(NdotV, 1.1);// Fades to 0 at the silhouette edge of the shell

        // Dynamic flare intensity
        float intensity = falloff * 0.7 * coronaNoise;

        // Interpolate colors: colorA (hot center) -> colorB (cooler edge) -> transparent
        vec3 glowColor = mix(colorB * 0.7, colorA * 0.5, falloff);

        // Boost glow color slightly for additive blending vibrancy
        outColor = vec4(glowColor * 1.5, intensity);
    } else {
        vec3 surfaceCoord = sphereCoord * noiseScale + vec3(time * 0.05, time * 0.1, 0.0);
        float terrainHeight = fbm(surfaceCoord);

        vec3 objectColor;
        // Pixel-art styled hard classification
        if (terrainHeight < 0.45) {
            objectColor = colorA;
        } else {
            objectColor = colorB;
        }

        // Add a bright, glowing rim light to the solid body
        float rimGlow = pow(fresnel, 3.5);
        objectColor = mix(objectColor, colorA * 1.8, rimGlow);

        outColor = vec4(objectColor, 1.0);
    }
}

