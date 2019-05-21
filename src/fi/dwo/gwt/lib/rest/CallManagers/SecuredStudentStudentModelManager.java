package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;

import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentStudentModelRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredStudentStudentModelManager {

	SecuredStudentStudentModelRestCaller service = GWT.create(SecuredStudentStudentModelRestCaller.class);
	
	public Promise<List<DomStudentModelContext>> getStudentModels(DomContext context) {
		PromiseCallback<List<DomStudentModelContext>> callback = new PromiseCallback<>();
		RestContext rest = new RestContext();
		rest.setRestContext(context);
		service.getStudentModels(PathId.getId(context), rest, callback);
		return callback.getPromise();
	}
	
	public Promise<DomStudentModelDataScore> getStudentModelDataScore(DomContext context, DomStudentModelContextId id) {
		PromiseCallback<DomStudentModelDataScore> callback = new PromiseCallback<>();
		
		RestStudentModelContextId rest = new RestStudentModelContextId();
		rest.setDomStudentModelContext(id);
		rest.setRestContext(context);
		service.getStudentModelDataScore(PathId.getId(context), rest, callback);
		
		return callback.getPromise();
	}
	
	public Promise<DomLRS> getLRS(DomContext context) {
	  PromiseCallback<DomLRS> callback = new PromiseCallback<>();
	  RestContext rest = new RestContext();
	  rest.setRestContext(context);
	  service.getLRS(PathId.getId(context), rest, callback);
	  return callback.getPromise();
	}
}
