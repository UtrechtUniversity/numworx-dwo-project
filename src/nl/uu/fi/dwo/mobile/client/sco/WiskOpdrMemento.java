package nl.uu.fi.dwo.mobile.client.sco;

import org.osgi.util.promise.Promise;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;

import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleView;
import nl.uu.fi.dwo.rest.dom.entities.DomStudentModelContextId;

public class WiskOpdrMemento extends Memento {

	public WiskOpdrMemento(ActivityComponent activity, Scorm2004IF api, ViewModuleView view, Promise<DomStudentModelContextId> studentModel) {
		super(activity, api, view, studentModel);
	}

	@Override
	public void onClose(CloseEvent<Window> event) {
		close();
	}

	@Override
	public void onWindowClosing(ClosingEvent event) {
		close();
	}


}
