package op_lando.map.entity;

import java.util.Collection;
import java.util.Collections;

import op_lando.map.CollidableDrawable;
import op_lando.map.Drawable;
import op_lando.map.physicquantity.Position;

public abstract class SimpleEntity extends CollidableDrawable implements Entity {
	private Position pos;

	protected SimpleEntity() {
		pos = new Position();
	}

	@Override
	public void update(double tDelta) {
		
	}

	@Override
	public Collection<? extends Drawable> getDrawables() {
		return Collections.singleton(this);
	}

	@Override
	public Position getPosition() {
		return pos;
	}
}
