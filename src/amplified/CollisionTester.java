package amplified;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

import amplified.map.collisions.CollisionResult;
import amplified.map.collisions.Polygon;
import amplified.map.collisions.PolygonCollision;

//black - barrier, blue - frame start position, red - unobstructed frame end position, green - final position after collisions
public class CollisionTester {
	private static final Polygon BASE_BARRIER = new Polygon(new Vector2f[] { new Vector2f(0, 0), new Vector2f(30, 0), new Vector2f(30, 20), new Vector2f(0, 20) });

	private final Color[] c = { Color.black, Color.blue, Color.red, Color.green };
	private final Polygon[] p = new Polygon[4];
	private final Vector2f velocity = new Vector2f(30, 100);

	public void glInit() throws LWJGLException {
		DisplayMode mode = null;
		mode = new DisplayMode(800, 600);
		Display.setDisplayMode(mode);
		Display.create();
		Display.setVSyncEnabled(true);

		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glClearColor(100f / 255, 149f / 255, 237f / 255, 255f / 255); //cornflower blue
		GL11.glEnable(GL11.GL_BLEND); //enable alpha blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glViewport(0, 0, 800, 600);
		GL11.glPointSize(10);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, -1, 1);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	private void drawGame() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				switch (Keyboard.getEventKey()) {
					case Keyboard.KEY_W:
						velocity.setY(velocity.getY() + 5);
						break;
					case Keyboard.KEY_A:
						velocity.setX(velocity.getX() - 5);
						break;
					case Keyboard.KEY_S:
						velocity.setY(velocity.getY() - 5);
						break;
					case Keyboard.KEY_D:
						velocity.setX(velocity.getX() + 5);
						break;
				}
			}
		}

		p[0] = Polygon.transformPolygon(BASE_BARRIER, new Matrix4f().translate(new Vector2f(Mouse.getX(), Mouse.getY()))); //barrier
		p[1] = new Polygon(new Vector2f[] { new Vector2f(380, 280), new Vector2f(395, 260), new Vector2f(410, 280), new Vector2f(395, 300) }); //start position
		p[2] = Polygon.transformPolygon(p[1], new Matrix4f().translate(velocity)); //end position. since tDelta == 1, we don't need to scale velocity
		CollisionResult r = PolygonCollision.collision(p[0], p[2], velocity, 1);
		if (r.collision())
			p[3] = Polygon.transformPolygon(p[2], new Matrix4f().translate(r.getCollisionInformation().getMinimumTranslationVector())); //position at time of impact
		else
			p[3] = p[2];

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glPushMatrix();
		{
			for (int i = 0; i < 4; i++) {
				c[i].bind();
				GL11.glBegin(GL11.GL_LINE_LOOP);
				Vector2f[] vertices = p[i].getVertices();
				for (int j = 0; j < vertices.length; j++)
					GL11.glVertex2f(vertices[j].getX(), vertices[j].getY());
				GL11.glEnd();
			}
		}
		GL11.glPopMatrix();
	}

	public static void main(String[] args) throws LWJGLException, InterruptedException {
		CollisionTester t = new CollisionTester();
		t.glInit();
		while (!Display.isCloseRequested()) {
			t.drawGame();
			Display.update();
			Display.sync(60);
		}
	}
}
