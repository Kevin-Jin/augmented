package amplified.map.entity.player;

import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.Texture;

import amplified.map.Animation;
import amplified.map.CollidableDrawable;
import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.CollisionInformation;
import amplified.map.collisions.Polygon;
import amplified.map.entity.AuxiliaryEntity;
import amplified.map.entity.SimpleEntity;
import amplified.map.physicquantity.Position;
import amplified.map.state.Camera;
import amplified.map.state.Input;
import amplified.map.state.MapState;

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
	public void setRotation(float rot) {
		throw new UnsupportedOperationException("Cannot rotate an AvatarLegs");
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
	public void markStartPosition() {
		startPos.set(pos);
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		Position savedStartPos = new Position(startPos.getX(), startPos.getY());
		super.preCollisionsUpdate(tDelta, input, camera, map);
		startPos.set(savedStartPos);
	}

	@Override
	public void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log, Camera camera) {
		//TODO: maybe instead of restoring all jump time when we hit a
		//completely horizontal collidable that either is or contacts a platform,
		//just recharge jump time gradually as long as we are (in)directly
		//touching a platform, as long as angle of colliding surface is less
		//than 45 degrees from horizontal?
		if ((input.downKeys().contains(Integer.valueOf(Keyboard.KEY_A)) || input.downKeys().contains(Integer.valueOf(Keyboard.KEY_D))) && parent.getBody().isWalking())
			animation.update(tDelta);
		else
			animation.reset();

		super.postCollisionsUpdate(tDelta, input, log, camera);
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
