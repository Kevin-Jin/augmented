package op_lando.map.entity.player;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.SimpleEntity;
import op_lando.resources.TextureCache;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class AvatarArm extends SimpleEntity {
	public AvatarArm() {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(4, 0),
				new Vector2f(16, 12),
				new Vector2f(13, 17),
				new Vector2f(0, 4)
			}), new Polygon(new Vector2f[] {
				new Vector2f(16, 12),
				new Vector2f(32, 12),
				new Vector2f(32, 17),
				new Vector2f(13, 17)
			}), new Polygon(new Vector2f[] {
				new Vector2f(32, 12),
				new Vector2f(36, 8),
				new Vector2f(44, 8),
				new Vector2f(44, 20),
				new Vector2f(35, 20),
				new Vector2f(32, 17)
			})
		}));
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("arm");
	}
}
