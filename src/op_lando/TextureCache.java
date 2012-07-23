package op_lando;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.opengl.Texture;

public class TextureCache {
	private static final Map<String, Texture> loaded = new HashMap<String, Texture>();

	public static Texture getTexture(String key) {
		return loaded.get(key);
	}

	public static void setTexture(String key, Texture value) {
		loaded.put(key, value);
	}

	public static void flush() {
		for (Texture texture : loaded.values())
			texture.release();
		loaded.clear();
	}
}
