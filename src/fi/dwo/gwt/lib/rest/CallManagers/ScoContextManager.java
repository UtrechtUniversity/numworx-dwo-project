package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Promise;

import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

public interface ScoContextManager {

	Promise<DomScoContext> getSco(DomScoContext dummy, DomDwoProfile value);

	Promise<List<DomScoContext>> getScos(DomCourse parent, DomDwoProfile value);

}