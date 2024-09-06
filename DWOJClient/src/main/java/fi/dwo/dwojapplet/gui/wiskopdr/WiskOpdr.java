package fi.dwo.dwojapplet.gui.wiskopdr;

import java.util.Locale;

import fi.beans.mainframe.AppletStub;
import fi.dwo.dwojapplet.domain.DwoHelper;

public class WiskOpdr {
  
  private static final int defaultEditorWidth = 850;
  private static final int defaultEditorHeight = 350;
  private static final int defaultDocumentWidth = 780;
  private static final int defaultDocumentHeight = 300;

//	public static WiskOpdrEditPanel getWiskOpdrEditPanel(String description) {
//	  return getWiskOpdrEditPanel(description, DwoHelper.getAu().getLocale(), null, defaultEditorWidth, defaultEditorHeight, defaultDocumentWidth, defaultDocumentHeight);
//	}

	public static WiskOpdrEditPanel getWiskOpdrEditPanel(String description, AppletStub stub) {
		  return getWiskOpdrEditPanel(description, DwoHelper.getAu().getLocale(), stub, defaultEditorWidth, defaultEditorHeight, defaultDocumentWidth, defaultDocumentHeight);
	}

	public static WiskOpdrEditPanel getWiskOpdrEditPanel(String description, Locale locale, int ew, int eh, int dw, int dh) {
	  return new WiskOpdrEditPanel(description, locale, null, ew, eh, dw, dh);
	}

	public static WiskOpdrEditPanel getWiskOpdrEditPanel(String description, Locale locale, AppletStub stub, int ew, int eh, int dw, int dh) {
		  return new WiskOpdrEditPanel(description, locale, stub, ew, eh, dw, dh);
	}
		
	@Deprecated
	public static WiskOpdrPanel getWiskOpdrPanel(String s) {
		return getWiskOpdrPanel(s, DwoHelper.getAu().getLocale());
	}

	@Deprecated
	public static WiskOpdrPanel getWiskOpdrPanel(String s, Locale locale) {
		return new WiskOpdrPanel(s, locale);
	}

	public static WiskOpdrPanel getWiskOpdrPanel(String s, Locale locale, AppletStub stub) {
		return new WiskOpdrPanel(s, locale, stub);
	}
	
}
