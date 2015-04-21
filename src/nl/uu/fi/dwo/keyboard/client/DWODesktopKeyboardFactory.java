package nl.uu.fi.dwo.keyboard.client;

public class DWODesktopKeyboardFactory implements KeyboardFactory {

	@Override
	public AbstractKeyboard getKeyboard() {
		return new DWOTabbedDesktopKeyboard();
	}
}
