package nl.uu.fi.dwo.lms.gwtclient.gwt;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import nl.uu.fi.dwo.lms.gwtclient.gwt.dagger.DaggerStudentModelComponent;

public class StudentModelPanel implements EntryPoint {

	@Override
	public void onModuleLoad() {
		DaggerStudentModelComponent.create().controller().go(RootLayoutPanel.get());
	}

}
