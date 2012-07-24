package op_lando.map.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;

public abstract class CompoundEntity<E extends Enum<E>> implements Entity {
	private List<DrawableEntity> drawables;
	private BoundingPolygon universal;

	protected abstract BodyEntity<E> getBody();

	protected abstract Collection<? extends AuxiliaryEntity<E>> getAuxiliaries();

	protected void partsConstructed() {
		drawables = new ArrayList<DrawableEntity>(getAuxiliaries().size() + 1);
		drawables.add(getBody());
		drawables.addAll(getAuxiliaries());
		drawables = Collections.unmodifiableList(drawables);

		BoundingPolygon[] all = new BoundingPolygon[drawables.size()];
		for (int i = 0; i < all.length; i++)
			all[i] = drawables.get(i).getBoundingPolygon();
		universal = new BoundingPolygon(all);
	}

	@Override
	public List<DrawableEntity> getDrawables() {
		return drawables;
	}

	@Override
	public Position getPosition() {
		return getBody().getPosition();
	}

	@Override
	public void setPosition(Position pos) {
		getBody().setPosition(pos);
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
	public void update(double tDelta, Input input, Camera camera) {
		getBody().update(tDelta, input, camera);
		for (AuxiliaryEntity<E> child : getAuxiliaries()) {
			child.setPosition(new Position(getBody().getAttachPoint(child.getType())));
			child.setFlip(getBody().flipHorizontally());
			child.update(tDelta, input, camera);
		}
		getBody().setBoundingPolygon(universal);
	}
}
