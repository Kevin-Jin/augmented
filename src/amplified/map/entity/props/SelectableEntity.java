package amplified.map.entity.props;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import amplified.map.CollidableDrawable;
import amplified.map.Platform;
import amplified.map.collisions.BoundingPolygon;
import amplified.map.collisions.CollisionResult;
import amplified.map.collisions.PolygonCollision;
import amplified.map.entity.SimpleEntity;
import amplified.map.entity.player.TractorBeam;
import amplified.map.physicquantity.Position;
import amplified.map.state.Camera;
import amplified.map.state.Input;
import amplified.map.state.MapState;
import amplified.resources.EntityKinematics;

public abstract class SelectableEntity extends SimpleEntity {
	private static final float SCALE_RATE = 1f;
	private static final float ANGULAR_VELOCITY = (float) Math.PI; //in radians / second
	private static final float ROTATE_INTERVAL = (float) Math.PI / 2;
	private final float minScale, maxScale;
	private float rot;
	private byte rotateState;
	private float scale;
	private boolean continueRot, selected;

	protected SelectableEntity(BoundingPolygon boundPoly, float initScale, float minScale, float maxScale) {
		super(boundPoly, new EntityKinematics());
		this.minScale = minScale;
		this.maxScale = maxScale;
		scale = initScale;
	}

	public void select() {
		selected = true;
		vel.setScalarComponents(0, 0);
	}

	public void unselect() {
		selected = false;
		vel.setScalarComponents(0, 0);
	}

	public void downScale(double tDelta, TractorBeam beam) {
		//Position targetBeamHit = beam.getBeamHit();
		float oldWidth = getWidth();
		scale -= SCALE_RATE * tDelta;
		if (scale < minScale)
			scale = minScale;
		//TODO: set pos so that afterwards, (beam.getBeamHit() == targetBeamHit)
		pos.add((oldWidth - getWidth()) / 2, (oldWidth - getWidth()) / 2);
	}

	private boolean collidesWith(double tDelta, List<CollidableDrawable> others) {
		for (CollidableDrawable curDrawable : others) {
			if (!(curDrawable instanceof TractorBeam) && curDrawable != this && curDrawable.isVisible()) {
				//ignore beam and self
				CollisionResult cr = PolygonCollision.boundingPolygonCollision(curDrawable, this, (float) tDelta);
				if (cr.collision() && (cr.getCollisionInformation().getMinimumTranslationVector().getX() != 0 || cr.getCollisionInformation().getMinimumTranslationVector().getY() != 0))
					return true;
			}
		}
		return false;
	}
	private boolean collidesWithPlatformsOnly(double tDelta, List<CollidableDrawable> others){
		for (CollidableDrawable curDrawable : others){
			if (!(curDrawable instanceof TractorBeam) && curDrawable != this && curDrawable.isVisible()) {
				CollisionResult cr = PolygonCollision.boundingPolygonCollision(curDrawable, this, (float) tDelta);
				if (cr.collision())
					if (curDrawable instanceof Platform){
						Vector2f vector = cr.getCollisionInformation().getMinimumTranslationVector();
						pos.add(vector.getX(), vector.getY());
					}
					else
						return false;
			}
		}
		return true;
	}

	public void upScale(double tDelta, TractorBeam beam, Input input, Camera camera, MapState map) {
		float oldScale = scale;
		Position oldPos = new Position(pos);

		float oldWidth = getWidth();
		scale += SCALE_RATE * tDelta;
		if (scale > maxScale)
			scale = maxScale;
		pos.add((oldWidth - getWidth()) / 2, (oldWidth - getWidth()) / 2);
		recalculateBoundingPolygon(UpdateTime.PRE_COLLISIONS, camera, input);
		boolean fail = !collidesWithPlatformsOnly(tDelta,map.getCollidables());
		
		if (!fail) {
			recalculateBoundingPolygon(UpdateTime.PRE_COLLISIONS, camera, input);
			fail = collidesWith(tDelta, map.getCollidables());
		}

		if (fail) {
			scale = oldScale;
			pos.set(oldPos);
			recalculateBoundingPolygon(UpdateTime.PRE_COLLISIONS, camera, input);
		}
	}

	public void rotateCounterClockwise() {
		rotateState = 1;
		continueRot = true;
	}

	public void rotateClockwise() {
		rotateState = -1;
		continueRot = true;
	}

	public void drag(int x, int y, double tDelta) {
		startPos.set(pos);
		vel.setScalarComponents(x / tDelta, y / tDelta);
	}

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

	public void setRotation(float rot) {
		this.rot = rot;
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
		if (selected) {
			EntityKinematics temp = motionProperties;
			motionProperties = null; //prevent superclass implementation from updating velocity and position
			Position savedStartPos = new Position(startPos);
			super.preCollisionsUpdate(tDelta, input, camera, map);
			startPos.set(savedStartPos);
			motionProperties = temp;
		} else {
			super.preCollisionsUpdate(tDelta, input, camera, map);
		}
	}

	@Override
	public Vector2f getOrigin() {
		return getCenter();
	}

	@Override
	public void setWidth(double w) {
		scale = (float) (w / super.getWidth());
	}

	@Override
	public void setHeight(double h) {
		scale = (float) (h / super.getHeight());
	}
}
