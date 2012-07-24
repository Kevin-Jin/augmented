package op_lando.resources;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.TrueTypeFont;

//silly Slick2D devs deprecated TrueTypeFont but Slick-Util packagers never
//included the UnicodeFont class...
public class FontCache {
	private static final Map<String, TrueTypeFont> loaded = new HashMap<String, TrueTypeFont>();

	public static TrueTypeFont getFont(String key) {
		return loaded.get(key);
	}

	public static void setFont(String key, TrueTypeFont value) {
		loaded.put(key, value);
	}

	public static void flush() {
		loaded.clear();
	}
}
