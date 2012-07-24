package op_lando.resources;

import java.util.Collection;
import java.util.Map;

import op_lando.map.Platform;
import op_lando.map.physicquantity.Position;

public class LevelLayout {
	private final Map<Byte, Platform> footholds;
	private final double yDeceleration;
	private final double yVelocityMin;
	private final int width;
	private final int height;
	private Position startPos;
	private String nextMap;
	private String outsideBg, insideBg;
	private double expiration;

	public LevelLayout(int width, int height, Map<Byte, Platform> footholds, Position startPos, int yDeceleration, int yVelocityMin, String nextMap, String outsideBg, String insideBg, double expiration) {
		this.width = width;
		this.height = height;
		this.footholds = footholds;
		this.startPos = startPos;
		this.yDeceleration = yDeceleration;
		this.yVelocityMin = yVelocityMin;
		this.nextMap = nextMap;
		this.outsideBg = outsideBg;
		this.insideBg = insideBg;
		this.expiration = expiration;
	}

	public double getGravitationalFieldStrength() {
		return yDeceleration;
	}

	public double getTerminalVelocity() {
		return yVelocityMin;
	}

	public Collection<Platform> getPlatforms() {
		return footholds.values();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Position getStartPosition() {
		return startPos;
	}

	public String getNextMap() {
		return nextMap;
	}

	public String getOutsideBackground() {
		return outsideBg;
	}

	public String getInsideBackground() {
		return insideBg;
	}

	public double getExpiration() {
		return expiration;
	}
}
