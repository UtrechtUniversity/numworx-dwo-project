package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;

public interface LRSManager {

	Promise<DomLRS> getLRS(DomContext context, DomDwoProfile profile);

}
