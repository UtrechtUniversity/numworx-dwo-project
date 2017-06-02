package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

public class SecuredStudentScoDataManager implements StudentScoDataManager {

	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoDataManager#getValues(nl.uu.fi.dwo.rest.dom.entities.DomScoContext, nl.uu.fi.dwo.rest.dom.entities.DomHasRole, java.util.Collection)
	 */
	@Override
	public Promise<Map<String,String>> getValues(DomScoContext sco, DomHasRole role, Collection<String> keys) {
		Map<String, String> emptyMap = Collections.emptyMap();
		return Promises.resolved(emptyMap);
	}
	
	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoDataManager#setValues(nl.uu.fi.dwo.rest.dom.entities.DomScoContext, nl.uu.fi.dwo.rest.dom.entities.DomHasRole, java.util.Map)
	 */
	@Override
	public Promise<Void> setValues(DomScoContext sco, DomHasRole role, Map<String,String> map) {
		return Promises.resolved(null);
	}
}
