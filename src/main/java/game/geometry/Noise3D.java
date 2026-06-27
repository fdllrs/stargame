package game.geometry;

public class Noise3D {
	public static float fbm(float x, float y, float z) {
		float sum = 0.0f;
		float amplitude = 0.5f;
		float fx = x, fy = y, fz = z;
		for (int i = 0; i < 4; i++) {
			sum += amplitude * noise(fx, fy, fz);
			fx *= 2.0f;
			fy *= 2.0f;
			fz *= 2.0f;
			amplitude *= 0.5f;
		}
		return sum;
	}

	public static float noise(float x, float y, float z) {
		float ix = (float) Math.floor(x);
		float iy = (float) Math.floor(y);
		float iz = (float) Math.floor(z);

		float fx = x - ix;
		float fy = y - iy;
		float fz = z - iz;

		float ux = fx * fx * ( 3.0f - 2.0f * fx );
		float uy = fy * fy * ( 3.0f - 2.0f * fy );
		float uz = fz * fz * ( 3.0f - 2.0f * fz );

		// 8 corners of the cube
		float n000 = hash(ix, iy, iz);
		float n100 = hash(ix + 1.0f, iy, iz);
		float n010 = hash(ix, iy + 1.0f, iz);
		float n110 = hash(ix + 1.0f, iy + 1.0f, iz);
		float n001 = hash(ix, iy, iz + 1.0f);
		float n101 = hash(ix + 1.0f, iy, iz + 1.0f);
		float n011 = hash(ix, iy + 1.0f, iz + 1.0f);
		float n111 = hash(ix + 1.0f, iy + 1.0f, iz + 1.0f);

		// Interpolate along x
		float nx00 = n000 + ux * ( n100 - n000 );
		float nx10 = n010 + ux * ( n110 - n010 );
		float nx01 = n001 + ux * ( n101 - n001 );
		float nx11 = n011 + ux * ( n111 - n011 );

		// Interpolate along y
		float nxy0 = nx00 + uy * ( nx10 - nx00 );
		float nxy1 = nx01 + uy * ( nx11 - nx01 );

		// Interpolate along z
		return nxy0 + uz * ( nxy1 - nxy0 );
	}

	public static float hash(float x, float y, float z) {
		return (float) fract(Math.sin(dot(x, y, z)) * 43758.5453);
	}

	private static double fract(double val) {
		return val - Math.floor(val);
	}

	private static float dot(float x, float y, float z) {
		return x * 12.9898f + y * 78.233f + z * 45.164f;
	}
}
