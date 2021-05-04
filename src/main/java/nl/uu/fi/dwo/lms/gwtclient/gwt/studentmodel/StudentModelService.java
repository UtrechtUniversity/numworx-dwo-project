package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.i18n.client.LocaleInfo;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherStudentModelManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@RoleScope
public class StudentModelService implements DescriptionService {

	private final String lang = LocaleInfo.getCurrentLocale().getLocaleName();

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

	public Promise<DomStudentModelContext> getStudentModel(PersistenceId pid) {
		return getModels().map(list -> {
			for(DomStudentModelContext item: list) {
				if (item.getId().equals(pid)) return item;
			}
			throw new NoSuchElementException();
		}).then(p -> { 
			DomStudentModelContext sm = p.getValue();
			if (sm.getModelStructure().getCategories() == null) {
				return manager.getStudentModel(context, sm)
						.then( q -> {  
							sm.getModelStructure().setCategories(q.getValue().getModelStructure().getCategories());
							sm.getModelStructure().setInfo(q.getValue().getModelStructure().getInfo());
							sm.setOptLock(q.getValue().getOptLock());
							return p;} );
			}
			
			return p;
		});
		
	}

	@Override
	public Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info) {
			return manager.getDescription(id, info.getId(), lang, context);
	}
	
	public Promise<DomStudentModelContext4Student> getForClass(DomStudentModelContextId id, DomSchoolClassId sc ) {
		return manager.getStudentModelForClass(context, id, sc);
	}
	
	public Promise<DomStudentModelScorePerTeacher> getScores(DomStudentModelScorePerTeacher scores) {
		return manager.getScores(context, scores);
	}
	
	Promise<DomStudentModelContext4Student> stap0(Promise<DomStudentModelContext4Student> p, DomStudentModelContextId cid, DomSchoolClassId schoolClass) {
		if (p.getValue() != null)		
			return p;
		return getStudentModel(cid.getId()).map( model -> {
			DomStudentModelContext4Student result = new DomStudentModelContext4Student();
			result.setFilter(Collections.emptyMap());
			result.setId(model.getId());
			result.setModelStructure(model.getModelStructure());
			result.setOptLock(model.getOptLock());
			result.setSchoolClass(schoolClass);
			return result;
		});
	}

}
