package fi.wiskopdr.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

public class Text {
	public static TextIF rb = new Text_nl(); 
							//GWT.create(TextConstants.class);
}

// This is the GWT way:
interface TextConstants extends ConstantsWithLookup, TextIF {
	@DefaultStringValue("of")
	String ofLabel();
	@DefaultStringValue("geen oplossingen")
	String geenOplossingenLabel();
	@DefaultStringValue("alles is een oplossing")
	String allesOplossingLabel();
// enzovoort
}