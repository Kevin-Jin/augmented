package amplified.gui;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.Rectangle;

import amplified.Game.GameState;
import amplified.ScreenFiller;
import amplified.map.Drawable;
import amplified.map.entity.player.Player;
import amplified.map.physicquantity.Position;
import amplified.map.state.Input;

//TODO
//Draw Logo
//Draw Player
//Draw some background
public class GuiMainMenu extends ScreenFiller {
	private final Input input;
	private final List<GuiButton> buttons;
	private final Map<Byte, ZAxisLayer> layers;

	private Player p;
	private float logoScale;
	private boolean growLogo;

	public GuiMainMenu(Rectangle bounds, List<GuiButton> buttons, Input input, Drawable... overlays) {
		this.input = input;
		this.buttons = buttons;
		ZAxisLayer overlayLayer = new ZAxisLayer((byte) 0);
		overlayLayer.getDrawables().addAll(buttons);
		for (Drawable overlay : overlays)
			overlayLayer.getDrawables().add(overlay);
		this.layers = Collections.singletonMap(ZAxisLayer.OVERLAY, overlayLayer);

		p = new Player();
	}

	@Override
	public GameState update(double tDelta) {
		input.markedPointer().setLocation(input.cursorPosition());

		if (logoScale < 0.75 && !growLogo)
			growLogo = true;
		else if (logoScale > 1.5 && growLogo)
			growLogo = false;
		if (growLogo)
			logoScale += (float) (0.3 * tDelta);
		else
			logoScale -= (float) (0.3 * tDelta);

		p.setPosition(new Position(50,600));

		for (GuiButton button : buttons)
			button.updateState(input.cursorPosition(), input.downButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)));
		return GameState.TITLE_SCREEN;
	}

	@Override
	public Map<Byte, ZAxisLayer> getLayers() {
		return layers;
	}
}
