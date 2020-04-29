package nl.uu.fi.dwo.keyboard.client;

import com.google.gwt.resources.client.CssResource;

public interface ResponsiveCSS extends CssResource {
	int SMALL = 700;
	int EXTRASMALL = 600;
	String small();
	String normal();
	String extrasmall();
}
