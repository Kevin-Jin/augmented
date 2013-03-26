package amplified;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import amplified.Game.GameState;
import amplified.map.Drawable;

public abstract class ScreenFiller {
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

	public abstract GameState update(double tDelta);

	public abstract Map<Byte, ZAxisLayer> getLayers();
}
