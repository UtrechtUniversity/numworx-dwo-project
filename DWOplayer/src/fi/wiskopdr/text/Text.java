package fi.wiskopdr.text;

import com.google.gwt.core.client.GWT;

public class Text {
	public static TextConstants constants = GWT.create(TextConstants.class);
	@Deprecated
	public static TextIF rb = //new Text_nl(); // voorlopig maar op 2 plekken nodig. FIXME welke zijn nodig?
							    constants;
}