package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherStudentModelManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;

class StudentModelService {

	SecuredTeacherStudentModelManager manager = new SecuredTeacherStudentModelManager();

	final DomContext context;
	Promise<List<DomStudentModelContext>> models;
	
	@Inject StudentModelService(DwoGlobalVars vars) {
		context = new DomContext();
		context.setDomHasRole(vars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
		context.setRealm(vars.getCurrentLoginContext().getRealm());
		if (!vars.isPremium()) {
			models = Promises.resolved(Collections.emptyList());
		}
	}

	public Promise<List<DomStudentModelContext>> getModels() {
		if (models == null)
			return models = manager.getReducedList(context);
		return models;
	}
}
