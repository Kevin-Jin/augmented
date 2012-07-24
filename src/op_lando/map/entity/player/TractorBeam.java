package op_lando.map.entity.player;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.SimpleEntity;
import op_lando.resources.TextureCache;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class TractorBeam extends SimpleEntity {
	public TractorBeam() {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 0),
				new Vector2f(0, 18),
				new Vector2f(210, 18),
				new Vector2f(210, 0)
			})
		}));
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("beam");
	}
}
