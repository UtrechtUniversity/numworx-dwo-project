package fi.dwo.dwojapplet.domain.rest;

import java.util.Collections;
import java.util.List;

import fi.dwo.dwojapplet.REST.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomCourseStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class PublicScoContextManager {

	/**
	 * Retrieve a deeplink sco.
	 * Only public scos from a non-limited profile.
	 * @param domScoId
	 * @return
	 * @throws Dwo2Exception
	 */
	public static DomScoContext get(DomScoContext domScoId, DomDwoProfile profile) throws Dwo2Exception
	{
		RestScoContext rest = new RestScoContext();
		rest.setRestContext(new DomContext());
		rest.setDomDwoProfile(profile);
		rest.setDomScoContext(domScoId);
		DomScoContext result = StoredRestManager.getInstance().put("rest/public/scoContext/get", DomScoContext.class, rest);
		return result;
	}
	
	/** 
	 * Get the scos of a course.
	 * Only public courses are allowed from a non-limited profile.
	 * @param course
	 * @return ordered list of scos
	 * @throws Dwo2Exception
	 */
	
	public static List<DomScoContext> getScos(DomCourse course, DomDwoProfile profile) throws Dwo2Exception
	{
		// Als een profiel "L"imited is, dan is er geen guest access mogelijk.
		if(profile.getDwoProfileRights().contains("l")) return Collections.EMPTY_LIST;
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setDomCourse(course);
		rest.setRestContext(new DomContext());
		List<DomScoContext> result = StoredRestManager.getInstance().getPutList("rest/public/scoContext/getScos", RestListClassTypes.DomScoContext, rest);
		return result;
	}
	
}
