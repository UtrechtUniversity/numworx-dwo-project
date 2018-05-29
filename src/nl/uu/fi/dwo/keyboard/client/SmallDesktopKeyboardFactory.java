package nl.uu.fi.dwo.keyboard.client;

public class SmallDesktopKeyboardFactory extends DesktopKeyboardFactory implements KeyboardFactory {

	public SmallDesktopKeyboardFactory() {
	}

	@Override
	public AbstractKeyboard getKeyboard() {
		return new TabbedDesktopKeyboard(true);
	}

}
