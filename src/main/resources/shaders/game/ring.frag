#version 330 core

out vec4 outColor;

uniform vec3 lightPosition;
uniform vec3 lightColor;
uniform vec3 colorA;
uniform vec3 colorB;
uniform float radius;

in vec3 surfaceNormal;
in vec3 worldPosition;
in vec3 localPosition;

#include "noise.glsl"
#include "dither.glsl"

void main() {
    float dist = length(localPosition);
    float rInner = radius * 1.4;
    float rOuter = radius * 2.3;

    // Soften the inner and outer boundaries of the ring
    float alpha = smoothstep(rInner, rInner + 0.1 * radius, dist) *
    (1.0 - smoothstep(rOuter - 0.1 * radius, rOuter, dist));

    // Generate concentric stripes using a 1D slice of noise
    float n = fbm(vec3(dist * 6.0 / radius, 0.1, 0.1));
    alpha *= (0.2 + 0.8 * n);

    // Retro screen-space dithering for transparency
    int x = int(gl_FragCoord.x) % 4;
    int y = int(gl_FragCoord.y) % 4;
    float threshold = BAYER_4X4[y * 4 + x];
    if (alpha < threshold) {
        discard; // Discards pixels below the dither threshold
    }

    // Basic lighting on the flat ring
    vec3 normal = vec3(0.0, 1.0, 0.0);
    if (surfaceNormal.y < 0.0) normal.y = -1.0;

    vec3 lightDir = normalize(lightPosition - worldPosition);
    float brightness = max(abs(dot(normal, lightDir)), 0.05); // light both sides slightly

    // Base ring color is a gradient between colorA and colorB
    vec3 objectColor = mix(colorA, colorB, 0.5 + 0.5 * sin(dist * 5.0 / radius));
    vec3 lit = (AMBIENT_STRENGTH * 2.0 + brightness) * objectColor * lightColor;

    outColor = vec4(ditheredBand(lit.r), ditheredBand(lit.g), ditheredBand(lit.b), 1.0);
}
