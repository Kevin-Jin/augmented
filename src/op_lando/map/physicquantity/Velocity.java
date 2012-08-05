package op_lando.map.physicquantity;

import org.lwjgl.util.vector.Vector2f;

public class Velocity {
	private double x;
	private double y;

	public Velocity(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Velocity() {
		this(0, 0);
	}

	public double getX() {
		return x;
	}

	public void setX(double value) {
		x = value;
	}

	public double getY() {
		return y;
	}

	public void setY(double value) {
		y = value;
	}

	public void setScalarComponents(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setEuclideanVector(double magnitude, double direction) {
		x = magnitude * Math.cos(direction);
		y = magnitude * Math.sin(direction);
	}

	public Vector2f asVector() {
		return new Vector2f((float) x, (float) y);
	}

	public void set(Velocity vel) {
		this.x = vel.x;
		this.y = vel.y;
	}
}