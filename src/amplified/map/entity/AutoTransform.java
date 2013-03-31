package amplified.map.entity;

import org.newdawn.slick.Color;

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
		public void transform(AutoTransformable ent, double tDelta) {
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
		public void transform(AutoTransformable ent, double tDelta) {
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
		private static final double TWO_PI = Math.PI * 2;
		private static final double NORMALIZE_CENTER = 0; //[-PI, PI]

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

		private float normalize(double a) {
			return (float) (a - TWO_PI * Math.floor((a + Math.PI - NORMALIZE_CENTER) / TWO_PI));
		}

		@Override
		public void transform(AutoTransformable ent, double tDelta) {
			elapsed += tDelta;
			if (elapsed > endAfter) {
				if (inProgress) {
					ent.setRotation(normalize(finalDtheta + rotInit));
					inProgress = false;
				}
			} else if (elapsed > startAfter) {
				if (!inProgress) {
					rotInit = ent.getRotation();
					if (ent.flipHorizontally())
						rotInit += Math.PI;
					inProgress = true;
				}
				ent.setRotation(normalize(velAng * (elapsed - startAfter) + rotInit));
			}
		}

		@Override
		public void reset() {
			elapsed = 0;
			inProgress = false;
			rotInit = 0;
		}
	}

	public static class Caption extends AutoTransform {
		private final String font;
		private final Color color;
		private final String text;
		private final double cursorAfter, noCursorAfter;
		private final double period;
		private int index;
		private double elapsed, elapsedSinceStart;
		private boolean initialized;
		private boolean hadCursor;

		public Caption(double startAfter, double endAfter, String font, Color color, String text, double cursorAfter, double noCursorAfter) {
			super(startAfter, endAfter);
			this.font = font;
			this.color = color;
			this.text = text;
			this.cursorAfter = cursorAfter;
			this.noCursorAfter = noCursorAfter;
			period = (endAfter - startAfter) / text.length();
		}

		private boolean showCursor() {
			return cursorAfter != -1 && elapsed > cursorAfter && (noCursorAfter == -1 || elapsed <= noCursorAfter);
		}

		private void updateCaption(AutoTransformable ent) {
			String cap = text.substring(0, index);
			if (showCursor())
				cap += "_";
			ent.setCaption(cap);
		}

		@Override
		public void transform(AutoTransformable ent, double tDelta) {
			elapsed += tDelta;
			if (!initialized) {
				ent.initializeCaption(font, color);
				if (showCursor()) {
					hadCursor = true;
					ent.setCaption("_");
				}
				initialized = true;
			}
			if (elapsed > startAfter) {
				elapsedSinceStart += tDelta;
				int lettersTyped;
				if (period != 0)
					lettersTyped = (int) (elapsedSinceStart / period);
				else if (index < text.length())
					lettersTyped = text.length();
				else
					lettersTyped = 0;
				if (lettersTyped > 0) {
					elapsedSinceStart -= lettersTyped * period;
					index = Math.min(lettersTyped + index, text.length());
					updateCaption(ent);
				} else if (showCursor() ^ hadCursor) {
					updateCaption(ent);
				}
			} else if (showCursor() ^ hadCursor) {
				updateCaption(ent);
			}
		}

		@Override
		public void reset() {
			elapsed = 0;
			elapsedSinceStart = 0;
			index = 0;
			initialized = false;
		}
	}

	protected final double startAfter, endAfter;

	public AutoTransform(double startAfter, double endAfter) {
		this.startAfter = startAfter;
		this.endAfter = endAfter;
	}

	public abstract void transform(AutoTransformable ent, double tDelta);

	public abstract void reset();
}
