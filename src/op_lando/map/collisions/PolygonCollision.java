package op_lando.map.collisions;

import org.lwjgl.util.vector.Vector2f;

public class PolygonCollision {
	public static CollisionResult collision(Polygon a, Polygon b) {
		Vector2f axis = new Vector2f(1, 1);
		Vector2f translationAxis = new Vector2f(0, 0);
		Vector2f collisionEdge = new Vector2f(0, 0);

		final int NOTFOUND = -1;
		float minIntervalDistance = NOTFOUND;

		float intervalDist;
		float tmp, maxA, minA, minB, maxB;
		for (int i = 0; i < a.getVertexCount(); ++i) {
			Vector2f edge = a.getEdges()[i];
			axis = new Vector2f(-edge.getY(), edge.getX());
			axis.normalise();
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
				intervalDist = Math.abs(intervalDist);
				if (intervalDist < minIntervalDistance || minIntervalDistance == NOTFOUND) {
					minIntervalDistance = intervalDist;
					collisionEdge = edge;
					translationAxis = axis;
				}
			}
		}

		for (int i = 0; i < b.getVertexCount(); ++i) {
			Vector2f edge = b.getEdges()[i];
			axis = new Vector2f(-edge.getY(), edge.getX());
			axis.normalise();
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
				intervalDist = Math.abs(intervalDist);
				if (intervalDist < minIntervalDistance || minIntervalDistance == NOTFOUND) {
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
		return new CollisionResult(translationAxis, collisionEdge, null);
	}

	private static float intervalDistance(float minA, float maxA, float minB, float maxB) {
		if (minA < minB)
			return minB - maxA;
		else
			return minA - maxB;
	}

	public static CollisionResult boundingPolygonCollision(BoundingPolygon a, BoundingPolygon b) {
		CollisionResult largestTranslation = new CollisionResult();
		for (Polygon polygonA : a.getPolygons()) {
			for (Polygon polygonB : b.getPolygons()) {
				CollisionResult result = collision(polygonA, polygonB);
				if (result.collision() && (!largestTranslation.collision() || largestTranslation.getCollisionInformation().getMinimumTranslationVector().lengthSquared() < result.getCollisionInformation().getMinimumTranslationVector().lengthSquared()))
					largestTranslation = result;
			}
		}
		return largestTranslation;
	}
}
