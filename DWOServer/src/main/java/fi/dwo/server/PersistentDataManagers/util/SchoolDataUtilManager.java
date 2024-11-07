package fi.dwo.server.PersistentDataManagers.util;

import fi.dwo.commons.persistence.entities.PersistentSchool;
import fi.dwo.commons.persistence.entities.PersistentSchoolData;
import fi.dwo.server.PersistentDataManagers.cache.SchoolDataCache;
import fi.dwo.server.PersistentDataManagers.core.SchoolDataManager;
import nl.uu.fi.dwo.rest.dom.entities.util.DelState;

public class SchoolDataUtilManager {

	private SchoolDataUtilManager() {
	}

	public static PersistentSchoolData find(PersistentSchool school) {
		Long id = school.getSchoolID();
		PersistentSchoolData data = SchoolDataCache.get(id);
		if (data == null) {
			data = SchoolDataManager.findEntity(id);
			if (data == null) {
				data = new PersistentSchoolData(id);
				data.setOptlock(0L);
			} else {
				if (data.getDelState() != DelState.not)
					data.setSchoolData("{}");
			}
			SchoolDataCache.put(data);
		}
		return data;
	}

	public static PersistentSchoolData findEntity(Long id) {
		SchoolDataCache.remove(id);
		return SchoolDataManager.findEntity(id);
	}

	public static PersistentSchoolData edit(PersistentSchoolData data) {
		data = SchoolDataManager.edit(data);
		SchoolDataCache.putIfPresent(data);
		return data;
	}
	
	
	
}
