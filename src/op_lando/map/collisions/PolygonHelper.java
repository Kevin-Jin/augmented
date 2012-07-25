package op_lando.map.collisions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

public class PolygonHelper {
	public static Polygon createPolygon(Polygon p, Vector2f translation) {
		Vector2f[] vertices = new Vector2f[p.getVertexCount()];
		for (int i = 0; i < p.getVertexCount(); ++i)
			vertices[i] = Vector2f.add(p.getVertices()[i], translation, null);

		return new Polygon(vertices);
	}

	public static Polygon polygonRepresentingTranslation(Polygon startPoly, Vector2f translation) {
		final boolean sameSign = Math.signum(translation.getX()) == Math.signum(translation.getY());
		int vertexcount = startPoly.getVertexCount();
		Polygon endPoly = createPolygon(startPoly, translation);

		Polygon polyForTop = (translation.getY() > 0) ? endPoly : startPoly;
		Polygon polyForBot = (polyForTop == startPoly) ? endPoly : startPoly;
		List<Vector2f> topVertices = new ArrayList<Vector2f>(vertexcount * 2);
		List<Vector2f> bottomVertices = new ArrayList<Vector2f>(vertexcount * 2);
		float minX = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < vertexcount; ++i) {
			// top left clockwise
			Vector2f vertex = polyForTop.getVertices()[i];
			if (vertex.getX() < minX) {
				minX = vertex.getX();
				if (vertex.getX() > maxX)
					maxX = vertex.getX();
				topVertices.add(vertex);
			} else if (!sameSign && vertex.getX() == minX) {
				topVertices.add(vertex);
			} else if (vertex.getX() > maxX) {
				maxX = vertex.getX();
				topVertices.add(vertex);
			} else if (sameSign && vertex.getX() == maxX) {
				topVertices.add(vertex);
			}
		}

		Collections.sort(topVertices, new Comparator<Vector2f>() {
			@Override
			public int compare(Vector2f a, Vector2f b) {
				int x = Float.compare(a.getX(), b.getX());
				if (x != 0)
					return x;
				return (sameSign) ? Float.compare(b.getY(), a.getY()) : Float.compare(a.getY(), b.getY());
			}
		});

		minX = Float.POSITIVE_INFINITY;
		maxX = Float.NEGATIVE_INFINITY;
		for (int i = vertexcount - 1; i >= 0; --i) {
			// bottom left counterclockwise
			Vector2f vertex = polyForBot.getVertices()[i];
			if (vertex.getX() < minX) {
				minX = vertex.getX();
				if (vertex.getX() > maxX)
					maxX = vertex.getX();
				bottomVertices.add(vertex);
			} else if (sameSign && vertex.getX() == minX) {
				bottomVertices.add(vertex);
			} else if (vertex.getX() > maxX) {
				maxX = vertex.getX();
				bottomVertices.add(vertex);
			} else if (!sameSign && vertex.getX() == maxX) {
				bottomVertices.add(vertex);
			}
		}

		Collections.sort(bottomVertices, new Comparator<Vector2f>() {
			@Override
			public int compare(Vector2f a, Vector2f b) {
				int x = Float.compare(b.getX(), a.getX());
				if (x != 0)
					return x;
				return (sameSign) ? Float.compare(a.getY(), b.getY()) : Float.compare(b.getY(), a.getY());
			}
		});

		topVertices.addAll(bottomVertices);

		return new Polygon(topVertices.toArray(new Vector2f[topVertices.size()]));
	}

	public static BoundingPolygon boundingPolygonRepresentingTranslation(BoundingPolygon b, Vector2f translation) {
		Polygon[] polygons = new Polygon[b.getPolygons().length];
		for (int i = 0; i < b.getPolygons().length; i++)
			polygons[i] = polygonRepresentingTranslation(b.getPolygons()[i], translation);
		return new BoundingPolygon(polygons);
	}
}
