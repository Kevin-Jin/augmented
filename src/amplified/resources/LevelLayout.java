package amplified.resources;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import amplified.map.Platform;
import amplified.map.RetractablePlatform;
import amplified.map.entity.AutoTransform;
import amplified.map.physicquantity.Position;
import amplified.resources.map.BoxSpawnInfo;
import amplified.resources.map.NBoxSpawnInfo;
import amplified.resources.map.OverlayInfo;
import amplified.resources.map.RectangleSpawnInfo;
import amplified.resources.map.SwitchSpawnInfo;

public class LevelLayout {
	private final Map<Byte, Platform> footholds;
	private final double yDeceleration;
	private final double yVelocityMin;
	private final int width;
	private final int height;
	private final Position startPos, endPos;
	private final List<AutoTransform> avatarAutoTransforms, beamAutoTransforms, legsAutoTransforms, exitAutoTransforms;
	private final List<BoxSpawnInfo> boxes;
	private final List<RectangleSpawnInfo> rects;
	private final List<NBoxSpawnInfo> nBoxes;
	private final List<SwitchSpawnInfo> switches;
	private final List<OverlayInfo> tips;
	private final List<RetractablePlatform> doors;
	private final String nextMap;
	private final String outsideBg, insideBg;
	private final double expiration;
	private final boolean cutscene;

	public LevelLayout(int width, int height, Map<Byte, Platform> footholds, Position startPos, List<AutoTransform> avatarAutoTransforms, List<AutoTransform> beamAutoTransforms, List<AutoTransform> legsAutoTransforms, Position endPos, List<AutoTransform> exitAutoTransforms, double yDeceleration, double yVelocityMin, List<BoxSpawnInfo> boxes, List<RectangleSpawnInfo> rects, List<NBoxSpawnInfo> nBoxes, List<SwitchSpawnInfo> switches, List<OverlayInfo> tips, List<RetractablePlatform> doors, String nextMap, String outsideBg, String insideBg, double expiration, boolean cutscene) {
		this.width = width;
		this.height = height;
		this.footholds = footholds;
		this.startPos = startPos;
		this.avatarAutoTransforms = avatarAutoTransforms;
		this.beamAutoTransforms = beamAutoTransforms;
		this.endPos = endPos;
		this.exitAutoTransforms = exitAutoTransforms;
		this.legsAutoTransforms = legsAutoTransforms;
		this.yDeceleration = yDeceleration;
		this.yVelocityMin = yVelocityMin;
		this.boxes = boxes;
		this.rects = rects;
		this.nBoxes = nBoxes;
		this.switches = switches;
		this.tips = tips;
		this.doors = doors;
		this.nextMap = nextMap;
		this.outsideBg = outsideBg;
		this.insideBg = insideBg;
		this.expiration = expiration;
		this.cutscene = cutscene;
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

	public List<AutoTransform> getAvatarAutoTransforms() {
		return avatarAutoTransforms;
	}

	public List<AutoTransform> getBeamAutoTransforms() {
		return beamAutoTransforms;
	}

	public List<AutoTransform> getLegsAutoTransforms() {
		return legsAutoTransforms;
	}

	public Position getEndPosition() {
		return endPos;
	}

	public List<AutoTransform> getExitAutoTransforms() {
		return exitAutoTransforms;
	}

	public List<BoxSpawnInfo> getBoxes() {
		return boxes;
	}

	public List<RectangleSpawnInfo> getRectangles() {
		return rects;
	}

	public List<NBoxSpawnInfo> getNBoxes() {
		return nBoxes;
	}

	public List<SwitchSpawnInfo> getSwitches() {
		return switches;
	}

	public List<OverlayInfo> getTips() {
		return tips;
	}

	public List<RetractablePlatform> getDoors() {
		return doors;
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

	public boolean isCutscene() {
		return cutscene;
	}
}
