package op_lando.map.entity.player;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.SimpleEntity;
import op_lando.resources.TextureCache;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class AvatarBody extends SimpleEntity {
	public AvatarBody() {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(30, 12),
				new Vector2f(18, 22),
				new Vector2f(9, 42),
				new Vector2f(9, 58),
				new Vector2f(16, 76),
				new Vector2f(27, 86),
				new Vector2f(38, 86),
				new Vector2f(50, 75),
				new Vector2f(56, 57),
				new Vector2f(56, 42),
				new Vector2f(49, 24),
				new Vector2f(35, 12)
			}), new Polygon(new Vector2f[] {
				new Vector2f(30, 0),
				new Vector2f(30, 11),
				new Vector2f(35, 11),
				new Vector2f(35, 0)
			}), new Polygon(new Vector2f[] {
				new Vector2f(1, 36),
				new Vector2f(9, 36),
				new Vector2f(9, 65),
				new Vector2f(1, 65)
			})
		}));
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("body");
	}
}
