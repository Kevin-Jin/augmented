package amplified.gui;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import amplified.Game.GameState;
import amplified.ScreenFiller;
import amplified.map.state.Input;

public class GuiPauseMenu extends ScreenFiller {
	private final Input input;
	private final List<GuiButton> buttons;
	private final SortedMap<Byte, ZAxisLayer> layers;

	public GuiPauseMenu(List<GuiButton> buttons, ScreenFiller underlay, Input input) {
		this.input = input;
		this.buttons = buttons;

		layers = new TreeMap<Byte, ZAxisLayer>();
		layers.putAll(underlay.getLayers());
		ZAxisLayer overlay = new ZAxisLayer((byte) 0);
		overlay.getDrawables().addAll(buttons);
		ZAxisLayer existingOverlay = layers.get(ZAxisLayer.OVERLAY);
		if (existingOverlay != null)
			overlay.getDrawables().addAll(existingOverlay.getDrawables());
		layers.put(ZAxisLayer.OVERLAY, overlay);
	}

	@Override
	public GameState update(double tDelta) {
		input.markedPointer().setLocation(input.cursorPosition());

		for (GuiButton button : buttons)
			button.update(input);
		return GameState.PAUSE;
	}

	@Override
	public SortedMap<Byte, ZAxisLayer> getLayers() {
		return layers;
	}
}
