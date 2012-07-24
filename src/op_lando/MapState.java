package op_lando;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapState {
	public static class ZAxisLayer {
		public static final Byte
			FAR_BACKGROUND = Byte.valueOf((byte) 0),
			MAIN_BACKGROUND = Byte.valueOf((byte) 1),
			MIDGROUND = Byte.valueOf((byte) 2),
			FOREGROUND = Byte.valueOf((byte) 3),
			OVERLAY = Byte.valueOf((byte) 4)
		;

		private float parallax;
		private List<Drawable> drawables;

		public ZAxisLayer(float parallax) {
			this.parallax = parallax;
			this.drawables = new ArrayList<Drawable>();
		}

		public float getParallaxFactor() {
			return parallax;
		}

		public List<Drawable> getDrawables() {
			return drawables;
		}
	}

	private static final int FLOOR_VISIBLE_PIXELS = 20;
	private static final int CEILING_VISIBLE_PIXELS = 20;
	private static final int LEFT_WALL_VISIBLE_PIXELS = 20;
	private static final int RIGHT_WALL_VISIBLE_PIXELS = 20;

	private LevelLayout layout;
	//private final Player player;
	//private final SortedMap<Byte, Entity> entities;
	private final SortedMap<Byte, ZAxisLayer> layers;

	public MapState(Drawable... overlays) {
		layers = new TreeMap<Byte, ZAxisLayer>();

		layers.put(ZAxisLayer.FAR_BACKGROUND, new ZAxisLayer(0.25f));
		layers.put(ZAxisLayer.MAIN_BACKGROUND, new ZAxisLayer(0.5f));
		layers.put(ZAxisLayer.MIDGROUND, new ZAxisLayer(1));
		layers.put(ZAxisLayer.FOREGROUND, new ZAxisLayer(2));
		layers.put(ZAxisLayer.OVERLAY, new ZAxisLayer(0));

		for (Drawable overlay : overlays)
			layers.get(ZAxisLayer.OVERLAY).getDrawables().add(overlay);
	}

	public void setLayout(LevelLayout layout) {
		this.layout = layout;

		layers.get(ZAxisLayer.MIDGROUND).getDrawables().clear();
		layers.get(ZAxisLayer.MIDGROUND).getDrawables().addAll(layout.getPlatforms());

		layers.get(ZAxisLayer.FAR_BACKGROUND).getDrawables().clear();
		layers.get(ZAxisLayer.MAIN_BACKGROUND).getDrawables().clear();

		layers.get(ZAxisLayer.FOREGROUND).getDrawables().clear();

		//todo: populate entities from layout
	}

	public Rectangle getCameraBounds() {
		return new Rectangle(-LEFT_WALL_VISIBLE_PIXELS, -FLOOR_VISIBLE_PIXELS, layout.getWidth() + LEFT_WALL_VISIBLE_PIXELS + RIGHT_WALL_VISIBLE_PIXELS, layout.getHeight() + FLOOR_VISIBLE_PIXELS + CEILING_VISIBLE_PIXELS);
	}

	public SortedMap<Byte, ZAxisLayer> getLayers() {
		return layers;
	}
}
