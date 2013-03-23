package amplified.map.entity;

public abstract class AutoTransform {
	public static class Scale extends AutoTransform {
		private final double finalFactor;
		private final double rate;
		private double elapsed;
		private boolean inProgress;
		private double wInit, hInit;

		public Scale(double startAfter, double endAfter, double finalFactor) {
			super(startAfter, endAfter);
			this.finalFactor = finalFactor;
			this.rate = finalFactor / (endAfter - startAfter);
		}

		@Override
		public void transform(Entity ent, double tDelta) {
			elapsed += tDelta;
			if (elapsed > endAfter) {
				if (inProgress) {
					//ent.setWidth(finalFactor * wInit);
					//ent.setHeight(finalFactor * hInit);
					inProgress = false;
				}
			} else if (elapsed > startAfter) {
				if (!inProgress) {
					//wInit = ent.getWidth();
					//hInit = ent.getHeight();
					inProgress = true;
				}
				//ent.setWidth(wInit * rate * elapsed);
				//ent.setWidth(hInit * rate * elapsed);
			}
		}

		@Override
		public void reset() {
			elapsed = 0;
			inProgress = false;
			wInit = hInit = 0;
		}
	}

	public static class Translate extends AutoTransform {
		private final double finalDx, finalDy;
		private final double velX, velY;
		private double elapsed;
		private boolean inProgress;
		private double xInit, yInit;

		public Translate(double startAfter, double endAfter, double finalDx, double finalDy) {
			super(startAfter, endAfter);
			this.finalDx = finalDx;
			this.finalDy = finalDy;
			this.velX = finalDx / (endAfter - startAfter);
			this.velY = finalDy / (endAfter - startAfter);
		}

		@Override
		public void transform(Entity ent, double tDelta) {
			elapsed += tDelta;
			if (elapsed > endAfter) {
				if (inProgress) {
					ent.getPosition().set(xInit + finalDx, yInit + finalDy);
					inProgress = false;
				}
			} else if (elapsed > startAfter) {
				if (!inProgress) {
					xInit = ent.getPosition().getX();
					yInit = ent.getPosition().getY();
					inProgress = true;
				}
				ent.getPosition().add(velX * tDelta, velY * tDelta);
			}
		}

		@Override
		public void reset() {
			elapsed = 0;
			inProgress = false;
			xInit = yInit = 0;
		}
	}

	public static class Rotate extends AutoTransform {
		private final double finalDtheta;
		private final double velAng;
		private double elapsed;
		private boolean inProgress;
		private double rotInit;

		public Rotate(double startAfter, double endAfter, double finalDtheta) {
			super(startAfter, endAfter);
			this.finalDtheta = finalDtheta;
			velAng = finalDtheta / (endAfter - startAfter);
		}

		@Override
		public void transform(Entity ent, double tDelta) {
			elapsed += tDelta;
			if (elapsed > endAfter) {
				if (inProgress) {
					ent.setRotation((float) (finalDtheta + rotInit));
					inProgress = false;
				}
			} else if (elapsed > startAfter) {
				if (!inProgress) {
					rotInit = ent.getRotation();
					inProgress = true;
				}
				ent.setRotation((float) (velAng * elapsed + rotInit));
			}
		}

		@Override
		public void reset() {
			elapsed = 0;
			inProgress = false;
			rotInit = 0;
		}
	}

	protected final double startAfter, endAfter;

	public AutoTransform(double startAfter, double endAfter) {
		this.startAfter = startAfter;
		this.endAfter = endAfter;
	}

	public abstract void transform(Entity ent, double tDelta);

	public abstract void reset();
}
