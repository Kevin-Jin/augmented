package amplified.map.entity;

public abstract class AutoTransform {
	public static class Scale extends AutoTransform {
		private final double finalDw, finalDh;
		private final double rateW, rateH;
		private double elapsed;
		private boolean inProgress;
		private double wInit, hInit;

		public Scale(double startAfter, double endAfter, double finalDw, double finalDh) {
			super(startAfter, endAfter);
			this.finalDw = finalDw;
			this.finalDh = finalDh;
			this.rateW = finalDw / (endAfter - startAfter);
			this.rateH = finalDh / (endAfter - startAfter);
		}

		@Override
		public void transform(Entity ent, double tDelta) {
			elapsed += tDelta;
			if (elapsed > endAfter) {
				if (inProgress) {
					if (rateW != 0)
						ent.setWidth(finalDw + wInit);
					if (rateH != 0)
						ent.setHeight(finalDh + hInit);
					inProgress = false;
				}
			} else if (elapsed > startAfter) {
				if (!inProgress) {
					wInit = ent.getWidth();
					hInit = ent.getHeight();
					inProgress = true;
				}
				if (rateW != 0)
					ent.setWidth(rateW * (elapsed - startAfter) + wInit);
				if (rateH != 0)
					ent.setHeight(rateH * (elapsed - startAfter) + hInit);
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
				ent.getPosition().set(velX * (elapsed - startAfter) + xInit, velY * (elapsed - startAfter) + yInit);
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
				ent.setRotation((float) (velAng * (elapsed - startAfter) + rotInit));
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
