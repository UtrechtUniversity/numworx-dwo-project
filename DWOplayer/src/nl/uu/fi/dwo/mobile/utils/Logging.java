package nl.uu.fi.dwo.mobile.utils;

import java.util.Map;

import nl.uu.fi.dwo.interaction.client.OpdrNavIF;

public interface Logging {
	void log(Map<String,?> parameters);

	void setCommunicationRoot(OpdrNavIF comRoot);

	void setLogID(String string);

	void setClassName(String string);

	default String getLogID() { return null; }

	void setLogObjectives(boolean[][] objectives);	
	void setSMObjectives(String[] objectives);
	String[] getSMObjectives();

	default void setSMForeknowledge(String[] foreknowledge) { }
	default String[] getSMForeknowledge() { return null; }

    void setMaxScore(int max);

	void setLogOption(boolean logOption);

	default void updateLog(Map<String, ?> map) { }

	default void getStateHook(Map<String, Object> h) {}
	default void setStateHook(Map<String,Object> state) {}

	default void setSMGuess(Number smGuess) { }


}
