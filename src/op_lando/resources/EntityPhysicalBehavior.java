package op_lando.resources;

public class EntityPhysicalBehavior {
	private double xVelocityMax;
	private double xAcceleration;
	private double xDeceleration;
	private double yVelocityMax;
	private double yAcceleration;
	private double tMaxJump;

	public EntityPhysicalBehavior(double velXMax, double accelX, double decelX, double velYMax, double accelY, double maxJumpT) {
		xVelocityMax = velXMax;
		xAcceleration = accelX;
		xDeceleration = decelX;
		yVelocityMax = velYMax;
		yAcceleration = accelY;
		tMaxJump = maxJumpT;
	}

	public double getWalkAcceleration() {
		return xAcceleration;
	}

	public double getStopDeceleration() {
		return xDeceleration;
	}

	public double getMaxWalkVelocity() {
		return xVelocityMax;
	}

	public double getJetPackAcceleration() {
		return yAcceleration;
	}

	public double getJetPackMaxVelocity() {
		return yVelocityMax;
	}

	public double getMaxJumpTime() {
		return tMaxJump;
	}
}
