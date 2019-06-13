package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@RoleScope
public class StudentResultsService implements StudentResults {
	
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
	Map<PersistenceId, Promise<DomStudentModelDataScore>> map = new HashMap<>();

	public Promise<List<DomStudentModelContext>> getModels() {
		if (models == null) {
			models = manager.getStudentModels(context).map(this::trimObjectives);
		} else if (models.isDone() && models.getFailure() != null ) {
			models = manager.getStudentModels(context).map(this::trimObjectives);
		}		
		return models;
	}
	
	public Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id) {
		PersistenceId pid = id.getId();
		Promise<DomStudentModelDataScore> result = map.get(pid);
		if (result == null) {
			result = manager.getStudentModelDataScore(context, id);
		} else if (result.isDone() && result.getFailure() != null) {
			result = manager.getStudentModelDataScore(context, id);
		}
		map.put(pid, result);
		return result;
	}
	
	List<DomStudentModelContext> trimObjectives(List<DomStudentModelContext> list) {
		list.forEach(item -> {
			DomStudentModelStructure structure = item.getModelStructure();
			structure.getCategories().forEach(cat -> { 
				List<DomStudentModelObj> objectives = cat.getObjectives();
				int size = objectives.size();
				ListIterator<DomStudentModelObj> iterator = objectives.listIterator(size); 
				while(iterator.hasPrevious()) {
					DomStudentModelObj obj = iterator.previous();
					if (isEmptyTitle(obj)) {
						iterator.remove();
					} else {
						break;
					}
				}
			});
		});
		
		
		return list;
	}

	private boolean isEmptyTitle(DomStudentModelObj obj) {
		Map<String, String> title = obj.getInfo().getTitle();
		return title.isEmpty()
			|| title.values().stream().allMatch(x -> x == null || x.isEmpty())
				;
	}
	
	public void clear() {
		map.clear();
	}
}
