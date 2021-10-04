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

import com.gargoylesoftware.htmlunit.html.DomProcessingInstruction;
import com.google.gwt.i18n.client.LocaleInfo;

import dagger.Lazy;
import fi.dwo.gwt.lib.rest.CallManagers.MethodManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredStudentStudentModelManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel.StudentModelPresenter;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfileFull;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassAndProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructureScore;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@RoleScope
public class StudentResultsService implements StudentResults {
	
	SecuredStudentStudentModelManager manager;
	MethodManager methodMan;
	DomContext context;
	DomSchoolClass sc;
	Promise<DomDwoProfileFull> profile;
	@Inject Lazy<AdviseMeService> adviseMe;
	

	@Inject StudentResultsService(SecuredStudentStudentModelManager manager, DwoGlobalVars vars, MethodManager methodMan) {
		this.manager = manager;
		this.methodMan = methodMan;
		context = new DomContext();
		context.setDomHasRole(vars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
		context.setRealm(vars.getCurrentLoginContext().getRealm());
		sc = vars.getCurrentSchoolClass();
		profile = vars.getProfile();
		if (!vars.isPremium()) {
			models = Promises.resolved(Collections.emptyList());
		} else {
			DomMethod value = new DomMethod();
			value.setMethod("Geen methode"); // FIXME i18n
			methods.put(null, Promises.resolved(value));
		}
	}

	Promise<List<DomStudentModelContext4Student>> models;
	Map<PersistenceId, Promise<DomStudentModelDataScore>> map = new HashMap<>();
	Map<PersistenceId, Promise<DomMethod>> methods = new HashMap<>();

	public Promise<List<DomStudentModelContext4Student>> getModels() {
		if (models == null || (models.isDone() && models.getFailure() != null) ) {
			
			models = profile.then( p -> manager.getReducedModelsForClass(context, sc, p.getValue()))
					//.recoverWith(p -> manager.getStudentModels(context))					
					.then(this::insertAdviseMe);
		}		
		return models;
	}
	
	
	private Promise<List<DomStudentModelContext4Student>> insertAdviseMe(Promise<List<DomStudentModelContext4Student>> p) {
		List<DomStudentModelContext4Student> list = p.getValue();
		Iterator<DomStudentModelContext4Student> iter = list.iterator();
		String lang = LocaleInfo.getCurrentLocale().getLocaleName();
		boolean advise = false;
		while (iter.hasNext()) {
			DomStudentModelContext4Student context = iter.next();
			String title = StudentModelPresenter.getTitle(context.getModelStructure().getInfo(),lang);
			if ("AdviseMe:".equals(title)) {
				advise = true;
				iter.remove();
				break;
			}
		}
		if (advise) 
			return adviseMe.get().getModels()
					.map( l -> { l = new ArrayList<>(l); l.addAll(list); return l; });
		return p;
	}
	
	
	public Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id) {
		PersistenceId pid = id.getId();
		Promise<DomStudentModelDataScore> result = map.get(pid);
		if (result == null) {
			if (id instanceof DomStudentModelContext4Student) {
				DomStudentModelStructureScore s = ((DomStudentModelContext4Student) id).getModelStructure().generateStudentModelStructureScore();
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
	
	List<DomStudentModelContext4Student> trimObjectives(List<DomStudentModelContext4Student> list) {
		list.forEach(this::trimObjectives);
		return list;
	}


	private DomStudentModelContext4Student trimObjectives(DomStudentModelContext4Student item) {
		DomStudentModelStructure structure = item.getModelStructure();
		trimStructure(structure);
		return item;
	}
	private DomStudentModelContext trimObjectives(DomStudentModelContext item) {
		trimStructure(item.getModelStructure());
		return item;
	}

	private void trimStructure(DomStudentModelStructure structure) {
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

	private Promise<DomStudentModelContext4Student> getFull(Promise<DomStudentModelContext4Student> p) {
		if (p.getValue().getModelStructure().getCategories() == null) {
			return manager.getStudentModel(context, p.getValue(), sc)
					.map(this::trimObjectives)
					.then(q -> { copy0(p,q); return p;}); 
		}
		return p;
	}

	private void copy(Promise<DomStudentModelContext4Student> p, Promise<DomStudentModelContext4Student> q) {
		DomStudentModelContext4Student vp = p.getValue();
		DomStudentModelContext4Student vq = q.getValue();
		vp.setModelStructure(vq.getModelStructure());
		vp.setOptLock(vq.getOptLock());
		vp.setFilter(vq.getFilter());
	}
	private void copy0(Promise<DomStudentModelContext4Student> p, Promise<DomStudentModelContext> q) {
		DomStudentModelContext4Student vp = p.getValue();
		DomStudentModelContext vq = q.getValue();
		vp.setModelStructure(vq.getModelStructure());
		//vp.setOptLock(vq.getOptLock());
		//vp.setFilter(vq.getFilter());
	}

	@Override
	public Promise<DomStudentModelContext4Student> getModel(DomStudentModelContextId id) {
		return getModels()
				.map(list -> list.stream().filter(item -> id.getId().equals(item.getId())).findAny())
				.map(Optional::get) // can produce an failure
				.then(this::getFull);//.recover(p -> null);
	}

	String lang = LocaleInfo.getCurrentLocale().getLocaleName();

	@Override
	public Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info) {
		return manager.getDescription(id, sc, info.getId(), lang, context);
	}

	@Override
	public Promise<DomMethod> getActiveMethod(DomStudentModelStructure structure) {		
		return methods.computeIfAbsent(structure.getActiveMethod(), id -> { 			
			DomMethod method = new DomMethod(id);
			return methodMan.getMethod(context, method);
		});
	}
}
