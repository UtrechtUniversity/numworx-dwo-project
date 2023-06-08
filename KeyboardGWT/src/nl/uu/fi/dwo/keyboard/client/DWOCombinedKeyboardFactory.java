package nl.uu.fi.dwo.keyboard.client;

public class DWOCombinedKeyboardFactory implements KeyboardFactory {

	private boolean premium;
	private CombinedState state;

	@Override
	public AbstractKeyboard getKeyboard() {
	      DWOTabbedCombinedKeyboard kb = new DWOTabbedCombinedKeyboard(premium, state);
	      return kb;
	}

	@Override
	public void setPremium(boolean premium) {
	    this.premium = premium;
	}

	public void setCombinedState(CombinedState state) {
		this.state = state;
	}
}
