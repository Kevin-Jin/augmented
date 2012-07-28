package op_lando.map.entity.player;

import java.util.List;

import op_lando.map.CollidableDrawable;
import op_lando.map.collisions.BoundingPolygon;
import op_lando.map.collisions.CollisionInformation;
import op_lando.map.collisions.Polygon;
import op_lando.map.entity.AuxiliaryEntity;
import op_lando.map.entity.SimpleEntity;
import op_lando.map.physicquantity.Position;
import op_lando.map.state.Camera;
import op_lando.map.state.Input;
import op_lando.map.state.MapState;
import op_lando.resources.SoundCache;
import op_lando.resources.TextureCache;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;

public class TractorBeam extends SimpleEntity implements AuxiliaryEntity<PlayerPart> {
	private static final float SHOOT_VELOCITY = 1000f; //pixels per second
	private final int[] TOP_VERTEX_INDEX = { 0, 0 };
	private final int[] BOTTOM_VERTEX_INDEX = { 0, 1 };

	private final Player parent;
	private float rot;
	private float length;
	private final Position compliantPos;
	private CollidableDrawable selection;
	private Vector2f pointInSelected;

	public TractorBeam(Player parent) {
		super(new BoundingPolygon(new Polygon[] {
			new Polygon(new Vector2f[] {
				new Vector2f(0, 0),
				new Vector2f(0, 18),
				new Vector2f(210, 18),
				new Vector2f(210, 0)
			})
		}), null);

		this.parent = parent;
		compliantPos = new Position();
	}

	private void lengthUpdated() {
		compliantPos.set(pos.getX(), pos.getY() + getOrigin().getY() * -getScale().getY() - getHeight());
	}

	private void beginExtend() {
		SoundCache.getSound("beam").playAsSoundEffect(1, 1, true);
	}

	private void extend(double tDelta) {
		if (selection == null)
			length += SHOOT_VELOCITY * tDelta;
		else
			length = (float) Math.sqrt(Math.pow(pos.getX() - getBeamHit().getX(), 2) + Math.pow(pos.getY() - getBeamHit().getY(), 2));
	}

	private void beginRetract() {
		selection = null;
		SoundCache.getSound("beam").stop();
	}

	private void retract(double tDelta) {
		selection = null;
		length -= SHOOT_VELOCITY * tDelta;
		if (length < 0)
			length = 0;
	}

	public boolean isBeamHit() {
		return selection != null;
	}

	public Position getBeamHit() {
		return new Position(Matrix4f.transform(selection.getWorldMatrix(), new Vector4f(pointInSelected.getX(), pointInSelected.getY(), 1, 1), null));
	}

	private void setBeamHit(Position value) {
		pointInSelected = new Vector2f(Matrix4f.transform(Matrix4f.invert(selection.getWorldMatrix(), null), value.asVector4f(), null));
	}

	private Position getTopCornerPosition() {
		return new Position(transformedBoundPoly.getPolygons()[TOP_VERTEX_INDEX[0]].getVertices()[TOP_VERTEX_INDEX[1]]);
	}

	private Position getBottomCornerPosition() {
		return new Position(transformedBoundPoly.getPolygons()[BOTTOM_VERTEX_INDEX[0]].getVertices()[BOTTOM_VERTEX_INDEX[1]]);
	}

	@Override
	public void collision(CollisionInformation collisionInfo, List<CollidableDrawable> otherCollidables) {
		CollidableDrawable other = collisionInfo.getCollidedWith();
		if (selection != other && other != parent.getBody()) {
			Position hitPos = new Position(pos.getX() + length * Math.cos(rot), pos.getY() + length * Math.sin(rot));
			selection = other;
			Vector2f hitPosVector = hitPos.asVector();
			Vector2f lengthVector = Vector2f.sub(hitPosVector, pos.asVector(), null);
			if (!other.getBoundingPolygon().isPointInsideBoundingPolygon(hitPosVector) && !other.getBoundingPolygon().isPointInsideBoundingPolygon(Vector2f.add(getBottomCornerPosition().asVector(), lengthVector, null)) && !other.getBoundingPolygon().isPointInsideBoundingPolygon(Vector2f.add(getTopCornerPosition().asVector(), lengthVector, null))) {
				// our beam overextends to the collided entity (the front of the
				// beam does not actually hit the collided entity), so trim it
				// assert that either the bottom or the top edge of the beam had
				// to have intersected one of the edges of the collided entity

				// find the intersection of the line that travels in between the
				// two edges of the beam, and the closest edge of the collided
				// entity though center of beam's front does not necessarily
				// have to intersect with the collided entity, if it does, we
				// can make the grabbing process smoother and less "jumpy"
				// because we change the angle of the player less
				Vector2f polygonEdgeIntersect = other.getBoundingPolygon().closestPoint(pos, hitPos);
				if (!Float.isNaN(polygonEdgeIntersect.getX()) && !Float.isNaN(polygonEdgeIntersect.getY())) {
					// an intersection was made
					hitPos = new Position(polygonEdgeIntersect);
					length = (float) Math.sqrt(Math.pow(pos.getX() - hitPos.getX(), 2) + Math.pow(pos.getY() - hitPos.getY(), 2));
					recalculateBoundingPolygon(UpdateTime.COLLISION, null, null);
					lengthUpdated();
				} else {
					// if our center line did not intersect, find the
					// intersection of the bottom edge of the beam and the
					// closest edge of the collided entity
					polygonEdgeIntersect = other.getBoundingPolygon().closestPoint(getBottomCornerPosition(), new Position(Vector2f.add(transformedBoundPoly.getPolygons()[BOTTOM_VERTEX_INDEX[0]].getVertices()[BOTTOM_VERTEX_INDEX[1]], lengthVector, null)));
					if (!Float.isNaN(polygonEdgeIntersect.getX()) && !Float.isNaN(polygonEdgeIntersect.getY())) {
						// an intersection was made
						hitPos = new Position(polygonEdgeIntersect);
						length = (float) Math.sqrt(Math.pow(pos.getX() - hitPos.getX(), 2) + Math.pow(pos.getY() - hitPos.getY(), 2));
						recalculateBoundingPolygon(UpdateTime.COLLISION, null, null);
						lengthUpdated();
					} else {
						// if neither our bottom edge nor center line
						// intersected, find the intersection of the top edge of
						// the beam and the closest edge of the collided entity
						polygonEdgeIntersect = other.getBoundingPolygon().closestPoint(getTopCornerPosition(), new Position(Vector2f.add(transformedBoundPoly.getPolygons()[TOP_VERTEX_INDEX[0]].getVertices()[TOP_VERTEX_INDEX[1]], lengthVector, null)));
						if (!Float.isNaN(polygonEdgeIntersect.getX()) && !Float.isNaN(polygonEdgeIntersect.getY())) {
							// an intersection was made
							hitPos = new Position(polygonEdgeIntersect);
							length = (float) Math.sqrt(Math.pow(pos.getX() - hitPos.getX(), 2) + Math.pow(pos.getY() - hitPos.getY(), 2));
							recalculateBoundingPolygon(UpdateTime.COLLISION, null, null);
							lengthUpdated();
						}
					}
				}
			}
			setBeamHit(hitPos);
		}
	}

	@Override
	public int getMovabilityIndex() {
		return Integer.MAX_VALUE;
	}

	@Override
	public BoundingPolygon getSelfBoundingPolygon() {
		return null;
	}

	@Override
	public void preCollisionsUpdate(double tDelta, Input input, Camera camera, MapState map) {
		if (input.pressedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			beginExtend();
		else if (input.heldButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			extend(tDelta);
		else if (input.releasedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
			beginRetract();
		else if (length > 0)
			retract(tDelta);
		if (selection != null && selection.getMovabilityIndex() != 0) {
			//TODO: drag
			//set angle of velocity from selection's current pos to mouse cursor
			//and use very high magnitude (magnitude of translation * 60?)
			//make sure to update our length!
		}
		lengthUpdated();

		super.preCollisionsUpdate(tDelta, input, camera, map);
	}

	@Override
	public Vector2f getOrigin() {
		return new Vector2f(0, getTexture().getImageHeight() / 2f);
	}

	@Override
	public Position getPosition() {
		return compliantPos;
	}

	@Override
	public void setPosition(Position pos) {
		super.setPosition(pos);
		extend(0);
		lengthUpdated();
	}

	@Override
	public float getWidth() {
		return length;
	}

	@Override
	public float getHeight() {
		return length == 0 ? 0 : super.getHeight();
	}

	@Override
	public float getRotation() {
		return rot;
	}

	public void setRotation(float rot) {
		this.rot = rot;
	}

	@Override
	public void setFlip(boolean flip) {
		if (flip)
			rot += Math.PI;
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture("beam");
	}

	@Override
	public PlayerPart getType() {
		return PlayerPart.BEAM;
	}
}
