package op_lando;

import java.util.Collection;
import java.util.Collections;

public abstract class SimpleEntity extends AbstractDrawable implements Entity {
	public Collection<? extends Drawable> getDrawables() {
		return Collections.singleton(this);
	}
}
