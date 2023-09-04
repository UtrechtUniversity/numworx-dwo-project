package fi.dwo.gwt.lib.rest.CallManagers;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.PublicStudentModelRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;

public class PublicStudentModelManager implements LRSManager {
	PublicStudentModelRestCaller service = GWT.create(PublicStudentModelRestCaller.class);
	
	@Override
	public Promise<DomLRS> getLRS(DomContext context, DomDwoProfile profile) {
		PromiseCallback<DomLRS> result = new PromiseCallback<>();
		RestDwoProfile rest = new RestDwoProfile();
		rest.setRestContext(context);
		rest.setDomDwoProfile(profile);
		service.getLRS(rest, result);
		return result.getPromise();
	}
}
