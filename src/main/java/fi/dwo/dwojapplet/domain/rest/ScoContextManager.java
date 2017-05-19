package fi.dwo.dwojapplet.domain.rest;

import java.util.List;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

public interface ScoContextManager {

	/**
	 * Retrieve a deeplink sco.
	 * Only public scos from a non-limited profile.
	 * @param id
	 * @return
	 * @throws Dwo2Exception
	 */
	DomScoContext get(PersistenceId id) throws Dwo2Exception;
	
	/** 
	 * Get the scos of a course.
	 * Only public courses are allowed from a non-limited profile.
	 * @param course
	 * @return ordered list of scos
	 * @throws Dwo2Exception
	 */
	
	List<DomScoContext> getScos(DomCourse course) throws Dwo2Exception;
	
}
