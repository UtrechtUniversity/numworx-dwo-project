package nl.uu.fi.dwo.lms.gwtclient.gwt.dagger;

import javax.inject.Singleton;

import com.google.web.bindery.event.shared.EventBus;

import dagger.Component;
import nl.uu.fi.dwo.lms.gwtclient.gwt.BootPanelController;

@Component(modules= {BootModule.class} )
@Singleton
public interface BootComponent {

		EventBus getEventBus();
		BootPanelController controller();
}
