package op_lando.map.state;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Point;

public class Input {
	public static final int
		MOUSE_LEFT_CLICK = 0,
		MOUSE_RIGHT_CLICK = 1,
		MOUSE_MIDDLE_CLICK = 2
	;

	private final Point pointerPos;
	private final Set<Integer> downButtons;
	private final Set<Integer> downKeys;

	private final Point pointerChange;
	private final Set<Integer> updatedButtons;
	private final Set<Integer> updatedKeys;

	public Input() {
		pointerPos = new Point();
		downButtons = new HashSet<Integer>();
		downKeys = new HashSet<Integer>();

		pointerChange = new Point();
		updatedButtons = new HashSet<Integer>();
		updatedKeys = new HashSet<Integer>();
	}

	public void update() {
		pointerPos.setLocation(Mouse.getX(), Mouse.getY());
		pointerChange.setLocation(Mouse.getDX(), Mouse.getDY());
		updatedButtons.clear();
		while (Mouse.next()) {
			if (Mouse.getEventButtonState())
				downButtons.add(Integer.valueOf(Mouse.getEventButton()));
			else
				downButtons.remove(Integer.valueOf(Mouse.getEventButton()));
			updatedButtons.add(Integer.valueOf(Mouse.getEventButton()));
		}
		updatedKeys.clear();
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState())
				downKeys.add(Integer.valueOf(Keyboard.getEventKey()));
			else
				downKeys.remove(Integer.valueOf(Keyboard.getEventKey()));
			updatedKeys.add(Integer.valueOf(Keyboard.getEventKey()));
		}
	}

	public Point cursorPosition() {
		//don't let calling code mess up our state
		return new Point(pointerPos);
	}

	public Point cursorTranslate() {
		return new Point(pointerChange);
	}

	/**
	 * Recommended way to test if a mouse button (the left one, in this example) is pressed:
	 * <pre>
	 *   Input i;
	 *   if (i.pressedButtons().contains(Integer.valueOf(Input.MOUSE_LEFT_CLICK)))
	 *       //do stuff</pre>
	 * Recommended way to process every pressed mouse button:
	 * <pre>
	 *   Input i;
	 *   for (Integer btn : i.pressedButtons()) {
	 *       switch (btn.intValue()) {
	 *           case Input.MOUSE_LEFT_CLICK:
	 *               //do stuff
	 *               break;
	 *           case Input.MOUSE_RIGHT_CLICK:
	 *               //do stuff
	 *               break;
	 *           case Input.MOUSE_MIDDLE_CLICK:
	 *               //do stuff
	 *               break;
	 *       }
	 *   }</pre>
	 * Keep in mind that using the constants <code>Input.MOUSE_LEFT_CLICK</code>,
	 * <code>Input.MOUSE_RIGHT_CLICK</code>, and <code>Input.MOUSE_MIDDLE_CLICK</code>
	 * to reference the integer representations will make the code easier to read.
	 * @return a Set of Integers that contains the Integer representation of
	 * every mouse button that is currently down but was not down in the last
	 * frame.
	 */
	public Set<Integer> pressedButtons() {
		Set<Integer> copy = new HashSet<Integer>(downButtons);
		copy.retainAll(updatedButtons);
		return copy;
	}

	/**
	 * @see #pressedButtons() Recommended usage/conventions.
	 * @return a Set of Integers that contains the Integer representation of
	 * every mouse button that is currently down and was down in the last frame
	 * as well.
	 */
	public Set<Integer> heldButtons() {
		Set<Integer> copy = new HashSet<Integer>(downButtons);
		copy.removeAll(updatedButtons);
		return copy;
	}

	/**
	 * @see #pressedButtons() Recommended usage/conventions.
	 * @return a Set of Integers that contains the Integer representation of
	 * every mouse button that is currently down.
	 */
	public Set<Integer> downButtons() {
		//don't let calling code mess up our state
		return Collections.unmodifiableSet(downButtons);
	}

	/**
	 * @see #pressedButtons() Recommended usage/conventions.
	 * @return a Set of Integers that contains the Integer representation of
	 * every mouse button that is currently up but was down in the last frame.
	 */
	public Set<Integer> releasedButtons() {
		Set<Integer> copy = new HashSet<Integer>(updatedButtons);
		copy.removeAll(downButtons);
		return copy;
	}

	/**
	 * Recommended way to test if a key (the letter 'a' in this example) is pressed:
	 * <pre>
	 *   Input i;
	 *   if (i.pressedKeys().contains(Integer.valueOf(org.lwjgl.input.Keyboard.KEY_A)))
	 *       //do stuff</pre>
	 * Recommended way to process every pressed key:
	 * <pre>
	 *   Input i;
	 *   for (Integer key : i.pressedKeys()) {
	 *       switch (key.intValue()) {
	 *           case org.lwjgl.input.Keyboard.KEY_UP:
	 *               //do stuff
	 *               break;
	 *           case org.lwjgl.input.Keyboard.KEY_LEFT:
	 *               //do stuff
	 *               break;
	 *           case org.lwjgl.input.Keyboard.KEY_DOWN:
	 *               //do stuff
	 *               break;
	 *           case org.lwjgl.input.Keyboard.KEY_RIGHT:
	 *               //do stuff
	 *               break;
	 *           //etc.
	 *       }
	 *   }</pre>
	 * Keep in mind that using the constants <code>org.lwjgl.input.Keyboard.KEY_*</code>
	 * to reference the integer representations will make the code easier to read.
	 * @return a Set of Integers that contains the Integer representation of
	 * every keyboard key that is currently down but was not down in the last
	 * frame.
	 */
	public Set<Integer> pressedKeys() {
		Set<Integer> copy = new HashSet<Integer>(downKeys);
		copy.retainAll(updatedKeys);
		return copy;
	}

	/**
	 * @see #pressedKeys() Recommended usage/conventions.
	 * @return a Set of Integers that contains the Integer representation of
	 * every keyboard key that is currently down and was down in the last frame
	 * as well.
	 */
	public Set<Integer> heldKeys() {
		Set<Integer> copy = new HashSet<Integer>(downKeys);
		copy.removeAll(updatedKeys);
		return copy;
	}

	/**
	 * @see #pressedKeys() Recommended usage/conventions.
	 * @return a Set of Integers that contains the Integer representation of
	 * every keyboard key that is currently down.
	 */
	public Set<Integer> downKeys() {
		//don't let calling code mess up our state
		return Collections.unmodifiableSet(downKeys);
	}

	/**
	 * @see #pressedKeys() Recommended usage/conventions.
	 * @return a Set of Integers that contains the Integer representation of
	 * every keyboard key that is currently up but was down in the last frame.
	 */
	public Set<Integer> releasedKeys() {
		Set<Integer> copy = new HashSet<Integer>(updatedKeys);
		copy.removeAll(downKeys);
		return copy;
	}
}
