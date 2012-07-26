package op_lando.map.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import op_lando.map.AbstractCollidable;
import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.physicquantity.Acceleration;
import op_lando.map.physicquantity.Position;
import op_lando.map.physicquantity.Velocity;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.map.state.MapState;
import op_lando.resources.EntityPhysicalBehavior;

import org.lwjgl.util.vector.Vector2f;

public abstract class SimpleEntity extends AbstractCollidable implements DrawableEntity {
	protected final Position pos;
	protected final Velocity vel;
	protected final Acceleration accel;
	protected final EntityPhysicalBehavior physics;

	//TODO: load EntityPhysicalBehavior from a cache?
	protected SimpleEntity(BoundingPolygon boundPoly, EntityPhysicalBehavior quantities) {
		super(boundPoly, boundPoly);
		pos = new Position();
		vel = new Velocity();
		accel = new Acceleration();
		physics = quantities;
	}

	@Override
	public void collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables) {
		Vector2f negationVector = collisionInfo.getMinimumTranslationVector();
		pos.add(negationVector.getX(), negationVector.getY());
		if (negationVector.getY() >= 0 && collisionInfo.getCollidingSurface().getY() == 0)
			vel.setY(0);
		else if (negationVector.getY() < 0 && collisionInfo.getCollidingSurface().getY() == 0)
			vel.setY(0);

		transformedBoundPoly = BoundingPolygon.transformBoundingPolygon(baseBoundPoly, this);
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		transformedBoundPoly = BoundingPolygon.transformBoundingPolygon(baseBoundPoly, this);
	}

	@Override
	public void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log) {
		
	}

	@Override
	public Collection<? extends DrawableEntity> getDrawables() {
		return Collections.singleton(this);
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
