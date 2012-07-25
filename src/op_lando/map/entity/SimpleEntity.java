package op_lando.map.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import op_lando.map.Collidable;
import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.entity.player.TractorBeam;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;

public abstract class SimpleEntity extends CollidableDrawable implements DrawableEntity {
	private final Position pos;

	protected SimpleEntity(BoundingPolygon boundPoly) {
		super(boundPoly, boundPoly);
		pos = new Position();
	}

	@Override
	public boolean collision(CollisionInformation collisionInfo, List<Collidable> otherCollidables) {
		Collidable other = collisionInfo.getCollidedWith();
		if (other instanceof TractorBeam) {
			collisionInfo.setCollidedWith(this);
			collisionInfo.negateMinimumTranslationVector();
			other.collision(collisionInfo, otherCollidables);
			return false;
		} else {
			Vector2f negationVector = collisionInfo.getMinimumTranslationVector();
			pos.setX(pos.getX() + negationVector.getX());
			pos.setY(pos.getY() + negationVector.getY());

			transformedBoundPoly = BoundingPolygon.transformBoundingPolygon(baseBoundPoly, this);
		}
		return true;
	}

	@Override
	public void update(double tDelta, Input input, Camera camera) {
		transformedBoundPoly = BoundingPolygon.transformBoundingPolygon(baseBoundPoly, this);
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
		this.pos.setX(pos.getX());
		this.pos.setY(pos.getY());
	}
}
