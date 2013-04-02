package amplified.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;
import org.lwjgl.util.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import amplified.Game.GameState;
import amplified.ScreenFiller;
import amplified.map.AbstractDrawable;
import amplified.map.Drawable;
import amplified.map.DrawableOverlayText;
import amplified.map.DrawableTexture;
import amplified.map.physicquantity.Position;
import amplified.map.state.Input;
import amplified.resources.FontCache;
import amplified.resources.TextureCache;

@SuppressWarnings("deprecation")
public class GuiMainMenu extends ScreenFiller {
	private static final double PERIOD = 0.032;

	private static final Random r = new Random();

	private static class Letter extends AbstractDrawable {
		private final Position pos;
		private int transparency;
		private String text;

		public Letter(int x, int y) {
			pos = new Position(x, y);
			text = String.valueOf((char) (r.nextInt('~' - '!' + 1) + '!'));
		}

		public Texture getTexture() {
			return TextureCache.getTexture("spacer");
		}

		@Override
		public Position getPosition() {
			return pos;
		}

		@Override
		public Color getTint() {
			return new Color(0, 255, 0, transparency);
		}

		@Override
		public DrawableOverlayText getCaption() {
			return new DrawableOverlayText(new Point(0, 0), FontCache.getFont("rain"), text);
		}

		public void update(int index) {
			if (index == 0)
				transparency = 255;
			transparency = Math.max(0, transparency - 10);
			text = String.valueOf((char) (r.nextInt('~' - '!' + 1) + '!'));
		}
	}

	private static class Column {
		private final List<Letter> letters;
		private final int mod;
		private int index;

		public Column(List<Letter> letters, int head, int wrap) {
			this.letters = letters;
			this.mod = wrap;
			index = head;
		}

		public List<Letter> getLetters() {
			return letters;
		}

		public void update() {
			for (int i = 0; i < letters.size(); i++)
				letters.get(i).update(i - index);
			index = (index + 1) % mod;
		}
	}

	private final Input input;
	private final Rectangle bounds;
	private final List<GuiButton> buttons;
	private final List<Column> letters;
	private final Map<Byte, ZAxisLayer> layers;
	private boolean initialized;

	private DrawableTexture logo;
	private int logoWidth, logoHeight;
	private float logoScale;
	private boolean growLogo;

	private double elapsed;

	public GuiMainMenu(Rectangle bounds, List<GuiButton> buttons, Input input, Drawable... overlays) {
		this.input = input;
		this.bounds = bounds;
		this.buttons = buttons;
		this.letters = new ArrayList<Column>();
		ZAxisLayer overlayLayer = new ZAxisLayer((byte) 0);
		overlayLayer.getDrawables().addAll(buttons);
		for (Drawable overlay : overlays)
			overlayLayer.getDrawables().add(overlay);
		this.layers = Collections.singletonMap(ZAxisLayer.OVERLAY, overlayLayer);

		logoScale = 0.75f;
	}

	private void initialize() {
		Texture texture = TextureCache.getTexture("logo");
		logoWidth = texture.getImageWidth();
		logoHeight = texture.getImageHeight();
		logo = new DrawableTexture("logo", new Position());
		layers.get(ZAxisLayer.OVERLAY).getDrawables().add(0, logo);

		//populate letters
		int height = FontCache.getFont("rain").getHeight();
		int width = FontCache.getFont("rain").getWidth("M");
		int total = bounds.getHeight() / height + 1;
		for (int x = 0; x < bounds.getWidth() - width; x += width) {
			int random = r.nextInt(total);
			List<Letter> column = new ArrayList<Letter>();
			for (int i = 1; i <= total; i++)
				column.add(new Letter(x, (total - i) * height));
			letters.add(new Column(column, random, total));
		}
		initialized = true;
	}

	@Override
	public GameState update(double tDelta) {
		if (!initialized)
			initialize();

		input.markedPointer().setLocation(input.cursorPosition());

		if (logoScale < 0.75 && !growLogo)
			growLogo = true;
		else if (logoScale > 1.5 && growLogo)
			growLogo = false;
		if (growLogo)
			logoScale += (float) (0.3 * tDelta);
		else
			logoScale -= (float) (0.3 * tDelta);
		logo.setWidth(logoScale * logoWidth);
		logo.setHeight(logoScale * logoHeight);
		logo.getPosition().set((bounds.getWidth() - logo.getWidth()) / 2, (bounds.getHeight() - logo.getHeight()) * 3 / 4);

		for (GuiButton button : buttons)
			button.update(input);
		elapsed += tDelta;
		if (elapsed >= PERIOD) {
			for (Column c : letters)
				c.update();
			elapsed = 0;
		}
		return GameState.TITLE_SCREEN;
	}

	@Override
	public Map<Byte, ZAxisLayer> getLayers() {
		return layers;
	}

	public void drawBackground() {
		GL11.glPushMatrix();
		{
			GL11.glLoadIdentity();
			for (Column c : letters) {
				for (Letter l : c.getLetters()) {
					DrawableOverlayText caption = l.getCaption();
					caption.getFont().drawString((float) l.pos.getX(), (float) l.pos.getY(), caption.getMessage(), l.getTint());
				}
			}
		}
		GL11.glPopMatrix();
	}
}
