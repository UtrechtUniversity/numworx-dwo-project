package nl.uu.fi.dwo.mobile.client.dagger;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.gwt.user.client.ui.Widget;

import dagger.Module;
import dagger.Provides;
import nl.uu.fi.dwo.mobile.client.sco.Memento;
import nl.uu.fi.dwo.mobile.client.sco.MementoModule;
import nl.uu.fi.dwo.mobile.client.sco.SMLogger;
import nl.uu.fi.dwo.mobile.client.sco.Scorm2004IF;
import nl.uu.fi.dwo.mobile.client.sco.WiskOpdrMemento;
import nl.uu.fi.dwo.mobile.client.ui.ActivityComponent;
import nl.uu.fi.dwo.mobile.client.ui.RPCHandler;
import nl.uu.fi.dwo.mobile.client.ui.views.ViewModuleViewImpl;

@Module
public class PrintViewModule {

	static class PrintMementoModule extends MementoModule {
		@Override
		protected Memento memento(ActivityComponent a, Provider<Scorm2004IF> api) {
			return new WiskOpdrMemento(a, api.get());
		}

	}
	
   final ViewModuleViewImpl createEntryView(RPCHandler rpc, boolean header, Scorm2004IF api, ActivityComponent.Builder builder) {
		return new ViewModuleViewImpl(
				builder.loggingModule(new SMLogger.LoggingModule()).mementoModule(new PrintMementoModule()).build(), 
				rpc, header, api) {

			@Override
			public Widget asWidget() {
				return contentPanel;
			}

		}
		.initialize();
	}

  @Provides @Singleton protected ViewModuleViewImpl getViewModuleView(RPCHandler rpc, @Named("parentAPI") Scorm2004IF api, ActivityComponent.Builder builder) {
    return createEntryView(rpc, true, api, builder);
  }
  
  @Provides @Singleton protected SMLogger.LoggingModule loggingModule() { return new SMLogger.LoggingModule(); }
  
  protected PrintViewModule() {}
}
