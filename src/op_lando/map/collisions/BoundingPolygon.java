package op_lando.map.collisions;

import op_lando.map.Drawable;
import op_lando.map.physicquantity.Position;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class BoundingPolygon {
	private Polygon[] polygons;

	public BoundingPolygon(Polygon[] polygons) {
		this.polygons = polygons;
	}

	public BoundingPolygon(BoundingPolygon[] boundPolygons) {
		polygons = makeBoundingPolygon(boundPolygons);
	}

	public static Polygon[] makeBoundingPolygon(BoundingPolygon[] boundPolygons) {
		int size = 0;
		for (BoundingPolygon boundPolygon : boundPolygons)
			size += boundPolygon.getPolygons().length;
		Polygon[] polygons = new Polygon[size];
		int lastIndex = 0;
		for (BoundingPolygon boundPolygon : boundPolygons) {
			int polygonCount = boundPolygon.getPolygons().length;
			System.arraycopy(boundPolygon.getPolygons(), 0, polygons, lastIndex, polygonCount);
			lastIndex += polygonCount;
		}
		return polygons;
	}

	public Polygon[] getPolygons() {
		return polygons;
	}

	public void setPolygons(Polygon[] value) {
		polygons = value;
	}

	public static BoundingPolygon transformBoundingPolygon(BoundingPolygon boundingPoly, Drawable drawable) {
		Polygon[] oldPolygons = boundingPoly.polygons;
		Polygon[] polygons = new Polygon[oldPolygons.length];
		Matrix4f m = drawable.getWorldMatrix();
		for (int i = 0; i < polygons.length; ++i)
			polygons[i] = Polygon.transformPolygon(oldPolygons[i], m);
		return new BoundingPolygon(polygons);
	}

	public boolean isPointInsideBoundingPolygon(Vector2f point) {
		for (Polygon p : polygons)
			if (p.isPointInsidePolygon(point))
				return true;

		return false;
	}

	private static Vector2f findIntersection(Vector2f pointA1, Vector2f pointA2, Vector2f pointB1, Vector2f pointB2) {
		float ua = (pointB2.getX() - pointB1.getX()) * (pointA1.getY() - pointB1.getY()) - (pointB2.getY() - pointB1.getY()) * (pointA1.getX() - pointB1.getX());
		float ub = (pointA2.getX() - pointA1.getX()) * (pointA1.getY() - pointB1.getY()) - (pointA2.getY() - pointA1.getY()) * (pointA1.getX() - pointB1.getX());
		float denominator = (pointB2.getY() - pointB1.getY()) * (pointA2.getX() - pointA1.getX()) - (pointB2.getX() - pointB1.getX()) * (pointA2.getY() - pointA1.getY());

		if (denominator != 0) {
			ua /= denominator;
			ub /= denominator;

			// check if the intersection occurs on the segments, and not some
			// far distance away
			if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1)
				return new Vector2f(pointA1.getX() + ua * (pointA2.getX() - pointA1.getX()), pointA1.getY() + ua * (pointA2.getY() - pointA1.getY()));
		} else {
			// parallel
			if (ua == 0 && ub == 0)
				// coincident - infinity many intersection points
				return new Vector2f((pointA1.getX() + pointA2.getX()) / 2, (pointA1.getY() + pointA2.getY()) / 2);
		}

		return new Vector2f(Float.NaN, Float.NaN);
	}

	public Vector2f closestPoint(Position source, Position end) {
		Vector2f vect = new Vector2f(Float.NaN, Float.NaN);
		float smallestDistanceSquared = Float.POSITIVE_INFINITY;
		for (Polygon p : polygons) {
			Vector2f intersectPoint;
			for (int i = 0; i < p.getVertexCount(); i++) {
				intersectPoint = findIntersection(p.getVertices()[i], Vector2f.add(p.getVertices()[i], p.getEdges()[i], null), source.asVector(), end.asVector());
				if (!Float.isNaN(intersectPoint.getX()) && !Float.isNaN(intersectPoint.getY())) {
					float distanceSquaredFromThisEdge = Vector2f.sub(intersectPoint, source.asVector(), null).lengthSquared();
					if (distanceSquaredFromThisEdge < smallestDistanceSquared) {
						vect = intersectPoint;
						smallestDistanceSquared = distanceSquaredFromThisEdge;
					}
				}
			}
		}
		return vect;
	}

	@Override
	public String toString() {
		String s = "";
		for (Polygon p : polygons)
			s += p.toString() + "\n";
		return s;
	}
}
