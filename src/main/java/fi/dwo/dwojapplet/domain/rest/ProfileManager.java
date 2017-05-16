package fi.dwo.dwojapplet.domain.rest;

import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public interface ProfileManager {

	DomDwoProfileFull get(String name) throws Dwo2Exception;
	DomDwoProfileFull get(long id) throws Dwo2Exception;
	DomDwoProfileFull get(int id) throws Dwo2Exception;
}
