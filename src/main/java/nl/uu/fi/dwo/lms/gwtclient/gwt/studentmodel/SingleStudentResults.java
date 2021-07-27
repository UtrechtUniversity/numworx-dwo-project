package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResults;
import nl.uu.fi.dwo.rest.dom.entities.DomMapEntry;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomSchoolClass;
import nl.uu.fi.dwo.rest.dom.entities.DomStudent;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelScorePerTeacher;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;
import nl.uu.fi.dwo.rest.dom.entities.DomUser;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;

@RoleScope public class SingleStudentResults implements StudentResults {

	@Inject StudentModelService service;
	@Inject SingleStudentResults() {
	}

	private DomStudentModelContext currentModel;
	DomSchoolClass schoolClass;
	DomStudent user;
	
	@Override
	public Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info) {
		return service.getDescription(id, info);
	}

	@Override
	public void clear() {
	}

	@Override
	public Promise<List<DomStudentModelContext4Student>> getModels() {
		return getModel(currentModel).map(Collections::singletonList);
	}

	@Override
	public Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id) {
		DomStudentModelScorePerTeacher scores = new DomStudentModelScorePerTeacher();
		scores.setSchoolClasses(Collections.singletonList(new DomMapEntry<>(schoolClass.getId(), schoolClass)));
		scores.setStudentModelContexts(Collections.singletonList(new DomMapEntry<>(currentModel.getId(), currentModel)));
		scores.setStudents(Collections.singletonList(new DomMapEntry<>(user.getId(), user)));
		return service.getScores(scores).map( all -> all.getStudentScores().get(0));
	}

	@Override
	public Promise<DomStudentModelContext4Student> getModel(DomStudentModelContextId id) {
		return service.getForClass(currentModel, schoolClass).then(p->service.stap0(p,id,schoolClass));
	}

	public void setUser(DomUser user) {
		this.user = new DomStudent(user);
	}

	public void setSchoolClass(DomSchoolClass schoolClass) {
		this.schoolClass = schoolClass;
	}

	public void setState(JavaScriptObject resultState) {
		JSONObject state = new JSONObject(resultState);
		String id = state.get("id").isString().stringValue();
		currentModel = new DomStudentModelContext();
		currentModel.setId(new PersistenceId(id));		
	}

	@Override
	public Promise<DomMethod> getActiveMethod(DomStudentModelStructure structure) {
		return service.getActiveMethod(structure.getActiveMethod());
	}

}
