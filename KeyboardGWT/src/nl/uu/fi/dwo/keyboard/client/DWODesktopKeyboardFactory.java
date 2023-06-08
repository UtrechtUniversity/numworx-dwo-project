package nl.uu.fi.dwo.keyboard.client;

public class DWODesktopKeyboardFactory implements KeyboardFactory {

	private boolean premium;

  @Override
	public AbstractKeyboard getKeyboard() {
      DWOTabbedDesktopKeyboard kb = new DWOTabbedDesktopKeyboard();
      kb.setPremium(premium);
      return kb;
	}

  @Override
  public void setPremium(boolean premium) {
    this.premium = premium;
  }
}
