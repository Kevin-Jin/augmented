package op_lando.resources;

import java.util.HashMap;
import java.util.Map;

import op_lando.map.Platform;
import op_lando.map.physicquantity.Position;

public class LevelCache {
	private static final Map<String, LevelLayout> loaded = new HashMap<String, LevelLayout>();

	public static LevelLayout getLevel(String key) {
		return loaded.get(key);
	}

	public static void setLevel(String key, LevelLayout value) {
		loaded.put(key, value);
	}

	private static LevelLayout constructTutorialLevel() {
		int width = 1000;
		int height = 800;
		Map<Byte, Platform> footholds = new HashMap<Byte, Platform>();
		// ground platform
		/*footholds.put(Byte.valueOf((byte) 0), new Platform(-99999, width, 0, -99999));
		// left wall
		footholds.put(Byte.valueOf((byte) 1), new Platform(-99999, 0, height + 99999, -99999));
		// right wall
		footholds.put(Byte.valueOf((byte) 2), new Platform(width, width + 99999, height + 99999, -99999));
		// top wall
		footholds.put(Byte.valueOf((byte) 3), new Platform(-99999, width + 99999, height + 99999, height));

		footholds.put(Byte.valueOf((byte) 4), new Platform(300, 400, 200, 0));

		// footholds[5] = new Platform(725, 775, 1100, 175);
		footholds.put(Byte.valueOf((byte) 5), new Platform(975, 1075, 300, 0));

		// main center
		footholds.put(Byte.valueOf((byte) 7), new Platform(1000, 1300, 900, 700));
		// small to the right
		footholds.put(Byte.valueOf((byte) 8), new Platform(1300, 1500, 870, 850));
		// left
		footholds.put(Byte.valueOf((byte) 9), new Platform(950, 1000, 1100, 700));

		// top
		footholds.put(Byte.valueOf((byte) 10), new Platform(1000, 1500, 1100, 1050));
		// top to the right
		footholds.put(Byte.valueOf((byte) 11), new Platform(1500, 1951, 1100, 1060));

		// top part extending downwards
		footholds.put(Byte.valueOf((byte) 12), new Platform(1100, 1300, 1050, 940));

		// right wall, for exit
		footholds.put(Byte.valueOf((byte) 13), new Platform(1650, 1951, 800, 0));*/

		footholds.put(Byte.valueOf((byte) 0), new Platform(0, 10, -20, 0));

		return new LevelLayout(width, height, footholds, new Position(100, 100), -400, -400, "mid1", "scrollingWindowBg", "mainBg", Double.POSITIVE_INFINITY);
	}

	public static void initialize() {
		loaded.put("tutorial", constructTutorialLevel());
	}
}
