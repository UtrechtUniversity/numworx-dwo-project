package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContext;
import nl.uu.fi.dwo.rest.dom.entities.DomScoContextFull;
import nl.uu.fi.dwo.rest.dom.entities.DomScoData;
import nl.uu.fi.dwo.rest.entities.RestScoContext;
import nl.uu.fi.dwo.rest.entities.RestScoContextFull;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

/**
 * CRUD for teachers on courses.
 * 
 * @author wim
 *
 */
public class SecuredTeacherScoContextManager extends AbstractScoContextManager {
  public SecuredTeacherScoContextManager(DomContext context) {
    super(context);
  }

  private static final Logger LOG =
      Logger.getLogger(SecuredTeacherScoContextManager.class.getName());

  /**
   * Update a course. Not all fields are updatable!
   * 
   * @param edit the course
   * @return the edited course
   */
  public DomScoContextFull update(DomScoContextFull edit, DomScoData data, DomDwoProfile dwoProfile)
      throws Dwo2Exception {
    RestScoContextFull rest = new RestScoContextFull();
    rest.setDomScoContext(edit);
    rest.setDomScoData(data);
    rest.setRestContext(context);
    rest.setDomDwoProfile(dwoProfile);
    DomScoContextFull result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/scoContext/update", DomScoContextFull.class, rest);
    LOG.log(Level.FINE, "Updated sco for the teacher with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return result;
  }

  public Integer countStudents(DomScoContext edit, DomDwoProfile profile) throws Dwo2Exception {
	  RestScoContext rest = new RestScoContext();
	  rest.setDomScoContext(edit);
	  rest.setDomDwoProfile(profile);
	  rest.setRestContext(getContext());
	  Integer result = StoredRestManager.getInstance()
			  .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/scoContext/countStudents", Integer.class, rest);
	  return result;
  }
  
  public DomScoContextFull add(DomScoContextFull edit, DomScoData data, DomDwoProfile dwoProfile)
      throws Dwo2Exception {
    RestScoContextFull rest = new RestScoContextFull();
    rest.setDomScoContext(edit);
    rest.setRestContext(context);
    rest.setDomScoData(data);
    rest.setDomDwoProfile(dwoProfile);
    DomScoContextFull result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/scoContext/add", DomScoContextFull.class, rest);
    LOG.log(Level.FINE, "Added sco for the teacher with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return result;
  }

  public Boolean remove(DomScoContext sco, DomDwoProfile profile) throws Dwo2Exception {
    RestScoContext rest = new RestScoContext();
    rest.setDomDwoProfile(profile);
    rest.setDomScoContext(sco);
    rest.setRestContext(context);
    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/scoContext/remove",
        Boolean.class, rest);
    LOG.log(Level.FINE, "Removed sco for the teacher with username {0}.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return result;
  }

  public Boolean trash(DomScoContext sco, DomDwoProfile profile) throws Dwo2Exception {
	    RestScoContext rest = new RestScoContext();
	    rest.setDomDwoProfile(profile);
	    rest.setDomScoContext(sco);
	    rest.setRestContext(context);
	    Boolean result = StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/scoContext/trash",
	        Boolean.class, rest);
	    LOG.log(Level.FINE, "Removed sco for the teacher with username {0}.",
	        new Object[] {RestAuthenticator.getInstance().getUsername()});
	    return result;
	  }
  

}
