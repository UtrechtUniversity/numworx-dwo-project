package nl.uu.fi.dwo.mobile.utils;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

public interface Logging {
	void log(Map<String,?> parameters);

	void setCommunicationRoot(OpdrNavIF comRoot);

	void setLogID(String string);

	void setClassName(String string);
}
