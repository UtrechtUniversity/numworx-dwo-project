package nl.uu.fi.dwo.lms.gwtclient.gwt;

import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.user.client.ui.LayoutPanel;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelCategory;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextInfo;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelObj;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelStructure;

public class StudentModelController {
	
	String lang = "nl";

	@Inject StudentModelController() { }
	@Inject StudentResultsGraph graph;
	
	private DomStudentModelStructure mock() {
		DomStudentModelStructure result = new DomStudentModelStructure();
		result.setActiveMethod(null);
		DomStudentModelContextInfo info = new DomStudentModelContextInfo(Collections.singletonMap(lang, "titel1"), Collections.singletonMap(lang, ""));
		result.setInfo(info);
		DomStudentModelCategory category = new DomStudentModelCategory();
		category.setInfo(info);
		result.setCategories(Collections.singletonList(category));
		
		DomStudentModelObj obj1, obj2;
		obj1 = new DomStudentModelObj();
		obj2 = new DomStudentModelObj();
		category.setObjectives(Arrays.asList(obj1, obj2));
		info = new DomStudentModelContextInfo(Collections.singletonMap(lang, "obj1"), Collections.singletonMap(lang, ""));
		info.setId("obj1");
		info.setX(10);info.setY(20);
		info.setMethods(Collections.emptyMap());
		obj1.setInfo(info);
		
		info = new DomStudentModelContextInfo(Collections.singletonMap(lang, "obj2"), Collections.singletonMap(lang, ""));
		info.setId("obj2");
		info.setVoorkennis(Collections.singletonList("obj1"));
		info.setX(100); info.setY(100);
		info.setMethods(Collections.emptyMap());
		obj2.setInfo(info);
		return result;
	}
	
	
	

	public void go(LayoutPanel root) {
		
		root.add(graph);
		DomStudentModelContext4Student item = new DomStudentModelContext4Student();
		DomStudentModelStructure modelStructure = mock();
		item.setModelStructure(modelStructure);
		Promise<DomStudentModelDataScore> score = Promises.failed(new IllegalArgumentException());

		DomMethod method = new DomMethod();
		method.books = Collections.emptyList();
		method.chapters = Collections.emptyList();
		method.edges = Collections.emptyList();
		method.standard = true;
		method.method = "Geen methode";
		
		graph.setModelScore(item, score, method);
	}
}
