package op_lando.map.collisions;

import op_lando.map.CollidableDrawable;

import org.lwjgl.util.vector.Vector2f;

public class PolygonCollision {
	private static void normalize(Vector2f v) {
		try {
			v.normalise();
		} catch (IllegalStateException e) {
			v.set(Float.NaN, Float.NaN);
		}
	}

	public static CollisionResult collision(Polygon a, Polygon b, Vector2f vr) {
		Vector2f axis;
		Vector2f translationAxis = null;
		Vector2f collisionEdge = null;

		float minIntervalDistance = Float.POSITIVE_INFINITY;

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
			minB = maxB = Vector2f.dot(b.getVertices()[0], axis);
			for (int j = 1; j < b.getVertexCount(); ++j) {
				tmp = Vector2f.dot(b.getVertices()[j], axis);
				if (tmp > maxB)
					maxB = tmp;
				else if (tmp < minB)
					minB = tmp;
			}
			intervalDist = intervalDistance(minA, maxA, minB, maxB);
			if (intervalDist > 0) {
				return new CollisionResult();
			} else {
				intervalDist *= -1;
				if (intervalDist < minIntervalDistance) {
					minIntervalDistance = intervalDist;
					collisionEdge = edge;
					translationAxis = axis;
				}
			}
		}

		for (int i = 0; i < b.getVertexCount(); ++i) {
			Vector2f edge = b.getEdges()[i];
			axis = new Vector2f(-edge.getY(), edge.getX());
			normalize(axis);
			minB = maxB = Vector2f.dot(axis, b.getVertices()[0]);
			for (int j = 1; j < b.getVertexCount(); ++j) {
				tmp = Vector2f.dot(axis, b.getVertices()[j]);
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
				return new CollisionResult();
			} else {
				intervalDist *= -1;
				if (intervalDist < minIntervalDistance) {
					minIntervalDistance = intervalDist;
					collisionEdge = edge;
					translationAxis = axis;
				}
			}
		}

		Vector2f d = Vector2f.sub(a.getCenter(), b.getCenter(), null);
		if (Vector2f.dot(d, translationAxis) > 0.0f)
			translationAxis.negate();
		translationAxis.scale(minIntervalDistance);
		return new CollisionResult(translationAxis, collisionEdge);
	}

	private static float intervalDistance(float minA, float maxA, float minB, float maxB) {
		if (minA < minB)
			return minB - maxA;
		else
			return minA - maxB;
	}

	public static CollisionResult boundingPolygonCollision(CollidableDrawable a, CollidableDrawable b) {
		Vector2f vr = Vector2f.sub(a.getVelocity().asVector(), b.getVelocity().asVector(), null); //relative velocity
		Polygon[] bPolygons = b.getBoundingPolygon().getPolygons();

		CollisionResult largestTranslation = new CollisionResult();
		for (Polygon polygonA : a.getBoundingPolygon().getPolygons()) {
			for (Polygon polygonB : bPolygons) {
				CollisionResult result = collision(polygonA, polygonB, vr);
				if (result.collision() && (!largestTranslation.collision() || largestTranslation.getCollisionInformation().getMinimumTranslationVector().lengthSquared() < result.getCollisionInformation().getMinimumTranslationVector().lengthSquared()))
					largestTranslation = result;
			}
		}
		return largestTranslation;
	}
}
