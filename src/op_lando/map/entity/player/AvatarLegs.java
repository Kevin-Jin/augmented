package op_lando.map.entity.player;

import java.util.Map;
import java.util.Set;

import op_lando.map.Animation;
import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.AuxiliaryEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.map.state.Input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

public class AvatarLegs extends SimpleEntity implements AuxiliaryEntity<PlayerPart> {
	private final Player parent;
	private Animation animation;
	private boolean flipHorizontally;

	public AvatarLegs(Player parent) {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 0),
				new Vector2f(42, 0),
				new Vector2f(42, 34),
				new Vector2f(0, 34)
			})
		}), null);

		this.parent = parent;

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

	@Override
	public void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log) {
		//TODO: maybe instead of restoring all jump time when we hit a
		//completely horizontal collidable that either is or contacts a platform,
		//just recharge jump time gradually as long as we are (in)directly
		//touching a platform, as long as angle of colliding surface is less
		//than 45 degrees from horizontal?
		if ((input.downKeys().contains(Integer.valueOf(Keyboard.KEY_A)) || input.downKeys().contains(Integer.valueOf(Keyboard.KEY_D))) && parent.getBody().isWalking())
			animation.update(tDelta);
		else
			animation.reset();

		super.postCollisionsUpdate(tDelta, input, log);
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
