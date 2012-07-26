package op_lando.map.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.util.vector.Vector2f;

import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.map.state.MapState;

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

	public void updateChildPositionsAndPolygons(Vector2f delta) {
		for (AuxiliaryEntity<E> child : getAuxiliaries()) {
			child.getPosition().add(delta.getX(), delta.getY());
			child.recalculateSelfBoundingPolygon();
		}
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
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		List<BoundingPolygon> polygons = new ArrayList<BoundingPolygon>(drawables.size());

		getBody().preCollisionsUpdate(tDelta, input, camera, map);
		if (getBody().isVisible() && getBody().getSelfBoundingPolygon() != null)
			polygons.add(getBody().getSelfBoundingPolygon());
		for (AuxiliaryEntity<E> child : getAuxiliaries()) {
			child.setPosition(new Position(getBody().getAttachPoint(child.getType())));
			child.setFlip(getBody().flipHorizontally());
			child.preCollisionsUpdate(tDelta, input, camera, map);
			if (child.isVisible() && child.getSelfBoundingPolygon() != null)
				polygons.add(child.getSelfBoundingPolygon());
		}
		getBody().setBoundingPolygon(new BoundingPolygon(polygons.toArray(new BoundingPolygon[polygons.size()])));
	}

	@Override
	public void postCollisionsUpdate(double tDelta, Input input, Map<CollidableDrawable, Set<CollisionInformation>> log) {
		for (Entity component : getDrawables())
			component.postCollisionsUpdate(tDelta, input, log);
	}
}
