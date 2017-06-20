package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.Collection;
import java.util.Map;

import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

public class PublicStudentScoDataManager implements StudentScoDataManager {

	@Override
	public Promise<Map<String, String>> getValues(DomScoContext sco,
			DomContext context, Collection<String> keys) {
		return Promises.failed(new IllegalArgumentException());
	}

	@Override
	public Promise<?> setValues(DomScoContext sco, DomContext context,
			Map<String, String> map) {
		return Promises.failed(new IllegalArgumentException());
	}

}
