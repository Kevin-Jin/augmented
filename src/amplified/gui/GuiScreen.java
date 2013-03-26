package amplified.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.Rectangle;

import amplified.map.state.Input;

public class GuiScreen extends Gui {
	protected final Rectangle bounds;
	protected final List<GuiButton> buttons;

	public GuiScreen(Rectangle bounds) {
		this.bounds = bounds;
		buttons = new ArrayList<GuiButton>();
	}

	public void updateState(double tDelta, Input input) {
		for (GuiButton button : buttons)
			button.updateState(input.cursorPosition(), input.pressedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)));
	}

	@Override
	public void draw() {
		for (GuiButton button : buttons)
			button.draw();
	}

	public List<GuiButton> getButtons() {
		return buttons;
	}
}
