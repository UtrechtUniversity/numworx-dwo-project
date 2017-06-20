package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.Collection;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import org.osgi.util.promise.Promise;

public interface StudentScoDataManager {

	public abstract Promise<Map<String, String>> getValues(DomScoContext sco,
			DomContext domContext, Collection<String> keys);

	public abstract Promise<?> setValues(DomScoContext sco, DomContext domContext,
			Map<String, String> map);

}