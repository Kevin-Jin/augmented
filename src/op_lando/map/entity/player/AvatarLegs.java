package op_lando.map.entity.player;

import op_lando.map.Animation;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.AuxiliaryEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class AvatarLegs extends SimpleEntity implements AuxiliaryEntity<PlayerPart> {
	private Animation animation;
	private boolean flipHorizontally;

	public AvatarLegs() {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 0),
				new Vector2f(42, 0),
				new Vector2f(42, 34),
				new Vector2f(0, 34)
			})
		}));
		animation = new Animation(0.06, "legsRest", "legs1", "legs2", "legs3", "legs4", "legs5");
	}

	@Override
	public int getMovabilityIndex() {
		return 1;
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
	public void recalculateSelfBoundingPolygon() {
		transformedBoundPoly = BoundingPolygon.transformBoundingPolygon(baseBoundPoly, this);
	}

	private boolean onGround() {
		//TODO: implement
		return true;
	}

	@Override
	public void update(double tDelta, Input input, Camera camera) {
		if ((input.downKeys().contains(Integer.valueOf(Keyboard.KEY_A)) || input.downKeys().contains(Integer.valueOf(Keyboard.KEY_D))) && onGround())
			animation.update(tDelta);
		else
			animation.reset();

		super.update(tDelta, input, camera);
	}

	@Override
	public Texture getTexture() {
		return animation.getTexture();
	}

	@Override
	public PlayerPart getType() {
		return PlayerPart.LEGS;
	}
}
