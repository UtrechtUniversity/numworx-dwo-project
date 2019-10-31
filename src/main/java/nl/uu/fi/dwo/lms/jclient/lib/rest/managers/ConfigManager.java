package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.Locale;

import nl.uu.fi.dwo.rest.dom.entities.DomAppletConfig;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;

public interface ConfigManager {
	List<DomAppletConfig> getConfigurations(Locale l) throws Dwo2Exception;
	List<DomAppletConfig> getConfigurations(Locale locale, DomDwoProfile dwoProfile) throws Dwo2Exception;
}
