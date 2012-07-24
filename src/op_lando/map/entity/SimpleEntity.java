package op_lando.map.entity;

import java.util.Collection;
import java.util.Collections;

import op_lando.map.CollidableDrawable;
import op_lando.map.Drawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Input;

public abstract class SimpleEntity extends CollidableDrawable implements DrawableEntity {
	private final Position pos;

	protected SimpleEntity(BoundingPolygon boundPoly) {
		super(boundPoly, boundPoly);
		pos = new Position();
	}

	@Override
	public void update(double tDelta, Input input) {
		transformedBoundPoly = BoundingPolygon.transformBoundingPolygon(baseBoundPoly, this);
	}

	@Override
	public Collection<? extends Drawable> getDrawables() {
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
