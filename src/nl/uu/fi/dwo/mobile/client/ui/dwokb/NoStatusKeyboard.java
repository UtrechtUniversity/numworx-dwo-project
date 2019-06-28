package nl.uu.fi.dwo.mobile.client.ui.dwokb;

public class NoStatusKeyboard extends DWOKeyboard {

	/* (non-Javadoc)
	 * @see nl.uu.fi.dwo.mobile.client.ui.dwokb.DWOKeyboard#getStatusBarHeight()
	 */
	@Override
	public int getStatusBarHeight() {
		return 0;
	}

	public NoStatusKeyboard() {
		super();
		staticPanel.setVisible(false);
		staticPanel.removeFromParent();
	}
/**
 * always hidden.
 */
  @Override
  public void hide() {
  }

}
