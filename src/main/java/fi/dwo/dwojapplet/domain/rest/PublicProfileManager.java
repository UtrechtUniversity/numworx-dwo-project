package fi.dwo.dwojapplet.domain.rest;

import java.net.URLEncoder;

import fi.dwo.dwojapplet.REST.StoredRestManager;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public class PublicProfileManager implements ProfileManager {

	@Override
	public DomDwoProfileFull get(String name) throws Dwo2Exception {
		name = URLEncoder.encode(name);		
		return StoredRestManager.getInstance().get("rest/public/profile/"+name, DomDwoProfileFull.class);
	}

	@Override
	public DomDwoProfileFull get(long id) throws Dwo2Exception {
		return get(Long.toString(id));
	}

	@Override
	public DomDwoProfileFull get(int id) throws Dwo2Exception {
		return get(Integer.toString(id));
	}

}
