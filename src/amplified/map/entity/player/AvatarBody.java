package amplified.map.entity.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.ReadableVector2f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

import amplified.map.CollidableDrawable;
import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.CollisionInformation;
import amplified.map.collisions.Polygon;
import amplified.map.entity.BodyEntity;
import amplified.map.entity.Direction;
import amplified.map.entity.SimpleEntity;
import amplified.map.entity.props.SelectableEntity;
import amplified.map.physicquantity.Position;
import amplified.map.state.Camera;
import amplified.map.state.Input;
import amplified.map.state.MapState;
import amplified.resources.EntityKinematics;
import amplified.resources.TextureCache;

public class AvatarBody extends SimpleEntity implements BodyEntity<PlayerPart> {
	private final Player parent;
	private final Map<PlayerPart, Vector2f> baseAttachPoints;
	private final Map<PlayerPart, Vector2f> transformedAttachPoints;
	private final List<CollidableDrawable> flatSurfaces;
	private double rot;
	private boolean flipHorizontally;
	private BoundingPolygon parentBoundPoly;
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
		}), new EntityKinematics(100, 200, -400, 150, 800, 1.7));

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
	protected void recalculateBoundingPolygon(UpdateTime time, Camera camera, Input input) {
		if (time == UpdateTime.COLLISION)
			return;

		if (!input.isCutscene()) {
			camera.lookAt(parent.getPosition());
			if (parent.getBeam().isBeamHit())
				parent.lookAt(parent.getBeam().getBeamHit());
			else
				parent.lookAt(camera.mouseToWorld(input.cursorPosition().getX(), input.cursorPosition().getY()));
		}
		transformedBoundPoly = BoundingPolygon.transformBoundingPolygon(baseBoundPoly, this);

		setPosition(pos);
	}

	private boolean ignoreCollisions(Set<CollisionInformation> collidedWithCollisions) {
		final float TOLERANCE = 0.001f;

		//TODO: prevent pushing player horizontally by dragging box along platform
		for (CollisionInformation info : collidedWithCollisions)
			if (info.getMinimumTranslationVector().lengthSquared() >= TOLERANCE && info.getCollidingSurface().getX() >= TOLERANCE)
				return false;
		return true;
	}

	@Override
	public void collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables, Map<CollidableDrawable, Set<CollisionInformation>> log) {
		if (collisionInfo.getCollidedWith() instanceof SelectableEntity) {
			Set<CollisionInformation> collidedWithCollisions = log.get(collisionInfo.getCollidedWith());
			if (collidedWithCollisions == null || ignoreCollisions(collidedWithCollisions)) {
				collisionInfo.getCollidedWith().collision(collisionInfo.complement(this), otherCollidables, log);
				return;
			}
		}

		final float TOLERANCE = 0.001f;

		//TODO: fix collision fighting between legs polygon and body (self) polygon
		//when a thin CollidableDrawable goes between body and legs. Basically can't
		//have any horizontal platforms that are thinner than the legs are high.

		//TODO: fix being able to go inside a platform when moving a concave
		//vertex of the Player (e.g. connection between antenna and head,
		//perpendicular arm and legs, or perpendicular arm and head) diagonally
		//into one of the platform's corners
		super.collision(collisionInfo, otherCollidables, log);
		if (collisionInfo.getMinimumTranslationVector().getY() >= 0 && Math.abs(collisionInfo.getCollidingSurface().getY()) < TOLERANCE)
			flatSurfaces.add(collisionInfo.getCollidedWith());
	}

	public int getMovabilityIndex() {
		return 1;
	}

	@Override
	public void setPosition(Position pos) {
		super.setPosition(pos);
		for (Map.Entry<PlayerPart, Vector2f> entry : transformedAttachPoints.entrySet()) {
			Vector2f base = baseAttachPoints.get(entry.getKey());
			entry.getValue().set(Matrix4f.transform(getWorldMatrix(), new Vector4f(base.getX(), base.getY(), 1, 1), null));
		}
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

	@Override
	public Vector2f getOrigin() {
		return new Vector2f(getTexture().getImageWidth() / 2f, baseAttachPoints.get(PlayerPart.ARM).getY() + parent.getArm().getUntransformedBeamAttachPoint().getY());
	}

	@Override
	public float getRotation() {
		return (float) rot;
	}

	public void setRotation(float rot) {
		throw new UnsupportedOperationException("Cannot rotate an AvatarBody");
	}

	public float rotate(double realRot, boolean flip) {
		if (flipHorizontally)
			realRot -= Math.PI;
		if (flip) {
			flipHorizontally = !flipHorizontally;
			realRot -= Math.PI;
		}

		rot = Math.min(realRot, Math.PI / 4);
		if (flipHorizontally && rot < -Math.PI / 4 && rot > -Math.PI / 2)
			// uhh, kludgy check but it works!
			rot = -Math.PI / 4;
		return (float) realRot;
	}

	public float lookAt(Position pos) {
		ReadableVector2f rotateAbout = Matrix4f.transform(getWorldMatrix(), new Vector4f(getOrigin().getX(), getOrigin().getY(), 1, 1), null);
		double y = pos.getY() - rotateAbout.getY();
		double x = pos.getX() - rotateAbout.getX();
		double realRot = Math.atan2(y, x);
		return rotate(realRot, !flipHorizontally && x < 0 || flipHorizontally && x > 0);
	}

	@Override
	public boolean flipHorizontally() {
		return flipHorizontally;
	}

	@Override
	public BoundingPolygon getBoundingPolygon() {
		return parentBoundPoly;
	}

	public void setBoundingPolygon(BoundingPolygon parent) {
		parentBoundPoly = parent;
	}

	public BoundingPolygon getSelfBoundingPolygon() {
		return super.getBoundingPolygon();
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		canJump = remainingJump > 0;
		if (!input.isCutscene()) {
			if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_A)))
				move(Direction.LEFT);
			if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_D)))
				move(Direction.RIGHT);
			if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_W)))
				move(Direction.UP);
			if (input.downKeys().contains(Integer.valueOf(Keyboard.KEY_S)))
				move(Direction.DOWN);
		}
		super.preCollisionsUpdate(tDelta, input, camera, map);

		flatSurfaces.clear();
	}

	private boolean hitPlatform(Map<CollidableDrawable, Set<CollisionInformation>> log, CollidableDrawable root) {
		if (root.getMovabilityIndex() == 0)
			return true;
		for (CollisionInformation info : log.get(root)) {
			CollidableDrawable other = info.getCollidedWith();
			if (info.getMinimumTranslationVector().getY() > 0 && other != this && other != parent.getBeam() && hitPlatform(log, other))
				return true;
		}
		return false;
	}

	@Override
	public void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log, Camera camera) {
		for (CollidableDrawable test : flatSurfaces) {
			if (hitPlatform(log, test)) {
				remainingJump = motionProperties.getMaxJumpTime();
				break;
			}
		}

		super.postCollisionsUpdate(tDelta, input, log, camera);
	}

	public Texture getTexture() {
		return TextureCache.getTexture("body");
	}

	public Vector2f getAttachPoint(PlayerPart part) {
		switch (part) {
			case BEAM:
				return parent.getArm().getBeamAttachPoint();
			default:
				return transformedAttachPoints.get(part);
		}
	}

	public void reset() {
		getVelocity().setScalarComponents(0, 0);
		flipHorizontally = false;
		remainingJump = 0;
		canJump = false;
	}
}
