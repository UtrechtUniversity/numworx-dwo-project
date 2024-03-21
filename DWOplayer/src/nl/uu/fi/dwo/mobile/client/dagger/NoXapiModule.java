package nl.uu.fi.dwo.mobile.client.dagger;

import java.util.Optional;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.ui.views.XapiWrapper;

@Module
public abstract class NoXapiModule {
	
	@Provides static Optional<XapiWrapper> wrap() { 
			return Optional.empty();
	}
}
