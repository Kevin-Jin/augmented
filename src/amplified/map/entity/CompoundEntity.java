package amplified.map.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import amplified.map.CollidableDrawable;
import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.CollisionInformation;
import amplified.map.physicquantity.Position;
import amplified.map.state.Camera;
import amplified.map.state.Input;
import amplified.map.state.MapState;

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
	public void move(Direction to) {
		getBody().move(to);
	}

	@Override
	public Position getPosition() {
		return getBody().getPosition();
	}

	@Override
	public void setPosition(Position pos) {
		getBody().setPosition(pos);
		for (AuxiliaryEntity<E> child : getAuxiliaries())
			child.setPosition(new Position(getBody().getAttachPoint(child.getType())));
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
	public void setRotation(float rot) {
		getBody().setRotation(rot);
		for (AuxiliaryEntity<E> child : getAuxiliaries())
			child.setRotation(rot);
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		List<BoundingPolygon> polygons = new ArrayList<BoundingPolygon>(drawables.size());

		getBody().preCollisionsUpdate(tDelta, input, camera, map);
		if (getBody().isVisible() && getBody().getSelfBoundingPolygon() != null)
			polygons.add(getBody().getSelfBoundingPolygon());
		for (AuxiliaryEntity<E> child : getAuxiliaries()) {
			child.setFlip(getBody().flipHorizontally());
			child.markStartPosition();
			child.setPosition(new Position(getBody().getAttachPoint(child.getType())));
			child.preCollisionsUpdate(tDelta, input, camera, map);
			if (child.isVisible() && child.getSelfBoundingPolygon() != null)
				polygons.add(child.getSelfBoundingPolygon());
		}
		getBody().setBoundingPolygon(new BoundingPolygon(polygons.toArray(new BoundingPolygon[polygons.size()])));
	}

	@Override
	public void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log, Camera camera) {
		List<BoundingPolygon> polygons = new ArrayList<BoundingPolygon>(drawables.size());

		getBody().postCollisionsUpdate(tDelta, input, log, camera);
		if (getBody().isVisible() && getBody().getSelfBoundingPolygon() != null)
			polygons.add(getBody().getSelfBoundingPolygon());
		for (AuxiliaryEntity<E> child : getAuxiliaries()) {
			child.setFlip(getBody().flipHorizontally());
			child.setPosition(new Position(getBody().getAttachPoint(child.getType())));
			child.postCollisionsUpdate(tDelta, input, log, camera);
			if (child.isVisible() && child.getSelfBoundingPolygon() != null)
				polygons.add(child.getSelfBoundingPolygon());
		}
		getBody().setBoundingPolygon(new BoundingPolygon(polygons.toArray(new BoundingPolygon[polygons.size()])));
	}
}
