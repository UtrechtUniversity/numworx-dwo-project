package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import java.util.Map;

import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;

import dagger.Component;
import nl.uu.fi.dwo.lms.gwtclient.gwt.BootPanelController;
import nl.uu.fi.dwo.lms.gwtclient.gwt.studentresults.StudentResultsModule;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

@Component(modules= {BootModule.class} )
@Singleton
public interface BootComponent {

		EventBus getEventBus();
		BootPanelController controller();
		TeacherComponent.Builder teacherBuilder();
		SchoolAdminComponent.Builder schoolAdminBuilder();
		GuestComponent.Builder guestBuilder();
		StudentComponent.Builder studentBuilder();
		
//		Map<RoleType, PresenterBuilder> presenters();
}
