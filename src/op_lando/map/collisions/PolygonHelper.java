package op_lando.map.collisions;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

public class PolygonHelper {
	private static boolean equal(Vector2f a, Vector2f b) {
		final float TOLERANCE = 0.001f;
		return Math.abs(a.getX() - b.getX()) < TOLERANCE && Math.abs(a.getY() - b.getY()) < TOLERANCE;
	}

	private static boolean undefined(Vector2f a) {
		return Float.isNaN(a.getX()) && Float.isNaN(a.getY());
	}

	private static boolean intersects(Vector2f[] line, Polygon... polygons) {
		for (Polygon polygon : polygons) {
			Vector2f[] vertices = polygon.getVertices();
			for (int i = 0; i < vertices.length; i++) {
				Vector2f intersect = BoundingPolygon.findIntersection(vertices[i], vertices[(i + 1) % vertices.length], line[0], line[1]);
				if (!undefined(intersect) && !equal(intersect, line[0]) && !equal(intersect, line[1]))
					return true;
			}
		}
		return false;
	}

	private static Vector2f[] preimageToImage(Vector2f a, Vector2f translation) {
		return new Vector2f[] { a, Vector2f.add(a, translation, null) };
	}

	//java's remainder operator returns the negative remainder instead of the
	//positive one, so we have to make sure that the left side of the operation
	//is never negative. otherwise, we would get a negative index.
	private static int positiveMod(int a, int b) {
		return (a + b) % b;
	}

	public static Polygon createPolygon(Polygon p, Vector2f translation) {
		Vector2f[] vertices = new Vector2f[p.getVertexCount()];
		for (int i = 0; i < p.getVertexCount(); ++i)
			vertices[i] = Vector2f.add(p.getVertices()[i], translation, null);

		return new Polygon(vertices);
	}

	public static Polygon polygonRepresentingTranslation(Polygon startPoly, Vector2f translation) {
		Vector2f[] preimage = startPoly.getVertices();
		List<Vector2f> ret = new ArrayList<Vector2f>();
		boolean noEdgesParallelWithTranslation = true;
		for (int i = 0; i < preimage.length && noEdgesParallelWithTranslation; i++)
			if (startPoly.getEdges()[i].y / startPoly.getEdges()[i].x == translation.y / translation.x)
				noEdgesParallelWithTranslation = false;

		if (noEdgesParallelWithTranslation) {
			Polygon endPoly = createPolygon(startPoly, translation);
			//start from a preimage point. if the line segment from the preimage
			//point to its corresponding image point does not intersect either
			//the preimage or image polygons (besides at the vertices/endpoints),
			//connect those vertices. let A be the vertex on the preimage and A'
			//be the vertex on the image. connect A' to its adjacent image
			//vertex such that the slope of edge formed is closer to the
			//direction of translation than if the other vertex was connected.
			//continue connecting adjacent vertices B' in the same direction
			//until the line segment from B' to B only intersects the preimage
			//and image polygons at vertices/endpoints. Connect B' to B and then
			//connect adjacent preimage vertices in the OPPOSITE direction that
			//you went in the image. continue connecting until you hook back up
			//with A.
			for (int i = 0; i < preimage.length; i++) {
				Vector2f[] line = preimageToImage(preimage[i], translation);
				if (!intersects(line, startPoly, endPoly)) {
					int start = i;
					ret.add(line[0]);
					ret.add(line[1]);
					Vector2f[] lineAdd = preimageToImage(preimage[positiveMod(i + 1, preimage.length)], translation);
					Vector2f[] lineSub = preimageToImage(preimage[positiveMod(i - 1, preimage.length)], translation);

					//we can substitute finding the smaller of the slopes of the
					//edge between A' and each adjacent vertex by testing which
					//vertex is farthest from A.

					//1 if greater indexed adjacent vertex is farther away from
					//A, -1 if smaller indexed adjacent vertex is farther.
					int direction = (int) Math.signum(Vector2f.sub(lineAdd[1], line[0], null).lengthSquared() - Vector2f.sub(lineSub[1], line[0], null).lengthSquared());
					do {
						i = positiveMod(i + direction, preimage.length);
						line = preimageToImage(preimage[i], translation);
						ret.add(line[1]);
					} while (intersects(line, startPoly, endPoly));
					do {
						ret.add(preimage[i]);
						i = positiveMod(i - direction, preimage.length);
					} while (i != start);
					break;
				}
			}
		} else {
			int halfVertices = (preimage.length + 1) / 2; //ceiling of half of vertex count
			//find points of preimage closest in direction of translation
			//we can substitute finding the smallest 'halfVertices' slopes
			//between the center and each preimage vertex by testing which
			//vertices are closest to the same image vertex.
			Vector2f imageVertex = Vector2f.add(preimage[0], translation, null);
			Vector2f[] sortedPreimageCopy = new Vector2f[preimage.length];
			System.arraycopy(preimage, 0, sortedPreimageCopy, 0, preimage.length);
			//helps us order sortedPreimageCopy by vertex order after the
			//closest 'halfVertices' vertices have been moved to the front of
			//the array
			int[] indexMap = new int[preimage.length];
			for (int i = 0; i < preimage.length; i++)
				indexMap[i] = i;
			//only move the first 'halfVertices' vertices, sorted by distance to
			//imageVertex, to the front of sortedPreimageCopy
			for (int i = 0; i < halfVertices; i++) {
				int swap = i;
				float minLength = Vector2f.sub(sortedPreimageCopy[i], imageVertex, null).lengthSquared();
				for (int j = i + 1; j < sortedPreimageCopy.length; j++) {
					float length2 = Vector2f.sub(sortedPreimageCopy[j], imageVertex, null).lengthSquared();
					if (length2 < minLength) {
						swap = j;
						minLength = length2;
					}
				}
				if (swap != i) {
					Vector2f temp = sortedPreimageCopy[swap];
					sortedPreimageCopy[swap] = sortedPreimageCopy[i];
					sortedPreimageCopy[i] = temp;
					int temp2 = indexMap[swap];
					indexMap[swap] = indexMap[i];
					indexMap[i] = temp2;
				}
			}
			//selection sort first 'halfVertices' vertices by vertex index
			for (int i = 0; i < halfVertices - 1; i++) {
				int swap = i;
				for (int j = i + 1; j < halfVertices; j++)
					if (indexMap[j] < indexMap[swap])
						swap = j;
				if (swap != i) {
					Vector2f temp = sortedPreimageCopy[swap];
					sortedPreimageCopy[swap] = sortedPreimageCopy[i];
					sortedPreimageCopy[i] = temp;
					int temp2 = indexMap[swap];
					indexMap[swap] = indexMap[i];
					indexMap[i] = temp2;
				}
			}
			for (int i = 0; i < halfVertices; i++)
				ret.add(sortedPreimageCopy[i]);
			for (int i = halfVertices - 1; i >= 0; i--)
				ret.add(Vector2f.add(sortedPreimageCopy[i], translation, null));
		}
		return new Polygon(ret.toArray(new Vector2f[ret.size()]));
	}

	public static BoundingPolygon boundingPolygonRepresentingTranslation(BoundingPolygon b, Vector2f translation) {
		Polygon[] polygons = new Polygon[b.getPolygons().length];
		for (int i = 0; i < b.getPolygons().length; i++)
			polygons[i] = polygonRepresentingTranslation(b.getPolygons()[i], translation);
		return new BoundingPolygon(polygons);
	}
}
