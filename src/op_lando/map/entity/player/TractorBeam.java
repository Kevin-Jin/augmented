package op_lando.map.entity.player;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.AuxiliaryEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.resources.TextureCache;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class TractorBeam extends SimpleEntity implements AuxiliaryEntity<PlayerPart> {
	private float rot;

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
	public Vector2f getOrigin() {
		return new Vector2f(0, getTexture().getImageHeight() / 2f);
	}

	@Override
	public Vector2f getDrawPosition() {
		return getPosition().asVector();
	}

	@Override
	public float getRotation() {
		return rot;
	}

	public void setRotation(float rot) {
		this.rot = rot;
	}

	@Override
	public void setFlip(boolean flip) {
		if (flip)
			rot += Math.PI;
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("beam");
	}

	@Override
	public PlayerPart getType() {
		return PlayerPart.BEAM;
	}
}
