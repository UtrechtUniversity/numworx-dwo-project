package nl.uu.fi.dwo.mobile.client.ui.views;

import com.google.gwt.dom.client.Element;

public interface AnchorContext {

	void gotoUrl(String href);
	void gotoPlace(String token);
	default void prepareLeave() { }
	default void addElement(String anchor, Element e) { }
	default void gotoElement(String anchor) { }
}