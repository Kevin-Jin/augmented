package op_lando.map.physicquantity;

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
}
