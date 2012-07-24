package op_lando;

import java.util.Collection;

public abstract class CompoundEntity implements Entity {
	protected abstract Entity getBody();

	@Override
	public abstract Collection<? extends SimpleEntity> getDrawables();

	@Override
	public Position getPosition() {
		return getBody().getPosition();
	}

	@Override
	public boolean flipHorizontally() {
		return getBody().flipHorizontally();
	}

	@Override
	public float getRotation() {
		return getBody().getRotation();
	}

	@Override
	public void update(double tDelta) {
		for (Entity child : getDrawables())
			child.update(tDelta);
	}
}
