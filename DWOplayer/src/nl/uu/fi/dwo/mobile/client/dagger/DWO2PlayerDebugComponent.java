package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Singleton;

import dagger.Component;
import nl.uu.fi.dwo.mobile.client.ui.NeedLoginModule;

@Singleton
@Component(modules = { DebugModule.class, PlayerModule.class, PlaceModule.class, HeaderLessModule.class, NeedLoginModule.class, XapiModule.class })
public interface DWO2PlayerDebugComponent extends DWO2PlayerComponent {

	@Component.Builder
	interface Builder extends DWO2PlayerComponent.Builder {
		DWO2PlayerDebugComponent build();
	}

}
