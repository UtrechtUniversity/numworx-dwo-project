package nl.uu.fi.dwo.lms.gwtclient.gwt;

import javax.inject.Inject;

import org.osgi.util.promise.Promise;
import org.osgi.util.promise.Promises;

import com.google.gwt.user.client.ui.LayoutPanel;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraph;
import nl.uu.fi.dwo.rest.dom.entities.DomMethod;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContext4Student;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelDataScore;

public class StudentModelController {
	
	@Inject StudentModelController() { }
	@Inject StudentResultsGraph graph;

	public void go(LayoutPanel root) {
		
		root.add(graph);
		DomStudentModelContext4Student item = new DomStudentModelContext4Student();
		
		Promise<DomStudentModelDataScore> score = Promises.failed(new IllegalArgumentException());
		DomMethod method = new DomMethod();
		graph.setModelScore(item, score, method);
	}
}
