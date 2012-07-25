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

	protected abstract BodyEntity<E> getBody();

	protected abstract Collection<? extends AuxiliaryEntity<E>> getAuxiliaries();

	protected void partsConstructed() {
		drawables = new ArrayList<DrawableEntity>(getAuxiliaries().size() + 1);
		drawables.add(getBody());
		drawables.addAll(getAuxiliaries());
		drawables = Collections.unmodifiableList(drawables);
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
		List<BoundingPolygon> polygons = new ArrayList<BoundingPolygon>(drawables.size());

		getBody().update(tDelta, input, camera);
		if (getBody().isVisible() && getBody().getSelfBoundingPolygon() != null)
			polygons.add(getBody().getSelfBoundingPolygon());
		for (AuxiliaryEntity<E> child : getAuxiliaries()) {
			child.setPosition(new Position(getBody().getAttachPoint(child.getType())));
			child.setFlip(getBody().flipHorizontally());
			child.update(tDelta, input, camera);
			if (child.isVisible() && child.getSelfBoundingPolygon() != null)
				polygons.add(child.getSelfBoundingPolygon());
		}
		getBody().setBoundingPolygon(new BoundingPolygon(polygons.toArray(new BoundingPolygon[polygons.size()])));
	}
}
