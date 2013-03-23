package amplified.map.collisions;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import amplified.map.Drawable;

public class Polygon {
	protected final Vector2f[] vertices;
	private final int numOfVertices;
	protected final Vector2f[] edges;
	protected Vector2f center;

	public Polygon(Vector2f center, Vector2f[] vertices) {
		this.center = center;
		this.vertices = vertices;
		numOfVertices = vertices.length;
		edges = new Vector2f[numOfVertices];
		calculateEdges();
	}

	public Polygon(Vector2f[] vertices) {
		this.vertices = vertices;
		numOfVertices = vertices.length;
		edges = new Vector2f[numOfVertices];
		center = new Vector2f(0, 0);
		calculateCenter();
		calculateEdges();
	}

	public Polygon(Polygon p) {
		this.vertices = p.vertices;
		this.numOfVertices = p.vertices.length;
		this.edges = p.edges;
		this.center = p.center;
	}

	public int getVertexCount() {
		return numOfVertices;
	}

	public Vector2f[] getVertices() {
		return vertices;
	}

	public Vector2f[] getEdges() {
		return edges;
	}

	public Vector2f getCenter() {
		return center;
	}

	private void calculateCenter() {
		float cx = 0, cy = 0;
		for (Vector2f v : vertices) {
			cx += v.getX();
			cy += v.getY();
		}
		center.setX(cx / numOfVertices);
		center.setY(cy / numOfVertices);
	}

	private void calculateEdges() {
		for (int i = 0; i < numOfVertices - 1; ++i)
			edges[i] = Vector2f.sub(vertices[i + 1], vertices[i], null);
		edges[numOfVertices - 1] = Vector2f.sub(vertices[0], vertices[numOfVertices - 1], null);
	}

	public void transform(Matrix4f affineTransform) {
		for (int i = 0; i < numOfVertices; ++i)
			vertices[i] = new Vector2f(Matrix4f.transform(affineTransform, new Vector4f(vertices[i].getX(), vertices[i].getY(), 1, 1), null));
		center = new Vector2f(Matrix4f.transform(affineTransform, new Vector4f(center.getX(), center.getY(), 1, 1), null));
		calculateEdges();
	}

	public boolean isPointInsidePolygon(Vector2f point) {
		boolean inside = false;
		for (int i = 0; i < numOfVertices; ++i) {
			Vector2f cur = vertices[i];
			Vector2f next;
			if (i == numOfVertices - 1)
				next = vertices[0];
			else
				next = vertices[i + 1];
			float x = point.getX();
			float y = point.getY();
			if ((cur.getY() < y && next.getY() >= y) || (next.getY() < y && cur.getY() >= y))
				if (cur.getX() + (y - cur.getY()) / (next.getY() - cur.getY()) * (next.getX() - cur.getX()) < x)
					inside = !inside;
		}
		return inside;
	}

	public static Polygon transformPolygon(Polygon p, Matrix4f affineTransform) {
		Vector2f[] polygonVertices = new Vector2f[p.vertices.length];
		System.arraycopy(p.vertices, 0, polygonVertices, 0, polygonVertices.length);
		Polygon polygon = new Polygon(p.center, polygonVertices);
		polygon.transform(affineTransform);
		return polygon;
	}

	public static Polygon transformPolygon(Polygon p, Drawable drawable) {
		return transformPolygon(p, drawable.getWorldMatrix());
	}

	@Override
	public String toString() {
		String s = "Polygon with " + numOfVertices + " vertices. They are :\n";
		for (Vector2f v : vertices)
			s += v.toString() + "\n";
		return s;
	}
}
