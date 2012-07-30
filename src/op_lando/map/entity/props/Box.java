package op_lando.map.entity.props;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.resources.TextureCache;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class Box extends SelectableEntity {
	public Box() {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 230),
				new Vector2f(225, 230),
				new Vector2f(225, 0),
				new Vector2f(0, 0)
			})
		}));
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("box");
	}
}
