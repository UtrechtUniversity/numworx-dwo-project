package nl.uu.fi.dwo.keyboard.client;

public class TabletKeyboardFactory implements KeyboardFactory {

	@Override
	public AbstractKeyboard getKeyboard() {
		//return new TabbedTouchKeyboard(new TabletKeyboardStatistiek());
		return new TabbedTouchKeyboard();
	}

  @Override
  public void setPremium(boolean premium) {
  }

}
