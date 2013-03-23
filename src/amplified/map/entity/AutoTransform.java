package amplified.map.entity;

public abstract class AutoTransform {
	public static class Scale extends AutoTransform {
		private final double finalFactor;
		private final double rate;

		public Scale(double startAfter, double endAfter, double finalFactor) {
			super(startAfter, endAfter);
			this.finalFactor = finalFactor;
			this.rate = finalFactor / (endAfter - startAfter);
		}
	}

	public static class Translate extends AutoTransform {
		private final double finalDx, finalDy;
		private final double velX, velY;

		public Translate(double startAfter, double endAfter, double finalDx, double finalDy) {
			super(startAfter, endAfter);
			this.finalDx = finalDx;
			this.finalDy = finalDy;
			this.velX = finalDx / (endAfter - startAfter);
			this.velY = finalDy / (endAfter - startAfter);
		}
	}

	public static class Rotate extends AutoTransform {
		private final double finalDtheta;
		private final double angVel;

		public Rotate(double startAfter, double endAfter, double finalDtheta) {
			super(startAfter, endAfter);
			this.finalDtheta = finalDtheta;
			this.angVel = finalDtheta / (endAfter - startAfter);
		}
	}

	private final double startAfter, endAfter;

	public AutoTransform(double startAfter, double endAfter) {
		this.startAfter = startAfter;
		this.endAfter = endAfter;
	}
}
