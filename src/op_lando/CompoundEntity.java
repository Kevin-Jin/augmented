package op_lando;

public abstract class CompoundEntity implements Entity {
	protected abstract Entity getBody();

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
}
