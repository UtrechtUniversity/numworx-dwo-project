package nl.uu.fi.dwo.mobile.utils;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

public interface Logging {
	void log(Map<String,?> parameters);

	void setCommunicationRoot(OpdrNavIF comRoot);

	void setLogID(String string);

	void setClassName(String string);

	//String getLogID();

	void setLogObjectives(boolean[][] objectives);	
	void setSMObjectives(String[] objectives);

    void setMaxScore(int max);

	void setLogOption(boolean logOption);
}
