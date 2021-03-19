package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.List;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredStudentStudentModelRestCaller;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredStudentStudentModelManager {

	SecuredStudentStudentModelRestCaller service = GWT.create(SecuredStudentStudentModelRestCaller.class);
	
	public Promise<List<DomStudentModelContext>> getStudentModels(DomContext context) {
		RestContext rest = new RestContext();
		rest.setRestContext(context);
		return F(service::getStudentModels,PathId.getId(context), rest);
	}
	
	public Promise<DomStudentModelDataScore> getStudentModelDataScore(DomContext context, DomStudentModelContextId id) {
		RestStudentModelContextId rest = new RestStudentModelContextId();
		rest.setDomStudentModelContext(id);
		rest.setRestContext(context);
		return F(service::getStudentModelDataScore,PathId.getId(context), rest);
	}
	
	public Promise<DomLRS> getLRS(DomContext context) {
	  RestContext rest = new RestContext();
	  rest.setRestContext(context);
	  return F(service::getLRS,PathId.getId(context), rest);
	}
	
	public Promise<List<DomStudentModelContext>> getReducedModels(DomContext context, DomSchoolClass sc) {
		RestSchoolClass rest = new RestSchoolClass();
		rest.setDomSchoolClass(sc);
		rest.setRestContext(context);
		return F(service::getReducedList, PathId.getId(context), rest);
	}

	public Promise<List<DomStudentModelContext4Student>> getReducedModelsForClass(DomContext context, DomSchoolClass sc) {
		RestSchoolClass rest = new RestSchoolClass();
		rest.setDomSchoolClass(sc);
		rest.setRestContext(context);
		return F(service::getReducedListForClass, PathId.getId(context), rest);
	}
	
	public Promise<DomStudentModelContext> getStudentModel(DomContext context, DomStudentModelContextId id, DomSchoolClass sc) {
		RestStudentModelContextId rest = new RestStudentModelContextId();
		rest.setDomStudentModelContext(id);
		rest.setRestContext(context);
		rest.setDomSchoolClass(sc);
		return F(service::getStudentModel, PathId.getId(context), rest);
	}

	public Promise<DomStudentModelContext4Student> getStudentModelForClass(DomContext context, DomStudentModelContextId id, DomSchoolClass sc) {
		RestStudentModelContextId rest = new RestStudentModelContextId();
		rest.setDomStudentModelContext(id);
		rest.setRestContext(context);
		rest.setDomSchoolClass(sc);
		return F(service::getForClass, PathId.getId(context), rest);
		
	}
	
	@SuppressWarnings("unchecked")
	public Promise<String> getDescription(DomStudentModelContextId id, DomSchoolClass sc, String uuid, String locale,
			DomContext context) {
		final Deferred<String> defer = new Deferred<>();
		PersistenceId modelID = id.getId();
		
		String url = GwtRestVars.getInstance().getServer() + "sec:" + PathId.getId(context)
				+ "/student/studentmodel/getDescription?modelId=" + modelID 
				+ "&id=" + uuid 
				+ "&hasRoleId=" + context.getDomHasRole().getId() 
				+ "&schoolClassId=" + sc.getId()
				+ "&locale=" + locale;
		
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		rb.setTimeoutMillis(100000);
		rb.setHeader("Authorization", RestAuthenticator.instance.getAuthorization());
		try {
			rb.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == 401) {
						defer.fail(new Dwo2Exception(Dwo2ExceptionCode.User_AuthenticationError, response.getStatusText()));
					} else if(response.getStatusCode() == 200) {
						String text = response.getText();					
						defer.resolve(text);
					} else {
						defer.fail(new RequestException(response.getStatusText()));
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					defer.fail(exception);
				}
			});
		} catch (RequestException e) {
			defer.fail(e);
		}		
		return defer.getPromise().recoverWith(GwtRestVars.getInstance().new Retry(() -> getDescription(id, sc, uuid, locale, context) ));
	}

}
