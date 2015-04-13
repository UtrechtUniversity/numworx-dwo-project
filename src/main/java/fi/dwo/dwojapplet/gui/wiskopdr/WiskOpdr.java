package fi.dwo.dwojapplet.gui.wiskopdr;

public class WiskOpdr {

	public static WiskOpdrEditPanel getWiskOpdrEditPanel(String description) {
		return new WiskOpdrEditPanel(description);
	}

	public static WiskOpdrPanel getWiskOpdrPanel(String s) {
		return new WiskOpdrPanel(s);
	}

}
