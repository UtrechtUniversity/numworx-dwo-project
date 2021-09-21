package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.i18n.client.LocaleInfo;

import fi.dwo.gwt.lib.rest.CallManagers.MethodManager;
import fi.dwo.gwt.lib.rest.CallManagers.SecuredTeacherStudentModelManager;
import nl.uu.fi.dwo.lms.gwtclient.gwt.DwoGlobalVars;
import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.DescriptionService;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomLRS;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClassId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@RoleScope
public class StudentModelService implements DescriptionService {

	private final String lang = LocaleInfo.getCurrentLocale().getLocaleName();

	SecuredTeacherStudentModelManager manager = new SecuredTeacherStudentModelManager();

	final DomContext context;
	Promise<List<DomStudentModelContext>> models;
	
	Map<String, Promise<?>> promises = new LinkedHashMap<>();
	
	@Inject StudentModelService(DwoGlobalVars vars) {
		context = new DomContext();
		context.setDomHasRole(vars.getSchoolLogins().getActiveSchoolRoleAndClass().getHasRole());
		context.setRealm(vars.getCurrentLoginContext().getRealm());
		if (!vars.isPremium()) {
			models = Promises.resolved(Collections.emptyList());
		} else {
			DomMethod value = new DomMethod();
			value.setMethod("Geen methode"); // FIXME i18n
			methods.put(null, Promises.resolved(value));
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

	private String key(DomStudentModelContextId id, DomStudentModelContextInfo info) {
		return id.getId().getIdString() + "/" + info.getId();
	}
	
	private String key(DomStudentModelContextId id, DomSchoolClassId sc) {
		return id.getId().getIdString() + "/" + sc.getId().getIdString();
	}
	private String key(DomStudentModelContext4Student c) {
		return key(c, c.getSchoolClass());
	}
		
	private String key(DomStudentModelScorePerTeacher scores) {
		StringBuilder sb = new StringBuilder();
		for (DomMapEntry<PersistenceId, DomSchoolClass> x: scores.getSchoolClasses()) 
			sb.append(x.getKey().getIdString());
		sb.append("/");
		for (DomMapEntry<PersistenceId, DomStudentModelContext> x: scores.getStudentModelContexts())
			sb.append(x.getKey().getIdString());
		sb.append("/");
		if (scores.getStudents() != null) {
			for (DomMapEntry<PersistenceId, DomStudent> x: scores.getStudents())
				sb.append(x.getKey().getIdString());
		}
		return sb.toString();
	}
	
	private <T> Promise<T> put(String key,  Promise<T> value) {
		promises.put(key, value);
		return value;
	}
	
	private boolean containsKey(String key) {
		return promises.containsKey(key);
	}
	
	@SuppressWarnings("unchecked")
	private <T> Promise<T> get(String key) {
		return (Promise<T>) promises.get(key);
	}
	
	
	@Override
	public Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info) {
		    String key = key(id, info);
		    if (containsKey(key)) return get(key);
			return put(key, manager.getDescription(id, info.getId(), lang, context));
	}
	
	public Promise<DomStudentModelContext4Student> getForClass(DomStudentModelContextId id, DomSchoolClassId sc ) {
		String key = key(id, sc);
		if (containsKey(key)) return get(key);
		return put(key, manager.getStudentModelForClass(context, id, sc));
	}
	
	public Promise<DomStudentModelScorePerTeacher> getScores(DomStudentModelScorePerTeacher scores) {
		String key = key(scores);
		if (containsKey(key)) return get(key);
		return put(key, manager.getScores(context, scores));
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

	public Promise<Boolean> updateForClass(DomStudentModelContext4Student object) {
		String key = key(object);
		promises.remove(key);
		return manager.updateModelForClass(context, object);		
	}

	Map<PersistenceId, Promise<DomMethod>> methods = new HashMap<>();

	@Inject MethodManager methodMan;

	public Promise<DomMethod> getActiveMethod(PersistenceId pid) {		
		return methods.computeIfAbsent(pid, id -> { 			
			DomMethod method = new DomMethod(id);
			return methodMan.getMethod(context, method);
		});
	}

	public Promise<List<DomMethod>> getMethods() {
		return methodMan.getList(context).then(p -> { 
			List<DomMethod> all = p.getValue();
			for (DomMethod m: all) {
				PersistenceId id = m.getId();
				methods.computeIfAbsent(id, pid -> Promises.resolved(m));
			}
			all.add(0, methods.get(null).getValue());
			return p; });
	}
	
	
}
