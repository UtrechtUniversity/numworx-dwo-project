package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;
import nl.uu.fi.dwo.rest.entities.RestDwoProfile;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextId;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureSchoolAdminStudentModelManager implements SecureStudentModelManager {

	private static final Logger LOG =
		      Logger.getLogger(SecureSchoolAdminStudentModelManager.class.getName());

	private DomContext getContext() {
		    return StoredRestManager.getInstance().getAuthenticator().getContext();
	}
	
	@Override
	public List<DomStudentModelContext> getReducedList(DomDwoProfile profile) throws Dwo2Exception {
	    RestDwoProfile rest = new RestDwoProfile(profile, getContext());
	    List<DomStudentModelContext> src =
	        StoredRestManager.getInstance().getPutList("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/studentmodel/getReducedList",
	            RestListClassTypes.DomStudentModelContext, rest);
	    LOG.log(Level.FINE, "Retrieved list of studentmodels of the teacher with username {0}.",
	        new Object[] {RestAuthenticator.getInstance().getUsername()});
	    return src;
	}

	@Override
	public DomStudentModelContext updateModel(DomStudentModelContext submit) throws Dwo2Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DomStudentModelContext patchModel(DomStudentModelContextPatch submit) throws Dwo2Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DomStudentModelContext get(DomStudentModelContextId modelContext) throws Dwo2Exception {
		 RestStudentModelContext rest = new RestStudentModelContext();
		 rest.setRestContext(getContext());
		 DomStudentModelContext context = new DomStudentModelContext();
		 context.setId(modelContext.getId());
		 rest.setDomStudentModelContext(context);
		 DomStudentModelContext src =
				        StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/studentmodel/get",
				            DomStudentModelContext.class, rest);
		 LOG.log(Level.FINE, "Retrieved studentmodel of the teacher with username {0}.",
			new Object[] {RestAuthenticator.getInstance().getUsername()});
	     return src;
	}
	
	@Override
	public DomSchoolMethod getActiveMethod(DomStudentModelContextId id) throws Dwo2Exception {
	  RestStudentModelContextId rest = new RestStudentModelContextId();
	  rest.setRestContext(getContext());
	  rest.setDomStudentModelContext(id);
	  rest.setDomSchoolClass(null);
	  DomSchoolMethod result = StoredRestManager.getInstance()
			  .put("rest/sec:" + PathId.getId(getContext()) + "/schooladmin/studentmodel/getMethod", DomSchoolMethod.class, rest);
	  return result;
	}

}
