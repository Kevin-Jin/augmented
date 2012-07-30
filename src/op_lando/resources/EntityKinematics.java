package op_lando.resources;

//TODO: load EntityPhysicalBehavior from a cache, like LevelCache? maybe even
//a bounding polygon cache for all CollidableDrawables. so we can one day maybe
//load these from files instead of hard coding stuff? Tom loves caches.
public class EntityKinematics {
	private double xVelocityMax;
	private double xAcceleration;
	private double xDeceleration;
	private double yVelocityMax;
	private double yAcceleration;
	private double tMaxJump;

	public EntityKinematics(double velXMax, double accelX, double decelX, double velYMax, double accelY, double maxJumpT) {
		xVelocityMax = velXMax;
		xAcceleration = accelX;
		xDeceleration = decelX;
		yVelocityMax = velYMax;
		yAcceleration = accelY;
		tMaxJump = maxJumpT;
	}

	public EntityKinematics() {
		
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
