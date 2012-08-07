package amplified.map.entity.props;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.Polygon;
import amplified.resources.TextureCache;

public class Box extends SelectableEntity {
	public Box(float minScale, float maxScale) {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 230),
				new Vector2f(225, 230),
				new Vector2f(225, 0),
				new Vector2f(0, 0)
			})
		}), minScale, maxScale);
	}

	public Box() {
		this(0.1f, 10f);
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("box");
	}
}
