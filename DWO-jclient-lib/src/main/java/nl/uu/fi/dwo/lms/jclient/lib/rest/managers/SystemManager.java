package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSamlUser;
import nl.uu.fi.dwo.rest.dom.entities.DomSchool;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolFull;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolId;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomTeacher;
import nl.uu.fi.dwo.rest.entities.RestSamlUser;
import nl.uu.fi.dwo.rest.entities.RestSchool;
import nl.uu.fi.dwo.rest.entities.RestSchoolFull;
import nl.uu.fi.dwo.rest.entities.RestScoContextId;
import nl.uu.fi.dwo.rest.entities.RestSubmitStudentToSchoolClass;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SystemManager {

  final StoredRestManager manager;
  private static final Logger LOG = Logger.getLogger(SystemManager.class.getName());

  public SystemManager(StoredRestManager m) {
    manager = m;
  }

  private DomContext getContext() {
    return manager.getContext();
  }

  public DomSchoolFull getSchool(DomSchoolId submit) throws Dwo2Exception {
    RestSchool rest = new RestSchool();
    rest.setRestContext(getContext());
    rest.setDomSchool(new DomSchool());
    rest.getDomSchool().setId(submit.getId());
    rest.getDomSchool().setOptLock(submit.getOptLock());
    DomSchoolFull result =
        manager.put("rest/system/school/get", DomSchoolFull.class, rest);
    LOG.log(Level.FINE, "Retrieved full school with id {1} for system with username {0}.",
        new Object[] {manager.getAuthenticator().getUsername(), rest.getDomSchool().getId()});
    return result;
  }
  
  public DomSchoolFull getSchool(String school) throws Dwo2Exception {
    RestSchool rest = new RestSchool();
    rest.setRestContext(getContext());
    rest.setDomSchool(new DomSchool());
    rest.getDomSchool().setSchoolName(school);
    DomSchoolFull result =
        manager.put("rest/system/school/getByName", DomSchoolFull.class, rest);
    LOG.log(Level.FINE, "Retrieved full school with id {1} for system with username {0}.",
        new Object[] {manager.getAuthenticator().getUsername(), rest.getDomSchool().getId()});
    return result;
    
  }
  
  

  public List<DomSchoolClass> getSchoolClasses(DomSchoolId submit) throws Dwo2Exception {
    RestSchool rest = new RestSchool();
    rest.setRestContext(getContext());
    rest.setDomSchool(new DomSchool());
    rest.getDomSchool().setId(submit.getId());
    rest.getDomSchool().setOptLock(submit.getOptLock());
    List<DomSchoolClass> result =
        manager.getPutList("rest/system/schoolclass/getList", RestListClassTypes.DomSchoolClass, rest);
    LOG.log(Level.FINE, "Retrieved schoolclasses for id {1} for system with username {0}.",
      new Object[] {manager.getAuthenticator().getUsername(), rest.getDomSchool().getId()});
    return result;
  }

  public DomSamlUser requestSamlToken(DomSamlUser user) throws Dwo2Exception {
    RestSamlUser rest = new RestSamlUser();
    rest.setDomSamlUser(user);
    rest.setRestContext(getContext());
    DomSamlUser result =
        manager.put("rest/system/user/requestSamlToken", DomSamlUser.class, rest);
    LOG.log(Level.FINE, "Retrieved token for id {1} for system with username {0}.",
      new Object[] {manager.getAuthenticator().getUsername(), rest.getDomSamlUser().getSamlUserId()});
    return result;
  }
  
  public String getSuggestion ( String input ) throws Dwo2Exception {
	  String result = manager.put("rest/system/user/suggestion", String.class, input);
	  return result;
  }
  
  public Boolean submitSchool (DomSchoolFull school) throws Dwo2Exception {
	  RestSchoolFull rest = new RestSchoolFull();
	  rest.setDomSchoolFull(school);
	  rest.setRestContext(getContext());
	  Boolean result = 
			  manager.put("rest/system/school/submit", Boolean.class, rest);
	    LOG.log(Level.FINE, "Submit school for id {1} for system with username {0}.",
	    	      new Object[] {manager.getAuthenticator().getUsername(), school.getSchoolLogin()});
	  return result;
  }

	public List<DomTeacher> getTeachersInSchool(DomSchool school) throws Dwo2Exception {
		RestSchool rest = new RestSchool();
		rest.setDomSchool(school);
		rest.setRestContext(getContext());
		List<DomTeacher> result = 
				manager.getPutList("rest/system/school/getTeachersInSchoolList", RestListClassTypes.DomTeacher, rest);
		return result;
	}

	public Boolean submitStudentToSchoolClass(DomSubmitStudentToSchoolClass submit) throws Dwo2Exception {
		RestSubmitStudentToSchoolClass rest = new RestSubmitStudentToSchoolClass();
		rest.setRestContext(getContext());
		rest.setDomSubmitStudentToSchoolClass(submit);
		Boolean result = manager.put("rest/system/schoolclass/submitStudent", Boolean.class, rest);
		LOG.log(Level.FINE, "Submitted student {1} to schoolclass {2} for schooladmin with username {0}.",
				new Object[] { manager.getAuthenticator().getUsername(), submit.getStudent().getId(),
						submit.getSchoolClassTo().getId() });
		return result;
	}

	public DomSchoolId getSchool(DomScoContextId id) throws Dwo2Exception {
		RestScoContextId rest = new RestScoContextId();
		rest.setRestContext(getContext());
		rest.setDomScoContext(id);
		rest.setDomDwoProfile(null);
		DomSchoolId result = manager.put("rest/system/scoContext/getSchool", DomSchoolId.class, rest);
		return result;
	}
}
