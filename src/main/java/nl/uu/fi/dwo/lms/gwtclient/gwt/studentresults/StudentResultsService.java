package nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.i18n.client.LocaleInfo;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@RoleScope
public class StudentResultsService implements StudentResults {
	
	SecuredStudentStudentModelManager manager;
	DomContext context;
	DomSchoolClass sc;
	@Inject Lazy<AdviseMeService> adviseMe;

	@Inject StudentResultsService(SecuredStudentStudentModelManager manager, DwoGlobalVars vars) {
		this.manager = manager;
		context = new DomContext();
		context.setDomHasRole(vars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
		context.setRealm(vars.getCurrentLoginContext().getRealm());
		sc = vars.getCurrentSchoolClass();
		if (!vars.isPremium()) {
			models = Promises.resolved(Collections.emptyList());
		}
	}

	Promise<List<DomStudentModelContext>> models;
	Map<PersistenceId, Promise<DomStudentModelDataScore>> map = new HashMap<>();

	public Promise<List<DomStudentModelContext>> getModels() {
		if (models == null || (models.isDone() && models.getFailure() != null) ) {
			models = manager.getReducedModels(context, sc)
					.recoverWith(p -> manager.getStudentModels(context))					
					.then(this::insertAdviseMe);
		}		
		return models;
	}
	
	
	private Promise<List<DomStudentModelContext>> insertAdviseMe(Promise<List<DomStudentModelContext>> p) {
		List<DomStudentModelContext> list = p.getValue();
		Iterator<DomStudentModelContext> iter = list.iterator();
		String lang = LocaleInfo.getCurrentLocale().getLocaleName();
		boolean advise = false;
		while (iter.hasNext()) {
			DomStudentModelContext context = iter.next();
			String title = context.getModelStructure().getInfo().getTitle().get(lang);
			if ("AdviseMe:".equals(title)) {
				advise = true;
				iter.remove();
				break;
			}
		}
		if (advise) 
			return adviseMe.get().getModels().map( l -> { l = new ArrayList<>(l); l.addAll(list); return l; });
		return p;
	}
	
	
	public Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id) {
		PersistenceId pid = id.getId();
		Promise<DomStudentModelDataScore> result = map.get(pid);
		if (result == null) {
			if (id instanceof DomStudentModelContext) {
				DomStudentModelStructureScore s = ((DomStudentModelContext) id).getModelStructure().generateStudentModelStructureScore();
				s.recalculateAncestors();
				DomStudentModelDataScore ss = new DomStudentModelDataScore();
				ss.setDomStudentModelStructureScore(s);
				ss.setModelId(id);
				ss.setFetchTimeStamp(System.currentTimeMillis());
				result = Promises.resolved(ss);
			} else
				result = manager.getStudentModelDataScore(context, id);
		} else if (result.isDone() && result.getFailure() != null) {
			result = manager.getStudentModelDataScore(context, id);
		}
		map.put(pid, result);
		return result;
	}
	
	List<DomStudentModelContext> trimObjectives(List<DomStudentModelContext> list) {
		list.forEach(this::trimObjectives);
		return list;
	}


	private DomStudentModelContext trimObjectives(DomStudentModelContext item) {
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
		return item;
	}

	private boolean isEmptyTitle(DomStudentModelObj obj) {
		Map<String, String> title = obj.getInfo().getTitle();
		return  title == null ||
				title.isEmpty()
			|| title.values().stream().allMatch(x -> x == null || x.isEmpty())
				;
	}
	
	public void clear() {
		map.clear();
	}

	private Promise<DomStudentModelContext> getFull(Promise<DomStudentModelContext> p) {
		if (p.getValue().getModelStructure().getCategories() == null) {
			return manager.getStudentModel(context, p.getValue())
					.map(this::trimObjectives)
					.then(q -> { copy(p,q); return p;}); 
		}
		return p;
	}

	private void copy(Promise<DomStudentModelContext> p, Promise<DomStudentModelContext> q) {
		DomStudentModelContext vp = p.getValue();
		DomStudentModelContext vq = q.getValue();
		vp.setLastChangeTimeStamp(vq.getLastChangeTimeStamp());
		vp.setModelStructure(vq.getModelStructure());
		vp.setOptLock(vq.getOptLock());
		vp.setPublishState(vq.getPublishState());		
	}

	@Override
	public Promise<DomStudentModelContext> getModel(DomStudentModelContextId id) {
		return getModels()
				.map(list -> list.stream().filter(item -> id.getId().equals(item.getId())).findAny())
				.map(Optional::get) // can produce an failure
				.then(this::getFull);//.recover(p -> null);
	}
}
