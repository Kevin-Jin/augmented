package amplified.map.entity.props;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.Polygon;
import amplified.resources.TextureCache;

public class NBox extends SelectableEntity {
	public NBox() {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(1, 133),
				new Vector2f(35, 133),
				new Vector2f(35, 1),
				new Vector2f(1, 1)
			}), new Polygon(new Vector2f[] {
				new Vector2f(35, 35),
				new Vector2f(99, 35),
				new Vector2f(99, 1),
				new Vector2f(35, 1)
			}), new Polygon(new Vector2f[] {
				new Vector2f(99, 133),
				new Vector2f(133, 133),
				new Vector2f(133, 1),
				new Vector2f(99, 1) })
			}), 1f, 1.25f, 1.25f);
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("n");
	}
}
