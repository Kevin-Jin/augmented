package amplified.gui;

import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;
import org.newdawn.slick.Color;

import amplified.resources.FontCache;
import amplified.resources.TextureCache;

public class GuiButton extends Gui {
	public interface ButtonHandler {
		public void clicked();
	}

	private String text;
	private Rectangle bounds;
	private ButtonHandler handler;
	private boolean active, hover, down;

	public GuiButton(String text, Rectangle bounds, ButtonHandler handler) {
		this.text = text;
		this.bounds = bounds;
		this.handler = handler;
		this.active = true;
	}

	public boolean isPointInButton(Point point) {
		return point.getX() > bounds.getX() && point.getX() < bounds.getX() + bounds.getWidth() && 
				point.getY() > bounds.getY() && point.getY() < bounds.getY() + bounds.getHeight();
	}

	private void act() {
		if (active) {
			handler.clicked();
			active = false;
		}
	}

	private void setMouseHover() {
		active = hover = true;
		down = false;
	}

	private void setMouseUp() {
		hover = down = false;
		active = true;
	}

	private void setMouseDown() {
		hover = down = true;
	}

	public void updateState(Point mouse, boolean mouseDown) {
		if (isPointInButton(mouse)) {
			if (mouseDown) {
				setMouseDown();
				act();
			} else {
				setMouseHover();
			}
		} else {
			setMouseUp();
		}
	}

	@Override
	public void draw() {
		drawTexture(TextureCache.getTexture(down ? "buttonPressed" : hover ? "buttonHover" : "button"),bounds);
		this.drawCenteredString(FontCache.getFont("button"), bounds.getX() + bounds.getWidth()/2, bounds.getY() + bounds.getHeight()/2, text, Color.white);
	}
}
