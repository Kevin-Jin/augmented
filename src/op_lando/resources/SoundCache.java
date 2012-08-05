package op_lando.resources;

import java.util.HashMap;
import java.util.Map;

import op_lando.LowLevelUtil;

import org.newdawn.slick.openal.Audio;

public class SoundCache {
	private static final Map<String, Audio> loaded = new HashMap<String, Audio>();

	public static Audio getSound(String key) {
		return loaded.get(key);
	}

	public static void setSound(String key, Audio value) {
		loaded.put(key, value);
	}

	public static void flush() {
		loaded.clear();
		LowLevelUtil.releaseAudio();
	}
}
