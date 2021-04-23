package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.RoleScope;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResults;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;

@RoleScope
public class StudentResultsService implements StudentResults {
	
	@Inject StudentModelService service;
	

	@Inject StudentResultsService() {
	}


	@Override
	public Promise<String> getDescription(DomStudentModelContextId id, DomStudentModelContextInfo info) {
		return service.getDescription(id, info);
	}


	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Promise<List<DomStudentModelContext4Student>> getModels() {
		return Promises.failed(new NoSuchElementException());
	}


	@Override
	public Promise<DomStudentModelDataScore> getScore(DomStudentModelContextId id) {
		return Promises.failed(new NoSuchElementException());
	}

	private DomStudentModelContext4Student to4Student(DomStudentModelContext model) {
		DomStudentModelContext4Student result = new DomStudentModelContext4Student(model.getId());
		result.setFilter(Collections.emptyMap());
		result.setModelStructure(model.getModelStructure());
		result.setSchoolClass(null);
		result.setOptLock(model.getOptLock());
		return result;
	}

	@Override
	public Promise<DomStudentModelContext4Student> getModel(DomStudentModelContextId id) {
		return service.getStudentModel(id.getId()).map(this::to4Student);
	}

}
