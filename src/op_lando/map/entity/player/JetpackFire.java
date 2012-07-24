package op_lando.map.entity.player;

import op_lando.map.Animation;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.SimpleEntity;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class JetpackFire extends SimpleEntity {
	private Animation animation;

	public JetpackFire() {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 0),
				new Vector2f(8, 0),
				new Vector2f(8, 19),
				new Vector2f(0, 19)
			})
		}));
		animation = new Animation(0.2, "flame1", "flame2", "flame3", "flame4");
	}

	@Override
	public void update(double tDelta) {
		super.update(tDelta);
		animation.update(tDelta);
	}

	@Override
	public Texture getTexture() {
		return animation.getTexture();
	}
}
