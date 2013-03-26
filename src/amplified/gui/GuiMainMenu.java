package amplified.gui;

import org.lwjgl.util.Rectangle;

import amplified.map.entity.player.Player;
import amplified.map.physicquantity.Position;
import amplified.map.state.Input;

//TODO
//Draw Logo
//Draw Player
//Draw some background
public class GuiMainMenu extends GuiScreen {
	private Player p;
	private float logoScale;
	private boolean growLogo;

	public GuiMainMenu(Rectangle bounds) {
		super(bounds);
		p = new Player();
	}

	@Override
	public void updateState(double tDelta, Input input) {
		if (logoScale < 0.75 && !growLogo)
			growLogo = true;
		else if (logoScale > 1.5 && growLogo)
			growLogo = false;
		if (growLogo)
			logoScale += (float) (0.3 * tDelta);
		else
			logoScale -= (float) (0.3 * tDelta);

		p.setPosition(new Position(50,600));

		super.updateState(tDelta, input);
	}

	@Override
	public void draw() {
		super.draw();
	}
}
