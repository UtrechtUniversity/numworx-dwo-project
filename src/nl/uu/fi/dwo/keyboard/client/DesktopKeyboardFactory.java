package nl.uu.fi.dwo.keyboard.client;

public class DesktopKeyboardFactory implements KeyboardFactory {

	public DesktopKeyboardFactory() {
	}

	@Override
	public AbstractKeyboard getKeyboard() {
		return new DesktopKeyboard().init();
		//return new DesktopKeyboardMeetkunde();
	}

}
