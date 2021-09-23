package nl.uu.fi.dwo.lms.jclient.lib.rest.managers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.RestAuthenticator;
import nl.uu.fi.dwo.lms.jclient.lib.rest.transport.StoredRestManager;
import nl.uu.fi.dwo.rest.RestListClassTypes;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextPatch;
import nl.uu.fi.dwo.rest.entities.RestContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContext;
import nl.uu.fi.dwo.rest.entities.RestStudentModelContextPatch;
import nl.uu.fi.dwo.rest.exceptions.Dwo2Exception;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecureDwoAdminStudentModelManager implements SecureStudentModelManager {
	private static final Logger LOG =
		      Logger.getLogger(SecureDwoAdminStudentModelManager.class.getName());

	private final StoredRestManager restManager = StoredRestManager.getInstance();

	@Override
	public List<DomStudentModelContext> getReducedList() throws Dwo2Exception {
	    RestContext rest = new RestContext();
	    rest.setRestContext(getContext());
	    List<DomStudentModelContext> src =
	        restManager.getPutList("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/studentmodel/getReducedList",
	            RestListClassTypes.DomStudentModelContext, rest);
	    LOG.log(Level.FINE, "Retrieved list of studentmodels of the dwoadmin with username {0}.",
	        new Object[] {restManager.getAuthenticator().getUsername()});
	    return src;
	}

	private DomContext getContext() {
		    return restManager.getAuthenticator().getContext();
	}

	@Override
	public DomStudentModelContext updateModel(DomStudentModelContext submit)
		      throws Dwo2Exception {
		    RestStudentModelContext rest = new RestStudentModelContext();
		    rest.setRestContext(getContext());
		    rest.setDomStudentModelContext(submit);

		    DomStudentModelContext result = StoredRestManager.getInstance()
		        .put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/studentmodel/update", DomStudentModelContext.class, rest);
		    LOG.log(Level.FINE, "Updated studentmodel of dwoadmin with username {0} to his school.",
		        new Object[] {restManager.getAuthenticator().getUsername()});
		    return result;
		  }

	@Override
	public DomStudentModelContext patchModel(DomStudentModelContextPatch submit)
		      throws Dwo2Exception {
		    RestStudentModelContextPatch rest = new RestStudentModelContextPatch();
		    rest.setRestContext(getContext());
		    rest.setDomPatch(submit);

		    DomStudentModelContext result = StoredRestManager.getInstance()
		        .put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/studentmodel/patch", DomStudentModelContext.class, rest);
		    LOG.log(Level.FINE, "Patch studentmodel of dwoadmin with username {0} to his school.",
		        new Object[] {restManager.getAuthenticator().getUsername()});
		    return result;
		  }

	@Override
	public DomStudentModelContext get(DomStudentModelContextId modelContext) throws Dwo2Exception {
		 RestStudentModelContext rest = new RestStudentModelContext();
		 rest.setRestContext(getContext());
		 DomStudentModelContext context = new DomStudentModelContext();
		 context.setId(modelContext.getId());
		 rest.setDomStudentModelContext(context);
		 DomStudentModelContext src =
				        StoredRestManager.getInstance().put("rest/sec:" + PathId.getId(getContext()) + "/dwoadmin/studentmodel/get",
				            DomStudentModelContext.class, rest);
		 LOG.log(Level.FINE, "Retrieved studentmodel of the dwoadmin with username {0}.",
			new Object[] {RestAuthenticator.getInstance().getUsername()});
	     return src;
		}

	@Override
	public DomSchoolMethod getActiveMethod(DomStudentModelContextId modelContext) throws Dwo2Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
