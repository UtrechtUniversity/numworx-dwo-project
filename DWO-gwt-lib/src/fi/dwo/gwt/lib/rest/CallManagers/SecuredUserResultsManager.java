package fi.dwo.gwt.lib.rest.CallManagers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.util.function.Function;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.core.client.GWT;

import static fi.dwo.gwt.lib.rest.GwtRestVars.F;
import fi.dwo.gwt.lib.rest.client.RestCallers.SecuredUserCourseResultsRestCaller;
import fi.dwo.gwt.lib.rest.util.PromiseCallback;
import nl.uu.fi.dwo.rest.dom.entities.DomClassCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomContext;
import nl.uu.fi.dwo.rest.dom.entities.DomCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomDwoProfile;
import nl.uu.fi.dwo.rest.dom.entities.DomResultsPerStudentCourse;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentScoContext;
import nl.uu.fi.dwo.rest.entities.RestCourse;
import nl.uu.fi.dwo.rest.persistence.PersistenceId;
import nl.uu.fi.dwo.rest.util.PathId;

public class SecuredUserResultsManager implements UserResultsManager {

	SecuredUserCourseResultsRestCaller service;
	public SecuredUserResultsManager() {
		service = GWT.create(SecuredUserCourseResultsRestCaller.class);
	}

	@Override
	public Promise<DomResultsPerStudentCourse> getCourseResults(DomContext context, DomCourse course, DomDwoProfile profile) {
		RestCourse rest = new RestCourse();
		rest.setDomDwoProfile(profile);
		rest.setRestContext(context);
		rest.setDomCourse(course);
		Promise<List<DomStudentScoContext>> p = F(service::getCourseResults,PathId.getId(context), rest);
		return p.map(new Function<List<DomStudentScoContext>, DomResultsPerStudentCourse>() {

			@Override
			public DomResultsPerStudentCourse apply(List<DomStudentScoContext> t) {
				DomResultsPerStudentCourse result = new DomResultsPerStudentCourse();
				Map<PersistenceId, DomStudentScoContext> map = new HashMap<>();
				result.setStudentScoContexts(map);
				for (DomStudentScoContext context : t) {
					map.put(context.getScoID(), context);
				}
				return result;
			}
		});
	}

//	@Override
//	public Promise<DomResultsPerStudentCourse> getCourseResults(DomContext context, DomClassCourse classCourse,
//			DomDwoProfile profile) {
//		return Promises.failed(new IllegalArgumentException());
//	}

}
