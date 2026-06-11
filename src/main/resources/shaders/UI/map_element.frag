#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform vec4 uiColor;
uniform int elementType;
uniform vec2 elementSize;// width and height of the quad in screen pixels
uniform float time;

float signFunc(vec2 p1, vec2 p2, vec2 p3) {
    return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
}

bool PointInTriangle(vec2 pt, vec2 v1, vec2 v2, vec2 v3) {
    float d1 = signFunc(pt, v1, v2);
    float d2 = signFunc(pt, v2, v3);
    float d3 = signFunc(pt, v3, v1);

    bool has_neg = (d1 < 0.0) || (d2 < 0.0) || (d3 < 0.0);
    bool has_pos = (d1 > 0.0) || (d2 > 0.0) || (d3 > 0.0);

    return !(has_neg && has_pos);
}

void main() {
    if (elementType == 0) {
        // Solid Circle (Planets, Moons)
        vec2 uv = TexCoords - vec2(0.5);
        float dist = length(uv);
        float distPixels = (dist - 0.5) * elementSize.x;

        if (distPixels > 0.0) {
            discard;
        }

        float alpha = smoothstep(0.0, -1.5, distPixels * 7.0);
        FragColor = vec4(uiColor.rgb, uiColor.a * alpha);

    } else if (elementType == 1) {
        // Orbit Circle (Thin ring at radius 0.5)
        vec2 uv = TexCoords - vec2(0.5);
        float dist = length(uv);
        float distPixels = abs(dist - 0.5) * elementSize.x;
        float thickness = 2.0;// 1 pixel wide

        if (distPixels > thickness * 0.5 + 0.5) {
            discard;
        }

        float alpha = smoothstep(thickness * 0.5 + 0.5, thickness * 0.5 - 0.5, distPixels);
        FragColor = vec4(uiColor.rgb, uiColor.a * alpha);

    } else if (elementType == 2) {
        // Player Spacecraft Icon (Triangle / Chevron)
        vec2 p = TexCoords;
        vec2 A = vec2(0.5, 0.9);
        vec2 B = vec2(0.2, 0.15);
        vec2 C = vec2(0.5, 0.35);
        vec2 D = vec2(0.8, 0.15);

        if (PointInTriangle(p, A, B, C) || PointInTriangle(p, A, C, D)) {
            FragColor = uiColor;
        } else {
            discard;
        }

    } else if (elementType == 3) {
        // Target Selection Highlight (Spinning dashed circle at radius 0.45)
        vec2 uv = TexCoords - vec2(0.5);
        float dist = length(uv);
        float distPixels = abs(dist - 0.45) * elementSize.x;

        if (distPixels > 1.5) {
            discard;
        }

        float angle = atan(uv.y, uv.x);
        if (angle < 0.0) angle += 6.283185307;

        float dashPeriod = 6.283185307 / 4.0;// 4 dashes
        float localAngle = mod(angle - time * 2.0, dashPeriod);
        if (localAngle > dashPeriod * 0.6) {
            discard;
        }

        float alpha = smoothstep(1.5, 0.5, distPixels);
        FragColor = vec4(uiColor.rgb, uiColor.a * alpha);

    } else if (elementType == 4) {
        // Map Panel Background with Border and Grid Lines
        FragColor = uiColor;

        float borderThickness = 2.0;// pixels
        vec2 pixelCoords = TexCoords * elementSize;

        // Draw subtle grid lines
        float gridSpacing = 40.0;
        if (mod(pixelCoords.x, gridSpacing) < 1.0 || mod(pixelCoords.y, gridSpacing) < 1.0) {
            FragColor = mix(FragColor, vec4(0.0, 0.8, 1.0, 0.4), 0.15);
        }

        // Draw glowing cyan border
        if (pixelCoords.x < borderThickness || pixelCoords.x > elementSize.x - borderThickness ||
            pixelCoords.y < borderThickness || pixelCoords.y > elementSize.y - borderThickness) {
            FragColor = vec4(0.0, 0.8, 1.0, 0.8);
        }

    } else if (elementType == 5) {
        // Sun/Star with soft radial glow
        vec2 uv = TexCoords - vec2(0.5);
        float dist = length(uv);

        if (dist > 0.5) {
            discard;
        }

        float glow = pow(1.0 - dist * 1.8, 2.0);// radial falloff
        FragColor = vec4(uiColor.rgb, uiColor.a * glow);

    } else {
        discard;
    }
}
