package amplified.map.entity.player;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.Polygon;
import amplified.map.entity.AuxiliaryEntity;
import amplified.map.entity.SimpleEntity;
import amplified.map.physicquantity.Position;
import amplified.map.state.Camera;
import amplified.map.state.Input;
import amplified.map.state.MapState;
import amplified.resources.TextureCache;

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
		}), null);
	}

	public int getMovabilityIndex() {
		return 1;
	}

	@Override
	public void setPosition(Position pos) {
		super.setPosition(pos);
		if (flipHorizontally)
			this.pos.add(-getWidth() * Math.cos(rot), -getWidth() * Math.sin(rot) - getHeight());
		else
			this.pos.add(0, -getHeight());
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

	public void setFlip(boolean flip) {
		flipHorizontally = flip;
	}

	@Override
	public BoundingPolygon getBoundingPolygon() {
		return new BoundingPolygon(new Polygon[0]);
	}

	public BoundingPolygon getSelfBoundingPolygon() {
		return super.getBoundingPolygon();
	}

	public void markStartPosition() {
		startPos.set(pos);
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		Position savedStartPos = new Position(startPos);
		super.preCollisionsUpdate(tDelta, input, camera, map);
		startPos.set(savedStartPos);
	}

	public Texture getTexture() {
		return TextureCache.getTexture("arm");
	}

	public PlayerPart getType() {
		return PlayerPart.ARM;
	}

	public Vector2f getUntransformedBeamAttachPoint() {
		return BEAM_SOURCE;
	}

	public Vector2f getBeamAttachPoint() {
		return new Vector2f(Matrix4f.transform(getWorldMatrix(), new Vector4f(BEAM_SOURCE.getX(), BEAM_SOURCE.getY(), 1, 1), null));
	}
}
