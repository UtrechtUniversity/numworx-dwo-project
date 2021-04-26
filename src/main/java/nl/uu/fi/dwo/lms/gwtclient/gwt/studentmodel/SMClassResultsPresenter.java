package nl.uu.fi.dwo.lms.gwtclient.gwt.studentmodel;

import java.util.Map;

import javax.inject.Inject;

import nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.studentmodel.JsTeacherSMClassResultsView;
import nl.uu.fi.dwo.lms.gwtclient.gwt.persons.TaggedDomSchoolClass;
import nl.uu.fi.dwo.lms.gwtclient.gwt.ui.BasicDisplay;
import nl.uu.fi.dwo.rest.dom.DomTree;

public class SMClassResultsPresenter {

	public interface Display extends BasicDisplay {

		void showTree(DomTree<String> tree);

		void showSchoolClasses(Map<String, TaggedDomSchoolClass> schoolClasses);

		void setLoadingTreeMessage();

		void setEmptyTreeMessage();

		void setTitle(String title);
		
	}
	
	private Display view;
	
	@Inject void setView(JsTeacherSMClassResultsView view) {
		this.view = view;
	}
	
	@Inject SMClassResultsPresenter() {
		// TODO Auto-generated constructor stub
	}

	public void init() {
		view.init();
		view.clear();
	}
}
