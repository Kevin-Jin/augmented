package op_lando.map.entity.player;

import op_lando.map.Animation;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.AuxiliaryEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.map.state.MapState;
import op_lando.resources.SoundCache;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class JetpackFire extends SimpleEntity implements AuxiliaryEntity<PlayerPart> {
	private final Player parent;
	private final Animation animation;
	private float rot;
	private boolean flipHorizontally;
	private boolean visible;

	public JetpackFire(Player parent) {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 0),
				new Vector2f(8, 0),
				new Vector2f(8, 19),
				new Vector2f(0, 19)
			})
		}), null);

		this.parent = parent;

		animation = new Animation(0.2, "flame1", "flame2", "flame3", "flame4");
	}

	@Override
	public int getMovabilityIndex() {
		return 1;
	}

	@Override
	public Vector2f getOrigin() {
		return new Vector2f(getTexture().getImageWidth() / 2f, 0);
	}

	@Override
	public void setPosition(Position pos) {
		super.setPosition(pos);
		this.pos.add(-getOrigin().getX() * getScale().getX(), -getHeight());
	}

	@Override
	public float getWidth() {
		return visible ? super.getWidth() : 0;
	}

	@Override
	public float getHeight() {
		return visible ? super.getHeight() : 0;
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
	public BoundingPolygon getBoundingPolygon() {
		return new BoundingPolygon(new Polygon[0]);
	}

	@Override
	public BoundingPolygon getSelfBoundingPolygon() {
		return super.getBoundingPolygon();
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		boolean wasVisible = visible;
		visible = (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_W)) && parent.getBody().canJump());
		if (visible) {
			animation.update(tDelta);
			if (!wasVisible)
				SoundCache.getSound("jetpack").playAsSoundEffect(1f, 1f, true);
		} else {
			if (wasVisible)
				SoundCache.getSound("jetpack").stop();
		}
		setPosition(pos);

		super.preCollisionsUpdate(tDelta, input, camera, map);
	}

	@Override
	public Texture getTexture() {
		return animation.getTexture();
	}

	@Override
	public PlayerPart getType() {
		return PlayerPart.FIRE;
	}
}
