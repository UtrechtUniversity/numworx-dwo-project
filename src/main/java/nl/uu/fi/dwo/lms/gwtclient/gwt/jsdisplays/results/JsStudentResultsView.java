package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import javax.inject.Inject;

import dagger.Reusable;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsPresenter.Display;

@Reusable
public class JsStudentResultsView implements Display {

	@Inject JsStudentResultsView() {
	}

	@Override
	public void init() {
		JsStudentResultsDisplay.init();

	}

	@Override
	public void clear() {
		JsStudentResultsDisplay.clear();

	}

	@Override
	public void setHelp(String url) {
		JsStudentResultsDisplay.setHelp(url);

	}

	@Override
	public String getId() {
		return JsStudentResultsDisplay.getId();
	}

	@Override
	public void setTitle(String title) {
		JsStudentResultsDisplay.setTitle(title);
	}
}
