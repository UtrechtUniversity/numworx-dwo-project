package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileId;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestSchoolClass;
import nl.uu.fi.dwo.rest.entities.RestSchoolMethod;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext4Student;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextPatch;
import nl.uu.fi.dwo.rest.entities.RestStudentModelScorePerTeacher;

/**
 * Manages the school roles and classes registered in HasRole.
 *
 * @author G.A.J. van der Plas
 */
public class SecureTeacherStudentModelManager implements SecureStudentModelManager {

  private static final Logger LOG =
      Logger.getLogger(SecureTeacherStudentModelManager.class.getName());

//  public static List<DomStudentModelContext> getList() throws Dwo2Exception {
//    RestContext rest = new RestContext();
//    rest.setRestContext(getContext());
//    List<DomStudentModelContext> src =
//        StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/getList",
//            RestListClassTypes.DomStudentModelContext, rest);
//    LOG.log(Level.FINE, "Retrieved list of studentmodels of the teacher with username {0}.",
//        new Object[] {RestAuthenticator.getInstance().getUsername()});
//    return src;
//  }  
  @Override
  public List<DomStudentModelContext> getReducedList(DomDwoProfile profile) throws Dwo2Exception {
	  	RestDwoProfile rest = new RestDwoProfile(profile, getContext());
	    List<DomStudentModelContext> src =
	        StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/getReducedList",
	            RestListClassTypes.DomStudentModelContext, rest);
	    LOG.log(Level.FINE, "Retrieved list of studentmodels of the teacher with username {0}.",
	        new Object[] {RestAuthenticator.getInstance().getUsername()});
	    return src;
	  }

  static DomContext getContext() {
    return RestAuthenticator.getInstance().getContext();
  }

  public static DomStudentModelContext addModel(DomStudentModelContext submit, DomDwoProfileId profile)
      throws Dwo2Exception {
    RestStudentModelContext rest = new RestStudentModelContext();
    rest.setRestContext(getContext());
    rest.setDomStudentModelContext(submit);
    rest.setDomDwoProfile(profile);

    DomStudentModelContext result = StoredRestManager.getInstance()
        .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/add", DomStudentModelContext.class, rest);
    LOG.log(Level.FINE, "Added studentmodel of teacher with username {0} to his school.",
        new Object[] {RestAuthenticator.getInstance().getUsername()});
    return result;
  }

  public DomStudentModelContext updateModel(DomStudentModelContext submit)
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
  
  public static DomSchoolMethod updateActiveMethod(DomSchoolMethod submit) 
  			throws Dwo2Exception {
	  RestSchoolMethod rest = new RestSchoolMethod();
	  rest.setRestContext(getContext());
	  rest.setDomSchoolMethod(submit);
	  DomSchoolMethod result = StoredRestManager.getInstance()
			  .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/updateMethod", DomSchoolMethod.class, rest);
	  return result;
  }
  
  public DomSchoolMethod getActiveMethod(DomStudentModelContextId id) throws Dwo2Exception {
	  RestStudentModelContextId rest = new RestStudentModelContextId();
	  rest.setRestContext(getContext());
	  rest.setDomStudentModelContext(id);
	  rest.setDomSchoolClass(null);
	  DomSchoolMethod result = StoredRestManager.getInstance()
			  .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/getMethod", DomSchoolMethod.class, rest);
	  return result;
  }
  
  
  
  public static Boolean updateModelForClass(DomStudentModelContext4Student submit) 
			throws Dwo2Exception {
	  RestStudentModelContext4Student rest = new RestStudentModelContext4Student();
	  rest.setRestContext(getContext());
	  rest.setDomStudentModelContext(submit);
	  Boolean result = StoredRestManager.getInstance()
			  .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/updateForClass", Boolean.class, rest);
	  return result;
}

  
  
	public static DomStudentModelContext4Student getForClass(DomStudentModelContextId modelContext, DomSchoolClassId sc) throws Dwo2Exception {
		 RestStudentModelContextId rest = new RestStudentModelContextId();
		 rest.setRestContext(getContext());
		 rest.setDomStudentModelContext(modelContext);
		 rest.setDomSchoolClass(sc);
		 DomStudentModelContext4Student src =
				        StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/getForClass",
				        		DomStudentModelContext4Student.class, rest);
		 LOG.log(Level.FINE, "Retrieved  studentmodel for schoolclass of the teacher with username {0}.",
			new Object[] {RestAuthenticator.getInstance().getUsername()});
	     return src;
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

  public static Boolean removeModel(DomStudentModelContext submit)
	      throws Dwo2Exception {
	    RestStudentModelContext rest = new RestStudentModelContext();
	    rest.setRestContext(getContext());
	    rest.setDomStudentModelContext(submit);

	    Boolean result = StoredRestManager.getInstance()
	        .put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/remove", Boolean.class, rest);
	    LOG.log(Level.FINE, "Removed studentmodel of teacher with username {0} to his school.",
	        new Object[] {RestAuthenticator.getInstance().getUsername()});
	    return result;
	  }
  
//	public static DomStudentModelScorePerTeacher getScores(DomStudentModelScorePerTeacher dom) throws Dwo2Exception {
//		RestStudentModelScorePerTeacher rest = new RestStudentModelScorePerTeacher(getContext(), dom);
//		DomStudentModelScorePerTeacher result = StoredRestManager.getInstance().put(
//				"rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/getScores",
//				DomStudentModelScorePerTeacher.class, rest);
//		LOG.log(Level.FINE, "Got studentmodelscore of teacher with username {0} to his school.",
//				new Object[] { RestAuthenticator.getInstance().getUsername() });
//		return result;
//	}
	
	public static DomLRS getLRS() throws Dwo2Exception {
	  RestContext rest = new RestContext();
	  rest.setRestContext(getContext());
	  DomLRS lrs = StoredRestManager.getInstance().put(
	    "rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/getLRS",
	    DomLRS.class, rest);
	  return lrs;
	}

	public DomStudentModelContext get(DomStudentModelContextId modelContext) throws Dwo2Exception {
	 RestStudentModelContext rest = new RestStudentModelContext();
	 rest.setRestContext(getContext());
	 DomStudentModelContext context = new DomStudentModelContext();
	 context.setId(modelContext.getId());
	 rest.setDomStudentModelContext(context);
	 DomStudentModelContext src =
			        StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/teacher/studentmodel/get",
			            DomStudentModelContext.class, rest);
	 LOG.log(Level.FINE, "Retrieved studentmodel of the teacher with username {0}.",
		new Object[] {RestAuthenticator.getInstance().getUsername()});
     return src;
	}
}
