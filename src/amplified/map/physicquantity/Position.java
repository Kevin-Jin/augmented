package amplified.map.physicquantity;

import org.lwjgl.util.vector.ReadableVector2f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class Position {
	private double x;
	private double y;

	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Position(ReadableVector2f vector) {
		x = vector.getX();
		y = vector.getY();
	}

	public Position() {
		this(0, 0);
	}

	public double getX() {
		return x;
	}

	public void setX(double newX) {
		this.x = newX;
	}

	public double getY() {
		return y;
	}

	public void setY(double newY) {
		this.y = newY;
	}

	public void set(Position pos) {
		x = pos.getX();
		y = pos.getY();
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void add(double deltaX, double deltaY) {
		x += deltaX;
		y += deltaY;
	}

	public Vector2f asVector() {
		return new Vector2f((float) x, (float) y);
	}

	public Vector4f asVector4f() {
		return new Vector4f((float) x, (float) y, 1, 1);
	}
}
