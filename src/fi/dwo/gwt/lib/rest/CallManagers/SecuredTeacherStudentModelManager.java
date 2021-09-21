package fi.dwo.gwt.lib.rest.CallManagers;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;

import java.util.List;

import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

import fi.dwo.gwt.lib.rest.GwtRestVars;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredTeacherStudentModelRestCaller;
import fi.dwo.gwt.lib.rest.util.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext4Student;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.entities.RestStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.exceptions.Dwo2ExceptionCode;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredTeacherStudentModelManager {

  private SecuredTeacherStudentModelRestCaller service = GWT.create(SecuredTeacherStudentModelRestCaller.class);
  
  public Promise<DomLRS> getLRS(DomContext context) {
    RestContext rest = new RestContext();
    rest.setRestContext(context);
    return F(service::getLRS,PathId.getId(context), rest);
  }
  
  public Promise<List<DomStudentModelContext>> getReducedList(DomContext context) {
	RestContext rest = new RestContext();
	rest.setRestContext(context);
	return F(service::getReducedList,PathId.getId(context), rest);
  }
  
  public Promise<DomStudentModelContext> getStudentModel(DomContext context, DomStudentModelContextId id) {
		 RestStudentModelContext rest = new RestStudentModelContext();
		 rest.setRestContext(context);
		 DomStudentModelContext sm = new DomStudentModelContext();
		 sm.setId(id.getId());
		 rest.setDomStudentModelContext(sm);
		 return F(service::getStudentModel, PathId.getId(context), rest);

  }
  
  public Promise<DomStudentModelContext4Student> getStudentModelForClass(DomContext context, DomStudentModelContextId id, DomSchoolClassId sc) {
	  RestStudentModelContextId rest = new RestStudentModelContextId();
	  rest.setRestContext(context);
	  rest.setDomSchoolClass(sc);
	  rest.setDomStudentModelContext(id);
	  return F(service::getStudentModelForClass, PathId.getId(context), rest);
  }
  
  public  Promise<Boolean> updateModelForClass(DomContext context, DomStudentModelContext4Student submit)  {
	  RestStudentModelContext4Student rest = new RestStudentModelContext4Student();
	  rest.setRestContext(context);
	  rest.setDomStudentModelContext(submit);
	  return F(service::updateModelForClass, PathId.getId(context), rest);
  }
  
  public Promise<DomStudentModelScorePerTeacher> getScores(DomContext context, DomStudentModelScorePerTeacher submit) {
	  RestStudentModelScorePerTeacher rest = new RestStudentModelScorePerTeacher(context, submit);
	  return F(service::getScores, PathId.getId(context), rest);
  }

public Promise<String> getDescription(DomStudentModelContextId pid, String uuid, String locale, DomContext context) {
	final Deferred<String> defer = new Deferred<>();
	PersistenceId modelID = pid.getId();
	
	String url = GwtRestVars.getInstance().getServer() + "sec:" + PathId.getId(context)
			+ "/teacher/studentmodel/getDescription?modelId=" + modelID 
			+ "&id=" + uuid 
			+ "&hasRoleId=" + context.getDomHasRole().getId() 
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
	return defer.getPromise().recoverWith(GwtRestVars.getInstance().new Retry(() -> getDescription(pid, uuid, locale, context) ));
  }

/*
 *   public DomStudentModelContext updateModel(DomStudentModelContext submit)
          throws Dwo2Exception {
        RestStudentModelContext rest = new RestStudentModelContext();
        rest.setRestContext(getContext());
        rest.setDomStudentModelContext(submit);

        DomStudentModelContext result = StoredRestManager.getInstance()
            .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/update", DomStudentModelContext.class, rest);
        LOG.log(Level.FINE, "Updated studentmodel of teacher with username {0} to his school.",
            new Object[] {RestAuthenticator.getInstance().getUsername()});
        return result;
      }
   public DomStudentModelContext patchModel(DomStudentModelContextPatch submit)
          throws Dwo2Exception {
        RestStudentModelContextPatch rest = new RestStudentModelContextPatch();
        rest.setRestContext(getContext());
        rest.setDomPatch(submit);

        DomStudentModelContext result = StoredRestManager.getInstance()
            .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/patch", DomStudentModelContext.class, rest);
        LOG.log(Level.FINE, "Patch studentmodel of teacher with username {0} to his school.",
            new Object[] {RestAuthenticator.getInstance().getUsername()});
        return result;
      }
  public static DomSchoolMethod updateActiveMethod(DomSchoolMethod submit) 
            throws Dwo2Exception {
      RestSchoolMethod rest = new RestSchoolMethod();
      rest.setRestContext(getContext());
      rest.setDomSchoolMethod(submit);
      DomSchoolMethod result = StoredRestManager.getInstance()
              .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/updateMethod", DomSchoolMethod.class, rest);
      return result;
  }
  
  public static DomSchoolMethod getActiveMethod(DomStudentModelContextId id) throws Dwo2Exception {
      RestStudentModelContextId rest = new RestStudentModelContextId();
      rest.setRestContext(getContext());
      rest.setDomStudentModelContext(id);
      rest.setDomSchoolClass(null);
      DomSchoolMethod result = StoredRestManager.getInstance()
              .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/getMethod", DomSchoolMethod.class, rest);
      return result;
  }
  
  
*/

}
