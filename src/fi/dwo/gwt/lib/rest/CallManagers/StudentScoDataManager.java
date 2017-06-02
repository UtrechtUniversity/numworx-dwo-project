package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.Collection;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import org.osgi.util.promise.Promise;

public interface StudentScoDataManager {

	public abstract Promise<Map<String, String>> getValues(DomScoContext sco,
			DomHasRole role, Collection<String> keys);

	public abstract Promise<Void> setValues(DomScoContext sco, DomHasRole role,
			Map<String, String> map);

}