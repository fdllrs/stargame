const float AMBIENT_STRENGTH = 0.5;
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
