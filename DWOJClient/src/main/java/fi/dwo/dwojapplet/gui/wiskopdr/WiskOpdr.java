package fi.dwo.dwojapplet.gui.wiskopdr;

import java.util.Locale;

import fi.dwo.dwojapplet.domain.DwoHelper;

public class WiskOpdr {
  
  private static int defaultEditorWidth = 850;
  private static int defaultEditorHeight = 350;
  private static int defaultDocumentWidth = 780;
  private static int defaultDocumentHeight = 300;

	public static WiskOpdrEditPanel getWiskOpdrEditPanel(String description) {
	  return getWiskOpdrEditPanel(description, DwoHelper.getAu().getLocale(), defaultEditorWidth, defaultEditorHeight, defaultDocumentWidth, defaultDocumentHeight);
	}

	public static WiskOpdrEditPanel getWiskOpdrEditPanel(String description, Locale locale, int ew, int eh, int dw, int dh) {
	  return new WiskOpdrEditPanel(description, locale, ew, eh, dw, dh);
	}
		
	@Deprecated
	public static WiskOpdrPanel getWiskOpdrPanel(String s) {
		return getWiskOpdrPanel(s, DwoHelper.getAu().getLocale());
	}

	public static WiskOpdrPanel getWiskOpdrPanel(String s, Locale locale) {
		return new WiskOpdrPanel(s, locale);
	}

}
