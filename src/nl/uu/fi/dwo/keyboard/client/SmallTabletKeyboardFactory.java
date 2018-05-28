package nl.uu.fi.dwo.keyboard.client;

public class SmallTabletKeyboardFactory extends TabletKeyboardFactory implements KeyboardFactory {

	@Override
	public AbstractKeyboard getKeyboard() {
		//return new TabbedTouchKeyboard(new TabletKeyboardStatistiek());
		return new TabbedTouchKeyboard(true);
	}

}
