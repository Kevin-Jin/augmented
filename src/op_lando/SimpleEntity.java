package op_lando;

import java.util.Collection;
import java.util.Collections;

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
