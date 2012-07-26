package op_lando.map.entity.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.BodyEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.map.state.MapState;
import op_lando.resources.EntityPhysicalBehavior;
import op_lando.resources.TextureCache;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.ReadableVector2f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

public class AvatarBody extends SimpleEntity implements BodyEntity<PlayerPart> {
	private final Player parent;
	private final Map<PlayerPart, Vector2f> baseAttachPoints;
	private final Map<PlayerPart, Vector2f> transformedAttachPoints;
	private final List<CollidableDrawable> flatSurfaces;
	private double rot;
	private boolean flipHorizontally;
	private BoundingPolygon parentBoundPoly;
	private double remainingJump;
	private boolean canJump;

	public AvatarBody(Player parent) {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(30, 12),
				new Vector2f(18, 22),
				new Vector2f(9, 42),
				new Vector2f(9, 58),
				new Vector2f(16, 76),
				new Vector2f(27, 86),
				new Vector2f(38, 86),
				new Vector2f(50, 75),
				new Vector2f(56, 57),
				new Vector2f(56, 42),
				new Vector2f(49, 24),
				new Vector2f(35, 12)
			}), new Polygon(new Vector2f[] {
				new Vector2f(30, 0),
				new Vector2f(30, 11),
				new Vector2f(35, 11),
				new Vector2f(35, 0)
			}), new Polygon(new Vector2f[] {
				new Vector2f(1, 36),
				new Vector2f(9, 36),
				new Vector2f(9, 65),
				new Vector2f(1, 65)
			})
		}), new EntityPhysicalBehavior(100, 200, -400, 150, 800, 1.7));

		this.parent = parent;

		EnumMap<PlayerPart, Vector2f> attachPoints = new EnumMap<PlayerPart, Vector2f>(PlayerPart.class);
		attachPoints.put(PlayerPart.ARM, new Vector2f(26, 55));
		attachPoints.put(PlayerPart.LEGS, new Vector2f(8, 132));
		attachPoints.put(PlayerPart.FIRE, new Vector2f(5, 64));
		baseAttachPoints = Collections.unmodifiableMap(attachPoints);
		transformedAttachPoints = new EnumMap<PlayerPart, Vector2f>(PlayerPart.class);
		for (Map.Entry<PlayerPart, Vector2f> entry : attachPoints.entrySet())
			transformedAttachPoints.put(entry.getKey(), new Vector2f(entry.getValue()));
		flatSurfaces = new ArrayList<CollidableDrawable>();

		parentBoundPoly = getSelfBoundingPolygon();
	}

	public boolean canJump() {
		return canJump;
	}

	public boolean isWalking() {
		return !flatSurfaces.isEmpty();
	}

	@Override
	public void collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables) {
		//TODO: fix collision fighting between legs polygon and body (self) polygon
		//when a thin CollidableDrawable goes between body and legs. Basically can't
		//have any horizontal platforms that are thinner than the legs are high.

		//TODO: fix arm rotation based on position of body's origin before
		//collision handling (I think - just try walking horizontally into the
		//edge of a platform when cursor Y is not the same as origin Y. the arm
		//points in the wrong direction as long as A or D are held)

		//TODO: fix being able to go inside a platform when moving a concave
		//vertex of the Player (e.g. connection between antenna and head,
		//perpendicular arm and legs, or perpendicular arm and head) diagonally
		//into one of the platform's corners 
		super.collision(collisionInfo, otherCollidables);
		if (collisionInfo.getMinimumTranslationVector().getY() >= 0 && collisionInfo.getCollidingSurface().getY() == 0)
			flatSurfaces.add(collisionInfo.getCollidedWith());
		parent.updateChildPositionsAndPolygons(collisionInfo.getMinimumTranslationVector());
	}

	@Override
	public int getMovabilityIndex() {
		return 1;
	}

	@Override
	public Vector2f getOrigin() {
		return new Vector2f(getTexture().getImageWidth() / 2f, baseAttachPoints.get(PlayerPart.ARM).getY() + parent.getArm().getUntransformedBeamAttachPoint().getY());
	}

	@Override
	public float getRotation() {
		return (float) rot;
	}

	public float lookAt(Position pos) {
		ReadableVector2f rotateAbout = Matrix4f.transform(getWorldMatrix(), new Vector4f(getOrigin().getX(), getOrigin().getY(), 1, 1), null);
		double y = pos.getY() - rotateAbout.getY();
		double x = pos.getX() - rotateAbout.getX();
		double realRot = Math.atan2(y, x);
		if (flipHorizontally)
			realRot -= Math.PI;
		if (!flipHorizontally && x < 0 || flipHorizontally && x > 0) {
			flipHorizontally = !flipHorizontally;
			realRot -= Math.PI;
		}

		rot = Math.min(realRot, Math.PI / 4);
		if (flipHorizontally && rot < -Math.PI / 4 && rot > -Math.PI / 2)
			// uhh, kludgy check but it works!
			rot = -Math.PI / 4;
		return (float) realRot;
	}

	@Override
	public boolean flipHorizontally() {
		return flipHorizontally;
	}

	@Override
	public BoundingPolygon getBoundingPolygon() {
		return parentBoundPoly;
	}

	@Override
	public void setBoundingPolygon(BoundingPolygon parent) {
		parentBoundPoly = parent;
	}

	@Override
	public BoundingPolygon getSelfBoundingPolygon() {
		return super.getBoundingPolygon();
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		//TODO: have left and right walking velocity have the same angle as the
		//colliding surface below so magnitude is constant even if we are
		//walking up a slope.
		if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_A)))
			vel.setX(Math.max(vel.getX() - tDelta * physics.getWalkAcceleration(), -physics.getMaxWalkVelocity()));
		else if (vel.getX() < 0) //friction/air resistance
			vel.setX(Math.min(vel.getX() - tDelta * physics.getStopDeceleration(), 0));
		if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_D)))
			vel.setX(Math.min(vel.getX() + tDelta * physics.getWalkAcceleration(), physics.getMaxWalkVelocity()));
		else if (vel.getX() > 0) //friction/air resistance
			vel.setX(Math.max(vel.getX() + tDelta * physics.getStopDeceleration(), 0));
		if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_W)) && (canJump = remainingJump > 0)) {
			vel.setY(Math.min(vel.getY() + tDelta * physics.getJetPackAcceleration(), physics.getJetPackMaxVelocity()));
			if (vel.getY() > 0)
				remainingJump -= tDelta;
		}
		//gravity
		vel.setY(Math.max(vel.getY() + map.getGravitationalFieldStrength() * tDelta, map.getTerminalVelocity()));

		pos.add(vel.getX() * tDelta, vel.getY() * tDelta);

		camera.lookAt(parent.getPosition());
		if (parent.getBeam().isBeamHit())
			parent.lookAt(parent.getBeam().getBeamHit());
		else
			parent.lookAt(camera.mouseToWorld(input.cursorPosition().getX(), input.cursorPosition().getY()));

		for (Map.Entry<PlayerPart, Vector2f> entry : transformedAttachPoints.entrySet()) {
			Vector2f base = baseAttachPoints.get(entry.getKey());
			entry.getValue().set(Matrix4f.transform(getWorldMatrix(), new Vector4f(base.getX(), base.getY(), 1, 1), null));
		}
		flatSurfaces.clear();

		super.preCollisionsUpdate(tDelta, input, camera, map);

		double lastRot = rot;
		rot = 0;
		try {
			Vector2f coord = new Vector2f(baseAttachPoints.get(PlayerPart.LEGS));
			if (flipHorizontally)
				coord.setX(coord.getX() + 48);
			transformedAttachPoints.get(PlayerPart.LEGS).set(Matrix4f.transform(getWorldMatrix(), new Vector4f(coord.getX(), coord.getY(), 1, 1), null));
		} finally {
			rot = lastRot;
		}
	}

	private boolean hitPlatform(Map<CollidableDrawable, Set<CollisionInformation>> log, CollidableDrawable root) {
		if (root.getMovabilityIndex() == 0)
			return true;
		for (CollisionInformation info : log.get(root)) {
			CollidableDrawable other = info.getCollidedWith();
			if (other != this && hitPlatform(log, other))
				return true;
		}
		return false;
	}

	@Override
	public void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log) {
		for (CollidableDrawable test : flatSurfaces) {
			if (hitPlatform(log, test)) {
				remainingJump = physics.getMaxJumpTime();
				break;
			}
		}

		super.postCollisionsUpdate(tDelta, input, log);
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("body");
	}

	@Override
	public Vector2f getAttachPoint(PlayerPart part) {
		switch (part) {
			case BEAM:
				return parent.getArm().getBeamAttachPoint();
			default:
				return transformedAttachPoints.get(part);
		}
	}
}
