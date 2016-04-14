package nl.uu.fi.dwo.account.client.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface Text extends Constants {

	Text constants = GWT.create(Text.class);

	@DefaultStringValue("Mijn\u00A0Profiel")
	String GUIMNU_MY_PROFILE();
	@DefaultStringValue("Mijn\u00A0Schoollogins")
	String GUIMNU_MY_SCHOOLLOGINS();
	@DefaultStringValue("Even\u00A0wachten...")
	String GUIMSG_LOADING();

}
