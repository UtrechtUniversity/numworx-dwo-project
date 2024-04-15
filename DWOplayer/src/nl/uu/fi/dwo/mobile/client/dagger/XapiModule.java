package nl.uu.fi.dwo.mobile.client.dagger;

import java.util.Optional;

import javax.inject.Provider;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import nl.uu.fi.dwo.account.client.DwoGlobalVars;
import nl.uu.fi.dwo.mobile.client.DWOplayerParameters;
import nl.uu.fi.dwo.mobile.client.ui.views.XapiWrapper;
import nl.uu.fi.dwo.rest.dom.entities.RoleType;

@Module
public abstract class XapiModule {
	@Reusable
	@Provides static Optional<XapiWrapper> wrap(Provider<XapiWrapper> provider, DwoGlobalVars vars, DWOplayerParameters PARAMETERS) { 
		if (vars.getRoleType() != RoleType.STUDENT 
				|| PARAMETERS.inExam() 
				|| !PARAMETERS.getDwoEnv().contains("test")
		)
			return Optional.empty();
		return Optional.of(provider.get());
		
	}
}
