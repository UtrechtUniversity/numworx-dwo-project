package nl.uu.fi.dwo.interaction.client;

import java.util.Map;

public interface InteractionStub extends InteractionView {
	/**
	 * In plaats van constructor een methode
	 * @param launchData
	 * @param values map met randomvariabelen
	 */
	public void init(Map<String, Object> launchData, Map<String, Number> values);
}
