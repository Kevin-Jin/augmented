package amplified.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.newdawn.slick.Color;
import org.newdawn.slick.util.ResourceLoader;

import amplified.map.Platform;
import amplified.map.RetractablePlatform;
import amplified.map.Switchable;
import amplified.map.entity.AutoTransform;
import amplified.map.physicquantity.Position;
import amplified.resources.map.BoxSpawnInfo;
import amplified.resources.map.NBoxSpawnInfo;
import amplified.resources.map.OverlayInfo;
import amplified.resources.map.RectangleSpawnInfo;
import amplified.resources.map.SwitchSpawnInfo;

public class LevelCache {
	private static final Map<String, LevelLayout> loaded = new HashMap<String, LevelLayout>();

	public static LevelLayout getLevel(String key) {
		return loaded.get(key);
	}

	public static void setLevel(String key, LevelLayout value) {
		loaded.put(key, value);
	}

	private static Color parseColor(String hex) {
		return new Color(Integer.parseInt(hex.substring(1, 3), 16), Integer.parseInt(hex.substring(3, 5), 16), Integer.parseInt(hex.substring(5, 7), 16));
	}

	private static int parseFootholds(int width, int height, Map<Byte, Platform> footholds, Map<Byte, Switchable> switchables, List<RetractablePlatform> doors, XMLStreamReader r) throws XMLStreamException {
		for (int event = r.next(); event != XMLStreamReader.END_ELEMENT; event = r.next()) {
			if (event == XMLStreamReader.START_ELEMENT) {
				if (r.getLocalName().equals("foothold")) {
					if (!r.getAttributeLocalName(0).equals("id"))
						throw new RuntimeException("Invalid level format");
					double x1 = 0, x2 = 0, y1 = 0, y2 = 0;
					if (r.getAttributeLocalName(1).equals("anchor")) {
						String key = r.getAttributeValue(1);
						if (key.equals("bottom")) {
							x1 = -99999;
							x2 = width;
							y1 = 0;
							y2 = -99999;
						} else if (key.equals("left")) {
							x1 = -99999;
							x2 = 0;
							y1 = height + 99999;
							y2 = -99999;
						} else if (key.equals("right")) {
							x1 = width;
							x2 = width + 99999;
							y1 = height + 99999;
							y2 = -99999;
						} else if (key.equals("top")) {
							x1 = -99999;
							x2 = width + 99999;
							y1 = height + 99999;
							y2 = height;
						}
					} else if (r.getAttributeLocalName(1).equals("x1") && r.getAttributeLocalName(2).equals("x2")
							&& r.getAttributeLocalName(3).equals("y1") && r.getAttributeLocalName(4).equals("y2")) {
						x1 = Double.parseDouble(r.getAttributeValue(1));
						x2 = Double.parseDouble(r.getAttributeValue(2));
						y1 = Double.parseDouble(r.getAttributeValue(3));
						y2 = Double.parseDouble(r.getAttributeValue(4));
					} else {
						throw new RuntimeException("Invalid level format");
					}
					Platform foothold;
					if (r.getAttributeCount() > 5) {
						if (r.getAttributeCount() < 7 || !r.getAttributeLocalName(5).equals("switchableId") || !r.getAttributeLocalName(6).equals("color"))
							throw new RuntimeException("Invalid level format");
						boolean inverted;
						if (r.getAttributeCount() == 8)
							if (!r.getAttributeLocalName(7).equals("activated"))
								throw new RuntimeException("Invalid level format");
							else
								inverted = Boolean.parseBoolean(r.getAttributeValue(7));
						else
							inverted = false;
						foothold = new RetractablePlatform(x1, x2, y1, y2, inverted, parseColor(r.getAttributeValue(6)));
						switchables.put(Byte.valueOf(Byte.parseByte(r.getAttributeValue(5))), (Switchable) foothold);
						doors.add((RetractablePlatform) foothold);
					} else {
						foothold = new Platform(x1, x2, y1, y2);
					}
					footholds.put(Byte.valueOf(Byte.parseByte(r.getAttributeValue(0))), foothold);
					while ((event = r.next()) != XMLStreamReader.END_ELEMENT);
				} else {
					throw new RuntimeException("Invalid level format");
				}
			}
		}
		return XMLStreamReader.END_ELEMENT;
	}

	private static AutoTransform parseAutoTransformEntry(String key, XMLStreamReader r) {
		if (key.equals("scale")) {
			int attrCount = r.getAttributeCount();
			double dW = 0, dH = 0;
			for (int i = 0; i < attrCount - 2; i++) {
				String attrKey = r.getAttributeLocalName(i);
				if (attrKey.equals("dw"))
					dW = Double.parseDouble(r.getAttributeValue(i));
				else if (attrKey.equals("dh"))
					dH = Double.parseDouble(r.getAttributeValue(i));
				else
					throw new RuntimeException("Invalid level format");
			}
			if (!r.getAttributeLocalName(attrCount - 2).equals("duration") || !r.getAttributeLocalName(attrCount - 1).equals("after"))
				throw new RuntimeException("Invalid level format");
			double start = Double.parseDouble(r.getAttributeValue(attrCount - 1));
			double end = start + Double.parseDouble(r.getAttributeValue(attrCount - 2));
			return new AutoTransform.Scale(start, end, dW, dH);
		} else if (key.equals("translate")) {
			if (!r.getAttributeLocalName(0).equals("dx") || !r.getAttributeLocalName(1).equals("dy")
					|| !r.getAttributeLocalName(2).equals("duration") || !r.getAttributeLocalName(3).equals("after"))
				throw new RuntimeException("Invalid level format");
			double start = Double.parseDouble(r.getAttributeValue(3));
			double end = start + Double.parseDouble(r.getAttributeValue(2));
			double clipToDx = Double.parseDouble(r.getAttributeValue(0));
			double clipToDy = Double.parseDouble(r.getAttributeValue(1));
			return new AutoTransform.Translate(start, end, clipToDx, clipToDy);
		} else if (key.equals("rotate")) {
			if (!r.getAttributeLocalName(0).equals("angle") || !r.getAttributeLocalName(1).equals("duration") || !r.getAttributeLocalName(2).equals("after"))
				throw new RuntimeException("Invalid level format");
			double start = Double.parseDouble(r.getAttributeValue(2));
			double end = start + Double.parseDouble(r.getAttributeValue(1));
			double clipTo = Double.parseDouble(r.getAttributeValue(0));
			return new AutoTransform.Rotate(start, end, clipTo);
		} else if (key.equals("animate")) {
			if (!r.getAttributeLocalName(0).equals("duration") || !r.getAttributeLocalName(1).equals("after"))
				throw new RuntimeException("Invalid level format");
			double start = Double.parseDouble(r.getAttributeValue(1));
			double end = start + Double.parseDouble(r.getAttributeValue(0));
			return new AutoTransform.Animate(start, end);
		} else if (key.equals("caption")) {
			if (!r.getAttributeLocalName(0).equals("font") || !r.getAttributeLocalName(1).equals("color") || !r.getAttributeLocalName(2).equals("text") || !r.getAttributeLocalName(3).equals("cursorAfter") || !r.getAttributeLocalName(4).equals("noCursorAfter") || !r.getAttributeLocalName(5).equals("duration") || !r.getAttributeLocalName(6).equals("after"))
				throw new RuntimeException("Invalid level format");
			double start = Double.parseDouble(r.getAttributeValue(6));
			double end = start + Double.parseDouble(r.getAttributeValue(5));
			double noCursorAfter = Double.parseDouble(r.getAttributeValue(4));
			double cursorAfter = Double.parseDouble(r.getAttributeValue(3));
			String text = r.getAttributeValue(2);
			Color color = parseColor(r.getAttributeValue(1));
			String font = r.getAttributeValue(0);
			return new AutoTransform.Caption(start, end, font, color, text, cursorAfter, noCursorAfter);
		} else {
			return null;
		}
	}

	private static int parseButton(Map<Byte, Switchable> allSwitchables, List<Switchable> associatedSwitchables, List<AutoTransform> sequence, XMLStreamReader r) throws XMLStreamException {
		for (int event = r.next(); event != XMLStreamReader.END_ELEMENT; event = r.next()) {
			if (event == XMLStreamReader.START_ELEMENT) {
				String key = r.getLocalName();
				if (key.equals("buttontrigger")) {
					if (!r.getAttributeLocalName(0).equals("switchableId"))
						throw new RuntimeException("Invalid level format");
					associatedSwitchables.add(allSwitchables.get(Byte.valueOf(Byte.parseByte(r.getAttributeValue(0)))));
					while ((event = r.next()) != XMLStreamReader.END_ELEMENT);
				} else {
					AutoTransform autoTransform = parseAutoTransformEntry(key, r);
					if (autoTransform != null) {
						sequence.add(autoTransform);
						while ((event = r.next()) != XMLStreamReader.END_ELEMENT);
					} else {
						throw new RuntimeException("Invalid level format");
					}
				}
			}
		}
		return XMLStreamReader.END_ELEMENT;
	}

	private static int parseAvatarAutoTransforms(List<AutoTransform> beam, List<AutoTransform> legs, List<AutoTransform> whole, XMLStreamReader r) throws XMLStreamException {
		for (int event = r.next(); event != XMLStreamReader.END_ELEMENT; event = r.next()) {
			if (event == XMLStreamReader.START_ELEMENT) {
				String key = r.getLocalName();
				if (key.equals("beam")) {
					event = parseAutoTransforms(beam, r);
				} else if (key.equals("legs")) {
					event = parseAutoTransforms(legs, r);
				} else {
					AutoTransform autoTransform = parseAutoTransformEntry(key, r);
					if (autoTransform != null) {
						whole.add(autoTransform);
						while ((event = r.next()) != XMLStreamReader.END_ELEMENT);
					} else {
						throw new RuntimeException("Invalid level format");
					}
				}
			}
		}
		return XMLStreamReader.END_ELEMENT;
	}

	private static int parseAutoTransforms(List<AutoTransform> sequence, XMLStreamReader r) throws XMLStreamException {
		for (int event = r.next(); event != XMLStreamReader.END_ELEMENT; event = r.next()) {
			if (event == XMLStreamReader.START_ELEMENT) {
				String key = r.getLocalName();
				AutoTransform autoTransform = parseAutoTransformEntry(key, r);
				if (autoTransform != null) {
					sequence.add(autoTransform);
					while ((event = r.next()) != XMLStreamReader.END_ELEMENT);
				} else {
					throw new RuntimeException("Invalid level format");
				}
			}
		}
		return XMLStreamReader.END_ELEMENT;
	}

	private static int parseEntities(Map<Byte, Switchable> switchables, List<BoxSpawnInfo> boxes, List<RectangleSpawnInfo> rects, List<NBoxSpawnInfo> nBoxes, List<SwitchSpawnInfo> switches, List<OverlayInfo> tips, Position startPos, List<AutoTransform> avatarAutoTransforms, List<AutoTransform> beamAutoTransforms, List<AutoTransform> legsAutoTransforms, Position endPos, List<AutoTransform> exitAutoTransforms, XMLStreamReader r) throws XMLStreamException {
		for (int event = r.next(); event != XMLStreamReader.END_ELEMENT; event = r.next()) {
			if (event == XMLStreamReader.START_ELEMENT) {
				String key = r.getLocalName();
				if (key.equals("avatar")) {
					if (!r.getAttributeLocalName(0).equals("x") || !r.getAttributeLocalName(1).equals("y"))
						throw new RuntimeException("Invalid level format");
					startPos.set(Double.parseDouble(r.getAttributeValue(0)), Double.parseDouble(r.getAttributeValue(1)));
					event = parseAvatarAutoTransforms(beamAutoTransforms, legsAutoTransforms, avatarAutoTransforms, r);
				} else if (key.equals("box")) {
					if (!r.getAttributeLocalName(0).equals("x") || !r.getAttributeLocalName(1).equals("y")
							|| !r.getAttributeLocalName(2).equals("scaleInit") || !r.getAttributeLocalName(3).equals("scaleMin") || !r.getAttributeLocalName(4).equals("scaleMax"))
						throw new RuntimeException("Invalid level format");
					Position pos = new Position(Double.parseDouble(r.getAttributeValue(0)), Double.parseDouble(r.getAttributeValue(1)));
					float scaleInit = Float.parseFloat(r.getAttributeValue(2));
					float scaleMin = Float.parseFloat(r.getAttributeValue(3));
					float scaleMax = Float.parseFloat(r.getAttributeValue(4));
					List<AutoTransform> autoTransforms = new ArrayList<AutoTransform>();
					event = parseAutoTransforms(autoTransforms, r);
					boxes.add(new BoxSpawnInfo(pos, scaleInit, scaleMin, scaleMax, autoTransforms));
				} else if (key.equals("rectangle")) {
					if (!r.getAttributeLocalName(0).equals("x") || !r.getAttributeLocalName(1).equals("y")
							|| !r.getAttributeLocalName(2).equals("scaleInit") || !r.getAttributeLocalName(3).equals("scaleMin") || !r.getAttributeLocalName(4).equals("scaleMax"))
						throw new RuntimeException("Invalid level format");
					Position pos = new Position(Double.parseDouble(r.getAttributeValue(0)), Double.parseDouble(r.getAttributeValue(1)));
					float scaleInit = Float.parseFloat(r.getAttributeValue(2));
					float scaleMin = Float.parseFloat(r.getAttributeValue(3));
					float scaleMax = Float.parseFloat(r.getAttributeValue(4));
					List<AutoTransform> autoTransforms = new ArrayList<AutoTransform>();
					event = parseAutoTransforms(autoTransforms, r);
					rects.add(new RectangleSpawnInfo(pos, scaleInit, scaleMin, scaleMax, autoTransforms));
				} else if (key.equals("nbox")) {
					if (!r.getAttributeLocalName(0).equals("x") || !r.getAttributeLocalName(1).equals("y"))
						throw new RuntimeException("Invalid level format");
					Position pos = new Position(Double.parseDouble(r.getAttributeValue(0)), Double.parseDouble(r.getAttributeValue(1)));
					List<AutoTransform> autoTransforms = new ArrayList<AutoTransform>();
					event = parseAutoTransforms(autoTransforms, r);
					nBoxes.add(new NBoxSpawnInfo(pos, autoTransforms));
				} else if (key.equals("button")) {
					if (!r.getAttributeLocalName(0).equals("x") || !r.getAttributeLocalName(1).equals("y")
							|| !r.getAttributeLocalName(2).equals("color"))
						throw new RuntimeException("Invalid level format");
					List<Switchable> associatedSwitchables = new ArrayList<Switchable>();
					Position pos = new Position(Double.parseDouble(r.getAttributeValue(0)), Double.parseDouble(r.getAttributeValue(1)));
					Color c = parseColor(r.getAttributeValue(2));
					List<AutoTransform> autoTransforms = new ArrayList<AutoTransform>();
					event = parseButton(switchables, associatedSwitchables, autoTransforms, r);
					switches.add(new SwitchSpawnInfo(c, pos, associatedSwitchables, autoTransforms));
				} else if (key.equals("overlay")) {
					if (!r.getAttributeLocalName(0).equals("x") || !r.getAttributeLocalName(1).equals("y")
							|| !r.getAttributeLocalName(2).equals("w") || !r.getAttributeLocalName(3).equals("h") || !r.getAttributeLocalName(4).equals("src"))
						throw new RuntimeException("Invalid level format");
					Position pos = new Position(Double.parseDouble(r.getAttributeValue(0)), Double.parseDouble(r.getAttributeValue(1)));
					int width = Integer.parseInt(r.getAttributeValue(2));
					int height = Integer.parseInt(r.getAttributeValue(3));
					String imageName = r.getAttributeValue(4);
					List<AutoTransform> autoTransforms = new ArrayList<AutoTransform>();
					event = parseAutoTransforms(autoTransforms, r);
					tips.add(new OverlayInfo(pos, width, height, imageName, autoTransforms));
				} else if (key.equals("exit")) {
					if (!r.getAttributeLocalName(0).equals("x") || !r.getAttributeLocalName(1).equals("y"))
						throw new RuntimeException("Invalid level format");
					endPos.set(Double.parseDouble(r.getAttributeValue(0)), Double.parseDouble(r.getAttributeValue(1)));
					event = parseAutoTransforms(exitAutoTransforms, r);
				} else {
					throw new RuntimeException("Invalid level format");
				}
			}
		}
		return XMLStreamReader.END_ELEMENT;
	}

	public static LevelLayout loadXml(String name, int windowWidth, int windowHeight) throws IOException {
		int width = windowWidth, height = windowHeight;
		Position startPos = new Position(-99999, -99999), endPos = new Position(99999, 99999);
		double yDeceleration = 0, yVelocityMin = 0, expiration = Double.POSITIVE_INFINITY;
		String nextMap = null, outsideBg = null, insideBg = null;
		Map<Byte, Platform> footholds = new HashMap<Byte, Platform>();
		List<BoxSpawnInfo> boxes = new ArrayList<BoxSpawnInfo>();
		List<RectangleSpawnInfo> rects = new ArrayList<RectangleSpawnInfo>();
		List<NBoxSpawnInfo> nBoxes = new ArrayList<NBoxSpawnInfo>();
		List<SwitchSpawnInfo> switches = new ArrayList<SwitchSpawnInfo>();
		List<OverlayInfo> tips = new ArrayList<OverlayInfo>();
		List<RetractablePlatform> doors = new ArrayList<RetractablePlatform>();
		List<AutoTransform> beamAutoTransforms = new ArrayList<AutoTransform>(), legsAutoTransforms = new ArrayList<AutoTransform>(), avatarAutoTransforms = new ArrayList<AutoTransform>(), exitAutoTransforms = new ArrayList<AutoTransform>();
		boolean cutscene;

		Map<Byte, Switchable> switchables = new HashMap<Byte, Switchable>();

		XMLInputFactory f = XMLInputFactory.newInstance();
		try {
			XMLStreamReader r = f.createXMLStreamReader(ResourceLoader.getResourceAsStream(name + ".xml"));
			if (r.getEventType() != XMLStreamReader.START_DOCUMENT)
				throw new RuntimeException("Invalid level format");
			if (r.next() != XMLStreamReader.START_ELEMENT || !r.getAttributeLocalName(0).equals("id") || !r.getAttributeValue(0).equals(name.substring(name.lastIndexOf('/') + 1)))
				throw new RuntimeException("Invalid level format");
			if (r.getLocalName().equals("level"))
				cutscene = false;
			else if (r.getLocalName().equals("cutscene"))
				cutscene = true;
			else
				throw new RuntimeException("Invalid level format");

			int event;
			for (event = r.next(); event != XMLStreamReader.END_ELEMENT; event = r.next()) {
				if (event == XMLStreamReader.START_ELEMENT) {
					String key = r.getLocalName();
					if (key.equals("width")) {
						while ((event = r.next()) != XMLStreamReader.END_ELEMENT)
							if (event == XMLStreamReader.CHARACTERS)
								width = Integer.parseInt(r.getText());
					} else if (key.equals("height")) {
						while ((event = r.next()) != XMLStreamReader.END_ELEMENT)
							if (event == XMLStreamReader.CHARACTERS)
								height = Integer.parseInt(r.getText());
					} else if (key.equals("gravity")) {
						if (!r.getAttributeLocalName(0).equals("acceleration") || !r.getAttributeLocalName(1).equals("terminalVelocity"))
							throw new RuntimeException("Invalid level format");
						yDeceleration = Double.parseDouble(r.getAttributeValue(0));
						yVelocityMin = Double.parseDouble(r.getAttributeValue(1));
						while ((event = r.next()) != XMLStreamReader.END_ELEMENT);
					} else if (key.equals("nextMap")) {
						while ((event = r.next()) != XMLStreamReader.END_ELEMENT)
							if (event == XMLStreamReader.CHARACTERS)
								nextMap = r.getText();
					} else if (key.equals("outerImage")) {
						while ((event = r.next()) != XMLStreamReader.END_ELEMENT)
							if (event == XMLStreamReader.CHARACTERS)
								outsideBg = r.getText();
					} else if (key.equals("innerImage")) {
						while ((event = r.next()) != XMLStreamReader.END_ELEMENT)
							if (event == XMLStreamReader.CHARACTERS)
								insideBg = r.getText();
					} else if (key.equals("timer")) {
						while ((event = r.next()) != XMLStreamReader.END_ELEMENT)
							if (event == XMLStreamReader.CHARACTERS)
								expiration = Double.parseDouble(r.getText());
					} else if (key.equals("footholds")) {
						event = parseFootholds(width, height, footholds, switchables, doors, r);
					} else if (key.equals("entities")) {
						event = parseEntities(switchables, boxes, rects, nBoxes, switches, tips, startPos, avatarAutoTransforms, beamAutoTransforms, legsAutoTransforms, endPos, exitAutoTransforms, r);
					} else {
						throw new RuntimeException("Invalid level format");
					}
				}
			}
			if (!r.getLocalName().equals(cutscene ? "cutscene" : "level"))
				throw new RuntimeException("Invalid level format");
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null;
		}
		return new LevelLayout(width, height, footholds, startPos, avatarAutoTransforms, beamAutoTransforms, legsAutoTransforms, endPos, exitAutoTransforms, yDeceleration, yVelocityMin, boxes, rects, nBoxes, switches, tips, doors, nextMap, outsideBg, insideBg, expiration, cutscene);
	}
}
