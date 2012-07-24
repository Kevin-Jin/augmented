package op_lando;

import java.util.Collection;

public interface Entity {
	Collection<? extends Drawable> getDrawables();

	Position getPosition();

	boolean flipHorizontally();

	float getRotation();

	void update(double tDelta);
}
