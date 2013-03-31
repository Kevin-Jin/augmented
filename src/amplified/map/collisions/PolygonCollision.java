package amplified.map.collisions;

import org.lwjgl.util.vector.Vector2f;

import amplified.map.CollidableDrawable;

public class PolygonCollision {
	private static void normalize(Vector2f v) {
		try {
			v.normalise();
		} catch (IllegalStateException e) {
			v.set(Float.NaN, Float.NaN);
		}
	}

	/**
	 * Static SAT if final area of polygon a collides with the origin area of polygon b.
	 * Otherwise, performs a swept SAT check across the swept area of both polygons.
	 * @param a Polygon A
	 * @param b Polygon B
	 * @param vr Relative velocity between polygons A and B
	 * @param tDelta the highest value of tEnter allowed - polygons A and B will not
	 * continuously move for any longer than this value on this frame.
	 * @return
	 */
	public static CollisionResult collision(Polygon a, Polygon b, Vector2f vr, float tDelta) {
		Vector2f displacement = new Vector2f(vr.getX() * tDelta, vr.getY() * tDelta);
		Vector2f axis;
		Vector2f translationAxis = null, sweptTranslationAxis = null;
		Vector2f collisionEdge = null, sweptCollisionEdge = null;

		boolean noOriginAreaCollisions = false;
		float tEnterMax = Float.NEGATIVE_INFINITY;
		float tExitMin = Float.POSITIVE_INFINITY;
		float minIntervalDistance = Float.POSITIVE_INFINITY;

		//since we only care about static SAT of the origin area of polygon b, and both
		//passed polygons are the final areas, we want to subtract the displacement from
		//each of polygon b's vertices to find its world space origin area vertices.
		//the displacement must then be subtracted from the resultant translation vector.
		float intervalDist;
		float tmp, maxA, minA, minB, maxB;
		for (int i = 0; i < a.getVertexCount(); ++i) {
			Vector2f edge = a.getEdges()[i];
			axis = new Vector2f(-edge.getY(), edge.getX());
			normalize(axis);
			minA = maxA = Vector2f.dot(a.getVertices()[0], axis);
			for (int j = 1; j < a.getVertexCount(); ++j) {
				tmp = Vector2f.dot(a.getVertices()[j], axis);
				if (tmp > maxA)
					maxA = tmp;
				else if (tmp < minA)
					minA = tmp;
			}
			minB = maxB = Vector2f.dot(Vector2f.sub(b.getVertices()[0], displacement, null), axis);
			for (int j = 1; j < b.getVertexCount(); ++j) {
				tmp = Vector2f.dot(Vector2f.sub(b.getVertices()[j], displacement, null), axis);
				if (tmp > maxB)
					maxB = tmp;
				else if (tmp < minB)
					minB = tmp;
			}
			intervalDist = intervalDistance(minA, maxA, minB, maxB);
			if (intervalDist > 0) {
				noOriginAreaCollisions = true;
			} else {
				intervalDist *= -1;
				if (intervalDist < minIntervalDistance) {
					minIntervalDistance = intervalDist;
					collisionEdge = edge;
					translationAxis = axis;
				}
			}

			// Convert the slab for B into a 1D ray
			float origin = (maxB + minB) * 0.5f; // Origin of ray
			float extent = (maxB - minB) * 0.5f; // Extent of slab B
			float direction = Vector2f.dot(vr, axis); // Direction of ray (projected onto current axis of separation)

			// Expand the slab for A by the extent of the slab for B This transforms a slab/slab collision test into a ray/slab collision test
			minA -= extent;
			maxA += extent;

			// Do a 1 dimensional collision check on projected axis
			final float TOLERANCE = 0.0001f;

			// If ray is parallel to the slab
			if (Math.abs(direction) < TOLERANCE) {
				// Ray is parallel to slab, but NOT inside the slab
				if (origin < minA || origin > maxA)
					return new CollisionResult();
			} else {
				// Compute intersection t value of ray with near and far plane of slab
				float tEnter = (minA - origin) / direction;
				float tExit = (maxA - origin) / direction;
	
				// Make "tEnter" be intersection with near plane, "tExit" with far plane
				if (tEnter > tExit) {
					float temp = tEnter;
					tEnter = tExit;
					tExit = temp;
				}

				// Compute the intersection of slab intersection intervals
				if (tEnter > tEnterMax) {
					tEnterMax = tEnter;
					sweptCollisionEdge = edge;
					sweptTranslationAxis = axis;
				}
				tExitMin = Math.min(tExitMin, tExit);

				// Exit with no collision as soon as slab intersection becomes empty
				if (tEnterMax > tExitMin)
					return new CollisionResult();
			}
		}

		for (int i = 0; i < b.getVertexCount(); ++i) {
			Vector2f edge = b.getEdges()[i];
			axis = new Vector2f(-edge.getY(), edge.getX());
			normalize(axis);
			minB = maxB = Vector2f.dot(axis, Vector2f.sub(b.getVertices()[0], displacement, null));
			for (int j = 1; j < b.getVertexCount(); ++j) {
				tmp = Vector2f.dot(axis, Vector2f.sub(b.getVertices()[j], displacement, null));
				if (tmp > maxB)
					maxB = tmp;
				else if (tmp < minB)
					minB = tmp;
			}
			minA = maxA = Vector2f.dot(a.getVertices()[0], axis);
			for (int j = 1; j < a.getVertexCount(); ++j) {
				tmp = Vector2f.dot(a.getVertices()[j], axis);
				if (tmp > maxA)
					maxA = tmp;
				else if (tmp < minA)
					minA = tmp;
			}
			intervalDist = intervalDistance(minA, maxA, minB, maxB);
			if (intervalDist > 0) {
				noOriginAreaCollisions = true;
			} else {
				intervalDist *= -1;
				if (intervalDist < minIntervalDistance) {
					minIntervalDistance = intervalDist;
					collisionEdge = edge;
					translationAxis = axis;
				}
			}

			// Convert the slab for B into a 1D ray
			float origin = (maxB + minB) * 0.5f; // Origin of ray
			float extent = (maxB - minB) * 0.5f; // Extent of slab B
			float direction = Vector2f.dot(vr, axis); // Direction of ray (projected onto current axis of separation)

			// Expand the slab for A by the extent of the slab for B This transforms a slab/slab collision test into a ray/slab collision test
			minA -= extent;
			maxA += extent;

			// Do a 1 dimensional collision check on projected axis
			final float TOLERANCE = 0.0001f;

			// If ray is parallel to the slab
			if (Math.abs(direction) < TOLERANCE) {
				// Ray is parallel to slab, but NOT inside the slab
				if (origin < minA || origin > maxA)
					return new CollisionResult();
			} else {
				// Compute intersection t value of ray with near and far plane of slab
				float tEnter = (minA - origin) / direction;
				float tExit = (maxA - origin) / direction;

				// Make "tEnter" be intersection with near plane, "tExit" with far plane
				if (tEnter > tExit) {
					float temp = tEnter;
					tEnter = tExit;
					tExit = temp;
				}

				// Compute the intersection of slab intersection intervals
				if (tEnter > tEnterMax) {
					tEnterMax = tEnter;
					sweptCollisionEdge = edge;
					sweptTranslationAxis = axis;
				}
				tExitMin = Math.min(tExitMin, tExit);

				// Exit with no collision as soon as slab intersection becomes empty
				if (tEnterMax > tExitMin)
					return new CollisionResult();
			}
		}
		if (tEnterMax < 0) {
			if (noOriginAreaCollisions)
				return new CollisionResult();
			Vector2f d = Vector2f.sub(a.getCenter(), Vector2f.sub(b.getCenter(), displacement, null), null);
			if (Vector2f.dot(d, translationAxis) > 0.0f)
				translationAxis.negate();
			translationAxis.scale(minIntervalDistance);
			//only bring the entity back to the origin position (so that minimum
			//translation vector can be applied to it) if displacement is
			//towards the other colliding entity
			if (tExitMin > 0)
				Vector2f.sub(translationAxis, displacement, translationAxis);
		} else if (tEnterMax <= tDelta) {
			translationAxis = new Vector2f(vr);
			translationAxis.scale(tEnterMax);

			//allow the object to move anywhere as long as it is not into the
			//object it is colliding with (i.e. subtract displacement that is
			//perpendicular to the colliding surface from overall displacement
			//and then add to it the vr vector scaled to tEnterMax in order to
			//get actual translationAxis)
			Vector2f newDisp = new Vector2f(displacement);
			//TODO: this is a temporary fix for getting stuck on platforms ONLY.
			//need a generalized solution using vector maths
			//this does not stop boxes from crossing thin, vertical walls
			if (Math.abs(sweptTranslationAxis.getX()) > 0.1)
				newDisp.setX(0);
			if (Math.abs(sweptTranslationAxis.getY()) > 0.1f)
				newDisp.setY(0);
			Vector2f.add(translationAxis, newDisp, translationAxis);

			Vector2f.sub(translationAxis, displacement, translationAxis);

			collisionEdge = sweptCollisionEdge;
		} else {
			return new CollisionResult();
		}

		return new CollisionResult(translationAxis, collisionEdge);
	}

	private static float intervalDistance(float minA, float maxA, float minB, float maxB) {
		if (minA < minB)
			return minB - maxA;
		else
			return minA - maxB;
	}

	public static CollisionResult boundingPolygonCollision(CollidableDrawable a, CollidableDrawable b, float tDelta) {
		Vector2f vr = Vector2f.sub(b.getVelocity().asVector(), a.getVelocity().asVector(), null); //relative velocity
		Polygon[] bPolygons = b.getBoundingPolygon().getPolygons();

		CollisionResult largestTranslation = new CollisionResult();
		for (Polygon polygonA : a.getBoundingPolygon().getPolygons()) {
			for (Polygon polygonB : bPolygons) {
				CollisionResult result = collision(polygonA, polygonB, vr, tDelta);
				if (result.collision() && (!largestTranslation.collision() || largestTranslation.getCollisionInformation().getMinimumTranslationVector().lengthSquared() < result.getCollisionInformation().getMinimumTranslationVector().lengthSquared()))
					largestTranslation = result;
			}
		}
		return largestTranslation;
	}
}
