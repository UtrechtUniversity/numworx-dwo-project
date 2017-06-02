package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.Collection;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomHasRole;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

public class PublicStudentScoDataManager implements StudentScoDataManager {

	@Override
	public Promise<Map<String, String>> getValues(DomScoContext sco,
			DomHasRole role, Collection<String> keys) {
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<Void> setValues(DomScoContext sco, DomHasRole role,
			Map<String, String> map) {
		return Promises.failed(new IllegalArgumentException());
	}

}
