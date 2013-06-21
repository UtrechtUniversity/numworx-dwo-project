package nl.uu.fi.dwo.interaction.client;

import java.util.Map;

public interface InteractionStub extends InteractionView {
	/**
	 * In plaats van constructor een methode
	 * @param launchData
	 * @param values map met randomvariabelen
	 */
	public void init(int width, int height, Map<String, Object> launchData, Map<String, Number> values);
}
