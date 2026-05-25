#version 330 core

in float vIntensity;
out vec4 FragColor;

void main() {
    // gl_PointCoord goes from (0,0) to (1,1) across the point. Center is (0.5, 0.5).
    vec2 coord = gl_PointCoord - vec2(0.5);
    float dist = length(coord);

    // If we are outside the radius of 0.5, discard the pixel entirely (makes it a circle)
    if (dist > 0.5) {
        discard;
    }

    // Create a soft glowing edge (fade out as it reaches the edge)
    float alpha = smoothstep(0.5, 0.1, dist) * vIntensity;

    // Pure white star, multiplied by its brightness
    FragColor = vec4(1.0, 1.0, 1.0, alpha);
}