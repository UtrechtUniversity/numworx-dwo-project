package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.persistence.entities.PersistentDwoSystemParameters;
import fi.dwo.server.PersistentDataManagers.cache.DwoSystemParametersCache;
import fi.dwo.server.PersistentDataManagers.core.DwoSystemParametersManager;

public class DwoSystemParametersUtilManager {

	static {
		DwoSystemParametersCache.clear(); // start afresh when booting
	}

	public static PersistentDwoSystemParameters findByName(String string) {
		PersistentDwoSystemParameters result;
		result = DwoSystemParametersCache.get(string);
		if (result == null) {
			result = DwoSystemParametersManager.findByName(string);
			if (result != null)
				DwoSystemParametersCache.put(result);
		}
		return result;
	}	
	
}
