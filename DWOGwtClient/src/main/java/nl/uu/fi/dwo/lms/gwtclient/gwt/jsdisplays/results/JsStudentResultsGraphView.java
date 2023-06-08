package nl.uu.fi.dwo.lms.gwtclient.gwt.jsdisplays.results;

import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;

import dagger.Reusable;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsGraphPresenter.Display;

@Reusable public class JsStudentResultsGraphView implements Display {

	@Inject JsStudentResultsGraphView() {
	}

	@Override
	public void init() {
	}

	@Override
	public void clear() {
//		JsStudentResultsGraphDisplay.clear();
	}

	@Override
	public void setHelp(String url) {
	}

	@Override
	public String getId() {
		return JsStudentResultsGraphDisplay.getId();
	}

	@Override
	public void hide() {
		JsStudentResultsGraphDisplay.hide();
	}

	@Override
	public void init(JavaScriptObject resultState) {
		JsStudentResultsGraphDisplay.init(resultState);
	}

}
