package fi.dwo.dwojapplet.gui.wiskopdr;

import java.util.Locale;

import fi.dwo.dwojapplet.domain.DwoHelper;

public class WiskOpdr {

	public static WiskOpdrEditPanel getWiskOpdrEditPanel(String description) {
		return new WiskOpdrEditPanel(description);
	}

	@Deprecated
	public static WiskOpdrPanel getWiskOpdrPanel(String s) {
		return getWiskOpdrPanel(s, DwoHelper.getAu().getLocale());
	}

	public static WiskOpdrPanel getWiskOpdrPanel(String s, Locale locale) {
		return new WiskOpdrPanel(s, locale);
	}

}
