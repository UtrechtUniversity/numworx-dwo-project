package nl.uu.fi.dwo.keyboard.client;

public class CombinedKeyboardFactory implements KeyboardFactory {

	private CombinedState state;

	@Override
	public AbstractKeyboard getKeyboard() {
		
		return new TabbedCombinedKeyboard(state);
	}

	@Override
  	public void setPremium(boolean premium) {
  	}

	@Override
	public void setCombinedState(CombinedState state) {
		this.state = state;
	}

}
