package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;

@RoleScope
public class StudentResultsService {
	
	private SecuredStudentStudentModelManager manager;
	private DomContext context;

	@Inject StudentResultsService(SecuredStudentStudentModelManager manager, DwoGlobalVars vars) {
		this.manager = manager;
		context = new DomContext();
		context.setDomHasRole(vars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
		context.setRealm(vars.getCurrentLoginContext().getRealm());
		if (!vars.isPremium()) {
			models = Promises.resolved(Collections.emptyList());
		}
	}

	Promise<List<DomStudentModelContext>> models;

	Promise<List<DomStudentModelContext>> getModels() {
		if (models == null) {
			models = manager.getStudentModels(context);
		} else if (models.isDone() && models.getFailure() != null ) {
			models = manager.getStudentModels(context);
		}		
		return models;
	}
	
}
