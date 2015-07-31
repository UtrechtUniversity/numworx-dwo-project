package nl.uu.fi.dwo.mobile.utils;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

public final class NoLogging implements Logging {

	public static final Logging instance = new NoLogging();
	
	private NoLogging() {}

	public void log(Map<String, ?> parameters) {
	}

	public void setCommunicationRoot(OpdrNavIF comRoot) {
	}

	public void setLogID(String string) {
	}

	public void setClassName(String string) {
	}

}
