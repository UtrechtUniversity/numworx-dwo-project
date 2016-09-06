package fi.dwo.server.PersistentDataManagers.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.PersistenceException;

import fi.dwo.commons.persistence.entities.PersistentHasRole;
import fi.dwo.commons.persistence.entities.PersistentSchoolClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClass;
import fi.dwo.commons.persistence.entities.PersistentStudentOfClassPK;
import fi.dwo.commons.util.DwoDateUtilities;
import fi.dwo.server.PersistentDataManagers.core.HasRoleManager;
import fi.dwo.server.PersistentDataManagers.core.StudentOfClassManager;

public class SchoolClassUtilManager {

	public static Boolean removeStudentFromSchoolClass(PersistentHasRole phr,
			PersistentSchoolClass schoolClass) {
		try {
		    PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID());
		    if (phr.getClassID() != null && socId != null && phr.getClassID().equals(socId.getClassID())) {
 
// Switch to a better class rather than null                	
		    	phr.setClassID(null);
// Strategy: take last/newest
		    	List<PersistentStudentOfClass> candidates = StudentOfClassManager.findEntities(phr.getPersistentHasRolePK());
		    	candidates = new ArrayList<>(candidates);
		    	candidates.sort((PersistentStudentOfClass o1, PersistentStudentOfClass o2) -> {
                            java.util.Date d1 = o1.getRegisterDate();
                            java.util.Date d2 = o2.getRegisterDate();
                            int c = d1.compareTo(d2);
                            if(c == 0) {
                                o1.getPersistentStudentOfClassPK().getClassID().compareTo(o2.getPersistentStudentOfClassPK().getClassID());
                            }
                            return 0;
                            });
		    	ListIterator<PersistentStudentOfClass> iterator = candidates.listIterator(candidates.size());
		    	while(iterator.hasPrevious()) {
		    		PersistentStudentOfClass last = iterator.previous();
		    		final Long lastID = last.getPersistentStudentOfClassPK().getClassID();
					if(!lastID .equals( schoolClass.getClassID()))
		    		{
		    			phr.setClassID(lastID);
		    			break;
		    		}
		    	}
		    	
		    	
		        HasRoleManager.edit(phr);
		    }
		    StudentOfClassManager.destroy(socId);
		}
		catch (PersistenceException e) {
		    return false;
		}
		return true;
	}

	public static Boolean registerStudentForSchoolClass(PersistentHasRole phr,
			PersistentSchoolClass schoolClass) {
		try {
		    PersistentStudentOfClassPK socId = new PersistentStudentOfClassPK(phr.getPersistentHasRolePK().getUserID(), schoolClass.getClassID(), phr.getPersistentHasRolePK().getSchoolGroupID());
		    PersistentStudentOfClass soc = new PersistentStudentOfClass();
		    soc.setPersistentStudentOfClassPK(socId);
		    soc.setRegisterDate(DwoDateUtilities.getCurrentDwoDate());
		    StudentOfClassManager.create(soc);
		    
		    if(phr.getClassID() == null) {
		    	phr.setClassID(schoolClass.getClassID());
		    	HasRoleManager.edit(phr); // TODO met try/catch?
		    }
		
		}
		catch (PersistenceException e) {
		    return false;
		}
		return true;
	}

	
}
