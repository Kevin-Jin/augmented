package op_lando.map.entity.player;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.AuxiliaryEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.resources.TextureCache;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

public class AvatarArm extends SimpleEntity implements AuxiliaryEntity<PlayerPart> {
	private static final Vector2f BEAM_SOURCE = new Vector2f(45, 14);
	private float rot;
	private boolean flipHorizontally;

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
	public int getMovabilityIndex() {
		return 1;
	}

	@Override
	public Vector2f getDrawPosition() {
		return new Vector2f(flipHorizontally ? (float) (getPosition().getX() + getOrigin().getX() * getScale().getX() - getWidth() * Math.cos(rot)) : ((float) getPosition().getX() + getOrigin().getX() * getScale().getX()), flipHorizontally ? (float) (getPosition().getY() - getOrigin().getY() * -getScale().getY() - getWidth() * Math.sin(rot)) : (float) getPosition().getY() - getOrigin().getY() * -getScale().getY());
	}

	@Override
	public float getRotation() {
		return rot;
	}

	public void setRotation(float rot) {
		this.rot = rot;
	}

	@Override
	public boolean flipHorizontally() {
		return flipHorizontally;
	}

	@Override
	public void setFlip(boolean flip) {
		flipHorizontally = flip;
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("arm");
	}

	@Override
	public PlayerPart getType() {
		return PlayerPart.ARM;
	}

	public Vector2f getUntransformedBeamAttachPoint() {
		return BEAM_SOURCE;
	}

	public Vector2f getBeamAttachPoint() {
		return new Vector2f(Matrix4f.transform(getTransformationMatrix(), new Vector4f(BEAM_SOURCE.getX(), BEAM_SOURCE.getY(), 1, 1), null));
	}
}
