package op_lando.map.entity.props;

import org.lwjgl.util.vector.Vector2f;

import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.entity.SimpleEntity;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.map.state.MapState;
import op_lando.resources.EntityKinematics;

public abstract class SelectableEntity extends SimpleEntity {
	private static final float SCALE_RATE = 1f;
	private static final float ANGULAR_VELOCITY = (float) Math.PI; //in radians / second
	private static final float ROTATE_INTERVAL = (float) Math.PI / 2;
	private final float minScale, maxScale;
	private float rot;
	private byte rotateState;
	private float scale;
	private boolean continueRot, dragged;

	protected SelectableEntity(BoundingPolygon boundPoly, float minScale, float maxScale) {
		super(boundPoly, new EntityKinematics());
		this.minScale = minScale;
		this.maxScale = maxScale;
		scale = 1;
	}

	public void downScale(double tDelta) {
		float oldWidth = getWidth();
		scale -= SCALE_RATE * tDelta;
		if (scale < minScale)
			scale = minScale;
		pos.add((oldWidth - getWidth()) / 2, (oldWidth - getWidth()) / 2);
	}

	public void upScale(double tDelta) {
		float oldWidth = getWidth();
		scale += SCALE_RATE * tDelta;
		if (scale > maxScale)
			scale = maxScale;
		pos.add((oldWidth - getWidth()) / 2, (oldWidth - getWidth()) / 2);
	}

	public void rotateCounterClockwise() {
		rotateState = 1;
		continueRot = true;
	}

	public void rotateClockwise() {
		rotateState = -1;
		continueRot = true;
	}

	public void drag(int x, int y) {
		dragged = true;
		startPos.set(pos);
		pos.add(x, y);
	}

	@Override
	public int getMovabilityIndex() {
		return 1;
	}

	@Override
	public float getWidth() {
		return scale * super.getWidth();
	}

	@Override
	public float getHeight() {
		return scale * super.getHeight();
	}

	@Override
	public float getRotation() {
		return rot;
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		if (rotateState == 1) {
			if (continueRot) {
				if (rot > 2 * Math.PI)
					rot -= 2 * Math.PI;
				rot += ANGULAR_VELOCITY * tDelta;
			} else {
				float stop = ROTATE_INTERVAL + (rot - (float) (rot % ROTATE_INTERVAL));
				rot += ANGULAR_VELOCITY * tDelta;
				if (rot >= stop) {
					rot = stop;
					rotateState = 0;
				}
			}
		} else if (rotateState == -1) {
			if (continueRot) {
				if (rot < 0)
					rot += 2 * Math.PI;
				rot -= ANGULAR_VELOCITY * tDelta;
			} else {
				float stop = rot - (float) (rot % ROTATE_INTERVAL);
				rot -= ANGULAR_VELOCITY * tDelta;
				if (rot <= stop) {
					rot = stop;
					rotateState = 0;
				}
			}
		}
		continueRot = false;
		if (dragged) {
			dragged = false;
			EntityKinematics temp = motionProperties;
			motionProperties = null;
			Position savedStartPos = new Position(startPos.getX(), startPos.getY());
			super.preCollisionsUpdate(tDelta, input, camera, map);
			startPos.set(savedStartPos);
			vel.setScalarComponents(0, 0);
			motionProperties = temp;
		} else {
			super.preCollisionsUpdate(tDelta, input, camera, map);
		}
	}

	@Override
	public Vector2f getOrigin() {
		return getCenter();
	}
}
