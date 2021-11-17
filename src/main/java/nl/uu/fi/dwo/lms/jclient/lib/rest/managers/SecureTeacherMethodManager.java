package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestMethod;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureTeacherMethodManager {

  private static final Logger LOG =
      Logger.getLogger(SecureTeacherMethodManager.class.getName());

  public static List<DomMethod> getList(DomDwoProfile profile) throws Dwo2Exception {
    RestDwoProfile rest = new RestDwoProfile();
    rest.setRestContext(getContext());
    rest.setDomDwoProfile(profile);
    List<DomMethod> src =
        StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/teacher/method/getList",
            RestListClassTypes.DomMethod, rest);
    LOG.log(Level.FINE, "Retrieved list of methods of the teacher with username {0}.",
        new Object[] {authenticator().getUsername()});
    return src;
  }  


  static RestAuthenticator authenticator() {
	  return StoredRestManager.getInstance().getAuthenticator();
  }
  
  static DomContext getContext() {
    return authenticator().getContext();
  }

  public static DomMethod addModel(DomMethod submit, DomDwoProfileId profile)
      throws Dwo2Exception {
    RestMethod rest = new RestMethod();
    rest.setRestContext(getContext());
    rest.setDomDwoProfile(profile);
    rest.setDomMethod(submit);

    DomMethod result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/method/add", DomMethod.class, rest);
    LOG.log(Level.FINE, "Added method of teacher with username {0} to his school.",
        new Object[] {authenticator().getUsername()});
    return result;
  }

  public static DomMethod updateModel(DomMethod submit, DomDwoProfileId profile)
	      throws Dwo2Exception {
	  	RestMethod rest = new RestMethod();
	    rest.setRestContext(getContext());
	    rest.setDomDwoProfile(profile);
	    rest.setDomMethod(submit);

	    DomMethod result = StoredRestManager.getInstance()
	        .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/method/update", DomMethod.class, rest);
	    LOG.log(Level.FINE, "Updated method of teacher with username {0} to his school.",
	        new Object[] {authenticator().getUsername()});
	    return result;
	  }
  

  public static Boolean removeMethod(DomMethod submit, DomDwoProfileId profile)
	      throws Dwo2Exception {
	  	RestMethod rest = new RestMethod();
	    rest.setRestContext(getContext());
	    rest.setDomDwoProfile(profile);
	    rest.setDomMethod(submit);

	    Boolean result = StoredRestManager.getInstance()
	        .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/method/remove", Boolean.class, rest);
	    LOG.log(Level.FINE, "Removed studentmodel of teacher with username {0} to his school.",
	        new Object[] {authenticator().getUsername()});
	    return result;
	  }
  

	public static DomMethod get(DomMethod context, DomDwoProfileId profile) throws Dwo2Exception {
		RestMethod rest = new RestMethod();
	 rest.setRestContext(getContext());
	 rest.setDomDwoProfile(profile);
	 rest.setDomMethod(context);
	 DomMethod src =
			        StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/method/get",
			            DomMethod.class, rest);
	 LOG.log(Level.FINE, "Retrieved method of the teacher with username {0}.",
		new Object[] {authenticator().getUsername()});
     return src;
	}
}
