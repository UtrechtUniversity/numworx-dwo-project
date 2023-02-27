package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.client.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.PublicScoContextRestCaller;
import fi.dwo.gwt.lib.rest.client.RestCallers.ScoContextRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.entities.RestScoContext;

public class PublicScoContextManager implements ScoContextManager {

	final ScoContextRestCaller server;
	
	PublicScoContextManager(ScoContextRestCaller caller) {
		server = caller;
	}

	public PublicScoContextManager() {
		this(GWT.<ScoContextRestCaller>create(PublicScoContextRestCaller.class));
	}
	
	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoContext#getSco(nl.uu.fi.dwo.rest.dom.entities.DomScoContext, nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile)
	 */
	@Override
	public Promise<DomScoContext> getSco(DomScoContext dummy, DomDwoProfile value, DomSchoolClassId schoolClassID, DomContext context) {
		RestScoContext rest = new RestScoContext();
		rest.setDomDwoProfile(value);
		rest.setDomScoContext(dummy);
		rest.setSchoolClassID(schoolClassID);
		rest.setRestContext(context);
		PromiseCallback<DomScoContext> callback = new PromiseCallback<DomScoContext>();
		server.get(rest, callback);
		return callback.getPromise();
	}

	/* (non-Javadoc)
	 * @see fi.dwo.gwt.lib.rest.CallManagers.ScoContext#getScos(nl.uu.fi.dwo.rest.dom.entities.DomCourse, nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile)
	 */
	@Override
	public Promise<List<DomScoContext>> getScos(DomCourse parent, DomDwoProfile value, DomSchoolClassId schoolClassID, DomContext context) {
		RestCourse rest = new RestCourse();
		PromiseCallback<List<DomScoContext>> callback = new PromiseCallback<List<DomScoContext>>();
		rest.setDomCourse(parent);
		rest.setDomDwoProfile(value);
		rest.setSchoolClassID(schoolClassID);
		rest.setRestContext(context);
		server.getScos(rest, callback);
		return callback.getPromise();
	}

}
