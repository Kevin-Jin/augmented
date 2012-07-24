package op_lando.map.entity.player;

import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.AuxiliaryEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.resources.SoundCache;
import op_lando.resources.TextureCache;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

public class TractorBeam extends SimpleEntity implements AuxiliaryEntity<PlayerPart> {
	private static final float SHOOT_VELOCITY = 1000f; //pixels per second

	private float rot;
	private float length;
	private CollidableDrawable selected;
	private Vector2f pointInSelected;

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

	private float getLength() {
		return length;
	}

	private void beginExtend() {
		SoundCache.getSound("beam").playAsSoundEffect(1, 1, true);
	}

	private void extend(double tDelta) {
		length += SHOOT_VELOCITY * tDelta;
	}

	private void beginRetract() {
		SoundCache.getSound("beam").stop();
	}

	private void retract(double tDelta) {
		length -= SHOOT_VELOCITY * tDelta;
		if (length < 0)
			length = 0;
	}

	public Position getBeamHit() {
		return new Position(Matrix4f.transform(selected.getTransformationMatrix(), new Vector4f(pointInSelected.getX(), pointInSelected.getY(), 1, 1), null));
	}

	public void setBeamHit(Position value) {
		pointInSelected = new Vector2f(Matrix4f.transform(Matrix4f.invert(selected.getTransformationMatrix(), null), value.asVector4f(), null));
	}

	@Override
	public void update(double tDelta, Input input, Camera camera) {
		if (input.pressedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			beginExtend();
		else if (input.heldButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			extend(tDelta);
		else if (input.releasedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			beginRetract();
		else if (getLength() > 0)
			retract(tDelta);

		super.update(tDelta, input, camera);
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
	public float getWidth() {
		return length;
	}

	@Override
	public float getHeight() {
		return length == 0 ? 0 : super.getHeight();
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
