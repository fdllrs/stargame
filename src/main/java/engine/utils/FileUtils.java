package engine.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtils {
	public String readFile(String path) {

		try (InputStream pathStream = FileUtils.class.getResourceAsStream(path)) {
			assertPathStreamExists(path, pathStream);
			return new String(pathStream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void assertPathStreamExists(String path, InputStream pathStream) {
		if (pathStream == null) {
			throw new IllegalArgumentException("Resource not found: " + path);
		}
	}
}
