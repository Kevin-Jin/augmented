package op_lando.map.collisions;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

public class PolygonHelper {
	private static boolean equal(Vector2f a, Vector2f b) {
		final float TOLERANCE = 0.05f;
		return Math.abs(a.getX() - b.getX()) < TOLERANCE && Math.abs(a.getY() - b.getY()) < TOLERANCE;
	}

	private static boolean undefined(Vector2f a) {
		return Float.isNaN(a.getX()) && Float.isNaN(a.getY());
	}

	private static boolean intersects(Vector2f[] line, Polygon... polygons) {
		for (Polygon polygon : polygons) {
			Vector2f[] vertices = polygon.getVertices();
			for (int i = 0; i < vertices.length; i++) {
				Vector2f intersect = BoundingPolygon.findIntersection(vertices[i], vertices[(i + 1) % vertices.length], line[0], line[1], true, false);
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

	private static boolean equal(float a, float b) {
		final float TOLERANCE = 0.0001f;
		return Math.abs(a - b) < TOLERANCE;
	}

	public static Polygon polygonRepresentingTranslation(Polygon startPoly, Vector2f translation) {
		if (translation.getX() == 0 && translation.getY() == 0)
			return startPoly;

		Polygon endPoly = createPolygon(startPoly, translation);
		Vector2f[] preimage = startPoly.getVertices();
		List<Vector2f> ret = new ArrayList<Vector2f>();

		//convex polygons can at most have two parallel edges
		int numEdgesParallel = 0;
		int[] edgesParallelWithTranslation = new int[2];
		for (int i = 0; i < preimage.length && numEdgesParallel < 2; i++)
			if (startPoly.getEdges()[i].x == 0 && translation.x == 0 || equal(startPoly.getEdges()[i].y / startPoly.getEdges()[i].x, translation.y / translation.x))
				edgesParallelWithTranslation[numEdgesParallel++] = i;

		//TODO: these two cases have striking parallels. maybe there is some
		//way to elegantly merge them.
		if (numEdgesParallel == 0) {
			//start from a preimage point. if the line segment from the preimage
			//point to its corresponding image point does not intersect either
			//the preimage or image polygons (besides at the vertices/endpoints),
			//connect those vertices. let A be the vertex on the preimage and A'
			//be the vertex on the image. connect A' to its adjacent image
			//vertex such that the angle formed between the translation vector
			//and the vector from A' to the vertex is smaller than that formed
			//between the translation vector and the vector from A' to the
			//other adjacent vertex. continue connecting adjacent vertices B' in
			//the same direction until the line segment from B' to B only
			//intersects the preimage and image polygons at vertices/endpoints.
			//Connect B' to B and then connect adjacent preimage vertices in the
			//same direction that you went in the image. continue connecting
			//until you hook back up with A.
			for (int i = 0; i < preimage.length; i++) {
				Vector2f[] preimageToImagePoints = preimageToImage(preimage[i], translation);
				if (!intersects(preimageToImagePoints, startPoly, endPoly)) {
					int start = i;
					ret.add(preimageToImagePoints[0]);
					ret.add(preimageToImagePoints[1]);

					//TODO: trig for Vector2f.angle takes too long. somehow use
					//slopes instead?
					Vector2f vectorAdd = Vector2f.sub(preimage[positiveMod(i + 1, preimage.length)], preimage[i], null);
					Vector2f vectorSub = Vector2f.sub(preimage[positiveMod(i - 1, preimage.length)], preimage[i], null);
					float thetaAdd = Vector2f.angle(translation, vectorAdd);
					float thetaSub = Vector2f.angle(translation, vectorSub);
					int direction = (int) Math.signum(thetaSub - thetaAdd);

					do {
						i = positiveMod(i + direction, preimage.length);
						preimageToImagePoints = preimageToImage(preimage[i], translation);
						ret.add(preimageToImagePoints[1]);
					} while (intersects(preimageToImagePoints, startPoly, endPoly));
					do {
						ret.add(preimage[i]);
						i = positiveMod(i + direction, preimage.length);
					} while (i != start);
					break;
				}
			}
		} else {
			Vector2f[] endPoints = new Vector2f[4];
			int[] vertexIndices = new int[2];
			for (int i = 0; i < numEdgesParallel; i++) {
				int edgeIndex = edgesParallelWithTranslation[i];
				Vector2f[] verts = new Vector2f[] { preimage[edgeIndex], preimage[positiveMod(edgeIndex + 1, preimage.length)] };
				//equivalent to (startPoly.getCenter() + endPoly.getCenter()) / 2
				Vector2f center = Vector2f.add(startPoly.getCenter(), new Vector2f(translation.getX() / 2, translation.getY() / 2), null);
				if (Vector2f.sub(verts[0], center, null).lengthSquared() > Vector2f.sub(verts[1], center, null).lengthSquared()) {
					endPoints[i * 2] = verts[0];
					endPoints[i * 2 + 1] = Vector2f.add(verts[1], translation, null);
					vertexIndices[i] = edgeIndex;
				} else {
					endPoints[i * 2] = verts[1];
					endPoints[i * 2 + 1] = Vector2f.add(verts[0], translation, null);
					vertexIndices[i] = positiveMod(edgeIndex + 1, preimage.length);
				}
			}
			if (numEdgesParallel == 1) {
				int usedEdge = edgesParallelWithTranslation[0];
				for (int i = 0; i < preimage.length; i++) {
					if (i != usedEdge && i != usedEdge + 1) {
						Vector2f[] line = preimageToImage(preimage[i], translation);
						if (!intersects(line, startPoly, endPoly)) {
							endPoints[2] = line[0];
							endPoints[3] = line[1];
							vertexIndices[1] = i;
							break;
						}
					}
				}
			}
			Vector2f[] preimageToImagePoints = endPoints;
			int i = vertexIndices[0];
			ret.add(preimageToImagePoints[0]);
			ret.add(preimageToImagePoints[1]);

			//TODO: trig for Vector2f.angle takes too long. somehow use slopes
			//instead?
			Vector2f vectorAdd = Vector2f.sub(preimage[positiveMod(i + 1, preimage.length)], preimage[i], null);
			Vector2f vectorSub = Vector2f.sub(preimage[positiveMod(i - 1, preimage.length)], preimage[i], null);
			float thetaAdd = Vector2f.angle(translation, vectorAdd);
			float thetaSub = Vector2f.angle(translation, vectorSub);
			int direction = (int) Math.signum(thetaSub - thetaAdd);

			do {
				i = positiveMod(i + direction, preimage.length);
				preimageToImagePoints = preimageToImage(preimage[i], translation);
				ret.add(preimageToImagePoints[1]);
			} while (!equal(preimageToImagePoints[1], endPoints[3]));
			do {
				ret.add(preimage[i]);
				i = positiveMod(i + direction, preimage.length);
			} while (i != vertexIndices[0]);
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
