package nl.uu.fi.dwo.mobile.client.ui.views;

public interface AnchorContext {

	void gotoUrl(String href);
	void gotoPlace(String token);
	default void prepareLeave() { }
}