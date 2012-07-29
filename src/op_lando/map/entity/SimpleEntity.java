package op_lando.map.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import op_lando.Game;
import op_lando.map.AbstractCollidable;
import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.collisions.PolygonHelper;
import op_lando.map.physicquantity.Acceleration;
import op_lando.map.physicquantity.Position;
import op_lando.map.physicquantity.Velocity;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.map.state.MapState;
import op_lando.resources.EntityKinematics;

import org.lwjgl.util.vector.Vector2f;

public abstract class SimpleEntity extends AbstractCollidable implements DrawableEntity {
	protected enum UpdateTime { PRE_COLLISIONS, COLLISION, POST_COLLISIONS }

	protected final Position pos;
	protected final Velocity vel;
	protected final Acceleration accel;
	protected final EntityKinematics motionProperties;
	private final Set<Direction> moves;
	protected double remainingJump;
	protected final Position startPos;

	protected SimpleEntity(BoundingPolygon boundPoly, EntityKinematics quantities) {
		super(boundPoly, boundPoly);
		pos = new Position();
		vel = new Velocity();
		accel = new Acceleration();
		moves = EnumSet.noneOf(Direction.class);
		motionProperties = quantities;
		startPos = new Position();
	}

	@SuppressWarnings("unused")
	protected void recalculateBoundingPolygon(UpdateTime time, Camera camera, Input input) {
		if (time == UpdateTime.POST_COLLISIONS && !Game.DEBUG)
			return;
		transformedBoundPoly = BoundingPolygon.transformBoundingPolygon(baseBoundPoly, this);
		transformedBoundPoly = PolygonHelper.sweepCollisionBoundingPolygon(transformedBoundPoly, new Position(startPos.getX() - pos.getX(), startPos.getY() - pos.getY()).asVector());
	}

	@Override
	public void collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables) {
		Vector2f negationVector = collisionInfo.getMinimumTranslationVector();
		pos.add(negationVector.getX(), negationVector.getY());
		if (negationVector.getY() >= 0 && collisionInfo.getCollidingSurface().getY() == 0)
			vel.setY(0);
		else if (negationVector.getY() < 0 && collisionInfo.getCollidingSurface().getY() == 0)
			vel.setY(0);

		recalculateBoundingPolygon(UpdateTime.COLLISION, null, null);
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		startPos.set(pos);
		if (motionProperties != null) {
			//TODO: have left and right walking velocity have the same angle as the
			//colliding surface below so magnitude is constant even if we are
			//walking up a slope.
			if (moves.contains(Direction.LEFT))
				vel.setX(Math.max(vel.getX() - tDelta * motionProperties.getWalkAcceleration(), -motionProperties.getMaxWalkVelocity()));
			else if (vel.getX() < 0) //friction/air resistance
				vel.setX(Math.min(vel.getX() - tDelta * motionProperties.getStopDeceleration(), 0));
			if (moves.contains(Direction.RIGHT))
				vel.setX(Math.min(vel.getX() + tDelta * motionProperties.getWalkAcceleration(), motionProperties.getMaxWalkVelocity()));
			else if (vel.getX() > 0) //friction/air resistance
				vel.setX(Math.max(vel.getX() + tDelta * motionProperties.getStopDeceleration(), 0));
			if (moves.contains(Direction.UP) && remainingJump > 0) {
				vel.setY(Math.min(vel.getY() + tDelta * motionProperties.getJetPackAcceleration(), motionProperties.getJetPackMaxVelocity()));
				if (vel.getY() > 0)
					remainingJump -= tDelta;
			}
			moves.clear();
			//gravity
			vel.setY(Math.max(vel.getY() + map.getGravitationalFieldStrength() * tDelta, map.getTerminalVelocity()));
			pos.add(vel.getX() * tDelta, vel.getY() * tDelta);
		}

		recalculateBoundingPolygon(UpdateTime.PRE_COLLISIONS, camera, input);
	}

	@Override
	public void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log, Camera camera) {
		recalculateBoundingPolygon(UpdateTime.POST_COLLISIONS, camera, input);
	}

	@Override
	public Collection<? extends DrawableEntity> getDrawables() {
		return Collections.singleton(this);
	}

	@Override
	public void move(Direction to) {
		moves.add(to);
	}

	@Override
	public Position getPosition() {
		return pos;
	}

	@Override
	public void setPosition(Position pos) {
		this.pos.set(pos);
	}
}
