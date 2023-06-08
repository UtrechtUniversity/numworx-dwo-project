package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.core.client.GWT;

public class DWOTabletKeyboardFactory implements KeyboardFactory {

    private boolean premium = false;
	public DWOTabletKeyboardFactory() {
	}

	@Override
	public AbstractKeyboard getKeyboard() {
		return new DWOTabbedTouchKeyboard(premium);
	}

	static public final DWOkeyboardBundle resources = GWT.create(DWOkeyboardBundle.class);

  @Override
  public void setPremium(boolean premium) {
    this.premium = premium;
  }
}
