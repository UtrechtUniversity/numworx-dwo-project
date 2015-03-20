package nl.uu.fi.dwo.keyboard.client;

public class TabletKeyboardFactory implements KeyboardFactory {

	public TabletKeyboardFactory() {
	}

	@Override
	public AbstractKeyboard getKeyboard() {
		//return new TabbedTouchKeyboard(new TabletKeyboardStatistiek());
		return new TabbedTouchKeyboard();
	}

}
