package amplified.gui;

import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;

import amplified.map.AbstractDrawable;
import amplified.map.DrawableOverlayText;
import amplified.map.physicquantity.Position;
import amplified.map.state.Input;
import amplified.resources.FontCache;
import amplified.resources.TextureCache;

@SuppressWarnings("deprecation")
public class GuiButton extends AbstractDrawable {
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

	private boolean isPointInButton(Point point) {
		return point.getX() > bounds.getX() && point.getX() < bounds.getX() + bounds.getWidth() && 
				point.getY() > bounds.getY() && point.getY() < bounds.getY() + bounds.getHeight();
	}

	private void act() {
		if (active) {
			handler.clicked();
			down = false;
			hover = false;
			active = false;
		}
	}

	public void update(Input input) {
		boolean mouseDown = input.downButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK));
		if (isPointInButton(input.cursorPosition())) {
			if (mouseDown && !down) {
				down = true;
				hover = true;
				active = true;
			} else if (!mouseDown && !down) {
				hover = true;
			} else if (!mouseDown && down) {
				act();
			}
		} else {
			down = false;
			if (!mouseDown)
				hover = false;
			active = false;
		}
	}

	@Override
	public Position getPosition() {
		return new Position(bounds.getX(), bounds.getY());
	}

	@Override
	public float getWidth() {
		return bounds.getWidth();
	}

	@Override
	public float getHeight() {
		return bounds.getHeight();
	}

	@Override
	public Texture getTexture() {
		return TextureCache.getTexture(down ? "buttonPressed" : hover ? "buttonHover" : "button");
	}

	@Override
	public Color getTint() {
		return Color.white;
	}

	@Override
	public DrawableOverlayText getCaption() {
		TrueTypeFont f = FontCache.getFont("button");
		return new DrawableOverlayText(new Point((bounds.getWidth() - f.getWidth(text)) / 2, bounds.getHeight() - f.getHeight(text)/2), f, text);
	}
}
