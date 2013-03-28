package amplified.map.entity.props;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.Polygon;
import amplified.resources.TextureCache;

public class RectangleBox extends SelectableEntity {
	public RectangleBox(float initScale, float minScale, float maxScale) {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 380),
				new Vector2f(85, 380),
				new Vector2f(85, 0),
				new Vector2f(0, 0)
			})
		}), initScale, minScale, maxScale);
	}

	public RectangleBox() {
		this(1f, 0.1f, 10f);
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("rect");
	}
}
