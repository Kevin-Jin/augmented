package amplified.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

@SuppressWarnings("deprecation")
public abstract class Gui{

	public abstract void draw();

	public void drawTexture(Texture texture, Rectangle bounds){
		texture.bind();
		GL11.glPushMatrix();
		{
			GL11.glLoadIdentity();
			GL11.glBegin(GL11.GL_QUADS);
			{
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2f(bounds.getX(), bounds.getY());
				GL11.glTexCoord2f(texture.getWidth(), 0);
				GL11.glVertex2f(bounds.getX() + bounds.getWidth(), bounds.getY());
				GL11.glTexCoord2f(texture.getWidth(), texture.getHeight());
				GL11.glVertex2f(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight());
				GL11.glTexCoord2f(0, texture.getHeight());
				GL11.glVertex2f(bounds.getX(), bounds.getY() + bounds.getHeight());
			}
			GL11.glEnd();
		}
		GL11.glPopMatrix();
		
		TextureImpl.unbind();
	}
	
	public void drawCenteredString(TrueTypeFont font, int cX, int cY, String text, Color c){
		int sWidth = font.getWidth(text);
		int sHeight = font.getHeight(text);
		font.drawString(cX-sWidth/2, cY-sHeight/2,text,c);
	}

}
