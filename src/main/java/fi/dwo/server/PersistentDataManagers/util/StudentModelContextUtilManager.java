package fi.dwo.server.PersistentDataManagers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fi.dwo.commons.persistence.entities.PersistentStudentModelContext;
import fi.dwo.commons.persistence.entities.PersistentStudentModelItem;
import fi.dwo.server.PersistentDataManagers.core.StudentModelContextManager;
import fi.dwo.server.PersistentDataManagers.core.StudentModelItemManager;
import jersey.repackaged.com.google.common.base.Objects;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class StudentModelContextUtilManager {

	public static void merge(PersistentStudentModelContext pModel) {
		List<PersistentStudentModelItem> objs = StudentModelItemManager.findEntities(pModel);
		// Merge
		Map<String, DomStudentModelObj> map = new HashMap<>();
		objs.forEach(item -> map.put(item.getId(), item.getItem()));
		for (DomStudentModelCategory cat : pModel.getModelStructure().getCategories()) {
			List<DomStudentModelObj> objList = cat.getObjectives();
			for (int i = 0; i < objList.size(); i++) {
				DomStudentModelObj obj = objList.get(i);
				if (obj.getInfo().getTitle() != null) continue; // safety 
				obj = map.getOrDefault(obj.getInfo().getId(), obj);
				objList.set(i, obj);
			}
		}
	}
	
	public static PersistentStudentModelContext edit(PersistentStudentModelContext pModel) {
		List<PersistentStudentModelItem> objs = StudentModelItemManager.findEntities(pModel);
		Map<String, PersistentStudentModelItem> map = new HashMap<>();
		objs.forEach(item -> map.put(item.getId(), item));
		DomStudentModelStructure structure = pModel.getModelStructure();
		DomStudentModelStructure copy = new DomStudentModelStructure();
		copy.setInfo(structure.getInfo());
		copy.setTimestamp(structure.getTimestamp());
		copy.setOwner(structure.getOwner());
		copy.setCategories(new ArrayList<>());
		for (DomStudentModelCategory cat : structure.getCategories()) {
			DomStudentModelCategory copycat = new DomStudentModelCategory();
			copycat.setInfo(cat.getInfo());
			copycat.setObjectives(new ArrayList<>());
			copy.getCategories().add(copycat);
			for(DomStudentModelObj obj: cat.getObjectives()) {
				String id = obj.getInfo().getId();
				if (id == null) {
					copycat.getObjectives().add(obj);
					continue;
				}
				DomStudentModelObj copyobj = new DomStudentModelObj();
				copyobj.setInfo(new DomStudentModelContextInfo());
				copyobj.getInfo().setId(obj.getInfo().getId());
				copycat.getObjectives().add(copyobj);
				PersistentStudentModelItem item = map.remove(id);
				if (item == null) {
					item = new PersistentStudentModelItem();
					item.setId(id);
					item.setModelID(pModel.getModelID());
					item.setSchoolID(pModel.getSchoolID());
					item.setItem(obj);
					StudentModelItemManager.create(item);
				} else if (!Objects.equal(obj, item.getItem())){
					item.setItem(obj);
					StudentModelItemManager.edit(item);
				}
			}
		}
		try { 
			pModel.setModelStructure(copy);
			pModel = StudentModelContextManager.edit(pModel);
			map.values().forEach(item -> StudentModelItemManager.destroy(item.getItemID()));
		} finally {
			pModel.setModelStructure(structure);
		}
		return (pModel);
	}

	public static List<DomStudentModelContext> reduce(List<DomStudentModelContext> list) {
    	for(DomStudentModelContext item: list) {
    		DomStudentModelStructure structure = item.getModelStructure();
    		structure.setCategories(null);
    		DomStudentModelContextInfo info = structure.getInfo();
			info.setDescription(null);
			info.setVoorkennis(null);
			info.setMethods(null);
    	}
		return list;
	}

	public static List<DomStudentModelContext4Student> reduce4s(List<DomStudentModelContext4Student> list) {
	    	for(DomStudentModelContext4Student item: list) {
	    		DomStudentModelStructure structure = item.getModelStructure();
	    		structure.setCategories(null);
	    		DomStudentModelContextInfo info = structure.getInfo();
				info.setDescription(null);
				info.setVoorkennis(null);
				info.setMethods(null);
	    	}
			return list;
	}
	
}
